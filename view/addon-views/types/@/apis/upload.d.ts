import { UploadRequestOptions } from "element-plus";

declare module "@/apis/upload" {
  export const elementUploadRequest: (
    options: UploadRequestOptions
  ) => Promise<void>;
}
