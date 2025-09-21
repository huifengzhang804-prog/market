import { ElNotification } from 'element-plus'
export function checkStatus(status: number): void {
    let errMessage = ''
    switch (status) {
        case 403:
            errMessage = 'systerm-api-errMsg403'
            break
        // 404请求不存在
        case 404:
            errMessage = 'systerm-api-errMsg404'
            break
        case 405:
            errMessage = 'systerm-api-errMsg405'
            break
        case 408:
            errMessage = 'systerm-api-errMsg408'
            break
        case 500:
            errMessage = 'systerm-api-errMsg500'
            break
        case 501:
            errMessage = 'systerm-api-errMsg501'
            break
        case 502:
            errMessage = 'systerm-api-errMsg502'
            break
        case 503:
            errMessage = 'systerm-api-errMsg503'
            break
        case 504:
            errMessage = 'systerm-api-errMsg504'
            break
        case 505:
            errMessage = 'systerm-api-errMsg505'
            break
        default:
    }

    if (errMessage) {
        ElNotification.error(errMessage)
    }
}
