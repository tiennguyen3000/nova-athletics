import { cn } from "@/lib/utils";
export function Card({className, ...props}:any){ return <div className={cn("rounded-xl border bg-white shadow-sm", className)} {...props} />; }
export function CardContent({className, ...props}:any){ return <div className={cn("p-4", className)} {...props} />; }
