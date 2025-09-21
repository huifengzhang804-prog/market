declare module "@/store/modules/GDRegionData" {
  export interface RegionData {
    adcode: string;
    center: string;
    citycode: string;
    districts: RegionData[];
    name: string;
    level: string;
    children: RegionData[];
    label: string;
    value: string;
  }
  export const regionData: RegionData[];
}
