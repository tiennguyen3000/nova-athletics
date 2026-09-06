"use client";
import { useEffect, useState } from "react";
import { apiGet, apiPost } from "@/lib/api";
import { DataTable } from "@/components/admin/DataTable";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
export default function Promotions(){
  const [list,setList]=useState<any[]>([]); const [code,setCode]=useState("NOVA20");
  function load(){ apiGet("/api/v1/admin/coupons").then(j=>setList(j.data?.content||j.data||[])).catch(()=>{}); }
  useEffect(()=>{ load(); },[]);
  return <div className="space-y-4"><h1 className="text-xl font-bold">Promotions</h1>
    <div className="bg-white border rounded p-4 flex gap-2"><Input value={code} onChange={e=>setCode(e.target.value)} placeholder="Code"/><Button onClick={async()=>{ await apiPost("/api/v1/admin/coupons",{code, name:"Promo "+code, discountType:"PERCENTAGE", discountValue:10, isActive:true, usageLimit:100}); load(); }}>Tao coupon</Button></div>
    <DataTable columns={[{key:"code",header:"Code"},{key:"discountType",header:"Loai"},{key:"discountValue",header:"Gia tri"},{key:"isActive",header:"Active"},{key:"usageLimit",header:"Limit"}]} data={list}/>
  </div>;
}
