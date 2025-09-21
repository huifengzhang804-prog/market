enum SubscribeType {
  ORDER_PAY,
}
export type MessageSubscribeType = Record<keyof typeof SubscribeType, string>
