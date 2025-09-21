import axios from 'axios'
import { useShopInfoStore } from '@/store/modules/shopInfo'
/**
 * @description: 手动上传
 * @param {any} request
 * @param {any} file 文件
 * @returns {*}
 */
export async function uploadFile(action: any, file: File) {
    const { token, id } = useShopInfoStore().getterShopInfo
    let formData = new FormData()
    formData.append('file', file, file.name)
    // 上传中的loading
    try {
        const {
            data: { data, code, msg },
        } = await axios({
            headers: {
                contentType: 'multipart/form-data', // 需要指定上传的方式
                Authorization: token,
                'Shop-Id': id,
                'Device-Id': 1,
                'Client-Type': import.meta.env.VITE_CLIENT_TYPE,
            },
            withCredentials: false,
            url: import.meta.env.VITE_BASE_URL + action,
            method: 'post',
            data: formData,
            timeout: Number(import.meta.env.VITE_REQUEST_TIME_OUT), // 防止文件过大超时
            // onUploadProgress: (progress) => {
            //     if (progress.lengthComputable) {
            //         uploadprogressPercentage.value = (Number((progress.loaded / progress.total).toFixed(2)) * 100).toFixed(0) // 进度条百分比
            //     }
            // },
        })
        if (code === 200) {
            return data
        } else {
            ElMessage.error(msg || '请求失败')
        }
    } catch (error) {
        ElMessage.error('网络异常')
    }
}
