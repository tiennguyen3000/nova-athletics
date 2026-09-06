import { ProductGrid } from "@/components/product/ProductGrid";
import { Filters } from "@/components/product/Filters";
export const dynamic="force-dynamic";
async function fetchProducts(sp: Record<string,string>){
  const qs=new URLSearchParams(sp).toString();
  try{ const r=await fetch(`${process.env.NEXT_PUBLIC_API_URL||"http://localhost:8080"}/api/v1/products?${qs}`, {cache:"no-store"}); const j=await r.json(); return {content:j.data?.content||j.data||[], total:j.data?.totalElements||0}; }catch{ return {content:[], total:0}; }
}
export default async function Page({searchParams}:{searchParams:Record<string,string>}){
  const gender = "";
  const sport = "TRAINING";
  const merged:any={...searchParams};
  if(gender) merged.gender=gender;
  if(sport) merged.sport=sport;
  const {content}=await fetchProducts(merged);
  return <div className="max-w-7xl mx-auto px-6 py-8 grid lg:grid-cols-[240px_1fr] gap-8">
    <aside className="hidden lg:block"><Filters/></aside>
    <div><h1 className="text-2xl font-bold">TRAINING</h1><p className="text-sm text-neutral-500">{content.length} san pham</p><div className="mt-6"><ProductGrid products={content}/></div></div>
  </div>;
}
