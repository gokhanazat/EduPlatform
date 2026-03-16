import { createServerSupabase } from "@/lib/supabase/server"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Search, Filter, BookOpen, Clock, Trophy, Star, ChevronRight } from "lucide-react"
import Link from "next/link"
import { Suspense } from "react"
import CategoryFilter from "@/components/home/CategoryFilter"
import SearchInput from "@/components/home/SearchInput"

export const dynamic = "force-dynamic"

export default async function StudentHomePage({
  searchParams,
}: {
  searchParams: { search?: string; category?: string };
}) {
  const supabase = await createServerSupabase()
  
  const query = searchParams.search || ""
  const category = searchParams.category || "Hepsi"

  let supabaseQuery = supabase
    .from("courses")
    .select("*")
    .eq("is_published", true)

  if (query) {
    supabaseQuery = supabaseQuery.ilike("title", `%${query}%`)
  }

  if (category !== "Hepsi") {
    supabaseQuery = supabaseQuery.eq("category", category)
  }

  const { data: courses } = await supabaseQuery.order("created_at", { ascending: false })

  const categories = ["Hepsi", "Programlama", "Tasarım", "İş Geliştirme", "Liderlik", "Diğer"]

  return (
    <div className="space-y-10 pb-20">
      {/* Hero Section / Welcome */}
      <div className="relative overflow-hidden rounded-[2.5rem] bg-indigo-600 p-8 md:p-12 text-white shadow-2xl shadow-indigo-200">
        <div className="relative z-10 max-w-2xl">
            <Badge className="bg-white/20 hover:bg-white/30 text-white border-none py-1.5 px-4 rounded-full mb-6 backdrop-blur-md">
                Yeni Eğitimler Yayında! 🚀
            </Badge>
            <h1 className="text-4xl md:text-5xl font-extrabold tracking-tight mb-4">
               Geleceğinizi <span className="text-sky-300">Tasarlayın</span>,<br /> Bilginizi Geliştirin.
            </h1>
            <p className="text-indigo-100 text-lg md:text-xl font-medium mb-8">
                ITSO Akademi ile profesyonel eğitimler alın, sertifikanızı kazanın ve kariyerinizde bir adım öne geçin.
            </p>
            <div className="flex flex-wrap gap-4">
                <div className="flex -space-x-3">
                    {[1,2,3,4].map(i => (
                        <div key={i} className="w-10 h-10 rounded-full border-2 border-indigo-600 bg-indigo-100 flex items-center justify-center text-indigo-600 text-xs font-bold overflow-hidden shadow-md">
                             <img src={`https://i.pravatar.cc/40?img=${i+10}`} alt="user" className="w-full h-full object-cover" />
                        </div>
                    ))}
                    <div className="w-10 h-10 rounded-full border-2 border-indigo-600 bg-sky-400 flex items-center justify-center text-white text-[10px] font-bold shadow-md">
                        +500
                    </div>
                </div>
                <div className="text-sm font-medium flex flex-col justify-center">
                    <span className="text-white">Aktif Öğrenci</span>
                    <span className="text-sky-200">Siz de aramıza katılın</span>
                </div>
            </div>
        </div>
        
        {/* Abstract Shapes */}
        <div className="absolute top-0 right-0 w-1/2 h-full hidden lg:block overflow-hidden">
            <div className="absolute top-1/4 right-[-10%] w-[500px] h-[500px] bg-white/10 rounded-full blur-3xl"></div>
            <div className="absolute bottom-[-20%] right-[10%] w-[300px] h-[300px] bg-sky-500/30 rounded-full blur-3xl shadow-emerald-200"></div>
        </div>
      </div>

      <div className="flex flex-col lg:flex-row gap-6 items-start lg:items-center justify-between">
            <Suspense fallback={<div className="h-12 w-full lg:w-96 bg-slate-50 animate-pulse rounded-2xl" />}>
                <CategoryFilter categories={categories} currentCategory={category} />
            </Suspense>
            <div className="flex items-center gap-3 w-full lg:w-auto">
                <Suspense fallback={<div className="h-12 w-80 bg-slate-50 animate-pulse rounded-2xl" />}>
                    <SearchInput defaultValue={query} />
                </Suspense>
            </div>
      </div>

      {/* Course Grid */}
      <div>
        <div className="flex items-center justify-between mb-8">
            <h3 className="text-2xl font-extrabold text-slate-900">
                {query ? `"${query}" Sonuçları` : category !== "Hepsi" ? `${category} Eğitimleri` : "Popüler Eğitimler"}
            </h3>
            <span className="text-sm font-bold text-slate-400 uppercase tracking-widest">{courses?.length || 0} Sonuç</span>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
            {courses?.map((course) => (
                <Link key={course.id} href={`/courses/${course.id}`} className="group cursor-pointer">
                    <Card className="border-none bg-white rounded-[2rem] overflow-hidden card-hover h-full flex flex-col shadow-sm">
                        <div className="relative aspect-video overflow-hidden">
                            {course.thumbnail_url ? (
                                <img 
                                    src={course.thumbnail_url} 
                                    alt={course.title}
                                    className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500"
                                />
                            ) : (
                                <div className="w-full h-full bg-slate-100 flex items-center justify-center">
                                    <BookOpen size={48} className="text-slate-200" />
                                </div>
                            )}
                            <div className="absolute top-4 left-4">
                                <Badge className="bg-white/90 backdrop-blur-md text-slate-900 border-none font-bold py-1 px-3 rounded-xl shadow-sm capitalize">
                                    {course.category}
                                </Badge>
                            </div>
                        </div>
                        <CardContent className="p-6 flex-1 flex flex-col">
                            <div className="flex items-center gap-1 text-emerald-600 font-bold text-xs mb-3">
                                <Star size={12} fill="currentColor" /> 4.8 (120+ Öğrenci)
                            </div>
                            <h4 className="text-lg font-bold text-slate-900 group-hover:text-primary transition-colors line-clamp-2 leading-tight mb-4">
                                {course.title}
                            </h4>
                            
                            <div className="mt-auto pt-4 border-t border-slate-50 flex items-center justify-between">
                                <div className="flex items-center gap-4">
                                    <div className="flex items-center gap-1.5 text-slate-400 text-xs font-semibold">
                                        <Clock size={14} /> {course.duration_minutes || 0} dk
                                    </div>
                                    {course.has_certificate && (
                                        <div className="flex items-center gap-1.5 text-amber-500 text-xs font-bold">
                                            <Trophy size={14} /> Sertifikalı
                                        </div>
                                    )}
                                </div>
                                <div className="w-8 h-8 rounded-full bg-slate-50 text-slate-300 flex items-center justify-center group-hover:bg-primary group-hover:text-white transition-all transform group-hover:translate-x-1">
                                    <ChevronRight size={18} />
                                </div>
                            </div>
                        </CardContent>
                    </Card>
                </Link>
            ))}
        </div>
        
        {!courses?.length && (
            <div className="bg-white rounded-[2.5rem] py-24 text-center space-y-4">
                <div className="w-20 h-20 bg-slate-50 rounded-full flex items-center justify-center mx-auto">
                    <BookOpen size={40} className="text-slate-200" />
                </div>
                <div className="max-w-xs mx-auto">
                    <h3 className="text-xl font-bold text-slate-900">Sonuç Bulunamadı</h3>
                    <p className="text-slate-500 text-sm mt-2">Aramanıza veya seçtiğiniz kategoriye uygun içerik bulunamadı.</p>
                </div>
            </div>
        )}
      </div>
    </div>
  )
}
