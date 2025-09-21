declare module "@/utils/types" {
  export type Long = number | string;
  export type Obj = Record<string, any>;
  /**
   * 分页列表响应格式
   */
  export type Pagination<T> = {
    current: number;
    size: number;
    total: number;
    pages: number;
    records: T;
    [key: string]: any;
  };

  /**
   * 分页列表响应格式
   */
  export interface L<T> {
    current: number;
    pages: number;
    records: T[];
    size: number;
    total: number;
    // 可能还有其他字段
    [key: string]: any;
  }
  /**
   * 通用分页参数
   */
  export interface PageParams {
    pages?: number;
    records?: PageParamsRecords;
    total?: number;
    size?: number;
    current?: number;
    orders?: PageParamsOrder;
  }

  export type PickPageParams<T extends keyof PageParams> = Required<
    Pick<PageParams, T>
  >;
  /**
   * 字段
   */
  export interface PageParamsRecords {
    id?: Long;
  }
  /**
   * 排序
   */
  export interface PageParamsOrder {
    column: string; // 需要进行排序的字段
    asc: boolean; // 是否正序排列 默认 true
  }
}
