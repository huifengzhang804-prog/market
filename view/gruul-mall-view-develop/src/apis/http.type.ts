/**
 * 请求数据 格式
 */
export type RequestData = {
    /**
     * 是否展示loading
     */
    showLoading?: boolean;
    /**
     * 请求地址url
     */
    url: string;
    /**
     * url参数
     */
    params?: any;
    /**
     * 请求体参数
     */
    data?: any;
}

/**
 * 响应数据格式
 */
export type R = {
    /**
     * 响应码
     */
    code: number;
    /**
     * 响应提示信息
     */
    msg: string;
    /**
     * 响应数据
     */
    data: any;
    success: boolean;
}