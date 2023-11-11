/**
 * @file Tests for logan-web-opensource
 */
import SipaJS from "../src"

require("fake-indexeddb/auto")

import IDBM from "../src/lib/idb-managed"
import LogManager from "../src/lib/logan/log-manager"
import LoganInstance from "../src/lib/logan/index"
import NodeIndex from "../src/lib/logan/node-index"
import {
  dateFormat2Day,
  M_BYTE,
  ONE_DAY_TIME_SPAN,
  sizeOf,
} from "../src/lib/logan/lib/utils"
import {
  LogSuccessHandler,
  ReportResult,
  ResultMsg,
} from "../src/lib/logan/interface"
import LoganDB, {
  LOG_DETAIL_TABLE_NAME,
  LOG_DAY_TABLE_NAME,
} from "../src/lib/logan/lib/logan-db"

const dbName = "testLogan"
const reportUrl = "http://192.168.0.19:8888"
const publicKey =
  "-----BEGIN PUBLIC KEY-----\n" +
  "MIGeMA0GCSqGSIb3DQEBAQUAA4GMADCBiAKBgG2m5VVtZ4mHml3FB9foDRpDW7Pw\n" +
  "Foa+1eYN777rNmIdnmezQqHWIRVcnTRVjrgGt2ndP2cYT7MgmWpvr8IjgN0PZ6ng\n" +
  "MmKYGpapMqkxsnS/6Q8UZO4PQNlnsK2hSPoIDeJcHxDvo6Nelg+mRHEpD6K+1FIq\n" +
  "zvdwVPCcgK7UbZElAgMBAAE=\n" +
  "-----END PUBLIC KEY-----"

const successHandler = (): void => {
  // noop
}

const errorHandler = (): void => {
  // noop
}

function clearDbFromWindow(): void {
  // @ts-ignore
  // noinspection JSConstantReassignment
  window.indexedDB = null
}

function setDbFromWindow(): void {
  // @ts-ignore
  // noinspection JSConstantReassignment
  window.indexedDB = require("fake-indexeddb").indexedDB
}

function mockXHR(
  status: number,
  responseText: string,
  statusText: string
): void {
  // @ts-ignore
  ;(window as any).XMLHttpRequest = class {
    status = 200
    responseText = ""
    statusText = ""
    readyState = 4

    open(): void {
      /* Noop */
    }

    onreadystatechange(): void {
      /* Noop */
    }

    setRequestHeader(): void {
      /* Noop */
    }

    send(): void {
      setTimeout(() => {
        this.readyState = 4
        this.status = status
        this.responseText = responseText
        this.statusText = statusText
        this.onreadystatechange()
      }, 1000)
    }
  }
}

describe("Logan API Tests", () => {
  beforeEach(() => {})
  afterEach(async () => {
    await IDBM.deleteDB(dbName)
    LogManager.resetQuota()
  })
  test("log", (done) => {
    LoganInstance.initConfig({
      dbName: dbName,
      reportUrl: reportUrl,
      publicKey: publicKey,
      errorHandler: errorHandler,
      successHandler: successHandler,
    })
    LoganInstance.log("aaa", 1)
    setTimeout(() => {
      done()
    }, 1000)
  })
  test("logWithEncryption", (done) => {
    LoganInstance.initConfig({
      dbName: dbName,
      reportUrl: reportUrl,
      publicKey: publicKey,
      errorHandler: errorHandler,
      successHandler: successHandler,
    })
    LoganInstance.logWithEncryption("aaa", 1)
    setTimeout(() => {
      try {
        expect(successHandler).toBeCalled
        expect(errorHandler).not.toBeCalled
        done()
      } catch (e) {
        done(e)
      }
    }, 1000)
  })
  test("customLog", (done) => {
    const customLogContent = JSON.stringify({
      content: "aaa",
      type: 100,
    })
    let expectLogContent: string, expectEncryptVersion: number
    const customSuccessHandler: LogSuccessHandler = (logConfig): void => {
      expectLogContent = logConfig.logContent
      expectEncryptVersion = logConfig.encryptVersion
    }
    LoganInstance.initConfig({
      reportUrl: reportUrl,
      publicKey: publicKey,
      dbName: dbName,
      errorHandler: errorHandler,
      successHandler: customSuccessHandler,
    })
    LoganInstance.customLog({
      logContent: customLogContent,
      encryptVersion: 1,
    })
    setTimeout(async () => {
      try {
        expect(errorHandler).not.toBeCalled
        expect(expectLogContent).toBe(customLogContent)
        expect(expectEncryptVersion).toBe(1)
        const logDetails = await IDBM.getItemsInRangeFromDB(dbName, {
          tableName: LOG_DETAIL_TABLE_NAME,
        })
        expect(logDetails.length).toBe(1)
        done()
      } catch (e) {
        done(e)
      }
    }, 1000)
  })
  test("log with large size", (done) => {
    LoganInstance.initConfig({
      reportUrl: reportUrl,
      publicKey: publicKey,
      dbName: dbName,
    })
    const logNum = 40
    for (let i = 0; i < logNum; i++) {
      let plaintext = ""
      // 200,000B
      for (let j = 0; j < 25000; j++) {
        plaintext += Math.random().toString(36).substring(2, 10)
      }
      const logString = "log" + i + ":" + plaintext
      LoganInstance.log(logString, 1)
    }
    setTimeout(async () => {
      try {
        expect.assertions(2)
        const dayResult = await IDBM.getItemsInRangeFromDB(dbName, {
          tableName: LOG_DAY_TABLE_NAME,
        })
        expect(dayResult[0].reportPagesInfo.pageSizes.length).toBe(9)
        expect(dayResult[0].totalSize).toBeLessThanOrEqual(7 * M_BYTE)
        done()
      } catch (e) {
        done(e)
      }
    }, 2000)
  })
  test("log with special char", (done) => {
    LoganInstance.initConfig({
      reportUrl: reportUrl,
      publicKey: publicKey,
      dbName: dbName,
    })
    const specialString = String.fromCharCode(200) + String.fromCharCode(3000)
    try {
      expect(sizeOf(specialString)).toBe(5)
    } catch (e) {
      done(e)
    }
    LoganInstance.log(specialString, 1)
    LoganInstance.logWithEncryption(specialString, 1)
    setTimeout(async () => {
      try {
        const logItems = await IDBM.getItemsInRangeFromDB(dbName, {
          tableName: LOG_DETAIL_TABLE_NAME,
        })
        expect(logItems.length).toBe(2)
        done()
      } catch (e) {
        done(e)
      }
    }, 1000)
  })
  test("report", (done) => {
    LoganInstance.initConfig({
      reportUrl: reportUrl,
      publicKey: publicKey,
      dbName: dbName,
    })

    try {
      expect.assertions(2)
    } catch (e) {
      done(e)
    }

    mockXHR(200, JSON.stringify({ errorCode: "000000" }), "OK")

    const date = new Date()
    const today = dateFormat2Day(date)
    const yesterday = dateFormat2Day(new Date(+date - ONE_DAY_TIME_SPAN))
    LoganInstance.log("aaa", 1)

    setTimeout(async () => {
      try {
        const deviceId = await SipaJS.getDeviceId()
        const reportResult = await LoganInstance.report({
          fromDayString: yesterday,
          toDayString: today,
          deviceId: deviceId,
          webSource: SipaJS.SOURCE_H5,
          environment: SipaJS.ENV_PROD,
          customInfo: JSON.stringify({
            appId: SipaJS.APPLICATION_ID_FP_TODO,
            unionId: SipaJS.REQUEST_FROM_TODO,
          }),
        })
        expect(reportResult[today].msg).toBe(
          LoganInstance.ResultMsg.REPORT_LOG_SUCCESS
        )
        expect(reportResult[yesterday].msg).toBe(LoganInstance.ResultMsg.NO_LOG)
        done()
      } catch (e) {
        done(e)
      }
    }, 1000)
  })
  test("customReport", (done) => {
    LoganInstance.initConfig({
      publicKey: publicKey,
      dbName: dbName,
    })

    mockXHR(200, JSON.stringify({ errorCode: "000000" }), "")

    const today = dateFormat2Day(new Date())
    LoganInstance.log("aaa", 1)
    setTimeout(async () => {
      try {
        const reportResult = await LoganInstance.report({
          fromDayString: today,
          toDayString: today,
          xhrOptsFormatter: () => {
            return {
              reportUrl: reportUrl,
            }
          },
        })
        expect.assertions(1)
        expect(reportResult[today].msg).toBe(
          LoganInstance.ResultMsg.REPORT_LOG_SUCCESS
        )
        done()
      } catch (e) {
        done(e)
      }
    }, 1000)
  })
})

describe("Logan Param Invalid Tests", () => {
  test("logType is not set", async () => {
    expect.assertions(1)
    LoganInstance.initConfig({
      reportUrl: reportUrl,
      dbName: dbName,
      errorHandler: (e: Error) => {
        expect(e.message).toBe("logType needs to be set")
      },
    })
    // @ts-ignore
    LoganInstance.log("aaa")
  })
  test("publicKey is not set", async () => {
    expect.assertions(1)
    LoganInstance.initConfig({
      reportUrl: reportUrl,
      dbName: dbName,
      errorHandler: (e: Error) => {
        expect(e.message).toBe(
          "publicKey needs to be set before logWithEncryption"
        )
      },
    })
    LoganInstance.logWithEncryption("aaa", 1)
  })
  test("reportConfig is not valid", async () => {
    expect.assertions(3)

    const fromDay = dateFormat2Day(
      new Date(+new Date() - ONE_DAY_TIME_SPAN * 2)
    )
    const toDay = dateFormat2Day(new Date())
    // @ts-ignore
    LoganInstance.report({}).catch((e) => {
      expect(e.message).toBe(
        "fromDayString is not valid, needs to be YYYY-MM-DD format"
      )
    })

    // @ts-ignore
    LoganInstance.report({
      deviceId: "aaa",
      fromDayString: toDay,
      toDayString: fromDay,
    }).catch((e) => {
      expect(e.message).toBe(
        "fromDayString needs to be no bigger than toDayString"
      )
    })

    // @ts-ignore
    LoganInstance.report({
      deviceId: "aaa",
      fromDayString: fromDay,
      toDayString: `${+new Date()}`,
    }).catch((e) => {
      expect(e.message).toContain("toDayString is not valid")
    })
  })
})

describe("Logan Exception Tests", () => {
  beforeEach(() => {
    LoganInstance.initConfig({
      reportUrl: reportUrl,
      publicKey: publicKey,
      dbName: dbName,
    })
  })
  afterEach(async () => {
    await IDBM.deleteDB(dbName)
    LogManager.resetQuota()
  })
  test("report fail if xhr status is not 200", (done) => {
    expect.assertions(2)

    mockXHR(100, "fail", "")

    LoganInstance.log("aaa", 1)

    setTimeout(async () => {
      try {
        const today = dateFormat2Day(new Date())
        const reportResult = await LoganInstance.report({
          deviceId: "aaa",
          fromDayString: today,
          toDayString: today,
        })
        expect(reportResult[today].msg).toBe(
          LoganInstance.ResultMsg.REPORT_LOG_FAIL
        )
        expect(reportResult[today].desc).toBe(
          "Request failed, status: 100, responseText: fail"
        )
        done()
      } catch (e) {
        done(e)
      }
    }, 1000)
  })
  test("report fail if server code is not 200", (done) => {
    expect.assertions(1)

    mockXHR(200, JSON.stringify({ errorCode: "000001" }), "")

    LoganInstance.log("aaa", 1)

    setTimeout(async () => {
      try {
        const today = dateFormat2Day(new Date())
        const reportResult = await LoganInstance.report({
          deviceId: "aaa",
          fromDayString: today,
          toDayString: today,
        })
        expect(reportResult[today].msg).toBe(
          LoganInstance.ResultMsg.REPORT_LOG_FAIL
        )
        done()
      } catch (e) {
        done(e)
      }
    }, 1000)
  })
  test("report with custom response", (done) => {
    expect.assertions(2)

    LoganInstance.log("aaa", 1)

    const today = dateFormat2Day(new Date())
    const report = async (): Promise<ReportResult> => {
      return await LoganInstance.report({
        deviceId: "aaa",
        fromDayString: today,
        toDayString: today,
        xhrOptsFormatter: () => {
          return {
            responseDealer: (xhrResponseText: any): any => {
              const responseOb = JSON.parse(xhrResponseText)
              if (responseOb.errorCode === "000000") {
                return {
                  resultMsg: ResultMsg.REPORT_LOG_SUCCESS,
                }
              } else {
                return {
                  resultMsg: ResultMsg.REPORT_LOG_FAIL,
                  desc: `responseOb.errorCode:${responseOb.errorCode}`,
                }
              }
            },
          }
        },
      })
    }

    setTimeout(async () => {
      try {
        mockXHR(200, JSON.stringify({ errorCode: "000000" }), "")
        const reportResult1 = await report()
        expect(reportResult1[today].msg).toBe(
          LoganInstance.ResultMsg.REPORT_LOG_SUCCESS
        )

        mockXHR(200, JSON.stringify({ errorCode: "000001" }), "")
        const reportResult2 = await report()
        expect(reportResult2[today].msg).toBe(
          LoganInstance.ResultMsg.REPORT_LOG_FAIL
        )

        done()
      } catch (e) {
        done(e)
      }
    }, 1000)
  })
  test("When IndexedDB is not supported", (done) => {
    expect.assertions(1)
    clearDbFromWindow()
    try {
      expect(LoganDB.idbIsSupported()).toBeFalsy()
      done()
    } catch (e) {
      done(e)
    } finally {
      setDbFromWindow()
    }
  })
  test("When exceeds log limit", (done) => {
    expect.assertions(4)
    clearDbFromWindow()

    const LOG_TRY_TIMES = 4
    const errorArr: Error[] = []

    LoganInstance.initConfig({
      logTryTimes: LOG_TRY_TIMES,
      reportUrl: reportUrl,
      publicKey: publicKey,
      dbName: dbName,
      errorHandler: (e: Error) => {
        errorArr.push(e)
      },
    })

    for (let i = 0; i < LOG_TRY_TIMES + 1; i++) {
      LoganInstance.log("aaa", 1)
    }

    setTimeout(async () => {
      try {
        setDbFromWindow()
        const logItems = await IDBM.getItemsInRangeFromDB(dbName, {
          tableName: LOG_DETAIL_TABLE_NAME,
        })

        expect(logItems.length).toBe(0)

        expect(errorArr[0].message).toBe(ResultMsg.DB_NOT_SUPPORT)

        // 测试save-log内部对tryTime的限制判断
        expect(errorArr[LOG_TRY_TIMES].message).toBe(ResultMsg.EXCEED_TRY_TIMES)

        LoganInstance.log("bbb", 1)

        // 测试index中logAsync对tryTime的限制判断
        expect(errorArr[LOG_TRY_TIMES + 1].message).toBe(
          ResultMsg.EXCEED_TRY_TIMES
        )
        done()
      } catch (e) {
        done(e)
      }
    }, 2000)
  })
})

describe("Test node-index", () => {
  test("node-index contains all property of logan-web", () => {
    for (const property in LoganInstance) {
      expect(NodeIndex[property]).toBeDefined()
      if (typeof (LoganInstance as any)[property] === "function") {
        expect(() => {
          NodeIndex[property]()
        }).not.toThrow()
      }
    }
  })
})
