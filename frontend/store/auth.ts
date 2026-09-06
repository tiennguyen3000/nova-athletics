"use client";
import { create } from "zustand";
type User = { id:number; email:string; customer?:any; roles?:string[] };
type State = { user: User|null; token:string|null; setAuth:(u:User,t:string)=>void; logout:()=>void; isAuth:boolean };
export const useAuth = create<State>((set)=>({
  user: typeof window!=="undefined" ? (()=>{ try{return JSON.parse(localStorage.getItem("user")||"null")}catch{return null}})() : null,
  token: typeof window!=="undefined" ? localStorage.getItem("accessToken") : null,
  isAuth: typeof window!=="undefined" ? !!localStorage.getItem("accessToken") : false,
  setAuth:(user, token)=>{ if(typeof window!=="undefined"){ localStorage.setItem("accessToken",token); localStorage.setItem("user",JSON.stringify(user)); } set({user, token, isAuth:true}); },
  logout:()=>{ if(typeof window!=="undefined"){ localStorage.removeItem("accessToken"); localStorage.removeItem("user"); } set({user:null, token:null, isAuth:false}); }
}));
