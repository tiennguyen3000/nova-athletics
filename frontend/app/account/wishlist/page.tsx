"use client";
import { useEffect, useState } from "react";
import { apiGet } from "@/lib/api";
import { ProductGrid } from "@/components/product/ProductGrid";
export default function Wishlist(){
  const [items,setItems]=useState<any[]>([]);
  useEffect(()=>{ apiGet("/api/v1/wishlist").then(j=>setItems(j.data?.content||j.data||[])).catch(()=>{}); },[]);
  return <div><h1 className="text-xl font-bold">Yeu thich</h1><div className="mt-4"><ProductGrid products={items.map((w:any)=>w.product||w)}/>{items.length===0 && <p className="text-sm text-neutral-500">Chua co san pham yeu thich</p>}</div></div>;
}
