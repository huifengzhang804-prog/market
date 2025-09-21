import { Channel, StompHook } from "@/composables/stomp/typs";

declare module "@/composables/stomp/StompHandler" {
  /**
   * 观察监听接收到的消息
   */
  export const stompHookMount: (key: Channel, hook: StompHook) => void;
}
