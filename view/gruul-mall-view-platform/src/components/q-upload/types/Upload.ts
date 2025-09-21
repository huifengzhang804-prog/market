import { ElMessage } from 'element-plus'
import axiosRequest from '@apis/request'
import type { UploadFile } from 'element-plus'
import { UploadAjaxError } from 'element-plus/es/components/upload/src/ajax'

export enum IMAGETYPE {
    'image/bmp',
    'image/jpg',
    'image/png',
    'image/tif',
    'image/gif',
    'image/pcx',
    'image/tga',
    'image/exif',
    'image/fpx',
    'image/svg',
    'image/psd',
    'image/cdr',
    'image/pcd',
    'image/dxf',
    'image/ufo',
    'image/eps',
    'image/ai',
    'image/raw',
    'image/WMF',
    'image/webp',
    'image/avif',
    'image/apng',
    'image/jpeg',
}
export type UploadConfig = {
    width: number
    height: number
    types: Array<keyof typeof IMAGETYPE>
    size: number
    /** 是否开启像素检测 */
    isBeyondLimit?: boolean | undefined
}
export type CusUploadRawFile = UploadFile & {
    type: keyof typeof IMAGETYPE
}
const defaultUploadConfig: UploadConfig = {
    width: 1000,
    height: 1000,
    types: ['image/png', 'image/jpg', 'image/gif', 'image/jpeg', 'image/webp'],
    size: 10,
}
/**
 * 上传图片校验
 * @param {UploadConfig} uploadConfig
 * @param {UploadFile} rawFile
 */
async function verifyFormat(rawFile: CusUploadRawFile, uploadConfig: Partial<UploadConfig>) {
    const mergeConfig = Object.assign(defaultUploadConfig, uploadConfig)
    const imageObject = new Image()
    const isInclude = mergeConfig.types.includes(rawFile.raw?.type as keyof typeof IMAGETYPE)
    const isBeyondSize = rawFile.size ? rawFile.size / 1024 / 1024 < mergeConfig.size : false
    let isBeyondLimit = mergeConfig.isBeyondLimit || false
    if (!isBeyondLimit) {
        isBeyondLimit = await new Promise<boolean>(function (resolve) {
            let _URL = window.URL || window.webkitURL
            if (rawFile.raw) {
                imageObject.src = _URL.createObjectURL(rawFile.raw)
                imageObject.onload = function () {
                    const type = imageObject.width < mergeConfig.width && imageObject.height < mergeConfig.height
                    resolve(type)
                }
            }
        })
    } else {
        let _URL = window.URL || window.webkitURL
        if (rawFile.raw) {
            imageObject.src = _URL.createObjectURL(rawFile.raw)
        }
    }

    const success = toastError(isInclude, isBeyondSize, isBeyondLimit, mergeConfig)
    return { success, src: imageObject.src, type: rawFile.raw?.type as keyof typeof IMAGETYPE }
}
/**
 * 异常抛错
 * @param {boolean} isInclude 类型是否包含
 * @param {boolean} isBeyondSize 大小判断
 * @param {boolean} isBeyondLimit 尺寸判断
 * @param {UploadConfig} uploadConfig
 * @returns {Boolean}
 */
function toastError(isInclude: boolean, isBeyondSize: boolean, isBeyondLimit: boolean, uploadConfig: UploadConfig): boolean {
    if (!isInclude) {
        ElMessage.error(`上传格式不支持`)
        return false
    } else if (!isBeyondSize) {
        ElMessage.error(`传输文件大小在${uploadConfig.size}MB以内!`)
        return false
    } else if (!isBeyondLimit) {
        ElMessage.error(`上传图片像素要小于${uploadConfig.width}*${uploadConfig.height}!`)
        return false
    }
    return true
}
/**
 * 手动上传
 * @param {any} request
 * @param {any} cuttingObject 裁剪后的文件
 * @returns {*}  ImgUrl
 */
async function httpRequest(request: any, cuttingObject: File) {
    const { action, file, filename, onError, onSuccess } = request
    console.log('手动上传手动上传', action)
    let formData = new FormData()
    formData.append(filename, cuttingObject, file.name)
    // 上传中的loading
    const {
        data: { data, code, msg },
    } = await axiosRequest
        .post(action, formData, {
            headers: {
                contentType: 'multipart/form-data', // 需要指定上传的方式
            },
            timeout: Number(import.meta.env.VITE_REQUEST_TIME_OUT), // 防止文件过大超时
        })
        .catch(() => onError(new UploadAjaxError('上传失败', 400, request.method, request.action)))
    if (code === 200) {
        onSuccess(data)
        return data
    }
    onError(new UploadAjaxError(msg, code, request.method, request.action))
}
export { httpRequest, verifyFormat }
