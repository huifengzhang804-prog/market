import { R } from "@/apis/http";

declare module "@/apis/afs" {
  export const doGetLogisticsTrajectoryByWaybillNo: (
    companyCode: string,
    waybillNo: string,
    packageId?: string
  ) => Promise<R<unknown>>;
}
