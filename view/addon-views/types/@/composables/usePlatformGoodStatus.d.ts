declare module "@/composables/usePlatformGoodStatus" {
  enum goodStatus {
    REFUSE,
    UNDER_REVIEW,
    SELL_OFF,
    SELL_ON,
    SELL_OUT,
    PLATFORM_SELL_OFF,
    UNUSABLE,
  }
  export function usePlatformGoodStatus(str: keyof typeof goodStatus): string;
}
