import Link from "next/link";
export function Footer(){
  return <footer className="bg-black text-neutral-400 text-sm">
    <div className="max-w-7xl mx-auto px-6 py-12 grid md:grid-cols-4 gap-8">
      <div><p className="text-white font-black">NOVA ATHLETICS</p><p className="mt-3 max-w-xs">Hieu suat cao. Thiet ke toi gian. Ben vung.</p></div>
      <div><p className="text-white font-semibold mb-3">Ho tro</p><Link href="/help" className="block py-1">Giao hang</Link><Link href="/help" className="block py-1">Doi tra</Link></div>
      <div><p className="text-white font-semibold mb-3">Ve NOVA</p><Link href="/about" className="block py-1">Cau chuyen</Link></div>
      <div><p className="text-white font-semibold mb-3">Nhan tin</p><p className="text-xs">Dang ky de nhan uu dai 10%</p><div className="mt-2 flex"><input placeholder="Email" className="rounded-l-full px-4 py-2 text-black w-full"/><button className="bg-white text-black rounded-r-full px-4">Gui</button></div></div>
    </div>
    <div className="border-t border-neutral-800 text-center py-4 text-xs">© 2026 NOVA ATHLETICS. Bao luu moi quyen.</div>
  </footer>;
}
