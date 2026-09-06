import { ProductGrid } from "@/components/product/ProductGrid";
export default async function CollectionPage({params}:{params:{slug:string}}){
  let products:any[]=[];
  try{ const r=await fetch(`${process.env.NEXT_PUBLIC_API_URL||"http://localhost:8080"}/api/v1/collections/${params.slug}`, {cache:"no-store"}); const j=await r.json(); products=j.data?.products||j.data||[]; }catch{}
  return <div className="max-w-7xl mx-auto px-6 py-8"><h1 className="text-2xl font-bold capitalize">{params.slug.replace(/-/g," ")}</h1><div className="mt-6"><ProductGrid products={products}/></div></div>;
}
