"use client";
import { useEffect, useState } from "react";
import { apiGet } from "@/lib/api";
export default function Reviews(){
  const [list,setList]=useState<any[]>([]);
  useEffect(()=>{ apiGet("/api/v1/account/reviews").then(j=>setList(j.data?.content||j.data||[])).catch(()=>{}); },[]);
  return <div><h1 className="text-xl font-bold">Danh gia cua toi</h1><div className="mt-4 space-y-2">{list.map((r:any)=><div key={r.id} className="border rounded p-3 text-sm"><p className="font-medium">{r.title} — {r.rating}★</p><p>{r.comment}</p></div>)}{list.length===0 && <p className="text-sm text-neutral-500">Chua co danh gia</p>}</div></div>;
}
