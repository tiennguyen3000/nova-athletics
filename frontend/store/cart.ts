"use client";
import { create } from "zustand";
import { apiGet, apiPost } from "@/lib/api";
type CartItem = { id:number; variantId:number; quantity:number; product?:any; variant?:any };
type State = { items: CartItem[]; count:number; subtotal:number; fetch:()=>Promise<void>; add:(variantId:number, qty:number)=>Promise<void>; update:(itemId:number, qty:number)=>Promise<void>; remove:(itemId:number)=>Promise<void> };
export const useCart = create<State>((set, get)=>({
  items:[], count:0, subtotal:0,
  fetch: async()=>{
    try{
      const token = typeof window!=="undefined"?localStorage.getItem("accessToken"):null;
      const cart = await apiGet("/api/v1/cart", !!token);
      const data = cart.data || cart;
      set({ items: data.items||[], count: data.items?.length||0, subtotal: data.subtotal||0 });
    }catch{ /* guest cart empty */ }
  },
  add: async(variantId, qty)=>{
    // optimistic then sync
    await apiPost("/api/v1/cart/items", {variantId, quantity:qty}, {auth:true});
    await get().fetch();
  },
  update: async(itemId, qty)=>{
    await fetch(`${process.env.NEXT_PUBLIC_API_URL||"http://localhost:8080"}/api/v1/cart/items/${itemId}`, {method:"PATCH", headers:{"Content-Type":"application/json", Authorization:"Bearer "+(localStorage.getItem("accessToken")||"")}, body: JSON.stringify({quantity:qty})});
    await get().fetch();
  },
  remove: async(itemId)=>{
    await fetch(`${process.env.NEXT_PUBLIC_API_URL||"http://localhost:8080"}/api/v1/cart/items/${itemId}`, {method:"DELETE", headers:{Authorization:"Bearer "+(localStorage.getItem("accessToken")||"")}});
    await get().fetch();
  }
}));
