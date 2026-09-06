"use client";
import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { apiGet } from "@/lib/api";
export default function CustomerDetail(){
  const {id}=useParams() as any; const [c,setC]=useState<any>(null);
  useEffect(()=>{ apiGet(`/api/v1/admin/customers/${id}`).then(j=>setC(j.data||j)).catch(()=>{}); },[id]);
  if(!c) return <p className="text-sm">Dang tai...</p>;
  return <div className="space-y-4"><h1 className="text-xl font-bold">Khach #{c.id}</h1><div className="bg-white border rounded p-4 text-sm space-y-1"><p>Email: {c.email||c.userEmail}</p><p>Don: {c.orderCount||0} — Tong chi: {c.lifetimeValue||0}</p><p>Don cuoi: {c.lastOrderAt||"—"}</p></div></div>;
}
