"use client"

import useSWR from "swr"
import Link from "next/link"
import { format } from "date-fns"
import { ArrowLeft, AlertCircle } from "lucide-react"
import { getDeliveryById } from "@/lib/api"
import type { DeliveryResponse } from "@/lib/types"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
  TableFooter,
} from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Skeleton } from "@/components/ui/skeleton"
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
} from "@/components/ui/card"

function formatCurrency(value: number) {
  return new Intl.NumberFormat("en-US", {
    style: "currency",
    currency: "RUB",
  }).format(value)
}

function formatDate(iso: string) {
  try {
    return format(new Date(iso), "MMM d, yyyy HH:mm")
  } catch {
    return iso
  }
}

function statusVariant(status: string) {
  switch (status) {
    case "CREATED":
      return "secondary"
    case "DELIVERED":
      return "default"
    case "CANCELLED":
      return "destructive"
    default:
      return "outline"
  }
}

function DetailSkeleton() {
  return (
    <div className="flex flex-col gap-6">
      <Skeleton className="h-8 w-48" />
      <div className="grid gap-4 sm:grid-cols-3">
        <Skeleton className="h-24" />
        <Skeleton className="h-24" />
        <Skeleton className="h-24" />
      </div>
      <Skeleton className="h-64" />
    </div>
  )
}

export function DeliveryDetail({ id }: { id: number }) {
  const { data, error, isLoading } = useSWR<DeliveryResponse>(
    `delivery-${id}`,
    () => getDeliveryById(id)
  )

  if (isLoading) return <DetailSkeleton />

  if (error) {
    return (
      <div className="flex flex-col gap-4">
        <Button variant="ghost" asChild className="w-fit">
          <Link href="/deliveries">
            <ArrowLeft className="h-4 w-4" />
            Назад к доставкам
          </Link>
        </Button>
        <Card>
          <CardContent className="py-8">
            <div className="flex items-center gap-3 rounded-lg border border-destructive/30 bg-destructive/5 p-4 text-destructive">
              <AlertCircle className="h-5 w-5 shrink-0" />
              <p className="text-sm">
                {error.message || "Delivery not found."}
              </p>
            </div>
          </CardContent>
        </Card>
      </div>
    )
  }

  if (!data) return null

  return (
    <div className="flex flex-col gap-6">
      <div className="flex items-center gap-4">
        <Button variant="ghost" size="sm" asChild>
          <Link href="/deliveries">
            <ArrowLeft className="h-4 w-4" />
            Назад
          </Link>
        </Button>
      </div>

      <div className="flex flex-col gap-1">
        <div className="flex items-center gap-3">
          <h1 className="text-2xl font-semibold text-foreground">
            Доставка #{data.id}
          </h1>
          <Badge variant={statusVariant(data.status)}>{data.status}</Badge>
        </div>
        <p className="font-mono text-sm text-muted-foreground">
          {data.deliveryNumber}
        </p>
      </div>

      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
        <Card>
          <CardHeader className="pb-2">
            <CardDescription>Поставщик</CardDescription>
          </CardHeader>
          <CardContent>
            <p className="text-lg font-semibold text-foreground">
              {data.supplierName}
            </p>
            <p className="text-xs text-muted-foreground">ID: {data.supplierId}</p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <CardDescription>Дата доставки</CardDescription>
          </CardHeader>
          <CardContent>
            <p className="text-lg font-semibold text-foreground">
              {formatDate(data.deliveryDate)}
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <CardDescription>Всего вес</CardDescription>
          </CardHeader>
          <CardContent>
            <p className="text-lg font-semibold text-foreground">
              {data.totalWeight.toFixed(2)} кг
            </p>
          </CardContent>
        </Card>
        <Card>
          <CardHeader className="pb-2">
            <CardDescription>Всего стоимость</CardDescription>
          </CardHeader>
          <CardContent>
            <p className="text-lg font-semibold text-foreground">
              {formatCurrency(data.totalCost)}
            </p>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>Элементы</CardTitle>
          <CardDescription>
            {data.items.length} {data.items.length === 1 ? "эллемент" : "элементов"} в доставке
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Продукт</TableHead>
                <TableHead>Тип</TableHead>
                <TableHead>Сорт</TableHead>
                <TableHead className="text-right">Вес (kг)</TableHead>
                <TableHead className="text-right">Цена за единицу</TableHead>
                <TableHead className="text-right">Всего</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {data.items.map((item) => (
                <TableRow key={item.id}>
                  <TableCell className="font-medium">
                    {item.productName}
                  </TableCell>
                  <TableCell>
                    <Badge variant="outline">{item.productType}</Badge>
                  </TableCell>
                  <TableCell className="text-muted-foreground">
                    {item.variety}
                  </TableCell>
                  <TableCell className="text-right tabular-nums">
                    {item.weight.toFixed(2)}
                  </TableCell>
                  <TableCell className="text-right tabular-nums">
                    {formatCurrency(item.unitPrice)}
                  </TableCell>
                  <TableCell className="text-right tabular-nums font-medium">
                    {formatCurrency(item.totalPrice)}
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
            <TableFooter>
              <TableRow>
                <TableCell colSpan={3} className="font-semibold">
                  Всего
                </TableCell>
                <TableCell className="text-right tabular-nums font-semibold">
                  {data.totalWeight.toFixed(2)}
                </TableCell>
                <TableCell />
                <TableCell className="text-right tabular-nums font-semibold">
                  {formatCurrency(data.totalCost)}
                </TableCell>
              </TableRow>
            </TableFooter>
          </Table>
        </CardContent>
      </Card>

      <div className="text-xs text-muted-foreground">
        Создано: {formatDate(data.createdAt)}
      </div>
    </div>
  )
}
