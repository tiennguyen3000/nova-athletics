"use client";
import { useEffect, useState } from "react";
import { apiGet } from "@/lib/api";
import { DataTable } from "@/components/admin/DataTable";
export default function AuditLogs(){
  const [list,setList]=useState<any[]>([]);
  useEffect(()=>{ apiGet("/api/v1/admin/audit-logs").then(j=>setList(j.data?.content||j.data||[])).catch(()=>{}); },[]);
  return <div className="space-y-4"><h1 className="text-xl font-bold">Audit Logs</h1>
    <DataTable columns={[
      {key:"actorEmail", header:"Actor"},
      {key:"action", header:"Action"},
      {key:"entityType", header:"Entity"},
      {key:"entityId", header:"ID"},
      {key:"createdAt", header:"Time"},
      {key:"ipAddress", header:"IP"},
    ]} data={list}/>
    <p className="text-xs text-neutral-500">Filter theo actor/action/entity/date — goi /api/v1/admin/audit-logs?actor=&action=&entity=&from=&to=</p>
  </div>;
}
