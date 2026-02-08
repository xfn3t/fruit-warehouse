import type {
  CreateDeliveryRequest,
  DeliveryResponse,
  DeliveryListItem,
  CreatePriceRequest,
  PriceResponse,
  ReportParams,
  ApiError,
} from "./types"

const BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080"

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    let error: ApiError
    try {
      const body = await response.json()
      error = {
        status: response.status,
        message: body.message || response.statusText,
        errors: body.errors,
      }
    } catch {
      error = {
        status: response.status,
        message: response.statusText,
      }
    }
    throw error
  }
  return response.json() as Promise<T>
}

// ─── Delivery API ─────────────────────────────────────────────────

export async function createDelivery(
  data: CreateDeliveryRequest
): Promise<DeliveryResponse> {
  const response = await fetch(`${BASE_URL}/api/v1/deliveries`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  })
  return handleResponse<DeliveryResponse>(response)
}

export async function getDeliveryById(
  id: number
): Promise<DeliveryResponse> {
  const response = await fetch(`${BASE_URL}/api/v1/deliveries/${id}`)
  return handleResponse<DeliveryResponse>(response)
}

export async function getAllDeliveries(): Promise<DeliveryListItem[]> {
  const response = await fetch(`${BASE_URL}/api/v1/deliveries`)
  return handleResponse<DeliveryListItem[]>(response)
}

export async function getDeliveriesBySupplier(
  supplierId: number
): Promise<DeliveryListItem[]> {
  const response = await fetch(
    `${BASE_URL}/api/v1/deliveries/supplier/${supplierId}`
  )
  return handleResponse<DeliveryListItem[]>(response)
}

// ─── Supplier Price API ───────────────────────────────────────────

export async function addOrUpdatePrice(
  supplierId: number,
  data: CreatePriceRequest
): Promise<PriceResponse> {
  const response = await fetch(
    `${BASE_URL}/api/v1/suppliers/${supplierId}/prices`,
    {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(data),
    }
  )
  return handleResponse<PriceResponse>(response)
}

export async function getSupplierPrices(
  supplierId: number,
  productId?: number
): Promise<PriceResponse[]> {
  const params = new URLSearchParams()
  if (productId !== undefined) {
    params.set("productId", String(productId))
  }
  const query = params.toString() ? `?${params.toString()}` : ""
  const response = await fetch(
    `${BASE_URL}/api/v1/suppliers/${supplierId}/prices${query}`
  )
  return handleResponse<PriceResponse[]>(response)
}

export async function getActivePrices(
  supplierId: number
): Promise<PriceResponse[]> {
  const response = await fetch(
    `${BASE_URL}/api/v1/suppliers/${supplierId}/prices/active`
  )
  return handleResponse<PriceResponse[]>(response)
}

export async function deletePrice(
  supplierId: number,
  priceId: number
): Promise<void> {
  const response = await fetch(
    `${BASE_URL}/api/v1/suppliers/${supplierId}/prices/${priceId}`,
    { method: "DELETE" }
  )
  if (!response.ok) {
    let error: ApiError
    try {
      const body = await response.json()
      error = {
        status: response.status,
        message: body.message || response.statusText,
      }
    } catch {
      error = {
        status: response.status,
        message: response.statusText,
      }
    }
    throw error
  }
}

// ─── Report API ───────────────────────────────────────────────────

export async function generateReport(params: ReportParams): Promise<Response> {
  const searchParams = new URLSearchParams({
    startDate: params.startDate,
    endDate: params.endDate,
    detailed: String(params.detailed),
    format: params.format,
  })
  const response = await fetch(
    `${BASE_URL}/api/v1/reports?${searchParams.toString()}`
  )
  if (!response.ok) {
    let error: ApiError
    try {
      const body = await response.json()
      error = {
        status: response.status,
        message: body.message || response.statusText,
      }
    } catch {
      error = {
        status: response.status,
        message: response.statusText,
      }
    }
    throw error
  }
  return response
}
