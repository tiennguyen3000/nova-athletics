"use client";
import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { apiGet, apiPost } from "@/lib/api";
import { Button } from "@/components/ui/button";
export default function EmployeeDetail(){
  const {id}=useParams() as any; const [e,setE]=useState<any>(null); const [role,setRole]=useState("EMPLOYEE");
  useEffect(()=>{ apiGet(`/api/v1/admin/employees/${id}`).then(j=>{setE(j.data||j); setRole(j.data?.role||"EMPLOYEE");}).catch(()=>{}); },[id]);
  if(!e) return <p className="text-sm">Dang tai...</p>;
  return <div className="space-y-4"><h1 className="text-xl font-bold">Nhan vien #{id}</h1><p className="text-sm">Email: {e.email||e.userEmail}</p><div className="flex gap-2"><select value={role} onChange={ev=>setRole(ev.target.value)} className="border rounded px-2 py-1 text-sm"><option>EMPLOYEE</option><option>MANAGER</option><option>ADMIN</option><option>SUPER_ADMIN</option></select><Button onClick={async()=>{ await apiPost(`/api/v1/admin/employees/${id}/role`,{role}); location.reload(); }}>Doi role</Button><Button variant="outline" onClick={async()=>{ await apiPost(`/api/v1/admin/employees/${id}/deactivate`,{}); }}>Vo hieu hoa</Button></div></div>;
}
