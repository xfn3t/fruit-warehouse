"use client"

import { useState } from "react"
import useSWR, { mutate } from "swr"
import { format } from "date-fns"
import { Trash2, AlertCircle, DollarSign } from "lucide-react"
import { getSupplierPrices, deletePrice } from "@/lib/api"
import type { PriceResponse, ApiError } from "@/lib/types"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Skeleton } from "@/components/ui/skeleton"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
  AlertDialogTrigger,
} from "@/components/ui/alert-dialog"
import { toast } from "sonner"

function formatCurrency(value: number) {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "RUB",
  }).format(value)
}

function formatDate(iso: string | null) {
  if (!iso) return "Нет конечной даты"
  try {
    return format(new Date(iso), "MMM d, yyyy")
  } catch {
    return iso
  }
}

export function PriceList({
  supplierId,
  productIdFilter,
}: {
  supplierId: number
  productIdFilter?: number
}) {
  const swrKey = productIdFilter
    ? `prices-${supplierId}-${productIdFilter}`
    : `prices-${supplierId}`

  const { data, error, isLoading } = useSWR<PriceResponse[]>(swrKey, () =>
    getSupplierPrices(supplierId, productIdFilter)
  )

  const [deleting, setDeleting] = useState<number | null>(null)

  async function handleDelete(priceId: number) {
    setDeleting(priceId)
    try {
      await deletePrice(supplierId, priceId)
      toast.success("Цена удалена успешно.")
      mutate(swrKey)
    } catch (err: unknown) {
      const apiErr = err as ApiError
      toast.error(apiErr.message || "Ошибка при удалении цены.")
    } finally {
      setDeleting(null)
    }
  }

  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Цены</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex flex-col gap-3">
            {Array.from({ length: 3 }).map((_, i) => (
              <Skeleton key={i} className="h-12 w-full" />
            ))}
          </div>
        </CardContent>
      </Card>
    )
  }

  if (error) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Цена</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex items-center gap-3 rounded-lg border border-destructive/30 bg-destructive/5 p-4 text-destructive">
            <AlertCircle className="h-5 w-5 shrink-0" />
            <p className="text-sm">
              {error.message || "Failed to load prices."}
            </p>
          </div>
        </CardContent>
      </Card>
    )
  }

  if (!data || data.length === 0) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Цены</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex flex-col items-center gap-4 py-12 text-center">
            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-muted">
              <DollarSign className="h-6 w-6 text-muted-foreground" />
            </div>
            <div>
              <p className="font-medium text-foreground">Цена не найдена</p>
              <p className="text-sm text-muted-foreground">
                Добавьте выше цену для этого поставщика.
              </p>
            </div>
          </div>
        </CardContent>
      </Card>
    )
  }

  return (
    <Card>
      <CardHeader>
        <CardTitle>Цены</CardTitle>
        <CardDescription>
          {data.length} цен {data.length === 1 ? "найдена" : "найдено"}
        </CardDescription>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Продукт</TableHead>
              <TableHead>Тип</TableHead>
              <TableHead>Сорт</TableHead>
              <TableHead className="text-right">Цена</TableHead>
              <TableHead>Дата от</TableHead>
              <TableHead>Дата до</TableHead>
              <TableHead className="w-10">
                <span className="sr-only">Действие</span>
              </TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {data.map((price) => (
              <TableRow key={price.id}>
                <TableCell className="font-medium">{price.productName}</TableCell>
                <TableCell>
                  <Badge variant="outline">{price.productType}</Badge>
                </TableCell>
                <TableCell className="text-muted-foreground">{price.variety}</TableCell>
                <TableCell className="text-right tabular-nums font-medium">
                  {formatCurrency(price.price)}
                </TableCell>
                <TableCell className="text-muted-foreground">
                  {formatDate(price.effectiveFrom)}
                </TableCell>
                <TableCell className="text-muted-foreground">
                  {formatDate(price.effectiveTo)}
                </TableCell>
                <TableCell>
                  <AlertDialog>
                    <AlertDialogTrigger asChild>
                      <Button
                        variant="ghost"
                        size="icon"
                        disabled={deleting === price.id}
                        aria-label={`Delete price for ${price.productName}`}
                        className="text-muted-foreground hover:text-destructive"
                      >
                        <Trash2 className="h-4 w-4" />
                      </Button>
                    </AlertDialogTrigger>
                    <AlertDialogContent>
                      <AlertDialogHeader>
                        <AlertDialogTitle>Удаление цены</AlertDialogTitle>
                        <AlertDialogDescription>
                          Вы уверены, что хотите удалить запись о цене для{" "}
                          <strong>{price.productName}</strong> ({formatCurrency(price.price)})? Это действие невозможно отменить.
                        </AlertDialogDescription>
                      </AlertDialogHeader>
                      <AlertDialogFooter>
                        <AlertDialogCancel>Закрыть</AlertDialogCancel>
                        <AlertDialogAction
                          onClick={() => handleDelete(price.id)}
                          className="bg-destructive text-destructive-foreground hover:bg-destructive/90"
                        >
                          Удалить
                        </AlertDialogAction>
                      </AlertDialogFooter>
                    </AlertDialogContent>
                  </AlertDialog>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  )
}
