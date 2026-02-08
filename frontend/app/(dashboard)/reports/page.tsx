"use client"

import { ReportGenerator } from "@/components/reports/report-generator"

export default function ReportsPage() {
  return (
    <div className="flex flex-col gap-6">
      <div>
        <h1 className="text-2xl font-semibold text-foreground">Отчеты</h1>
        <p className="text-sm text-muted-foreground">
          Создавайте отчеты о доставке в формате JSON, PDF или CSV.
        </p>
      </div>
      <ReportGenerator />
    </div>
  )
}
