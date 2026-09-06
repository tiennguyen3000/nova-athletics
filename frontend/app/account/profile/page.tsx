"use client";
import { useEffect, useState } from "react";
import { apiGet, apiPost } from "@/lib/api";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
export default function Profile(){
  const [me,setMe]=useState<any>(null);
  useEffect(()=>{ apiGet("/api/v1/auth/me").then(j=>setMe(j.data||j)).catch(()=>{}); },[]);
  return <div><h1 className="text-xl font-bold">Ho so</h1><p className="text-sm text-neutral-500 mt-2">{me?.user?.email}</p><div className="mt-4 space-y-3 max-w-md"><Input placeholder="Ten" defaultValue={me?.customer?.firstName}/><Input placeholder="Ho" defaultValue={me?.customer?.lastName}/><Button>Luu</Button></div></div>;
}
