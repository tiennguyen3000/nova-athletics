"use client";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";
import { apiPost } from "@/lib/api";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { useRouter } from "next/navigation";
const schema=z.object({name:z.string().min(2), slug:z.string().min(2), basePrice:z.coerce.number().min(0), description:z.string().optional()});
export default function NewProduct(){
  const router=useRouter();
  const {register, handleSubmit} = useForm({resolver: zodResolver(schema)});
  async function onSubmit(v:any){
    const j=await apiPost("/api/v1/admin/products", {...v, status:"DRAFT"});
    router.push("/admin/products/"+(j.data?.id||""));
  }
  return <div className="max-w-2xl"><h1 className="text-xl font-bold">Tao san pham</h1><form onSubmit={handleSubmit(onSubmit)} className="mt-6 space-y-3 bg-white border rounded p-4"><Input placeholder="Ten" {...register("name")} /><Input placeholder="Slug" {...register("slug")} /><Input type="number" placeholder="Gia goc" {...register("basePrice")} /><textarea placeholder="Mo ta" {...register("description")} className="border rounded px-3 py-2 w-full text-sm"/><Button type="submit">Tao</Button></form></div>;
}
