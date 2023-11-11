import JSEncrypt from "jsencrypt"
import CryptoJS from "crypto-js"
import * as AES from "crypto-js/aes"
import * as ENC_UTF8 from "crypto-js/enc-utf8"

export interface RSACipherOb {
  cipherText: string
  iv: string
  secretKey: string
}

function generateRandomBytes(byteLength: number): string {
  let result = ""
  while (result.length < byteLength) {
    result += Math.random().toString(36).substring(2, 3)
  }
  return result
}

/**
 * AES-128-CTR
 */
function ctrEncrypt(
  plaintext: string,
  keyString: string,
  ivString: string
): string {
  return aesEncrypt(
    plaintext,
    keyString,
    ivString,
    CryptoJS.mode.CTR,
    CryptoJS.pad.NoPadding
  )
}

function aesEncrypt(
  plaintext: string,
  keyString: string,
  ivString: string,
  mode: any,
  padding: any
): string {
  const iv = ENC_UTF8.parse(ivString)
  const key = ENC_UTF8.parse(keyString)
  const cipherResult = AES.encrypt(plaintext, key, {
    mode,
    padding,
    iv,
  })
  return cipherResult.toString()
}

function rsaEncrypt(plaintext: string, publicKey: string): string {
  const en = new JSEncrypt()
  en.setPublicKey(publicKey)
  const encrypt = en.encrypt(plaintext)
  if (encrypt) {
    return encrypt
  }
  return ""
}

export function encryptByRSA(
  plaintext: string,
  publicKey: string
): RSACipherOb {
  const iv = generateRandomBytes(16)
  const aesKey = generateRandomBytes(16)
  const cipherText = ctrEncrypt(plaintext, aesKey, iv)
  const secretKey = rsaEncrypt(aesKey, publicKey)
  return {
    cipherText,
    iv,
    secretKey,
  }
}
