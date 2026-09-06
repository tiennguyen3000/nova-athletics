"use client";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { LayoutDashboard, Package, Layers, ShoppingBag, Users, Warehouse, Ticket, Star, UserCog, BarChart3, ScrollText, Settings, Boxes, FolderTree } from "lucide-react";
const nav = [
  {href:"/admin", label:"Dashboard", icon: LayoutDashboard},
  {href:"/admin/products", label:"Products", icon: Package},
  {href:"/admin/categories", label:"Categories", icon: FolderTree},
  {href:"/admin/collections", label:"Collections", icon: Layers},
  {href:"/admin/orders", label:"Orders", icon: ShoppingBag},
  {href:"/admin/customers", label:"Customers", icon: Users},
  {href:"/admin/inventory", label:"Inventory", icon: Boxes},
  {href:"/admin/warehouses", label:"Warehouses", icon: Warehouse},
  {href:"/admin/promotions", label:"Promotions", icon: Ticket},
  {href:"/admin/reviews", label:"Reviews", icon: Star},
  {href:"/admin/employees", label:"Employees", icon: UserCog},
  {href:"/admin/reports", label:"Reports", icon: BarChart3},
  {href:"/admin/audit-logs", label:"Audit Logs", icon: ScrollText},
  {href:"/admin/settings", label:"Settings", icon: Settings},
];
export default function AdminLayout({children}:{children:React.ReactNode}){
  const path=usePathname();
  return <div className="min-h-screen bg-neutral-50">
    <div className="flex">
      <aside className="hidden lg:block w-64 border-r bg-white min-h-screen sticky top-0">
        <div className="p-4 font-black">NOVA ADMIN</div>
        <nav className="px-2 space-y-1">
          {nav.map(n=>{
            const Icon=n.icon; const active=path===n.href || (n.href!=="/admin" && path.startsWith(n.href));
            return <Link key={n.href} href={n.href} className={"flex items-center gap-2 px-3 py-2 rounded text-sm "+(active?"bg-black text-white":"hover:bg-neutral-100")}><Icon size={16}/>{n.label}</Link>;
          })}
        </nav>
      </aside>
      <div className="flex-1">
        <div className="bg-white border-b px-6 py-3 flex items-center justify-between sticky top-0 z-10">
          <div className="text-sm text-neutral-500">Admin / {path.replace("/admin","")||" Dashboard"}</div>
          <div className="flex items-center gap-3 text-sm"><span className="h-8 w-8 rounded-full bg-black text-white grid place-items-center">A</span></div>
        </div>
        <div className="p-6">{children}</div>
      </div>
    </div>
  </div>;
}
