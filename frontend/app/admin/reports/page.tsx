"use client";
import { useEffect, useState } from "react";
import { apiGet } from "@/lib/api";
import { StatCard } from "@/components/admin/StatCard";
export default function Reports(){
  const [data,setData]=useState<any>(null);
  useEffect(()=>{ apiGet("/api/v1/admin/reports/summary").then(j=>setData(j.data||j)).catch(()=>{}); },[]);
  return <div className="space-y-6"><h1 className="text-xl font-bold">Reports</h1>
    <div className="grid md:grid-cols-4 gap-4"><StatCard label="Revenue" value={String(data?.revenue||0)}/><StatCard label="Orders" value={String(data?.orders||0)}/><StatCard label="Customers" value={String(data?.customers||0)}/><StatCard label="Units" value={String(data?.unitsSold||0)}/></div>
    <div className="border rounded bg-white p-4"><p className="font-medium text-sm">Doanh thu theo ngay / Don theo ngay / Theo category / Theo product</p><p className="text-xs text-neutral-500 mt-2">Goi /api/v1/admin/reports?from=&to= — hien bieu do placeholder.</p></div>
  </div>;
}
