"use client";
import { useEffect, useState } from "react";
import { apiGet, apiPost } from "@/lib/api";
import { DataTable } from "@/components/admin/DataTable";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
export default function Inventory(){
  const [list,setList]=useState<any[]>([]); const [adjust,setAdjust]=useState({variantId:"", warehouseId:"", qty:"", type:"CORRECTION", reason:""});
  function load(){ apiGet("/api/v1/admin/inventory").then(j=>setList(j.data?.content||j.data||[])).catch(()=>{}); }
  useEffect(()=>{ load(); },[]);
  return <div className="space-y-4"><h1 className="text-xl font-bold">Inventory</h1>
    <DataTable columns={[
      {key:"sku", header:"SKU"},
      {key:"productName", header:"San pham"},
      {key:"warehouseCode", header:"Kho"},
      {key:"quantityOnHand", header:"On Hand"},
      {key:"quantityReserved", header:"Reserved"},
      {key:"available", header:"Available", render:(_:any,row:any)=> String((row.quantityOnHand||0)-(row.quantityReserved||0))},
    ]} data={list}/>
    <div className="bg-white border rounded p-4 space-y-2 max-w-xl">
      <p className="font-medium text-sm">Dieu chinh ton kho (reason bat buoc)</p>
      <div className="grid grid-cols-2 gap-2"><Input placeholder="variantId" value={adjust.variantId} onChange={e=>setAdjust({...adjust, variantId:e.target.value})}/><Input placeholder="warehouseId" value={adjust.warehouseId} onChange={e=>setAdjust({...adjust, warehouseId:e.target.value})}/><Input placeholder="So luong (+/-)" value={adjust.qty} onChange={e=>setAdjust({...adjust, qty:e.target.value})}/><select value={adjust.type} onChange={e=>setAdjust({...adjust, type:e.target.value})} className="border rounded px-2 py-1 text-sm"><option>DAMAGED</option><option>STOCK_COUNT</option><option>CORRECTION</option><option>RETURN</option><option>PURCHASE</option></select></div>
      <Input placeholder="Ly do" value={adjust.reason} onChange={e=>setAdjust({...adjust, reason:e.target.value})}/>
      <Button onClick={async()=>{ if(!adjust.reason) return alert("Reason bat buoc"); await apiPost("/api/v1/admin/inventory/adjust",{variantId:Number(adjust.variantId), warehouseId:Number(adjust.warehouseId), quantity:Number(adjust.qty), type: adjust.type, reason: adjust.reason}); load(); }}>Dieu chinh</Button>
      <p className="text-xs text-neutral-500">Goi POST /api/v1/admin/inventory/adjust — khong sua truc tiep DB.</p>
    </div>
  </div>;
}
