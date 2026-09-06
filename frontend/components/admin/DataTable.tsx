"use client";
export function DataTable({columns, data, loading}:{columns:{key:string;header:string; render?:(v:any,row:any)=>any}[]; data:any[]; loading?:boolean}){
  if(loading) return <div className="py-8 text-center text-sm text-neutral-500">Dang tai...</div>;
  if(!data?.length) return <div className="py-12 text-center border rounded"><p className="text-sm text-neutral-500">Khong co du lieu</p></div>;
  return <div className="border rounded overflow-hidden">
    <div className="overflow-x-auto"><table className="w-full text-sm">
      <thead className="bg-neutral-50"><tr>{columns.map(c=><th key={c.key} className="text-left px-3 py-2 font-medium whitespace-nowrap">{c.header}</th>)}</tr></thead>
      <tbody>{data.map((row,i)=><tr key={i} className="border-t hover:bg-neutral-50">{columns.map(c=><td key={c.key} className="px-3 py-2">{c.render?c.render(row[c.key],row):String(row[c.key]??"")}</td>)}</tr>)}</tbody>
    </table></div>
  </div>;
}
