import { notFound } from "next/navigation";
import { Button } from "@/components/ui/button";
export async function generateMetadata({params}:{params:{slug:string}}){
  try{ const r=await fetch(`${process.env.NEXT_PUBLIC_API_URL||"http://localhost:8080"}/api/v1/products/${params.slug}`, {next:{revalidate:60}}); const j=await r.json(); const p=j.data; return {title: p.name+" — NOVA ATHLETICS", description: p.subtitle || p.description?.slice(0,150)}; }catch{ return {title:"NOVA ATHLETICS"}; }
}
async function getProduct(slug:string){
  const r=await fetch(`${process.env.NEXT_PUBLIC_API_URL||"http://localhost:8080"}/api/v1/products/${slug}`, {next:{revalidate:60}});
  if(!r.ok) return null; const j=await r.json(); return j.data;
}
async function getReviews(id:number){
  try{ const r=await fetch(`${process.env.NEXT_PUBLIC_API_URL||"http://localhost:8080"}/api/v1/products/${id}/reviews`, {next:{revalidate:60}}); const j=await r.json(); return j.data?.content||[]; }catch{ return []; }
}
export default async function PDP({params}:{params:{slug:string}}){
  const product = await getProduct(params.slug);
  if(!product) return notFound();
  const reviews = await getReviews(product.id);
  const img = product.images?.[0]?.url || "https://picsum.photos/seed/"+product.id+"/800/800";
  return (
    <div className="max-w-7xl mx-auto px-6 py-8 grid lg:grid-cols-2 gap-8">
      <div className="bg-neutral-100 aspect-square grid place-items-center overflow-hidden">
        {/* eslint-disable-next-line @next/next/no-img-element */}
        <img src={img} alt={product.name} className="h-full w-full object-cover" />
      </div>
      <div>
        <p className="text-xs tracking-widest">{product.sport} — {product.gender}</p>
        <h1 className="text-3xl font-bold mt-2">{product.name}</h1>
        <p className="text-neutral-500">{product.subtitle}</p>
        <div className="mt-4 flex items-baseline gap-3">
          <span className="text-xl font-bold">{new Intl.NumberFormat("vi-VN",{style:"currency",currency:"VND"}).format(product.salePrice||product.basePrice)}</span>
          {product.salePrice && <span className="line-through text-sm text-neutral-400">{new Intl.NumberFormat("vi-VN",{style:"currency",currency:"VND"}).format(product.basePrice)}</span>}
        </div>
        <div className="mt-6 space-y-3">
          <p className="text-sm font-medium">Mau: {(product.variants||[]).map((v:any)=>v.color).join(", ") || "—"}</p>
          <p className="text-sm font-medium">Size: {(product.variants||[]).map((v:any)=>v.size).join(", ") || "—"}</p>
          <div className="flex gap-2">
            <a href="/cart"><Button className="flex-1">Them vao gio hang</Button></a>
            <Button variant="outline">Yeu thich</Button>
          </div>
          <p className="text-xs text-neutral-500">Mien phi van chuyen 30 ngay doi tra. Ton kho hien thi theo thoi gian thuc.</p>
        </div>
        <div className="mt-8 border-t pt-6">
          <h3 className="font-semibold">Mo ta</h3><p className="text-sm text-neutral-600 mt-2">{product.description}</p>
        </div>
        <div className="mt-8 border-t pt-6">
          <h3 className="font-semibold">Danh gia ({reviews.length})</h3>
          <div className="mt-3 space-y-3">{reviews.slice(0,5).map((r:any)=><div key={r.id} className="border rounded p-3 text-sm"><p className="font-medium">{r.title} — {r.rating}★</p><p className="text-neutral-600">{r.comment}</p></div>)}{reviews.length===0 && <p className="text-sm text-neutral-500">Chua co danh gia</p>}</div>
        </div>
      </div>
      <div className="lg:hidden fixed bottom-0 left-0 right-0 bg-white border-t p-3 flex gap-2"><Button className="flex-1">Them vao gio — {new Intl.NumberFormat("vi-VN",{style:"currency",currency:"VND"}).format(product.salePrice||product.basePrice)}</Button></div>
    </div>
  );
}
