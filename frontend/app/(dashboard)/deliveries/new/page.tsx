"use client"

import { DeliveryForm } from "@/components/deliveries/delivery-form"

export default function NewDeliveryPage() {
  return (
    <div className="mx-auto flex max-w-3xl flex-col gap-6">
      <div>
        <h1 className="text-2xl font-semibold text-foreground">
          Добавить новую поставку
        </h1>
        <p className="text-sm text-muted-foreground">
          Заполните данные о доставке и добавьте товары ниже.
        </p>
      </div>
      <DeliveryForm />
    </div>
  )
}
