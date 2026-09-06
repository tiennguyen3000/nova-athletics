export const API_URL = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";
type Opts = RequestInit & { auth?: boolean };
function getToken(){ if(typeof window==="undefined") return null; return localStorage.getItem("accessToken"); }
export async function apiFetch(path: string, opts: Opts={}){
  const headers: Record<string,string> = { "Content-Type":"application/json", ...(opts.headers as any)||{} };
  if(opts.auth!==false){ const t=getToken(); if(t) headers["Authorization"]="Bearer "+t; }
  const idem = (opts.headers as any)?.["Idempotency-Key"];
  const res = await fetch(`${API_URL}${path}`, { ...opts, headers, cache: "no-store" });
  const text = await res.text();
  let data: any=null; try{ data=text?JSON.parse(text):null; }catch{ data=text; }
  if(!res.ok){
    const msg = data?.error?.message || data?.message || res.statusText;
    const err: any = new Error(msg); err.status=res.status; err.data=data; throw err;
  }
  return data;
}
export async function apiGet(path:string, auth=true){ return apiFetch(path,{auth}); }
export async function apiPost(path:string, body:any, extra:Opts={}){ return apiFetch(path,{method:"POST", body:JSON.stringify(body), ...extra}); }
