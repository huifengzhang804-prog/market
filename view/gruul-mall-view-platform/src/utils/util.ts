import type { FormInstance } from 'element-plus'
/**
 * @description 对验证表单的Promise封装
 * @param { FormInstance | null } formRefs 验证表单实例对象
 * @returns { Promise<any> }
 */
export const validateForm = (formRefs?: FormInstance | null) => {
    if (!formRefs) {
        return Promise.reject(new Error('no form instance input'))
    }
    return new Promise((resolve, reject) => {
        formRefs?.validate((isValid, invalidFields) => {
            if (isValid) {
                resolve('success valid')
            } else {
                reject(invalidFields)
            }
        })
    })
}
