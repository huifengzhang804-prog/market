import Decimal from "decimal.js";

declare module "@/composables/useConvert" {
  type ConversionType = string | number | Decimal | undefined | null;
  export default function useConvert(): {
    divHundred: (val: ConversionType) => Decimal;
    divThousand: (val: ConversionType) => Decimal;
    divTenThousand: (val: ConversionType) => Decimal;
    mulHundred: (val: ConversionType) => Decimal;
    mulThousand: (val: ConversionType) => Decimal;
    mulTenThousand: (val: ConversionType) => Decimal;
    fixedUp: (val: ConversionType) => Decimal;
    fixedDown: (val: ConversionType) => Decimal;
    salesVolumeToStr: (val: ConversionType) => Decimal;
    divTenThouMillion: (val: ConversionType) => Decimal;
  };
}
