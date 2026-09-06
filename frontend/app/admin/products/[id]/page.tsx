"use client";
import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { apiGet, apiPost } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { StatusBadge } from "@/components/admin/StatusBadge";
export default function ProductDetail(){
  const {id}=useParams() as any;
  const [p,setP]=useState<any>(null); const [tab,setTab]=useState("general");
  useEffect(()=>{ apiGet(`/api/v1/admin/products/${id}`).then(j=>setP(j.data||j)).catch(()=>{}); },[id]);
  if(!p) return <p className="text-sm">Dang tai...</p>;
  return <div className="space-y-4">
    <div className="flex items-center justify-between"><h1 className="text-xl font-bold">{p.name}</h1><StatusBadge status={p.status}/></div>
    <div className="flex gap-2 text-sm border-b">{["general","pricing","variants","images","inventory","categories","seo"].map(t=><button key={t} onClick={()=>setTab(t)} className={"px-3 py-2 "+(tab===t?"border-b-2 border-black font-medium":"text-neutral-500")}>{t}</button>)}</div>
    <div className="bg-white border rounded p-4">
      {tab==="general" && <div className="space-y-2 text-sm"><p><b>Slug:</b> {p.slug}</p><p><b>Mo ta:</b> {p.description}</p><Button variant="outline" onClick={async()=>{ await apiPost(`/api/v1/admin/products/${id}/publish`,{}); location.reload(); }}>Publish</Button></div>}
      {tab==="variants" && <div className="text-sm"><p>SKU / barcode / mau / size / gia — goi <code>POST /api/v1/admin/products/{id}/variants</code></p><p className="text-neutral-500 mt-2">Vi du tao bien the nhanh Black 40-44, White 40-44</p></div>}
      {tab==="images" && <div className="text-sm">Upload qua <code>POST /api/v1/admin/media</code> — preview / delete / reorder / primary</div>}
      {tab==="inventory" && <p className="text-sm">Xem ton theo kho — <a href="/admin/inventory" className="underline">Inventory</a></p>}
      {tab!=="general" && tab!=="variants" && tab!=="images" && tab!=="inventory" && <p className="text-sm text-neutral-500">Tab {tab} — form placeholder</p>}
    </div>
  </div>;
}
