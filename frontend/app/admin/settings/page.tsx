"use client";
import { useEffect, useState } from "react";
import { apiGet, apiPost } from "@/lib/api";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
export default function Settings(){
  const [s,setS]=useState<any>({});
  useEffect(()=>{ apiGet("/api/v1/admin/settings").then(j=>setS(j.data||j)).catch(()=>{}); },[]);
  return <div className="space-y-4 max-w-2xl"><h1 className="text-xl font-bold">Settings</h1>
    <div className="bg-white border rounded p-4 space-y-3">
      <div><label className="text-sm">Phi van chuyen (VND)</label><Input value={s.shippingFee||""} onChange={e=>setS({...s, shippingFee:e.target.value})}/></div>
      <div><label className="text-sm">Thue (%)</label><Input value={s.taxRate||""} onChange={e=>setS({...s, taxRate:e.target.value})}/></div>
      <Button onClick={async()=>{ await apiPost("/api/v1/admin/settings", s); alert("Da luu"); }}>Luu</Button>
      <p className="text-xs text-neutral-500">Secret (JWT, MINIO, DB) khong luu o day — dung env var.</p>
    </div>
  </div>;
}
