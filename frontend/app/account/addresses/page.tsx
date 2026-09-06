"use client";
import { useEffect, useState } from "react";
import { apiGet, apiPost } from "@/lib/api";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
export default function Addresses(){
  const [list,setList]=useState<any[]>([]);
  const [form,setForm]=useState({recipientName:"", phone:"", line1:"", city:"HCM"});
  function load(){ apiGet("/api/v1/account/addresses").then(j=>setList(j.data||[])).catch(()=>{}); }
  useEffect(()=>{ load(); },[]);
  return <div><h1 className="text-xl font-bold">Dia chi</h1><div className="mt-4 space-y-2">{list.map((a:any)=><div key={a.id} className="border rounded p-3 text-sm">{a.recipientName} — {a.line1}, {a.city}</div>)}</div>
  <div className="mt-6 border rounded p-4 space-y-2 max-w-md"><h3 className="font-medium">Them dia chi</h3><Input placeholder="Nguoi nhan" value={form.recipientName} onChange={e=>setForm({...form, recipientName:e.target.value})}/><Input placeholder="SDT" value={form.phone} onChange={e=>setForm({...form, phone:e.target.value})}/><Input placeholder="Dia chi" value={form.line1} onChange={e=>setForm({...form, line1:e.target.value})}/><Input placeholder="Thanh pho" value={form.city} onChange={e=>setForm({...form, city:e.target.value})}/><Button onClick={async()=>{ await apiPost("/api/v1/account/addresses", form); load(); }}>Them</Button></div></div>;
}
