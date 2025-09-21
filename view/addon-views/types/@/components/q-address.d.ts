import { RegionData } from "@/store/modules/GDRegionData";

declare module "@/components/q-address" {
  export function AddressFn(data: RegionData[], arr: string[]): string;
}
