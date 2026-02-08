"use client"

import React from "react"

import { SidebarProvider, SidebarInset, SidebarTrigger } from "@/components/ui/sidebar"
import { AppSidebar } from "@/components/app-sidebar"
import { Separator } from "@/components/ui/separator"
import { Toaster } from "sonner"
import { SWRProvider } from "@/lib/swr-config"

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode
}) {
  return (
    <SWRProvider>
      <SidebarProvider>
        <AppSidebar />
        <SidebarInset>
          <header className="flex h-14 shrink-0 items-center gap-2 border-b bg-card px-4">
            <SidebarTrigger className="-ml-1" />
            <Separator orientation="vertical" className="mr-2 h-4" />
            <span className="text-sm font-medium text-muted-foreground">
              Дашборд
            </span>
          </header>
          <main className="flex-1 p-6">{children}</main>
        </SidebarInset>
        <Toaster position="top-right" richColors />
      </SidebarProvider>
    </SWRProvider>
  )
}
