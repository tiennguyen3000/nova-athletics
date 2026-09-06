"use client";
import Link from "next/link";
import { Heart } from "lucide-react";
import { formatVND } from "@/lib/utils";
import { Button } from "@/components/ui/button";
export function ProductCard({product}:{product:any}){
  const img = product.images?.[0]?.url || product.imageUrl || "https://picsum.photos/seed/"+(product.id||1)+"/600/600";
  const price = product.salePrice ?? product.basePrice;
  const hasSale = product.salePrice && product.salePrice < product.basePrice;
  return (
    <div className="group">
      <Link href={"/products/"+product.slug} className="block bg-neutral-100 aspect-square overflow-hidden relative">
        {/* eslint-disable-next-line @next/next/no-img-element */}
        <img src={img} alt={product.name} className="h-full w-full object-cover group-hover:scale-105 transition" loading="lazy" />
        {hasSale && <span className="absolute left-2 top-2 bg-red-600 text-white text-xs px-2 py-1 rounded-full">SALE</span>}
        <button className="absolute right-2 top-2 bg-white rounded-full p-2 shadow opacity-0 group-hover:opacity-100 transition"><Heart size={16}/></button>
      </Link>
      <div className="pt-3">
        <Link href={"/products/"+product.slug} className="font-medium text-sm line-clamp-1">{product.name}</Link>
        <p className="text-xs text-neutral-500">{product.subtitle || product.sport || "Lifestyle"}</p>
        <div className="flex items-center gap-2 mt-1">
          <span className="font-semibold text-sm">{formatVND(price)}</span>
          {hasSale && <span className="text-xs line-through text-neutral-400">{formatVND(product.basePrice)}</span>}
        </div>
        <div className="flex gap-1 mt-2">{(product.variants||[]).slice(0,4).map((v:any)=><span key={v.id} className="h-4 w-4 rounded-full border" style={{background:v.colorHex||"#ddd"}} title={v.color}/> )}</div>
      </div>
    </div>
  );
}
