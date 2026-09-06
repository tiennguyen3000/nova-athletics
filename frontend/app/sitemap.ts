import type { MetadataRoute } from "next";
export default async function sitemap(): Promise<MetadataRoute.Sitemap>{
  const base = "https://nova-athletics.local";
  let products:any[]=[];
  try{ const r=await fetch(`${process.env.NEXT_PUBLIC_API_URL||"http://localhost:8080"}/api/v1/products?size=100`, {next:{revalidate:3600}}); const j=await r.json(); products=j.data?.content||[]; }catch{}
  return [
    {url: base, lastModified: new Date()},
    {url: base+"/men"}, {url: base+"/women"},
    ...products.map((p:any)=>({url: base+"/products/"+p.slug, lastModified: new Date(p.updatedAt||Date.now())}))
  ];
}
