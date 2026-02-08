"use client"

import React from "react"
import { useState } from "react"
import { format, subDays } from "date-fns"
import { ru } from "date-fns/locale"
import { Loader2, Download, FileBarChart, AlertCircle } from "lucide-react"
import { generateReport } from "@/lib/api"
import type { ReportFormat, ApiError, DeliveryReport, SummaryItem, DetailedItem } from "@/lib/types"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import { Switch } from "@/components/ui/switch"
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
  CardDescription,
} from "@/components/ui/card"
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
import { Separator } from "@/components/ui/separator"
import { toast } from "sonner"

// Компонент для отображения детального отчета
function DetailedReportTable({ report }: { report: DeliveryReport }) {
  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h3 className="text-lg font-semibold">Детальный отчет по поставкам</h3>
        <Badge variant="outline">
          {report.detailedItems?.length} записей
        </Badge>
      </div>
      
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Поставщик</TableHead>
              <TableHead>Дата поставки</TableHead>
              <TableHead>Товар</TableHead>
              <TableHead>Тип</TableHead>
              <TableHead>Сорт</TableHead>
              <TableHead className="text-right">Вес (кг)</TableHead>
              <TableHead className="text-right">Цена за кг</TableHead>
              <TableHead className="text-right">Стоимость</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {report.detailedItems?.map((item) => (
              <TableRow key={item.deliveryNumber}>
                <TableCell className="font-medium">{item.supplierName}</TableCell>
                <TableCell>
                  {format(new Date(item.deliveryDate), "dd.MM.yyyy HH:mm", { locale: ru })}
                </TableCell>
                <TableCell>{item.productName}</TableCell>
                <TableCell>
                  <Badge variant="secondary">{item.productType}</Badge>
                </TableCell>
                <TableCell>{item.variety}</TableCell>
                <TableCell className="text-right">{item.weight.toFixed(2)}</TableCell>
                <TableCell className="text-right">{item.unitPrice} ₽</TableCell>
                <TableCell className="text-right font-medium">
                  {item.totalPrice.toLocaleString("ru-RU")} ₽
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
          <TableFooter>
            <TableRow>
              <TableCell colSpan={5} className="text-right font-medium">
                Итого:
              </TableCell>
              <TableCell className="text-right font-bold">
                {report.totalWeight.toFixed(2)} кг
              </TableCell>
              <TableCell></TableCell>
              <TableCell className="text-right font-bold">
                {report.totalCost.toLocaleString("ru-RU")} ₽
              </TableCell>
            </TableRow>
          </TableFooter>
        </Table>
      </div>
      
      <div className="grid grid-cols-2 gap-4 text-sm text-muted-foreground">
        <div>
          <p className="font-medium">Период отчета:</p>
          <p>
            {format(new Date(report.startDate), "dd.MM.yyyy", { locale: ru })} - 
            {format(new Date(report.endDate), "dd.MM.yyyy", { locale: ru })}
          </p>
        </div>
        <div>
          <p className="font-medium">Общая информация:</p>
          <p>Поставщиков: {new Set(report.detailedItems?.map(item => item.supplierName)).size}</p>
          <p>Типов товаров: {new Set(report.detailedItems?.map(item => item.productType)).size}</p>
        </div>
      </div>
    </div>
  )
}

// Компонент для отображения сводного отчета
function SummaryReportTable({ report }: { report: DeliveryReport }) {
  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h3 className="text-lg font-semibold">Сводный отчет</h3>
        <Badge variant="outline">
          {report.summaryItems?.length} позиций
        </Badge>
      </div>
      
      <div className="rounded-md border">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Поставщик</TableHead>
              <TableHead>Тип товара</TableHead>
              <TableHead>Сорт</TableHead>
              <TableHead className="text-right">Общий вес (кг)</TableHead>
              <TableHead className="text-right">Общая стоимость</TableHead>
              <TableHead className="text-right">Ср. цена за кг</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {report.summaryItems?.map((item, index) => {
              const avgPrice = item.totalCost / item.totalWeight
              return (
                <TableRow key={`${item.supplierName}-${item.productType}-${item.variety}-${index}`}>
                  <TableCell className="font-medium">{item.supplierName}</TableCell>
                  <TableCell>
                    <Badge variant="secondary">{item.productType}</Badge>
                  </TableCell>
                  <TableCell>{item.variety}</TableCell>
                  <TableCell className="text-right">{item.totalWeight.toFixed(2)}</TableCell>
                  <TableCell className="text-right font-medium">
                    {item.totalCost.toLocaleString("ru-RU")} ₽
                  </TableCell>
                  <TableCell className="text-right text-muted-foreground">
                    {avgPrice.toFixed(2)} ₽
                  </TableCell>
                </TableRow>
              )
            })}
          </TableBody>
          <TableFooter>
            <TableRow>
              <TableCell colSpan={3} className="text-right font-medium">
                Итого:
              </TableCell>
              <TableCell className="text-right font-bold">
                {report.totalWeight.toFixed(2)} кг
              </TableCell>
              <TableCell className="text-right font-bold">
                {report.totalCost.toLocaleString("ru-RU")} ₽
              </TableCell>
              <TableCell className="text-right text-muted-foreground">
                {(report.totalCost / report.totalWeight).toFixed(2)} ₽
              </TableCell>
            </TableRow>
          </TableFooter>
        </Table>
      </div>
      
      <div className="grid grid-cols-2 gap-4 text-sm text-muted-foreground">
        <div>
          <p className="font-medium">Период отчета:</p>
          <p>
            {format(new Date(report.startDate), "dd.MM.yyyy", { locale: ru })} - 
            {format(new Date(report.endDate), "dd.MM.yyyy", { locale: ru })}
          </p>
        </div>
        <div>
          <p className="font-medium">Статистика:</p>
          <p>Поставщиков: {new Set(report.summaryItems?.map(item => item.supplierName)).size}</p>
          <p>Типов товаров: {new Set(report.summaryItems?.map(item => item.productType)).size}</p>
        </div>
      </div>
    </div>
  )
}

// Компонент для отображения JSON в сыром виде (на случай если понадобится)
function RawJsonView({ data }: { data: unknown }) {
  return (
    <div className="space-y-2">
      <div className="flex justify-between items-center">
        <h3 className="text-lg font-semibold">JSON данные</h3>
        <Badge variant="outline">Сырой формат</Badge>
      </div>
      <pre className="max-h-[600px] overflow-auto rounded-lg border bg-muted p-4 text-xs font-mono text-foreground">
        {JSON.stringify(data, null, 2)}
      </pre>
    </div>
  )
}

function today() {
  return format(new Date(), "yyyy-MM-dd")
}

function thirtyDaysAgo() {
  return format(subDays(new Date(), 30), "yyyy-MM-dd")
}

export function ReportGenerator() {
  const [startDate, setStartDate] = useState(thirtyDaysAgo)
  const [endDate, setEndDate] = useState(today)
  const [detailed, setDetailed] = useState(true)
  const [reportFormat, setReportFormat] = useState<ReportFormat>("JSON")
  const [loading, setLoading] = useState(false)
  const [serverError, setServerError] = useState("")
  const [reportData, setReportData] = useState<DeliveryReport | null>(null)
  const [viewMode, setViewMode] = useState<"table" | "json">("table")

  async function handleGenerate(e: React.FormEvent) {
    e.preventDefault()
    setServerError("")
    setReportData(null)
    setLoading(true)

    try {
      const response = await generateReport({
        startDate,
        endDate,
        detailed,
        format: reportFormat,
      })

      if (reportFormat === "JSON") {
        const data = await response.json()
        setReportData(data)
        setViewMode("table")
        toast.success("Отчет успешно сгенерирован!")
      } else {
        // PDF или CSV — скачиваем файл
        const blob = await response.blob()
        const extension = reportFormat === "PDF" ? "pdf" : "csv"
        const fileName = `отчет_${startDate}_${endDate}.${extension}`
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement("a")
        a.href = url
        a.download = fileName
        document.body.appendChild(a)
        a.click()
        a.remove()
        window.URL.revokeObjectURL(url)
        toast.success(`${reportFormat} отчет скачан!`)
      }
    } catch (err: unknown) {
      const apiErr = err as ApiError
      setServerError(apiErr.message || "Ошибка при генерации отчета.")
      toast.error(apiErr.message || "Ошибка при генерации отчета.")
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="flex flex-col gap-6">
      <Card>
        <CardHeader>
          <CardTitle>Создание отчета</CardTitle>
          <CardDescription>
            Выберите период, формат и детализацию отчета по поставкам
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleGenerate} className="flex flex-col gap-4">
            {serverError && (
              <div className="flex items-center gap-3 rounded-lg border border-destructive/30 bg-destructive/5 p-3 text-sm text-destructive">
                <AlertCircle className="h-4 w-4 shrink-0" />
                {serverError}
              </div>
            )}

            <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
              <div className="flex flex-col gap-2">
                <Label htmlFor="report-start">Дата начала</Label>
                <Input
                  id="report-start"
                  type="date"
                  value={startDate}
                  onChange={(e) => setStartDate(e.target.value)}
                />
              </div>
              <div className="flex flex-col gap-2">
                <Label htmlFor="report-end">Дата окончания</Label>
                <Input
                  id="report-end"
                  type="date"
                  value={endDate}
                  onChange={(e) => setEndDate(e.target.value)}
                />
              </div>
              <div className="flex flex-col gap-2">
                <Label htmlFor="report-format">Формат</Label>
                <Select
                  value={reportFormat}
                  onValueChange={(v) => setReportFormat(v as ReportFormat)}
                >
                  <SelectTrigger id="report-format">
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="JSON">JSON (просмотр в браузере)</SelectItem>
                    <SelectItem value="PDF">PDF (скачать)</SelectItem>
                    <SelectItem value="CSV">CSV (скачать)</SelectItem>
                  </SelectContent>
                </Select>
              </div>
              <div className="flex flex-col gap-2">
                <Label>Детализация</Label>
                <div className="flex h-10 items-center gap-2">
                  <Switch
                    id="report-detailed"
                    checked={detailed}
                    onCheckedChange={setDetailed}
                  />
                  <Label htmlFor="report-detailed" className="font-normal text-muted-foreground">
                    {detailed ? "Детальный" : "Сводный"}
                  </Label>
                </div>
              </div>
            </div>

            <div className="flex justify-end">
              <Button type="submit" disabled={loading || !startDate || !endDate}>
                {loading ? (
                  <Loader2 className="h-4 w-4 animate-spin" />
                ) : reportFormat === "JSON" ? (
                  <FileBarChart className="h-4 w-4" />
                ) : (
                  <Download className="h-4 w-4" />
                )}
                {reportFormat === "JSON" ? "Создать отчет" : `Скачать ${reportFormat}`}
              </Button>
            </div>
          </form>
        </CardContent>
      </Card>

      {reportData && reportFormat === "JSON" && (
        <Card>
          <CardHeader>
            <CardTitle>Результаты отчета</CardTitle>
            <CardDescription>
              {format(new Date(reportData.startDate), "dd.MM.yyyy", { locale: ru })} - 
              {format(new Date(reportData.endDate), "dd.MM.yyyy", { locale: ru })} 
              {reportData.detailed ? " (детальный)" : " (сводный)"}
            </CardDescription>
            <div className="flex gap-2">
              <Button
                variant={viewMode === "table" ? "default" : "outline"}
                size="sm"
                onClick={() => setViewMode("table")}
              >
                Таблица
              </Button>
              <Button
                variant={viewMode === "json" ? "default" : "outline"}
                size="sm"
                onClick={() => setViewMode("json")}
              >
                JSON
              </Button>
            </div>
          </CardHeader>
          <CardContent>
            {viewMode === "table" ? (
              reportData.detailed ? (
                <DetailedReportTable report={reportData} />
              ) : (
                <SummaryReportTable report={reportData} />
              )
            ) : (
              <RawJsonView data={reportData} />
            )}
          </CardContent>
        </Card>
      )}
    </div>
  )
}