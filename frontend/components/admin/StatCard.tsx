export function StatCard({label, value, sub}:{label:string; value:string; sub?:string}){
  return <div className="border rounded-xl p-4 bg-white"><p className="text-xs text-neutral-500 uppercase tracking-wide">{label}</p><p className="text-2xl font-bold mt-1">{value}</p>{sub && <p className="text-xs text-neutral-400 mt-1">{sub}</p>}</div>;
}