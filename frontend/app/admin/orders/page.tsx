"use client";
import { useEffect, useState } from "react";
import { apiGet } from "@/lib/api";
import { DataTable } from "@/components/admin/DataTable";
import { StatusBadge } from "@/components/admin/StatusBadge";
import Link from "next/link";
export default function AdminOrders(){
  const [list,setList]=useState<any[]>([]); const [status,setStatus]=useState("");
  function load(){ const qs=status?`?status=${status}`:""; apiGet(`/api/v1/admin/orders${qs}`).then(j=>setList(j.data?.content||j.data||[])).catch(()=>{}); }
  useEffect(()=>{ load(); },[status]);
  return <div className="space-y-4"><h1 className="text-xl font-bold">Orders</h1>
    <div className="flex gap-2"><select value={status} onChange={e=>setStatus(e.target.value)} className="border rounded px-2 py-1 text-sm"><option value="">Tat ca</option><option>PENDING</option><option>CONFIRMED</option><option>SHIPPED</option><option>DELIVERED</option><option>CANCELLED</option></select><button onClick={load} className="border rounded px-3 py-1 text-sm">Loc</button></div>
    <DataTable columns={[
      {key:"orderNumber", header:"Ma don", render:(v:any,row:any)=><Link href={"/admin/orders/"+row.id} className="underline">{v}</Link>},
      {key:"status", header:"Trang thai", render:(v:any)=><StatusBadge status={v}/>},
      {key:"grandTotal", header:"Tong"},
      {key:"customerId", header:"Khach"},
      {key:"createdAt", header:"Ngay"},
    ]} data={list}/>
  </div>;
}
