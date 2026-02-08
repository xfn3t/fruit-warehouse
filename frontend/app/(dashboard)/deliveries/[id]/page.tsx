"use client"

import { use } from "react"
import { DeliveryDetail } from "@/components/deliveries/delivery-detail"

export default function DeliveryDetailPage({
  params,
}: {
  params: Promise<{ id: string }>
}) {
  const { id } = use(params)
  return <DeliveryDetail id={Number(id)} />
}
