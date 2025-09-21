/*
 * @Author: Liu_Fei 1260324799@qq.com
 * @Date: 2024-06-06 14:26:29
 * @LastEditors: Liu_Fei 1260324799@qq.com
 * @LastEditTime: 2024-06-11 13:05:29
 * @FilePath: \webShop\src\utils\rsa.ts
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 */
import { JSEncrypt } from "jsencrypt";
//const RSA = require("crypto-js/rsa");
//获取 rsa 公钥
const public_key = import.meta.env.VITE_RSA_PUBLIC_KEY;

/**
 * 获取RSA加密器
 * @return RSA加密器
 */
export function encryptor() {
  const encryptor = new JSEncrypt({ default_key_size: "2048" });
  encryptor.setPublicKey(public_key);
  return encryptor;
}

/**
 * RSA加密
 * @param encryptor RSA加密器
 * @param data 待加密数据
 * @return 加密后数据
 */
function enc(encryptor: JSEncrypt, data: string) {
  const encrypt = encryptor.encrypt(data);
  if (!encrypt) {
    throw new Error("rsa encrypt failed");
  }
  return encrypt;
}

/**
 * 业务加密逻辑函数类型
 */
type EncryptFunc = (enc: (data: string) => string) => void;

/**
 * RSA加密 不需要全局初始化，使用完即可释放
 * 避免反复初始化RSA加密器 共用同一个加密器
 * @param encryptFunc  业务加密逻辑函数
 */
export function encryptAccept(encryptFunc: EncryptFunc) {
  const c = encryptor();
  encryptFunc((data) => {
    return enc(c, data);
  });
}
