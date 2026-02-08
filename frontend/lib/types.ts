// ─── Delivery Types ───────────────────────────────────────────────

export interface DeliveryItemRequest {
  productId: number
  weight: number
}

export interface CreateDeliveryRequest {
  supplierId: number
  deliveryDate?: string // ISO format
  items: DeliveryItemRequest[]
}

export interface DeliveryItemResponse {
  id: number
  productId: number
  productName: string
  productType: string
  variety: string
  weight: number
  unitPrice: number
  totalPrice: number
}

export interface DeliveryResponse {
  id: number
  deliveryNumber: string
  supplierId: number
  supplierName: string
  deliveryDate: string
  status: string
  createdAt: string
  items: DeliveryItemResponse[]
  totalWeight: number
  totalCost: number
}

export interface DeliveryListItem {
  id: number
  deliveryNumber: string
  supplierId: number
  supplierName: string
  deliveryDate: string
  status: string
  createdAt: string
  totalWeight: number
  totalCost: number
}

export interface DetailedItem {
  supplierName: string
  deliveryNumber: string
  deliveryDate: string
  productName: string
  productType: string
  variety: string
  weight: number
  unitPrice: number
  totalPrice: number
}

export interface SummaryItem {
  supplierName: string
  productType: string
  variety: string
  totalWeight: number
  totalCost: number
}

export interface DeliveryReport {
  startDate: string
  endDate: string
  detailed: boolean
  summaryItems: SummaryItem[] | null
  detailedItems: DetailedItem[] | null
  totalWeight: number
  totalCost: number
}

// ─── Supplier Price Types ─────────────────────────────────────────

export interface CreatePriceRequest {
  productId: number
  price: number
  effectiveFrom: string // date only: "YYYY-MM-DD"
  effectiveTo: string | null
}

export interface PriceResponse {
  id: number
  supplierId: number
  productId: number
  productName: string
  productType: string
  variety: string
  price: number
  effectiveFrom: string
  effectiveTo: string | null
  createdAt: string
}

// ─── Report Types ─────────────────────────────────────────────────

export type ReportFormat = "JSON" | "PDF" | "CSV"

export interface ReportParams {
  startDate: string // "YYYY-MM-DD"
  endDate: string // "YYYY-MM-DD"
  detailed: boolean
  format: ReportFormat
}

// ─── API Error ────────────────────────────────────────────────────

export interface ApiError {
  status: number
  message: string
  errors?: Record<string, string>
}
