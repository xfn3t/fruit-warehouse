"use client"

import React from "react"
import { useState } from "react"
import { useRouter } from "next/navigation"
import { toast } from "sonner"
import { Plus, Trash2, Loader2 } from "lucide-react"
import { createDelivery } from "@/lib/api"
import type { CreateDeliveryRequest, DeliveryItemRequest, ApiError } from "@/lib/types"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
} from "@/components/ui/card"

interface FieldErrors {
  supplierId?: string
  deliveryDate?: string
  items?: string
  [key: string]: string | undefined
}

export function DeliveryForm() {
  const router = useRouter()
  const [supplierId, setSupplierId] = useState("")
  const [deliveryDate, setDeliveryDate] = useState("")
  const [items, setItems] = useState<DeliveryItemRequest[]>([
    { productId: 0, weight: 0 },
  ])
  const [submitting, setSubmitting] = useState(false)
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({})
  const [serverError, setServerError] = useState("")

  function addItem() {
    setItems((prev) => [...prev, { productId: 0, weight: 0 }])
  }

  function removeItem(index: number) {
    if (items.length <= 1) return
    setItems((prev) => prev.filter((_, i) => i !== index))
  }

  function updateItem(index: number, field: keyof DeliveryItemRequest, value: string) {
    setItems((prev) =>
      prev.map((item, i) =>
        i === index ? { ...item, [field]: Number(value) } : item
      )
    )
  }

  function validate(): boolean {
    const errors: FieldErrors = {}

    if (!supplierId || Number(supplierId) <= 0) {
      errors.supplierId = "ID поставщика обязателен и должен быть положительным числом."
    }

    if (items.length === 0) {
      errors.items = "Требуется хотя бы один товар."
    }

    for (let i = 0; i < items.length; i++) {
      if (!items[i].productId || items[i].productId <= 0) {
        errors[`item_${i}_productId`] = "ID товара должен быть положительным числом."
      }
      if (!items[i].weight || items[i].weight <= 0.001) {
        errors[`item_${i}_weight`] = "Вес должен быть больше 0.001 кг."
      }
    }

    setFieldErrors(errors)
    return Object.keys(errors).length === 0
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setServerError("")

    if (!validate()) return

    setSubmitting(true)

    const request: CreateDeliveryRequest = {
      supplierId: Number(supplierId),
      items: items.map((item) => ({
        productId: item.productId,
        weight: item.weight,
      })),
    }

    if (deliveryDate) {
      request.deliveryDate = new Date(deliveryDate).toISOString().replace("Z", "")
    }

    try {
      const result = await createDelivery(request)
      toast.success("Поставка успешно создана!")
      router.push(`/deliveries/${result.id}`)
    } catch (err: unknown) {
      const apiErr = err as ApiError
      if (apiErr.errors) {
        setFieldErrors(apiErr.errors)
      }
      setServerError(apiErr.message || "Произошла неожиданная ошибка.")
      toast.error(apiErr.message || "Не удалось создать поставку.")
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form onSubmit={handleSubmit} className="flex flex-col gap-6">
      <Card>
        <CardHeader>
          <CardTitle>Детали поставки</CardTitle>
          <CardDescription>
            Введите информацию о поставщике и дате поставки.
          </CardDescription>
        </CardHeader>
        <CardContent className="flex flex-col gap-4">
          {serverError && (
            <div className="rounded-lg border border-destructive/30 bg-destructive/5 p-3 text-sm text-destructive">
              {serverError}
            </div>
          )}

          <div className="grid gap-4 sm:grid-cols-2">
            <div className="flex flex-col gap-2">
              <Label htmlFor="supplierId">
                ID поставщика <span className="text-destructive">*</span>
              </Label>
              <Input
                id="supplierId"
                type="number"
                min="1"
                placeholder="например, 1"
                value={supplierId}
                onChange={(e) => setSupplierId(e.target.value)}
                aria-invalid={!!fieldErrors.supplierId}
              />
              {fieldErrors.supplierId && (
                <p className="text-xs text-destructive">{fieldErrors.supplierId}</p>
              )}
            </div>
            <div className="flex flex-col gap-2">
              <Label htmlFor="deliveryDate">Дата поставки</Label>
              <Input
                id="deliveryDate"
                type="datetime-local"
                value={deliveryDate}
                onChange={(e) => setDeliveryDate(e.target.value)}
                aria-invalid={!!fieldErrors.deliveryDate}
              />
              {fieldErrors.deliveryDate && (
                <p className="text-xs text-destructive">{fieldErrors.deliveryDate}</p>
              )}
            </div>
          </div>
        </CardContent>
      </Card>

      <Card>
        <CardHeader className="flex flex-row items-center justify-between">
          <div>
            <CardTitle>Товары</CardTitle>
            <CardDescription>Добавьте товары для поставки.</CardDescription>
          </div>
          <Button type="button" variant="outline" size="sm" onClick={addItem}>
            <Plus className="h-4 w-4" />
            Добавить товар
          </Button>
        </CardHeader>
        <CardContent className="flex flex-col gap-4">
          {fieldErrors.items && (
            <p className="text-xs text-destructive">{fieldErrors.items}</p>
          )}

          {items.map((item, index) => (
            <div
              key={index}
              className="flex items-end gap-3 rounded-lg border bg-muted/30 p-4"
            >
              <div className="flex flex-1 flex-col gap-2">
                <Label htmlFor={`productId-${index}`}>ID товара</Label>
                <Input
                  id={`productId-${index}`}
                  type="number"
                  min="1"
                  placeholder="например, 10"
                  value={item.productId || ""}
                  onChange={(e) => updateItem(index, "productId", e.target.value)}
                  aria-invalid={!!fieldErrors[`item_${index}_productId`]}
                />
                {fieldErrors[`item_${index}_productId`] && (
                  <p className="text-xs text-destructive">
                    {fieldErrors[`item_${index}_productId`]}
                  </p>
                )}
              </div>
              <div className="flex flex-1 flex-col gap-2">
                <Label htmlFor={`weight-${index}`}>Вес (кг)</Label>
                <Input
                  id={`weight-${index}`}
                  type="number"
                  min="0.002"
                  step="0.001"
                  placeholder="например, 25.5"
                  value={item.weight || ""}
                  onChange={(e) => updateItem(index, "weight", e.target.value)}
                  aria-invalid={!!fieldErrors[`item_${index}_weight`]}
                />
                {fieldErrors[`item_${index}_weight`] && (
                  <p className="text-xs text-destructive">
                    {fieldErrors[`item_${index}_weight`]}
                  </p>
                )}
              </div>
              <Button
                type="button"
                variant="ghost"
                size="icon"
                onClick={() => removeItem(index)}
                disabled={items.length <= 1}
                aria-label="Удалить товар"
                className="shrink-0 text-muted-foreground hover:text-destructive"
              >
                <Trash2 className="h-4 w-4" />
              </Button>
            </div>
          ))}
        </CardContent>
      </Card>

      <div className="flex justify-end gap-3">
        <Button
          type="button"
          variant="outline"
          onClick={() => router.push("/deliveries")}
          disabled={submitting}
        >
          Отмена
        </Button>
        <Button type="submit" disabled={submitting}>
          {submitting && <Loader2 className="h-4 w-4 animate-spin" />}
          Создать поставку
        </Button>
      </div>
    </form>
  )
}
