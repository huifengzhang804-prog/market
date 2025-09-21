import { UploadRequestOptions } from 'element-plus'
import axiosRequest from '@apis/request'
import { UploadAjaxError } from 'element-plus/es/components/upload/src/ajax'

/**
 * 饿了么 文件上传手动处理函数
 */
export const elementUploadRequest = (options: UploadRequestOptions) => {
    const { action, filename, file, onSuccess, onError } = options
    const formData = new FormData()
    formData.append(filename, file)
    return uploadFileApi(action, formData)
        .then((resp) => {
            const { code, msg } = resp.data
            if (code === 200) {
                onSuccess(resp.data)
                return resp
            }
            const error = new UploadAjaxError(msg, code, options.method, options.action)
            onError(error)
            return Promise.reject(error)
        })
        .catch((err) => {
            const error = new UploadAjaxError('上传失败', 400, options.method, options.action)
            onError(error)
            return Promise.reject(error)
        })
}

/**
 * 文件上传api
 * @param url 文件上传url路径
 * @param formData
 */
export const uploadFileApi = (url: string, formData: FormData) => {
    return axiosRequest.post(url, formData, {
        headers: {
            'Content-Type': 'multipart/form-data', // 需要指定上传的方式
        },
        timeout: Number(import.meta.env.VITE_REQUEST_TIME_OUT), // 防止文件过大超时
    })
}
