"use client"

import React from "react"

import { useState } from "react"
import { mutate } from "swr"
import { Loader2 } from "lucide-react"
import { addOrUpdatePrice } from "@/lib/api"
import type { CreatePriceRequest, ApiError } from "@/lib/types"
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
import { toast } from "sonner"

interface FieldErrors {
  [key: string]: string | undefined
}

export function PriceForm({
  supplierId,
  productIdFilter,
}: {
  supplierId: number
  productIdFilter?: number
}) {
  const [productId, setProductId] = useState("")
  const [price, setPrice] = useState("")
  const [effectiveFrom, setEffectiveFrom] = useState("")
  const [effectiveTo, setEffectiveTo] = useState("")
  const [submitting, setSubmitting] = useState(false)
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({})
  const [serverError, setServerError] = useState("")

  function validate(): boolean {
    const errors: FieldErrors = {}
    if (!productId || Number(productId) <= 0) {
      errors.productId = "ID должен быть положительным."
    }
    if (!price || Number(price) <= 0) {
      errors.price = "Цена должна быть больше 0"
    }
    if (!effectiveFrom) {
      errors.effectiveFrom = "Дата \"От\" обязательна"
    }
    setFieldErrors(errors)
    return Object.keys(errors).length === 0
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    setServerError("")
    if (!validate()) return

    setSubmitting(true)

    const request: CreatePriceRequest = {
      productId: Number(productId),
      price: Number(price),
      effectiveFrom,
      effectiveTo: effectiveTo || null,
    }

    try {
      await addOrUpdatePrice(supplierId, request)
      toast.success("Цена добавлена успешно!")
      // Reset form
      setProductId("")
      setPrice("")
      setEffectiveFrom("")
      setEffectiveTo("")
      setFieldErrors({})
      // Refresh list
      const swrKey = productIdFilter
        ? `prices-${supplierId}-${productIdFilter}`
        : `prices-${supplierId}`
      mutate(swrKey)
    } catch (err: unknown) {
      const apiErr = err as ApiError
      if (apiErr.errors) {
        setFieldErrors(apiErr.errors)
      }
      setServerError(apiErr.message || "An unexpected error occurred.")
      toast.error(apiErr.message || "Failed to add price.")
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Добавить цену</CardTitle>
        <CardDescription>
          Установите новый продукт для поставщика #{supplierId}.
        </CardDescription>
      </CardHeader>
      <CardContent>
        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          {serverError && (
            <div className="rounded-lg border border-destructive/30 bg-destructive/5 p-3 text-sm text-destructive">
              {serverError}
            </div>
          )}

          <div className="grid gap-4 sm:grid-cols-2">
            <div className="flex flex-col gap-2">
              <Label htmlFor="price-productId">ID Продукта</Label>
              <Input
                id="price-productId"
                type="number"
                min="1"
                placeholder="e.g. 10"
                value={productId}
                onChange={(e) => setProductId(e.target.value)}
                aria-invalid={!!fieldErrors.productId}
              />
              {fieldErrors.productId && (
                <p className="text-xs text-destructive">{fieldErrors.productId}</p>
              )}
            </div>
            <div className="flex flex-col gap-2">
              <Label htmlFor="price-amount">Цена (Руб.)</Label>
              <Input
                id="price-amount"
                type="number"
                min="0.01"
                step="0.01"
                placeholder="e.g. 2.50"
                value={price}
                onChange={(e) => setPrice(e.target.value)}
                aria-invalid={!!fieldErrors.price}
              />
              {fieldErrors.price && (
                <p className="text-xs text-destructive">{fieldErrors.price}</p>
              )}
            </div>
          </div>

          <div className="grid gap-4 sm:grid-cols-2">
            <div className="flex flex-col gap-2">
              <Label htmlFor="price-from">Дата от</Label>
              <Input
                id="price-from"
                type="date"
                value={effectiveFrom}
                onChange={(e) => setEffectiveFrom(e.target.value)}
                aria-invalid={!!fieldErrors.effectiveFrom}
              />
              {fieldErrors.effectiveFrom && (
                <p className="text-xs text-destructive">{fieldErrors.effectiveFrom}</p>
              )}
            </div>
            <div className="flex flex-col gap-2">
              <Label htmlFor="price-to">
                Дата до <span className="text-muted-foreground">(optional)</span>
              </Label>
              <Input
                id="price-to"
                type="date"
                value={effectiveTo}
                onChange={(e) => setEffectiveTo(e.target.value)}
              />
            </div>
          </div>

          <div className="flex justify-end">
            <Button type="submit" disabled={submitting}>
              {submitting && <Loader2 className="h-4 w-4 animate-spin" />}
              Добавить цену
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  )
}
