"use client";
import { useEffect, useState } from "react";
import { apiGet, apiPost } from "@/lib/api";
import { DataTable } from "@/components/admin/DataTable";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
export default function Collections(){
  const [list,setList]=useState<any[]>([]); const [name,setName]=useState("");
  function load(){ apiGet("/api/v1/admin/collections").then(j=>setList(j.data||[])).catch(()=>{}); }
  useEffect(()=>{ load(); },[]);
  return <div className="space-y-4"><h1 className="text-xl font-bold">Collections</h1><div className="bg-white border rounded p-4 flex gap-2"><Input value={name} onChange={e=>setName(e.target.value)} placeholder="Ten bo suu tap"/><Button onClick={async()=>{ await apiPost("/api/v1/admin/collections",{name, slug:name.toLowerCase().replace(/\s+/g,"-")}); load(); }}>Tao</Button></div><DataTable columns={[{key:"name",header:"Ten"},{key:"slug",header:"Slug"}]} data={list}/></div>;
}
