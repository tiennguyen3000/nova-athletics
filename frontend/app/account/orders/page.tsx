"use client";
import { useEffect, useState } from "react";
import { apiGet } from "@/lib/api";
import Link from "next/link";
export default function Orders(){
  const [orders,setOrders]=useState<any[]>([]);
  useEffect(()=>{ apiGet("/api/v1/orders").then(j=>setOrders(j.data?.content||j.data||[])).catch(()=>{}); },[]);
  return <div><h1 className="text-xl font-bold">Don hang</h1><div className="mt-4 space-y-3">{orders.map((o:any)=><Link key={o.id} href={"/account/orders/"+o.id} className="block border rounded p-4"><p className="font-medium">{o.orderNumber} — {o.status}</p><p className="text-sm text-neutral-500">{o.grandTotal} VND</p></Link>)}{orders.length===0 && <p className="text-sm text-neutral-500">Chua co don hang</p>}</div></div>;
}
