import { cn } from "@/lib/utils";
export function Input(props:any){ return <input className={cn("flex h-10 w-full rounded-md border border-neutral-200 bg-white px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-black", props.className)} {...props} />; }
