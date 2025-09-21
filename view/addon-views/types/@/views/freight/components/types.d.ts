declare module "@/views/freight/components/types" {
  /**
   * 后端返回模板列表信息
   */
  export interface ApiFreightTemplate {
    choiceConditionPostage: number;
    id: string;
    logisticsBaseModelVos: LogisticsBaseModelVos[];
    logisticsIncludePostageVos: LogisticsIncludePostageVos[];
    templateName: string;
    valuationModel: string;
  }

  export interface LogisticsBaseModelVos {
    firstAmount: number;
    firstQuantity: number;
    id: number;
    logisticsId: number;
    region: Region[];
    secondAmount: number;
    secondQuantity: string;
    valuationModel: string;
  }
  interface LogisticsIncludePostageVos {
    amountNum: 0;
    id: string;
    logisticsId: string;
    pieceNum: 20;
    postType: string;
    region: Region[];
    weight: 0;
  }
  export interface Region {
    upperCode: string;
    upperName: string;
    length: number;
    lowerName: string[];
    lowerCode: string[];
  }
}
