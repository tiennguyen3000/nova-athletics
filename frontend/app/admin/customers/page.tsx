"use client";
import { useEffect, useState } from "react";
import { apiGet } from "@/lib/api";
import { DataTable } from "@/components/admin/DataTable";
import Link from "next/link";
export default function Customers(){
  const [list,setList]=useState<any[]>([]);
  useEffect(()=>{ apiGet("/api/v1/admin/customers").then(j=>setList(j.data?.content||j.data||[])).catch(()=>{}); },[]);
  return <div className="space-y-4"><h1 className="text-xl font-bold">Customers</h1><DataTable columns={[
    {key:"id", header:"ID"},
    {key:"email", header:"Email", render:(v:any,row:any)=> row.email || row.userEmail || String(row.id)},
    {key:"firstName", header:"Ten"},
    {key:"status", header:"Trang thai"},
  ]} data={list.map((c:any)=>({...c, email:c.email||c.user?.email}))}/><p className="text-xs text-neutral-500">Search/filter/pagination server-side — goi /api/v1/admin/customers?q=&status=</p></div>;
}
