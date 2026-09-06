"use client";
import { useState } from "react";
import { apiPost } from "@/lib/api";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
export default function Reset(){
  const [token,setToken]=useState(""); const [pw,setPw]=useState(""); const [done,setDone]=useState(false);
  return <div className="max-w-md mx-auto px-6 py-12"><h1 className="text-xl font-bold">Dat lai mat khau</h1>{done?<p className="text-sm mt-4">Da dat lai. Dang nhap lai.</p>:<div className="mt-4 space-y-3"><Input value={token} onChange={(e:any)=>setToken(e.target.value)} placeholder="Token"/><Input type="password" value={pw} onChange={(e:any)=>setPw(e.target.value)} placeholder="Mat khau moi"/><Button onClick={async()=>{ await apiPost("/api/v1/auth/reset-password",{token,newPassword:pw},{auth:false}); setDone(true);}} className="w-full">Dat lai</Button></div>}</div>;
}
