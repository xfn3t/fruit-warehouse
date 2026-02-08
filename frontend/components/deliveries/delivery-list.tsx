"use client"

import useSWR from "swr"
import Link from "next/link"
import { format } from "date-fns"
import { ru } from "date-fns/locale"
import { Package, ArrowRight, AlertCircle } from "lucide-react"
import { getAllDeliveries } from "@/lib/api"
import type { DeliveryListItem } from "@/lib/types"
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

function formatCurrency(value: number) {
  return new Intl.NumberFormat("ru-RU", {
    style: "currency",
    currency: "RUB",
  }).format(value)
}

function formatDate(iso: string) {
  try {
    return format(new Date(iso), "d MMM yyyy, HH:mm", { locale: ru })
  } catch {
    return iso
  }
}

function LoadingSkeleton() {
  return (
    <div className="flex flex-col gap-3">
      {Array.from({ length: 5 }).map((_, i) => (
        <Skeleton key={i} className="h-14 w-full" />
      ))}
    </div>
  )
}

export function DeliveryList() {
  const { data, error, isLoading } = useSWR<DeliveryListItem[]>(
    "deliveries",
    getAllDeliveries
  )

  if (isLoading) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Последние поставки</CardTitle>
          <CardDescription>Загрузка поставок...</CardDescription>
        </CardHeader>
        <CardContent>
          <LoadingSkeleton />
        </CardContent>
      </Card>
    )
  }

  if (error) {
    return (
      <Card>
        <CardHeader>
          <CardTitle>Последние поставки</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="flex items-center gap-3 rounded-lg border border-destructive/30 bg-destructive/5 p-4 text-destructive">
            <AlertCircle className="h-5 w-5 shrink-0" />
            <p className="text-sm">
              {error.message || "Не удалось загрузить поставки. Пожалуйста, попробуйте ещё раз."}
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
          <CardTitle>Последние поставки</CardTitle>
          <CardDescription>Поставки</CardDescription>
        </CardHeader>
        <CardContent>
          <div className="flex flex-col items-center gap-4 py-12 text-center">
            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-muted">
              <Package className="h-6 w-6 text-muted-foreground" />
            </div>
            <div>
              <p className="font-medium text-foreground">Ещё нет поставок</p>
              <p className="text-sm text-muted-foreground">
                Создайте первую поставку, чтобы начать.
              </p>
            </div>
            <Button asChild>
              <Link href="/deliveries/new">Создать поставку</Link>
            </Button>
          </div>
        </CardContent>
      </Card>
    )
  }

  return (
    <Card>
      <CardHeader className="flex flex-row items-center justify-between">
        <div>
          <CardTitle>Последние поставки</CardTitle>
          <CardDescription>
            {data.length} {data.length === 1 ? "поставка" : (data.length > 1 && data.length < 5 ? "поставки" : "поставок")}
          </CardDescription>
        </div>
        <Button asChild>
          <Link href="/deliveries/new">Новая поставка</Link>
        </Button>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Номер</TableHead>
              <TableHead>Поставщик</TableHead>
              <TableHead>Дата</TableHead>
              <TableHead>Статус</TableHead>
              <TableHead className="text-right">Вес (кг)</TableHead>
              <TableHead className="text-right">Сумма</TableHead>
              <TableHead className="w-10">
                <span className="sr-only">Действия</span>
              </TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {data.map((delivery) => (
              <TableRow key={delivery.id}>
                <TableCell className="font-mono text-xs">
                  {delivery.deliveryNumber.slice(0, 8)}...
                </TableCell>
                <TableCell className="font-medium">
                  {delivery.supplierName}
                </TableCell>
                <TableCell className="text-muted-foreground">
                  {formatDate(delivery.deliveryDate)}
                </TableCell>
                <TableCell>
                  <Badge variant={statusVariant(delivery.status)}>
                    {delivery.status === "CREATED" ? "Создана" :
                     delivery.status === "DELIVERED" ? "Доставлена" :
                     delivery.status === "CANCELLED" ? "Отменена" :
                     delivery.status}
                  </Badge>
                </TableCell>
                <TableCell className="text-right tabular-nums">
                  {delivery.totalWeight.toFixed(2)}
                </TableCell>
                <TableCell className="text-right tabular-nums font-medium">
                  {formatCurrency(delivery.totalCost)}
                </TableCell>
                <TableCell>
                  <Button variant="ghost" size="icon" asChild>
                    <Link href={`/deliveries/${delivery.id}`} aria-label={`Посмотреть поставку ${delivery.deliveryNumber.slice(0, 8)}`}>
                      <ArrowRight className="h-4 w-4" />
                    </Link>
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  )
}
