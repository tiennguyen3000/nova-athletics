import "./globals.css";
import { Header } from "@/components/layout/Header";
import { Footer } from "@/components/layout/Footer";
import { AnnouncementBar } from "@/components/layout/AnnouncementBar";
import { QueryProvider } from "@/lib/query";
export const metadata = { title: "NOVA ATHLETICS — Hieu suat cao", description: "Premium sportswear — NOVA ATHLETICS" };
export default function RootLayout({children}:{children:React.ReactNode}){
  return <html lang="vi"><body><QueryProvider><AnnouncementBar/><Header/><main className="min-h-[60vh]">{children}</main><Footer/></QueryProvider></body></html>;
}
