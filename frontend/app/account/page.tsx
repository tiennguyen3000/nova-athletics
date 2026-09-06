"use client";
import { useAuth } from "@/store/auth";
import { Button } from "@/components/ui/button";
export default function Account(){
  const auth=useAuth();
  return <div><h1 className="text-xl font-bold">Tai khoan</h1><p className="text-sm text-neutral-500 mt-2">Xin chao {auth.user?.email || "khach"}</p><Button onClick={()=>auth.logout()} variant="outline" className="mt-4">Dang xuat</Button></div>;
}
