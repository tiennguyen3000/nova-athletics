"use client";
import { useEffect, useState } from "react";
import { apiGet } from "@/lib/api";
import { StatCard } from "@/components/admin/StatCard";
import { formatVND } from "@/lib/utils";
export default function AdminDashboard(){
  const [stats,setStats]=useState<any>(null);
  useEffect(()=>{ apiGet("/api/v1/admin/reports/summary").then(j=>setStats(j.data||j)).catch(()=>setStats({revenue: 0, orders:0, customers:0, products:0})); },[]);
  const s=stats||{};
  return <div className="space-y-6">
    <div className="flex items-center justify-between"><h1 className="text-xl font-bold">Dashboard</h1><select className="border rounded px-2 py-1 text-sm"><option>30 ngay</option><option>7 ngay</option><option>90 ngay</option></select></div>
    <div className="grid md:grid-cols-3 lg:grid-cols-6 gap-4">
      <StatCard label="Doanh thu" value={formatVND(s.revenue||0)} sub="30 ngay" />
      <StatCard label="Don hang" value={String(s.orders||0)} />
      <StatCard label="Khach hang" value={String(s.customers||0)} />
      <StatCard label="San pham" value={String(s.products||0)} />
      <StatCard label="Gia tri ton" value={formatVND(s.inventoryValue||0)} />
      <StatCard label="AOV" value={formatVND(s.aov||0)} />
    </div>
    <div className="grid lg:grid-cols-2 gap-4">
      <div className="border rounded bg-white p-4"><p className="font-semibold text-sm">Doanh thu theo ngay</p><div className="h-48 bg-neutral-50 grid place-items-center text-sm text-neutral-400 mt-3">Chart — Revenue by day (doi backend /admin/reports)</div></div>
      <div className="border rounded bg-white p-4"><p className="font-semibold text-sm">Don hang theo ngay</p><div className="h-48 bg-neutral-50 grid place-items-center text-sm text-neutral-400 mt-3">Chart — Orders by day</div></div>
    </div>
    <div className="border rounded bg-white p-4"><p className="font-semibold text-sm">Canh bao ton thap</p><p className="text-xs text-neutral-500 mt-1">available &lt;= reorderPoint</p><div className="text-sm text-neutral-400 mt-3">Load tu /api/v1/admin/inventory?lowStock=true</div></div>
  </div>;
}
