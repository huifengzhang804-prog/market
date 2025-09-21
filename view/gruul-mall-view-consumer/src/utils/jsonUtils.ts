/**
 * JSON 相关工具函数
 */

/**
 * 移除 JSON 字符串中的注释
 * 支持移除单行注释和多行注释
 * @param jsonString JSON 字符串
 * @returns 移除注释后的 JSON 字符串
 */
export function stripJsonComments(jsonString: string): string {
  // 正则表达式匹配单行注释和多行注释
  const singleLineCommentRegex = /\/\/.*?(?:\r|\n|$)/g
  const multiLineCommentRegex = /\/\*[\s\S]*?\*\//g

  // 先移除多行注释，再移除单行注释
  return jsonString.replace(multiLineCommentRegex, '').replace(singleLineCommentRegex, '\n').trim()
}

/**
 * 解析 JSON 字符串，自动移除注释
 * @param jsonString JSON 字符串
 * @returns 解析后的 JSON 对象
 */
export function parseJsonWithComments(jsonString: string): any {
  const cleanJsonContent = stripJsonComments(jsonString)
  return JSON.parse(cleanJsonContent)
}
