"use client";
import { useEffect, useState } from "react";
import { apiGet, apiPost } from "@/lib/api";
import { DataTable } from "@/components/admin/DataTable";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import Link from "next/link";
export default function Employees(){
  const [list,setList]=useState<any[]>([]); const [email,setEmail]=useState(""); const [role,setRole]=useState("EMPLOYEE");
  function load(){ apiGet("/api/v1/admin/employees").then(j=>setList(j.data?.content||j.data||[])).catch(()=>{}); }
  useEffect(()=>{ load(); },[]);
  return <div className="space-y-4"><h1 className="text-xl font-bold">Employees</h1>
    <div className="bg-white border rounded p-4 flex gap-2"><Input value={email} onChange={e=>setEmail(e.target.value)} placeholder="Email"/><select value={role} onChange={e=>setRole(e.target.value)} className="border rounded px-2 py-1 text-sm"><option>EMPLOYEE</option><option>MANAGER</option><option>ADMIN</option><option>SUPER_ADMIN</option></select><Button onClick={async()=>{ await apiPost("/api/v1/admin/employees",{email, password:"Temp123!@#", role}); load(); }}>Tao nhan vien</Button></div>
    <DataTable columns={[
      {key:"email",header:"Email", render:(v:any,row:any)=> <Link href={"/admin/employees/"+row.id} className="underline">{v||row.userEmail}</Link>},
      {key:"role",header:"Role"},
      {key:"isActive",header:"Active"},
      {key:"lastLoginAt",header:"Last login"},
    ]} data={list}/>
    <p className="text-xs text-neutral-500">Can EMPLOYEE_MANAGE moi duoc tao/sua role.</p>
  </div>;
}
