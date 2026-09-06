"use client";
import { useEffect, useState } from "react";
import { apiGet, apiPost } from "@/lib/api";
import { DataTable } from "@/components/admin/DataTable";
import { Button } from "@/components/ui/button";
export default function Reviews(){
  const [list,setList]=useState<any[]>([]); const [filter,setFilter]=useState("");
  function load(){ const qs=filter?`?status=${filter}`:""; apiGet(`/api/v1/admin/reviews${qs}`).then(j=>setList(j.data?.content||j.data||[])).catch(()=>{}); }
  useEffect(()=>{ load(); },[filter]);
  return <div className="space-y-4"><h1 className="text-xl font-bold">Reviews</h1>
    <select value={filter} onChange={e=>setFilter(e.target.value)} className="border rounded px-2 py-1 text-sm"><option value="">Tat ca</option><option>PENDING</option><option>APPROVED</option><option>REJECTED</option></select>
    <DataTable columns={[
      {key:"title",header:"Tieu de"},
      {key:"rating",header:"Sao"},
      {key:"status",header:"Trang thai"},
      {key:"id",header:"Hanh dong", render:(v:any,row:any)=><div className="flex gap-2"><Button variant="outline" onClick={async()=>{ await apiPost(`/api/v1/admin/reviews/${row.id}/approve`,{}); load(); }}>Duyet</Button><Button variant="outline" onClick={async()=>{ await apiPost(`/api/v1/admin/reviews/${row.id}/reject`,{}); load(); }}>Tu choi</Button></div>},
    ]} data={list}/>
  </div>;
}
