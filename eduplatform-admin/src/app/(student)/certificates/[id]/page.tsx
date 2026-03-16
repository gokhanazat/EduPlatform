"use client"

import { useState, useEffect, useRef } from "react"
import { useParams, useRouter } from "next/navigation"
import { createBrowserClient } from "@supabase/ssr"
import { 
  Download, 
  Share2, 
  ChevronLeft, 
  Printer,
  ShieldCheck,
  Award,
  Calendar,
  User,
  Hash
} from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import Link from "next/link"

export default function CertificatePage() {
  const { id } = useParams()
  const router = useRouter()
  const certificateRef = useRef<HTMLDivElement>(null)
  
  const [cert, setCert] = useState<any>(null)
  const [loading, setLoading] = useState(true)

  const supabase = createBrowserClient(
    process.env.NEXT_PUBLIC_SUPABASE_URL!,
    process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!
  )

  useEffect(() => {
    async function loadCertificate() {
      const { data } = await supabase
        .from("certificates")
        .select("*, courses(title, instructor_name), profiles(full_name)")
        .eq("id", id)
        .single()
      
      setCert(data)
      setLoading(false)
    }
    loadCertificate()
  }, [id])

  const handlePrint = () => {
    window.print()
  }

  if (loading) return <div className="min-h-screen flex items-center justify-center font-black animate-pulse text-primary tracking-widest uppercase">Belge Hazırlanıyor...</div>

  if (!cert) return (
    <div className="min-h-screen flex flex-col items-center justify-center p-6 bg-slate-50">
        <h1 className="text-2xl font-black text-slate-900">Sertifika Bulunamadı</h1>
        <Button onClick={() => router.back()} className="mt-4 rounded-2xl h-12 px-8 btn-primary font-bold">Geri Dön</Button>
    </div>
  )

  return (
    <div className="min-h-screen bg-slate-100 flex flex-col lg:flex-row gap-8 p-6 lg:p-12">
        {/* Sidebar Controls (Hidden on Print) */}
        <div className="lg:w-80 flex flex-col gap-6 print:hidden">
            <Link href="/profile" className="flex items-center gap-2 text-slate-500 font-bold hover:text-slate-900 transition-colors">
                <ChevronLeft size={20} /> Profile Dön
            </Link>

            <div className="space-y-2">
                <h1 className="text-3xl font-black text-slate-900 leading-tight">Sertifikanız Hazır!</h1>
                <p className="text-slate-500 font-medium">Bu belge başarınızın resmi kanıtıdır. İndirebilir veya yazdırabilirsiniz.</p>
            </div>

            <Card className="border-none shadow-sm rounded-3xl bg-white p-6 space-y-6">
                <div className="space-y-4">
                    <div className="flex items-center gap-3 text-slate-600">
                        <User size={18} className="text-primary" />
                        <div className="flex flex-col">
                            <span className="text-[10px] font-black uppercase tracking-widest text-slate-400">Öğrenci</span>
                            <span className="font-bold text-sm tracking-tight">{cert.profiles?.full_name}</span>
                        </div>
                    </div>
                    <div className="flex items-center gap-3 text-slate-600">
                        <Award size={18} className="text-amber-500" />
                        <div className="flex flex-col">
                            <span className="text-[10px] font-black uppercase tracking-widest text-slate-400">Eğitim</span>
                            <span className="font-bold text-sm tracking-tight">{cert.courses?.title}</span>
                        </div>
                    </div>
                    <div className="flex items-center gap-3 text-slate-600">
                        <Calendar size={18} className="text-emerald-500" />
                        <div className="flex flex-col">
                            <span className="text-[10px] font-black uppercase tracking-widest text-slate-400">Veriliş Tarihi</span>
                            <span className="font-bold text-sm tracking-tight">{new Date(cert.created_at).toLocaleDateString('tr-TR', { day:'numeric', month:'long', year:'numeric' })}</span>
                        </div>
                    </div>
                    <div className="flex items-center gap-3 text-slate-600">
                        <Hash size={18} className="text-indigo-500" />
                        <div className="flex flex-col">
                            <span className="text-[10px] font-black uppercase tracking-widest text-slate-400">Belge No</span>
                            <span className="font-bold text-sm tracking-tight uppercase">{cert.certificate_number}</span>
                        </div>
                    </div>
                </div>

                <div className="pt-4 flex flex-col gap-3">
                    <Button onClick={handlePrint} className="w-full h-14 rounded-2xl btn-primary font-black text-lg shadow-xl shadow-indigo-100 gap-2">
                        <Printer size={20} /> Yazdır / PDF
                    </Button>
                    <Button variant="outline" className="w-full h-14 rounded-2xl border-slate-200 font-bold gap-2 hover:bg-slate-50">
                        <Share2 size={18} /> Paylaş
                    </Button>
                </div>
            </Card>

            <div className="flex items-center gap-2 bg-emerald-50 text-emerald-600 p-4 rounded-2xl border border-emerald-100">
                <ShieldCheck size={20} className="shrink-0" />
                <p className="text-xs font-bold uppercase tracking-tight">Doğrulanmış Orijinal Belge</p>
            </div>
        </div>

        {/* Certificate Display Area */}
        <div className="flex-1 flex items-start justify-center overflow-x-auto pb-12">
            <div 
                ref={certificateRef}
                className="certificate-container w-[800px] aspect-[1.414/1] bg-white shadow-2xl rounded-sm relative overflow-hidden flex flex-col items-center justify-center p-20 text-center border-[20px] border-double border-slate-100 print:shadow-none print:border-none print:m-0 print:w-full print:h-screen"
                style={{ fontFamily: "'Inter', sans-serif" }}
            >
                {/* Background Decor */}
                <div className="absolute top-0 left-0 w-64 h-64 bg-primary/5 rounded-br-full -translate-x-32 -translate-y-32"></div>
                <div className="absolute bottom-0 right-0 w-64 h-64 bg-amber-500/5 rounded-tl-full translate-x-32 translate-y-32"></div>
                
                {/* Content */}
                <div className="border-2 border-slate-100 w-full h-full flex flex-col items-center justify-between p-12 relative">
                    <div className="space-y-6">
                        <div className="flex items-center justify-center gap-3">
                            <div className="w-12 h-12 bg-slate-900 text-white rounded-xl flex items-center justify-center text-xl font-black italic">I</div>
                            <span className="text-2xl font-black text-slate-900 tracking-tighter uppercase italic">ITSO Akademi</span>
                        </div>
                        <h2 className="text-slate-400 font-black uppercase tracking-[0.3em] text-[10px]">Başarı Sertifikası</h2>
                    </div>

                    <div className="space-y-4">
                        <p className="text-slate-500 font-medium">Sayın</p>
                        <h1 className="text-5xl font-black text-slate-900 tracking-tight underline decoration-primary decoration-4 underline-offset-8">
                            {cert.profiles?.full_name}
                        </h1>
                    </div>

                    <div className="space-y-6 max-w-md mx-auto">
                        <p className="text-slate-600 leading-relaxed font-medium">
                            <strong className="text-slate-900">{cert.courses?.title}</strong> isimli profesyonel gelişim eğitimini başarıyla tamamlayarak bu sertifikayı almaya hak kazanmıştır.
                        </p>
                    </div>

                    <div className="flex justify-between w-full pt-12 items-end">
                        <div className="text-left space-y-1">
                            <div className="text-[10px] font-black uppercase text-slate-400 tracking-widest">Tarih</div>
                            <div className="font-bold text-slate-900 border-b-2 border-slate-100 pb-1">{new Date(cert.created_at).toLocaleDateString('tr-TR')}</div>
                        </div>

                        <div className="space-y-4">
                             <div className="w-40 h-16 border-b-2 border-slate-900 flex items-center justify-center italic text-xl font-black text-slate-800 opacity-20">
                                Signature
                             </div>
                             <div className="text-center">
                                <span className="text-[10px] font-black uppercase text-slate-400 tracking-widest">Eğitmen</span>
                                <div className="font-bold text-slate-900">{cert.courses?.instructor_name || "ITSO Akademi"}</div>
                             </div>
                        </div>

                        <div className="text-right space-y-1">
                            <div className="text-[10px] font-black uppercase text-slate-400 tracking-widest">Sertifika No</div>
                            <div className="font-bold text-slate-900 border-b-2 border-slate-100 pb-1 uppercase">{cert.certificate_number}</div>
                        </div>
                    </div>

                    {/* Trust Seal */}
                    <div className="absolute bottom-12 right-12 opacity-10">
                         <div className="w-24 h-24 border-4 border-slate-900 rounded-full flex items-center justify-center rotate-12">
                            <span className="font-black text-[10px] uppercase text-center leading-tight">OFFICIAL<br/>CERTIFIED<br/>ITSO</span>
                         </div>
                    </div>
                </div>
            </div>
        </div>

        <style jsx global>{`
          @media print {
            body * {
              visibility: hidden;
            }
            .certificate-container, .certificate-container * {
              visibility: visible;
            }
            .certificate-container {
              position: absolute;
              left: 0;
              top: 0;
              width: 100% !important;
              height: 100% !important;
              box-shadow: none !important;
              border: none !important;
            }
            @page {
              size: landscape;
              margin: 0;
            }
          }
        `}</style>
    </div>
  )
}
