import { R } from "@/apis/http";

declare module "@/apis/setting" {
  export const doGetSupplierBasicSet: () => Promise<R<unknown>>;
  export const doPutSupplierBasicSet: (data: {
    payTimeout: string;
  }) => Promise<R<unknown>>;
}
