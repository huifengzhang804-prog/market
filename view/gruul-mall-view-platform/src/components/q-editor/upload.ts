import axios from 'axios'
import { useAdminInfo } from '@/store/modules/admin'
/**
 * 手动上传
 * @param {any} request
 * @param {any} file 文件
 */
export async function uploadFile(action: any, file: File) {
    const token = useAdminInfo().getterToken
    let formData = new FormData()
    formData.append('file', file, file.name)
    // 上传中的loading
    const {
        data: { data, code },
    } = await axios({
        headers: {
            contentType: 'multipart/form-data', // 需要指定上传的方式
            Authorization: token,
            'Shop-Id': 0,
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
    }
}
