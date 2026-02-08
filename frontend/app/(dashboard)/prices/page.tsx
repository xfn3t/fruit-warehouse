"use client"

import React from "react"

import { useState } from "react"
import { Search } from "lucide-react"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Button } from "@/components/ui/button"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { PriceList } from "@/components/prices/price-list"
import { PriceForm } from "@/components/prices/price-form"

export default function PricesPage() {
  const [supplierIdInput, setSupplierIdInput] = useState("")
  const [productIdInput, setProductIdInput] = useState("")
  const [activeSupplier, setActiveSupplier] = useState<number | null>(null)
  const [activeProductFilter, setActiveProductFilter] = useState<number | undefined>(undefined)

  function handleSearch(e: React.FormEvent) {
    e.preventDefault()
    const sid = Number(supplierIdInput)
    if (sid > 0) {
      setActiveSupplier(sid)
      const pid = Number(productIdInput)
      setActiveProductFilter(pid > 0 ? pid : undefined)
    }
  }

  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-2xl font-semibold text-foreground">Цены поставщика</h1>
        <p className="text-sm text-muted-foreground">
          Управление ценами на продукцию для поставщиков.
        </p>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Выбрать поставщика</CardTitle>
          <CardDescription>
            Введите идентификатор поставщика, чтобы просматривать цены и управлять ими.
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSearch} className="flex flex-wrap items-end gap-4">
            <div className="flex flex-col gap-2">
              <Label htmlFor="supplier-search">ID Поставщика</Label>
              <Input
                id="supplier-search"
                type="number"
                min="1"
                placeholder="e.g. 1"
                value={supplierIdInput}
                onChange={(e) => setSupplierIdInput(e.target.value)}
                className="w-40"
              />
            </div>
            <div className="flex flex-col gap-2">
              <Label htmlFor="product-filter">
                ID Продукта <span className="text-muted-foreground">(Опционально)</span>
              </Label>
              <Input
                id="product-filter"
                type="number"
                min="1"
                placeholder="Filter by product"
                value={productIdInput}
                onChange={(e) => setProductIdInput(e.target.value)}
                className="w-48"
              />
            </div>
            <Button type="submit" disabled={!supplierIdInput || Number(supplierIdInput) <= 0}>
              <Search className="h-4 w-4" />
              Загрузка цен
            </Button>
          </form>
        </CardContent>
      </Card>

      {activeSupplier !== null && (
        <>
          <PriceForm
            supplierId={activeSupplier}
            productIdFilter={activeProductFilter}
          />
          <PriceList
            supplierId={activeSupplier}
            productIdFilter={activeProductFilter}
          />
        </>
      )}
    </div>
  )
}
