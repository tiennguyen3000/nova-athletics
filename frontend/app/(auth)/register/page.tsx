"use client";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { apiPost } from "@/lib/api";
import { useAuth } from "@/store/auth";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
const schema=z.object({email:z.string().email(), password:z.string().min(8), firstName:z.string().min(1), lastName:z.string().min(1)});
export default function Register(){
  const router=useRouter(); const auth=useAuth();
  const {register, handleSubmit} = useForm({resolver: zodResolver(schema)});
  async function onSubmit(v:any){
    const j=await apiPost("/api/v1/auth/register", v, {auth:false});
    const data=j.data||j;
    auth.setAuth({id:data.id, email:v.email, customer:data.customer}, data.accessToken);
    router.push("/");
  }
  return <div className="max-w-md mx-auto px-6 py-12"><h1 className="text-2xl font-bold">Dang ky</h1><form onSubmit={handleSubmit(onSubmit)} className="mt-6 space-y-3"><Input placeholder="Ho" {...register("lastName")} /><Input placeholder="Ten" {...register("firstName")} /><Input placeholder="Email" {...register("email")} /><Input type="password" placeholder="Mat khau 8+ ky tu" {...register("password")} /><Button type="submit" className="w-full">Tao tai khoan</Button></form></div>;
}
