import type { RetrieveParam } from "@/apis/good/model";

declare module "@/apis/decoration" {
  export const doGetRetrieveCommodity: (
    data: Partial<RetrieveParam>
  ) => Promise<any>;
}
