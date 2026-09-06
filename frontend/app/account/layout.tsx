import Link from "next/link";
export default function AccountLayout({children}:{children:React.ReactNode}){
  return <div className="max-w-7xl mx-auto px-6 py-8 grid lg:grid-cols-[220px_1fr] gap-8">
    <nav className="space-y-1 text-sm">
      <Link href="/account" className="block py-2 font-semibold">Tong quan</Link>
      <Link href="/account/orders" className="block py-2">Don hang</Link>
      <Link href="/account/profile" className="block py-2">Ho so</Link>
      <Link href="/account/addresses" className="block py-2">Dia chi</Link>
      <Link href="/account/wishlist" className="block py-2">Yeu thich</Link>
      <Link href="/account/reviews" className="block py-2">Danh gia</Link>
    </nav>
    <div>{children}</div>
  </div>;
}
