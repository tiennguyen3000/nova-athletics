"use client";
import { useRouter, useSearchParams } from "next/navigation";
export function Filters(){
  const router=useRouter(); const sp=useSearchParams();
  function set(k:string,v:string){
    const p=new URLSearchParams(sp.toString());
    if(v) p.set(k,v); else p.delete(k);
    p.delete("page");
    router.push("?"+p.toString());
  }
  const genders=[["MEN","Nam"],["WOMEN","Nu"],["KIDS","Tre em"],["UNISEX","Unisex"]] as const;
  return (
    <div className="space-y-6">
      <div><p className="font-semibold text-sm mb-2">Gioi tinh</p>{genders.map(([val,label])=><label key={val} className="flex items-center gap-2 text-sm py-1"><input type="radio" name="gender" checked={sp.get("gender")===val} onChange={()=>set("gender", sp.get("gender")===val?"":val)}/>{label}</label>)}</div>
      <div><p className="font-semibold text-sm mb-2">Gia</p>
        <div className="flex gap-2"><input placeholder="Tu" className="border rounded px-2 py-1 w-24 text-sm" defaultValue={sp.get("minPrice")||""} onBlur={e=>set("minPrice", e.target.value)} /><input placeholder="Den" className="border rounded px-2 py-1 w-24 text-sm" defaultValue={sp.get("maxPrice")||""} onBlur={e=>set("maxPrice", e.target.value)}/></div>
      </div>
      <div><p className="font-semibold text-sm mb-2">Sap xep</p><select className="border rounded px-2 py-1 text-sm w-full" value={sp.get("sort")||""} onChange={e=>set("sort", e.target.value)}><option value="">Mac dinh</option><option value="price,asc">Gia tang dan</option><option value="price,desc">Gia giam dan</option><option value="createdAt,desc">Moi nhat</option></select></div>
    </div>
  );
}
