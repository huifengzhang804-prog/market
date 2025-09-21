declare module "@/utils/Storage" {
  export default class Storage {
    public setItem(key: string, value: any, time?: number): void;
    public getItem(key: string): any;
    public removeItem(key: string): void;
    public clear(): void;
    public getLength(): number;
    public hasItem(key: string): boolean;
  }
}
