export interface InvoiceHeadeItem {
  email: string
  header: string
  id: string
  isDefault: boolean
  ownerId: string
  ownerType: string
  invoiceHeaderType: 'PERSONAL' | 'ENTERPRISE'
  taxIdentNo?: string
}
