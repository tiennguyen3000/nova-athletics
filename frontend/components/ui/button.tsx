import { cn } from "@/lib/utils";
export function Button({className, variant="default", size="default", ...props}: any){
  const base="inline-flex items-center justify-center rounded-full font-medium transition-colors focus-visible:outline-none disabled:opacity-50";
  const variants:Record<string,string>={ default:"bg-black text-white hover:bg-neutral-800", outline:"border border-black bg-white hover:bg-neutral-100", ghost:"hover:bg-neutral-100" };
  const sizes:Record<string,string>={ default:"h-10 px-6 py-2", sm:"h-8 px-4 text-sm", lg:"h-12 px-8", icon:"h-10 w-10" };
  return <button className={cn(base, variants[variant]||variants.default, sizes[size]||sizes.default, className)} {...props} />;
}
