// 平台端网络请求模块
declare module "@/apis/http" {
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
    errorImmediately?: boolean;
    headers?: any;
  };
  /**
   * 响应数据格式
   */
  export type R<T> = {
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
    data: T;
    success: boolean;
  };
  export type Result<T = any> = {
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
    data: T;
    success: boolean;
  };
  // get
  const get: <T>(data: RequestData) => Promise<R<T>>;
  // post
  const post: <T>(data: RequestData) => Promise<R<T>>;
  // put
  const put: <T>(data: RequestData) => Promise<R<T>>;
  // delete
  const del: <T>(data: RequestData) => Promise<R<T>>;
  // head
  const head: <T>(data: RequestData) => Promise<R<T>>;
  // options
  const options: <T>(data: RequestData) => Promise<R<T>>;
  // patch
  const patch: <T>(data: RequestData) => Promise<R<T>>;
}
