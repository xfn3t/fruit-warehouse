"use client"

import { DeliveryList } from "@/components/deliveries/delivery-list"

export default function DeliveriesPage() {
  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-2xl font-semibold text-foreground">Поставки</h1>
        <p className="text-sm text-muted-foreground">
          Просматривайте и управляйте всеми поставками.
        </p>
      </div>
      <DeliveryList />
    </div>
  )
}
