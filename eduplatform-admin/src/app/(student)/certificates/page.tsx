import { createServerSupabase } from "@/lib/supabase/server"
import { Trophy, ArrowRight, Download, Calendar, ExternalLink, Award } from "lucide-react"
import { Card, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import Link from "next/link"

export const dynamic = "force-dynamic"

export default async function CertificatesPage() {
  const supabase = await createServerSupabase()
  const { data: { session } } = await supabase.auth.getSession()

  if (!session) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] text-center space-y-4">
        <div className="w-20 h-20 bg-slate-50 rounded-full flex items-center justify-center">
          <Trophy size={40} className="text-slate-200" />
        </div>
        <h3 className="text-xl font-bold text-slate-900">Giriş Yapmalısınız</h3>
        <p className="text-slate-500 max-w-xs mx-auto text-sm">Sertifikalarınızı görmek için lütfen hesabınıza giriş yapın.</p>
        <Button asChild className="rounded-2xl h-12 px-8 font-bold mt-4">
           <Link href="/login">Giriş Yap</Link>
        </Button>
      </div>
    )
  }

  const { data: certificates } = await supabase
    .from("certificates")
    .select("*, courses(title, category)")
    .eq("profile_id", session?.user.id)
    .order("created_at", { ascending: false })

  return (
    <div className="space-y-10 pb-20">
      <div className="space-y-2">
        <h1 className="text-3xl font-extrabold tracking-tight text-slate-900 flex items-center gap-3">
             <Trophy className="text-amber-500" size={32} /> Sertifikalarım
        </h1>
        <p className="text-slate-500 mt-1">Tamamladığınız eğitimlerden kazandığınız başarı belgeleri.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8">
        {certificates?.map((cert: any) => (
          <Card key={cert.id} className="border-none bg-white rounded-[2.5rem] overflow-hidden card-hover group shadow-sm flex flex-col">
            <div className="relative aspect-[1.414/1] bg-slate-100 flex items-center justify-center p-8 overflow-hidden">
                {/* Certificate Preview Mockup */}
                <div className="absolute inset-0 bg-gradient-to-br from-amber-50 to-indigo-50/30 opacity-50"></div>
                <div className="relative w-full h-full border-8 border-double border-amber-200/50 rounded-lg flex flex-col items-center justify-center text-center p-4 bg-white/80 backdrop-blur-sm shadow-xl">
                    <Award size={48} className="text-amber-500 mb-2 drop-shadow-md" />
                    <div className="text-[10px] uppercase tracking-tighter font-extrabold text-slate-400">Başarı Sertifikası</div>
                    <div className="text-xs font-black text-slate-800 line-clamp-2 mt-1 px-4">{cert.courses?.title}</div>
                    <div className="mt-4 w-1/2 h-0.5 bg-amber-200/50"></div>
                </div>
                
                {/* Hover Action Overlay */}
                <div className="absolute inset-0 bg-indigo-600/90 backdrop-blur-sm opacity-0 group-hover:opacity-100 transition-opacity duration-300 flex flex-col items-center justify-center gap-4 text-white">
                    <Button variant="outline" className="rounded-xl border-white/20 hover:bg-white/10 text-white font-bold h-12 px-6 gap-2">
                        <Download size={18} /> İndir (PDF)
                    </Button>
                    <Button variant="ghost" className="text-white/60 hover:text-white font-bold text-xs gap-1">
                        <ExternalLink size={14} /> Doğrulama Linki
                    </Button>
                </div>
            </div>
            
            <CardContent className="p-6">
               <div className="flex justify-between items-start mb-4">
                    <Badge className="bg-amber-50 text-amber-600 border-none font-bold py-1 px-3 rounded-lg shadow-inner">
                        Onaylandı
                    </Badge>
                    <div className="flex items-center gap-1.5 text-xs text-slate-400 font-bold uppercase tracking-widest">
                        <Calendar size={14} /> {new Date(cert.created_at).toLocaleDateString("tr-TR")}
                    </div>
               </div>
               <h3 className="text-lg font-bold text-slate-900 mb-6 group-hover:text-primary transition-colors">
                 {cert.courses?.title}
               </h3>
               
               <div className="flex items-center justify-between pt-4 border-t border-slate-50">
                    <div className="text-[10px] text-slate-400 font-extrabold uppercase tracking-widest">Sertifika ID: {cert.certificate_number || "EDU-123-X"}</div>
                    <div className="w-8 h-8 rounded-full bg-slate-50 text-slate-300 flex items-center justify-center group-hover:bg-primary group-hover:text-white transition-all transform group-hover:rotate-45">
                        <ArrowRight size={18} />
                    </div>
               </div>
            </CardContent>
          </Card>
        ))}

        {(!certificates || certificates.length === 0) && (
            <div className="md:col-span-2 lg:col-span-3 py-24 bg-white rounded-[3rem] text-center space-y-4 border border-slate-100 border-dashed">
                <div className="w-20 h-20 bg-amber-50 rounded-full flex items-center justify-center mx-auto">
                    <Trophy size={40} className="text-amber-200" />
                </div>
                <div className="max-w-xs mx-auto">
                    <h3 className="text-xl font-bold text-slate-900">Henüz Sertifikanız Yok</h3>
                    <p className="text-slate-500 text-sm mt-2">Kursları tamamlayarak başarı belgelerinizi burada toplayabilirsiniz.</p>
                    <Button asChild className="mt-6 rounded-2xl btn-primary h-12 px-8 font-bold">
                        <Link href="/home">Eğitimlere Göz At</Link>
                    </Button>
                </div>
            </div>
        )}
      </div>
    </div>
  )
}
