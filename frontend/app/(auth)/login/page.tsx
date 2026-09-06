"use client";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { apiPost } from "@/lib/api";
import { useAuth } from "@/store/auth";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import Link from "next/link";
const schema=z.object({email:z.string().email(), password:z.string().min(8)});
export default function Login(){
  const router=useRouter(); const auth=useAuth();
  const {register, handleSubmit, formState:{errors}} = useForm({resolver: zodResolver(schema)});
  async function onSubmit(v:any){
    const j=await apiPost("/api/v1/auth/login", v, {auth:false});
    const data=j.data||j;
    auth.setAuth({id:data.id||data.user?.id, email:v.email, customer:data.customer}, data.accessToken||data.token);
    router.push("/");
  }
  return <div className="max-w-md mx-auto px-6 py-12"><h1 className="text-2xl font-bold">Dang nhap</h1><form onSubmit={handleSubmit(onSubmit)} className="mt-6 space-y-3"><Input placeholder="Email" {...register("email")} />{errors.email && <p className="text-xs text-red-600">{String(errors.email.message)}</p>}<Input type="password" placeholder="Mat khau" {...register("password")} /><Button type="submit" className="w-full">Dang nhap</Button></form><p className="text-sm mt-4">Chua co tai khoan? <Link href="/register" className="underline">Dang ky</Link></p></div>;
}
