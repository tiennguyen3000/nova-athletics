"use client";
import { useEffect } from "react";
import { useCart } from "@/store/cart";
import { Button } from "@/components/ui/button";
import Link from "next/link";
import { formatVND } from "@/lib/utils";
export default function CartPage(){
  const cart=useCart();
  useEffect(()=>{ cart.fetch(); },[]);
  return <div className="max-w-5xl mx-auto px-6 py-8">
    <h1 className="text-2xl font-bold">Gio hang ({cart.count})</h1>
    <div className="mt-6 grid lg:grid-cols-[1fr_360px] gap-8">
      <div className="space-y-4">
        {cart.items.length===0 && <p className="text-neutral-500">Gio hang trong. <Link href="/men" className="underline">Tiep tuc mua sam</Link></p>}
        {cart.items.map((it:any)=><div key={it.id} className="flex gap-4 border rounded p-4">
          <div className="h-20 w-20 bg-neutral-100"/><div className="flex-1"><p className="font-medium text-sm">Variant #{it.variantId}</p><p className="text-xs text-neutral-500">So luong: {it.quantity}</p></div>
          <button onClick={()=>cart.remove(it.id)} className="text-sm underline">Xoa</button>
        </div>)}
      </div>
      <div className="border rounded p-4 h-fit">
        <p className="font-semibold">Tong</p>
        <p className="text-sm text-neutral-500 mt-2">Tam tinh: {formatVND(cart.subtotal||0)}</p>
        <p className="text-xs text-neutral-400 mt-2">Gia cuoi cung do backend tinh. Phi van chuyen 30k.</p>
        <Link href="/checkout"><Button className="w-full mt-4">Thanh toan</Button></Link>
      </div>
    </div>
  </div>;
}
