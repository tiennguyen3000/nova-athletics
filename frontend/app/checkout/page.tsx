"use client";
import { useEffect, useState } from "react";
import { apiGet, apiPost } from "@/lib/api";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { useRouter } from "next/navigation";
export default function CheckoutPage(){
  const router=useRouter();
  const [addrs,setAddrs]=useState<any[]>([]); const [selected,setSelected]=useState<number|null>(null);
  const [coupon,setCoupon]=useState(""); const [placing,setPlacing]=useState(false); const [err,setErr]=useState("");
  useEffect(()=>{ apiGet("/api/v1/account/addresses").then(j=>setAddrs(j.data||[])).catch(()=>{}); },[]);
  async function place(){
    if(!selected){ setErr("Chon dia chi giao hang"); return; }
    setPlacing(true); setErr("");
    try{
      const body:any={shippingAddressId:selected, paymentMethod:"MOCK"};
      if(coupon) body.couponCode=coupon;
      const j=await apiPost("/api/v1/checkout", body, {headers:{"Idempotency-Key": crypto.randomUUID()}});
      router.push("/account/orders/"+(j.data?.order?.id||j.data?.id||""));
    }catch(e:any){ setErr(e.message||"Loi thanh toan: "+(e.data?.error?.code||"")); }
    setPlacing(false);
  }
  return <div className="max-w-3xl mx-auto px-6 py-8">
    <h1 className="text-2xl font-bold">Thanh toan</h1>
    <div className="mt-6 space-y-6">
      <div><h3 className="font-semibold">Dia chi giao hang</h3><div className="mt-2 space-y-2">{addrs.map((a:any)=><label key={a.id} className="flex gap-2 border rounded p-3"><input type="radio" checked={selected===a.id} onChange={()=>setSelected(a.id)}/><span className="text-sm">{a.recipientName} — {a.line1}, {a.city} — {a.phone}</span></label>)}{addrs.length===0 && <p className="text-sm text-neutral-500">Chua co dia chi. <a href="/account/addresses" className="underline">Them dia chi</a></p>}</div></div>
      <div><h3 className="font-semibold">Ma giam gia</h3><div className="flex gap-2 mt-2"><Input value={coupon} onChange={e=>setCoupon(e.target.value)} placeholder="NOVA10"/><Button variant="outline">Ap dung</Button></div></div>
      <div><h3 className="font-semibold">Phuong thuc thanh toan</h3><p className="text-sm mt-2 border rounded p-3">MOCK — tu dong thanh cong (cho demo). Xu ly that bai ton kho / coupon het han se bao loi.</p></div>
      {err && <p className="text-sm text-red-600 bg-red-50 border border-red-200 rounded p-2">{err}</p>}
      <Button onClick={place} disabled={placing} className="w-full">{placing?"Dang xu ly...":"Dat hang"}</Button>
    </div>
  </div>;
}
