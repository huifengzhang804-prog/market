declare module "@/libs/validate" {
  export const REGEX_DATE: (str: string) => boolean;
  export const REGEX_TIME: (str: string) => boolean;
  export const REGEX_TIME_DATE: (str: string) => boolean;
  export const REGEX_HTTP_URL: (str: string) => boolean;
  export const REXGEX_NUMBERS: (str: string) => boolean;
  export const REGEX_BLANK: (str: string) => boolean;
  export const REGEX_MOBILE: (str: string) => boolean;
  export const REGEX_CITIZEN_ID: (str: string) => boolean;
  export const REGEX_EMAIL: (str: string) => boolean;
  export const REGEX_PASSWORD: (str: string) => boolean;
}
