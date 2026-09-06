"use client";
import { useState } from "react";
import { apiPost } from "@/lib/api";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
export default function Forgot(){
  const [email,setEmail]=useState(""); const [done,setDone]=useState(false);
  async function submit(){ await apiPost("/api/v1/auth/forgot-password",{email},{auth:false}); setDone(true); }
  return <div className="max-w-md mx-auto px-6 py-12"><h1 className="text-xl font-bold">Quen mat khau</h1>{done?<p className="text-sm mt-4">Neu email ton tai, link dat lai da duoc gui.</p>:<div className="mt-4 space-y-3"><Input value={email} onChange={(e:any)=>setEmail(e.target.value)} placeholder="Email"/><Button onClick={submit} className="w-full">Gui link</Button></div>}</div>;
}
