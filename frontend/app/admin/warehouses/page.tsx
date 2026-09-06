"use client";
import { useEffect, useState } from "react";
import { apiGet, apiPost } from "@/lib/api";
import { DataTable } from "@/components/admin/DataTable";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
export default function Warehouses(){
  const [list,setList]=useState<any[]>([]); const [name,setName]=useState(""); const [code,setCode]=useState("");
  function load(){ apiGet("/api/v1/admin/warehouses").then(j=>setList(j.data||j.data?.content||[])).catch(()=>{}); }
  useEffect(()=>{ load(); },[]);
  return <div className="space-y-4"><h1 className="text-xl font-bold">Warehouses</h1>
    <div className="bg-white border rounded p-4 flex gap-2"><Input placeholder="Code" value={code} onChange={e=>setCode(e.target.value)}/><Input placeholder="Ten kho" value={name} onChange={e=>setName(e.target.value)}/><Button onClick={async()=>{ await apiPost("/api/v1/admin/warehouses",{code, name, city:"HCM"}); load(); }}>Tao</Button></div>
    <DataTable columns={[{key:"code",header:"Code"},{key:"name",header:"Ten"},{key:"city",header:"City"},{key:"isActive",header:"Active"}]} data={list}/>
    <p className="text-xs text-neutral-500">Chi tiet kho + ton + transfer history — <a href="/admin/inventory" className="underline">Inventory</a></p>
  </div>;
}
