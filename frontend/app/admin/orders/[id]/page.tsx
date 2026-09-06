"use client";
import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { apiGet, apiPost } from "@/lib/api";
import { StatusBadge } from "@/components/admin/StatusBadge";
import { Button } from "@/components/ui/button";
const nextMap:Record<string,string[]>={ PENDING:["CONFIRMED","CANCELLED"], CONFIRMED:["PROCESSING","CANCELLED"], PROCESSING:["PACKED"], PACKED:["SHIPPED"], SHIPPED:["DELIVERED"], DELIVERED:["REFUNDED"] };
export default function OrderDetail(){
  const {id}=useParams() as any; const [o,setO]=useState<any>(null);
  function load(){ apiGet(`/api/v1/admin/orders/${id}`).then(j=>setO(j.data||j)).catch(()=>{}); }
  useEffect(()=>{ load(); },[id]);
  if(!o) return <p className="text-sm">Dang tai...</p>;
  const actions = nextMap[o.status]||[];
  return <div className="space-y-4">
    <h1 className="text-xl font-bold">Don {o.orderNumber} <StatusBadge status={o.status}/></h1>
    <div className="bg-white border rounded p-4 text-sm space-y-1"><p>Khach: {o.customerId}</p><p>Tong: {o.grandTotal} VND</p><p>Dia chi: {o.shippingAddress}</p><p>Thanh toan: {o.paymentStatus||o.status}</p></div>
    <div className="bg-white border rounded p-4"><p className="font-medium text-sm">Hanh dong</p><div className="flex gap-2 mt-2">{actions.map((a:string)=><Button key={a} variant="outline" onClick={async()=>{ await apiPost(`/api/v1/admin/orders/${id}/status`,{status:a}); load(); }}>{a}</Button>)}{o.status!=="CANCELLED" && o.status!=="REFUNDED" && <Button variant="outline" onClick={async()=>{ await apiPost(`/api/v1/admin/orders/${id}/cancel`,{}); load(); }}>Huy</Button>}<Button variant="outline" onClick={async()=>{ await apiPost(`/api/v1/admin/orders/${id}/refund`,{amount:o.grandTotal}); load(); }}>Hoan tien</Button></div><p className="text-xs text-neutral-500 mt-2">Khong cho phep chuyen trang thai sai — backend se tra 409 neu sai state machine.</p></div>
    <div className="bg-white border rounded p-4"><p className="font-medium text-sm">Timeline</p><div className="text-xs text-neutral-500 mt-2">ORDER_CREATED → PAYMENT_CONFIRMED → PROCESSING → PACKED → SHIPPED → DELIVERED → (REFUNDED/CANCELLED)</div></div>
  </div>;
}
