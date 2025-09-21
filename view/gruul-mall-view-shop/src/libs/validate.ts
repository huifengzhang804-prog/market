import { REGEX } from '@/constant'
/**
 * 日期
 */
const REGEX_DATE = (str: string) => REGEX.DATE.test(str)
/**
 * 时间
 */
const REGEX_TIME = (str: string) => REGEX.TIME.test(str)
/**
 * 日期 + 时间
 */
const REGEX_TIME_DATE = (str: string) => REGEX.TIME_DATE.test(str)
/**
 * 图片
 */
const REGEX_HTTP_URL = (str: string) => REGEX.HTTP_URL.test(str)
/**
 * 数字
 */
const REXGEX_NUMBERS = (str: string) => REGEX.NUMBERS.test(str)
/**
 * 文本
 */
const REGEX_BLANK = (str: string) => REGEX.BLANK.test(str)
/**
 * 手机号
 */
const REGEX_MOBILE = (str: string) => REGEX.MOBILE.test(str)
/**
 * 公民身份证
 */
const REGEX_CITIZEN_ID = (str: string) => REGEX.CITIZEN_ID.test(str)
/**
 * 邮箱
 */
const REGEX_EMAIL = (str: string) => REGEX.EMAIL.test(str)
/**
 * 密码校验，6-20位密码，至少使用字母、数字、符号中的2种组合
 */
const REGEX_PASSWORD = (str: string) => REGEX.PASSWORD.test(str)
export {
    REGEX_DATE,
    REGEX_TIME,
    REGEX_TIME_DATE,
    REGEX_HTTP_URL,
    REXGEX_NUMBERS,
    REGEX_BLANK,
    REGEX_MOBILE,
    REGEX_CITIZEN_ID,
    REGEX_EMAIL,
    REGEX_PASSWORD,
}
