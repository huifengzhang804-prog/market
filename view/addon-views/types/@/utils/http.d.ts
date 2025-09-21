import type { AxiosRequestConfig, AxiosResponse, AxiosInstance } from "axios";
import { Result } from "@/apis/http";

declare module "@/utils/http" {
  export interface RequestOptions {
    // 将请求参数拼接到url
    joinParamsToUrl?: boolean;
    // 请求拼接路径
    urlPrefix?: string;
    // 是否是mock接口
    isMock: boolean;
  }
  export abstract class AxiosTransform {
    // 发送请求前钩子
    beforeRequestHook?: (
      config: AxiosRequestConfig,
      options: RequestOptions
    ) => AxiosRequestConfig;
    // 请求成功
    transformRequestHook?: (
      res: AxiosResponse<R<any>>,
      options: RequestOptions
    ) => any;
    // 请求失败处理
    requestCatchHook?: (e: Error, options: RequestOptions) => Promise<any>;
    // 请求之前的拦截器
    requestInterceptors?: (
      config: AxiosRequestConfig,
      options: CreateAxiosOptions
    ) => AxiosRequestConfig;
    // 请求之后的拦截器
    responseInterceptors?: (res: AxiosResponse<any>) => AxiosResponse<any>;
    // 请求之前的拦截器错误处理
    requestInterceptorsCatch?: (error: Error) => void;
    // 请求之后的拦截器错误处理
    responseInterceptorsCatch?: (error: Error) => void;
  }
  export interface CreateAxiosOptions extends AxiosRequestConfig {
    transform?: AxiosTransform;
    requestOptions?: RequestOptions;
  }
  export class QAxios {
    private axiosInstance: AxiosInstance;
    private options: CreateAxiosOptions;
    constructor(options: CreateAxiosOptions);
    private getTransform(): AxiosTransform | undefined;
    private setupInterceptors(): void;
    public request<T = any>(
      config: AxiosRequestConfig,
      options?: RequestOptions
    ): Promise<T>;
    public get<T = any>(
      config: AxiosRequestConfig,
      options?: RequestOptions
    ): Promise<Result<T>>;
    public post<T = any>(
      config: AxiosRequestConfig,
      options?: RequestOptions
    ): Promise<Result<T>>;
    public put<T = any>(
      config: AxiosRequestConfig,
      options?: RequestOptions
    ): Promise<Result<T>>;
    public delete<T = any>(
      config: AxiosRequestConfig,
      options?: RequestOptions
    ): Promise<Result<T>>;
  }
  export const http: QAxios;
}
