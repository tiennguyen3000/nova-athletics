"use client";
import { useEffect, useState } from "react";
import { apiGet, apiPost } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { useParams } from "next/navigation";
export default function OrderDetail(){
  const {id}=useParams() as any;
  const [order,setOrder]=useState<any>(null);
  useEffect(()=>{ apiGet(`/api/v1/orders/${id}`).then(j=>setOrder(j.data||j)).catch(()=>{}); },[id]);
  if(!order) return <p className="p-6">Dang tai...</p>;
  return <div><h1 className="text-xl font-bold">Don {order.orderNumber}</h1><p className="text-sm">Trang thai: {order.status}</p><p className="text-sm">Tong: {order.grandTotal} VND</p><Button variant="outline" className="mt-4" onClick={async()=>{ await apiPost(`/api/v1/orders/${id}/cancel`,{}); location.reload(); }}>Huy don</Button></div>;
}
