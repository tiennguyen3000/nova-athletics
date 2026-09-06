"use client";
import { useState } from "react";
import { ProductGrid } from "@/components/product/ProductGrid";
import { Input } from "@/components/ui/input";
import { apiGet } from "@/lib/api";
export default function SearchPage(){
  const [q,setQ]=useState(""); const [results,setResults]=useState<any[]>([]); const [loading,setLoading]=useState(false);
  async function doSearch(){
    setLoading(true);
    try{ const j=await apiGet(`/api/v1/search?q=${encodeURIComponent(q)}`, false); setResults(j.data?.content||j.data||[]); }catch{ setResults([]); }
    setLoading(false);
  }
  return <div className="max-w-7xl mx-auto px-6 py-8">
    <h1 className="text-2xl font-bold">Tim kiem</h1>
    <div className="mt-4 flex gap-2"><Input value={q} onChange={(e:any)=>setQ(e.target.value)} placeholder="Tim san pham..." onKeyDown={(e:any)=>e.key==="Enter"&&doSearch()} /><button onClick={doSearch} className="bg-black text-white rounded-full px-6">Tim</button></div>
    <div className="mt-6">{loading?"Dang tim...": <ProductGrid products={results}/>}</div>
    {results.length===0 && q && !loading && <p className="text-center text-neutral-500 mt-6">Khong tim thay ket qua cho “{q}”</p>}
  </div>;
}
