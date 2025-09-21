declare module "@/utils/date" {
  type UnionDate = number | Date;
  export default class DateUtil {
    getY(ms?: UnionDate): number;
    getM(ms?: UnionDate): string;
    getD(ms?: UnionDate): string;
    getHour(ms?: UnionDate): number;
    getH(ms?: UnionDate): string;
    getMin(ms?: UnionDate): string;
    getS(ms?: UnionDate): string;
    getYMD(ms?: Date | number): string;
    getYMDs(ms?: UnionDate): string;
    getYMs(ms?: UnionDate): string;
    getMDs(ms?: UnionDate): string;
    getHMS(ms?: UnionDate): string;
    getYMDHMS(ms?: UnionDate): string;
    getYMDHMSs(ms?: UnionDate): string;
    getLastMonth(ms?: UnionDate, day?: number): string;
    getLastThreeMonth(ms?: Date, day?: number): string;
    getAddDays(ms?: Date | number, day?: number): string;
    getSubtracteDays(ms?: Date | number, day?: number): string;
    getTime(ms?: Date): number;
    getObj(ms?: Date): string[];
    formatLength(ms: number | string): string;
    unitReturnDate(ms: Date | number): Date;
    isLeapYear(year: string | number): boolean;
    MonthToDay(year: string | number, month: string | number): number;
  }
}
