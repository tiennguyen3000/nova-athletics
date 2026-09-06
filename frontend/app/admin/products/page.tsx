"use client";
import { useEffect, useState } from "react";
import { apiGet } from "@/lib/api";
import { DataTable } from "@/components/admin/DataTable";
import { StatusBadge } from "@/components/admin/StatusBadge";
import Link from "next/link";
import { Button } from "@/components/ui/button";
export default function AdminProducts(){
  const [data,setData]=useState<any[]>([]); const [q,setQ]=useState(""); const [loading,setLoading]=useState(false);
  async function load(){
    setLoading(true);
    try{ const j=await apiGet(`/api/v1/admin/products?size=20&q=${encodeURIComponent(q)}`); setData(j.data?.content||j.data||[]); }catch{}
    setLoading(false);
  }
  useEffect(()=>{ load(); },[]);
  return <div className="space-y-4">
    <div className="flex items-center justify-between"><h1 className="text-xl font-bold">Products</h1><Link href="/admin/products/new"><Button>Tao san pham</Button></Link></div>
    <div className="flex gap-2"><input value={q} onChange={e=>setQ(e.target.value)} onKeyDown={e=>e.key==="Enter"&&load()} placeholder="Tim theo ten/sku..." className="border rounded px-3 py-2 text-sm w-64"/><Button variant="outline" onClick={load}>Tim</Button></div>
    <DataTable loading={loading} columns={[
      {key:"name", header:"Ten", render:(v:any,row:any)=><Link href={"/admin/products/"+row.id} className="underline">{v}</Link>},
      {key:"status", header:"Trang thai", render:(v:any)=><StatusBadge status={v}/>},
      {key:"basePrice", header:"Gia"},
      {key:"id", header:"Hanh dong", render:(v:any,row:any)=><div className="flex gap-2"><Link href={"/admin/products/"+row.id} className="text-xs underline">Sua</Link><button className="text-xs">Nhan ban</button></div>},
    ]} data={data}/>
  </div>;
}
