import { v4 as uuidv4 } from "uuid"
import FingerprintJS from "@fingerprintjs/fingerprintjs"
import Logan from "../lib/logan/entry"

class SipaJS {
  // 前台
  static APPLICATION_ID_FP_TODO: number = 1000001

  // 中台
  static APPLICATION_ID_CP_SSO: number = 1000101

  static APPLICATION_ID_CP_BASE: number = 1000102

  static APPLICATION_ID_CP_PROCESS: number = 1000103

  // 后台
  static APPLICATION_ID_BP_AMS: number = 1000201

  static APPLICATION_ID_BP_AMS_PROCESS: number = 1000202

  // 后台-移动断
  static APPLICATION_ID_BP_AMS_MOBILE: number = 1000202

  // 请求头 token key
  static HEADER_TOKEN_KEY: string = "Authorization"

  // 请求头 token value prefix
  static HEADER_TOKEN_VALUE_PREFIX: string = "Bearer "

  // token key
  static STORAGE_TOKEN_KEY: string = "TOKEN"

  // 7天免登陆key
  static STORAGE_IF_FREE_KEY: string = "IF_FREE"

  // login info key
  static STORAGE_LOGIN_INFO_KEY: string = "LOGIN_INFO"

  // user info key
  static STORAGE_USER_INFO_KEY: string = "USER_INFO"

  // 成功
  static SUCCESS_CODE: string = "000000"

  // 登陆校验失败
  static LOGIN_VERIFICATION_FAILED: string[] = [
    "000030",
    "000034",
    "000035",
    "000036",
    "000037",
    "000038",
  ]

  // 权限校验失败
  static PERMISSION_VERIFICATION_FAILED: string[] = [
    "000031",
    "000032",
    "000033",
  ]

  // apm start
  static REQUEST_ID: string = "X-Request-Id"
  static REQUEST_FROM: string = "X-Request-From"
  static MERCHANT_ID: string = "X-Merchant-Id"

  static REQUEST_FROM_TODO: string = "todo"
  static REQUEST_FROM_SSO: string = "sso"
  static REQUEST_FROM_AMS: string = "ams"

  static UNKNOWN_DEVICE_ID = "unknown"

  static SOURCE_H5 = "h5"
  static SOURCE_MINI_APP = "mini_app"
  static SOURCE_PC = "pc"

  static ENV_LOCAL = "local"
  static ENV_DEV = "dev"
  static ENV_FAT = "fat"
  static ENV_PROD = "prod"

  static Logan = Logan

  static GATEWAY_DEV_URL = "http://gateway-dev.sipa.com"
  static GATEWAY_FAT_URL = "http://gateway-fat.sipa.com"
  static GATEWAY_PROD_URL = "http://gateway.sipa.com"

  static BASE_DEV_URL = `${SipaJS.GATEWAY_DEV_URL}/base-service-server/logan/web`
  static BASE_FAT_URL = `${SipaJS.GATEWAY_FAT_URL}/base-service-server/logan/web`
  static BASE_PROD_URL = `${SipaJS.GATEWAY_PROD_URL}/base-service-server/logan/web`

  static SIPA_PUBLIC_KEY = `
-----BEGIN PUBLIC KEY-----
MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDRg1q915Xlgc3pfzfDBT7YicfQ
WFPZ4ESft75VnhsASCReLQCo3SdMz/f4pQji1W5GeNmC4Ief5vj5P327pKJWhnAk
4oMob4Jql47TylZXYy4cg36L+bdBDaGmQpJs6Qlfid94b6Fni/9C33CUuOczsu5H
H6+BEExRJkhA5brbuwIDAQAB
-----END PUBLIC KEY-----
`

  static LOGAN_LOG_TRY_TIMES = 3
  static LOGAN_UPLOAD_INTERVAL = 2 * 60 * 1000

  static VERSION = "1.0.0"

  static getRequestId = (): string => {
    return uuidv4()
  }
  // apm end

  // web存储 设备 token key
  static storageDeviceKey = (
    applicationId: string,
    companyId: string
  ): string => {
    return `${SipaJS.STORAGE_TOKEN_KEY}-${applicationId}-${companyId}`
  }

  static getDeviceId = async () => {
    try {
      const fp = await FingerprintJS.load()
      const result = await fp.get()
      return result.visitorId
    } catch (e) {
      return SipaJS.UNKNOWN_DEVICE_ID
    }
  }

  static getDbName = (requestFrom = "unknown") => {
    return `${requestFrom}_logan`
  }
}

export default SipaJS
