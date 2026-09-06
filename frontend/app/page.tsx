import Link from "next/link";
import { ProductGrid } from "@/components/product/ProductGrid";
import { Button } from "@/components/ui/button";
async function getProducts(qs=""){
  try{ const r=await fetch(`${process.env.NEXT_PUBLIC_API_URL||"http://localhost:8080"}/api/v1/products?size=8&${qs}`, {next:{revalidate:60}}); const j=await r.json(); return j.data?.content || j.data || []; }catch{ return []; }
}
export default async function Home(){
  const featured = await getProducts("isFeatured=true");
  const latest = await getProducts("sort=createdAt,desc");
  return (
    <div>
      <section className="relative bg-neutral-100">
        <div className="max-w-7xl mx-auto px-6 py-16 lg:py-24 grid lg:grid-cols-2 gap-8 items-center">
          <div>
            <p className="text-xs tracking-[0.3em]">NOVA ATHLETICS — SS26</p>
            <h1 className="text-5xl lg:text-7xl font-black tracking-tighter leading-none mt-3">VUOT QUA<br/><span className="font-light">GIOI HAN</span></h1>
            <p className="mt-4 text-neutral-600 max-w-md">Bo suu tap hieu suat cao — nhe, thoang, ben vung. Duoc thiet ke cho van dong vien.</p>
            <div className="mt-6 flex gap-3"><Link href="/men"><Button>Mua cho Nam</Button></Link><Link href="/women"><Button variant="outline">Mua cho Nu</Button></Link></div>
          </div>
          <div className="bg-white aspect-[4/3] grid place-items-center text-neutral-400">Hero Editorial — thay bang video/ hinh campaign</div>
        </div>
      </section>
      <section className="max-w-7xl mx-auto px-6 py-12">
        <div className="flex items-end justify-between"><h2 className="text-2xl font-bold tracking-tight">Hang moi ve</h2><Link href="/new-arrivals" className="text-sm underline">Xem tat ca</Link></div>
        <div className="mt-6"><ProductGrid products={latest.slice(0,8)} /></div>
      </section>
      <section className="max-w-7xl mx-auto px-6 pb-6 grid md:grid-cols-3 gap-4">
        {[{t:"NAM",href:"/men"},{t:"NU",href:"/women"},{t:"TRE EM",href:"/kids"}].map(c=>(
          <Link key={c.t} href={c.href} className="bg-neutral-900 text-white aspect-[4/3] grid place-items-center text-2xl font-black tracking-widest">{c.t}</Link>
        ))}
      </section>
      <section className="max-w-7xl mx-auto px-6 py-12">
        <h2 className="text-2xl font-bold">Noi bat</h2>
        <div className="mt-6"><ProductGrid products={featured.slice(0,4)} /></div>
      </section>
      <section className="bg-black text-white">
        <div className="max-w-7xl mx-auto px-6 py-12 flex flex-col md:flex-row items-center justify-between gap-6">
          <p className="text-lg font-medium">Dang ky & nhan 10% cho don hang dau</p>
          <div className="flex gap-2"><input placeholder="Email cua ban" className="rounded-full px-4 py-2 text-black"/><Button variant="outline" className="bg-white text-black">Dang ky</Button></div>
        </div>
      </section>
    </div>
  );
}
