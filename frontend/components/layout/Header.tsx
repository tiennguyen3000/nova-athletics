"use client";
import Link from "next/link";
import { useState } from "react";
import { Search, User, Heart, ShoppingBag, Menu, X } from "lucide-react";
import { useCart } from "@/store/cart";
import { useAuth } from "@/store/auth";
export function Header(){
  const [open,setOpen]=useState(false);
  const [mega,setMega]=useState<string|null>(null);
  const cart = useCart(); const auth=useAuth();
  return (
  <header className="sticky top-0 z-50 bg-white border-b">
    <div className="hidden md:flex h-9 items-center justify-end gap-4 px-6 text-xs bg-neutral-100">
      <Link href="/help">Tro giup</Link><span className="opacity-30">|</span><Link href={auth.isAuth?"/account":"/login"}>{auth.isAuth?"Tai khoan":"Dang nhap"}</Link>
    </div>
    <div className="flex h-16 items-center justify-between px-4 lg:px-8">
      <div className="flex items-center gap-6">
        <button className="md:hidden" onClick={()=>setOpen(!open)}>{open?<X size={24}/>:<Menu size={24}/>}</button>
        <Link href="/" className="font-black text-xl tracking-tighter">NOVA<span className="font-light"> ATHLETICS</span></Link>
        <nav className="hidden lg:flex items-center gap-6 text-sm font-medium">
          {["MEN","WOMEN","KIDS"].map(l=> (
            <div key={l} className="relative" onMouseEnter={()=>setMega(l)} onMouseLeave={()=>setMega(null)}>
              <Link href={"/"+l.toLowerCase()} className="py-6">{l}</Link>
              {mega===l && (
                <div className="absolute left-0 top-full bg-white border shadow-xl p-6 w-[640px] grid grid-cols-3 gap-6">
                  <div><p className="font-semibold mb-2">Noi bat</p><Link href="/new-arrivals" className="block text-sm py-1 hover:underline">Hang moi ve</Link><Link href="/sale" className="block text-sm py-1 hover:underline">Sale</Link></div>
                  <div><p className="font-semibold mb-2">Mon the thao</p><Link href="/running" className="block text-sm py-1">Running</Link><Link href="/basketball" className="block text-sm py-1">Basketball</Link><Link href="/training" className="block text-sm py-1">Training</Link></div>
                  <div><p className="font-semibold mb-2">Bo suu tap</p><Link href="/collections/new-arrivals" className="block text-sm py-1">New Arrivals</Link></div>
                </div>
              )}
            </div>
          ))}
          <Link href="/collections/new-arrivals">New</Link><Link href="/sale">Sale</Link><Link href="/lifestyle">Lifestyle</Link>
        </nav>
      </div>
      <div className="flex items-center gap-2">
        <Link href="/search" className="hidden md:flex items-center gap-2 bg-neutral-100 rounded-full px-4 py-2 text-sm"><Search size={16}/> Tim kiem</Link>
        <Link href="/search" className="md:hidden p-2"><Search size={20}/></Link>
        <Link href="/account/wishlist" className="p-2 hidden md:block"><Heart size={20}/></Link>
        <Link href="/cart" className="p-2 relative"><ShoppingBag size={20}/>{cart.count>0 && <span className="absolute -top-1 -right-1 bg-red-600 text-white text-[10px] rounded-full h-4 w-4 grid place-items-center">{cart.count}</span>}</Link>
        <Link href={auth.isAuth?"/account":"/login"} className="hidden md:block p-2"><User size={20}/></Link>
      </div>
    </div>
    {open && (
      <div className="lg:hidden border-t bg-white p-4 space-y-3">
        <Link href="/men" className="block font-medium">MEN</Link><Link href="/women" className="block">WOMEN</Link><Link href="/kids" className="block">KIDS</Link>
        <Link href="/running" className="block">Running</Link><Link href="/basketball" className="block">Basketball</Link><Link href="/training" className="block">Training</Link>
        <Link href="/sale" className="block text-red-600">Sale</Link>
      </div>
    )}
  </header>
  );
}
