import { ProductCard } from "./ProductCard";
export function ProductGrid({products}:{products:any[]}){
  if(!products?.length) return <div className="py-12 text-center text-neutral-500">Khong co san pham</div>;
  return <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-4">{products.map((p:any)=><ProductCard key={p.id} product={p}/>)}</div>;
}
