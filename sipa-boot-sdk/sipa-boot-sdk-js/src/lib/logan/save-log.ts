import { LogConfig, LogEncryptMode, ResultMsg } from "./interface"
import Config from "./global-config"
import LoganDB from "./lib/logan-db"
import LogManager from "./log-manager"
import { invokeInQueue } from "./logan-operation-queue"
import * as ENC_UTF8 from "crypto-js/enc-utf8"
import * as ENC_BASE64 from "crypto-js/enc-base64"

interface LogStringOb {
  l: string
  iv?: string
  k?: string
  v?: number
}

let LoganDBInstance: LoganDB
function base64Encode(text: string): string {
  const textUtf8 = ENC_UTF8.parse(text)
  return textUtf8.toString(ENC_BASE64)
}

export default async function saveLog(logConfig: LogConfig): Promise<void> {
  try {
    if (!LogManager.canSave()) {
      throw new Error(ResultMsg.EXCEED_TRY_TIMES)
    }
    if (!LoganDB.idbIsSupported()) {
      throw new Error(ResultMsg.DB_NOT_SUPPORT)
    }
    if (!LoganDBInstance) {
      LoganDBInstance = new LoganDB(Config.get("dbName") as string | undefined)
    }
    if (logConfig.encryptVersion === LogEncryptMode.PLAIN) {
      const logStringOb: LogStringOb = {
        l: base64Encode(logConfig.logContent),
      }
      await invokeInQueue(async () => {
        await LoganDBInstance.addLog(JSON.stringify(logStringOb))
      })
    } else if (logConfig.encryptVersion === LogEncryptMode.RSA) {
      const publicKey = Config.get("publicKey")
      const encryptionModule = await import(
        /* webpackChunkName: "encryption" */ "./lib/encryption"
      )
      const cipherOb = encryptionModule.encryptByRSA(
        logConfig.logContent,
        `${publicKey}`
      )
      const logStringOb: LogStringOb = {
        l: cipherOb.cipherText,
        iv: cipherOb.iv,
        k: cipherOb.secretKey,
        v: LogEncryptMode.RSA,
      }
      await invokeInQueue(async () => {
        await LoganDBInstance.addLog(JSON.stringify(logStringOb))
      })
    } else {
      throw new Error(
        `encryptVersion ${logConfig.encryptVersion} is not supported.`
      )
    }
    await (Config.get("successHandler") as Function)(logConfig)
  } catch (e) {
    LogManager.errorTrigger()
    await (Config.get("errorHandler") as Function)(e)
  }
}
