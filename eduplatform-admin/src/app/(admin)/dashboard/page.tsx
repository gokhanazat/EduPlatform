import { createServerSupabase } from "@/lib/supabase/server"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Users, BookOpen, GraduationCap, Trophy, ArrowRight, TrendingUp } from "lucide-react"
import Link from "next/link"

export default async function DashboardPage() {
  const supabase = await createServerSupabase()
  const [
    { count: totalStudents },
    { count: publishedCourses },
    { count: totalEnrollments },
    { count: totalCerts },
  ] = await Promise.all([
    supabase.from("profiles").select("*", { count: "exact", head: true }).eq("role", "student"),
    supabase.from("courses").select("*", { count: "exact", head: true }).eq("is_published", true),
    supabase.from("enrollments").select("*", { count: "exact", head: true }),
    supabase.from("certificates").select("*", { count: "exact", head: true }),
  ])

  const { data: topCourses } = await supabase
    .from("courses")
    .select("id, title, category, thumbnail_url")
    .eq("is_published", true)
    .limit(4)

  const { data: recentStudents } = await supabase
    .from("profiles")
    .select("id, full_name, email, city")
    .eq("role", "student")
    .order("created_at", { ascending: false })
    .limit(5)

  const stats = [
    { label: "Toplam Üye", value: totalStudents, icon: Users, color: "bg-blue-50 text-blue-600" },
    { label: "Aktif Eğitim", value: publishedCourses, icon: BookOpen, color: "bg-emerald-50 text-emerald-600" },
    { label: "Kayıt Sayısı", value: totalEnrollments, icon: GraduationCap, color: "bg-indigo-50 text-indigo-600" },
    { label: "Sertifika", value: totalCerts, icon: Trophy, color: "bg-amber-50 text-amber-600" },
  ]

  return (
    <div className="space-y-10">
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-3xl font-extrabold tracking-tight text-slate-900">Dashboard</h1>
          <p className="text-slate-500 mt-1">Platformun genel performansına ve istatistiklerine göz atın.</p>
        </div>
        <div className="flex items-center gap-2 p-1.5 bg-white glass-card rounded-2xl">
            <Badge variant="secondary" className="bg-indigo-50 text-indigo-600 hover:bg-indigo-100 cursor-pointer px-4 py-1.5 rounded-xl border-none">Günlük</Badge>
            <Badge variant="outline" className="text-slate-500 hover:text-primary cursor-pointer px-4 py-1.5 rounded-xl">Haftalık</Badge>
            <Badge variant="outline" className="text-slate-500 hover:text-primary cursor-pointer px-4 py-1.5 rounded-xl">Aylık</Badge>
        </div>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
        {[
          { ...stats[0], href: "/whitelist" },
          { ...stats[1], href: "/manage-courses" },
          { ...stats[2], href: "/manage-courses" },
          { ...stats[3], href: "/manage-certificates" },
        ].map(s => (
          <Link href={s.href} key={s.label}>
            <Card className="card-hover border-none glass-card p-0 overflow-hidden cursor-pointer h-full">
              <CardContent className="p-6">
                <div className="flex items-center justify-between mb-4">
                  <div className={`p-3 rounded-2xl ${s.color}`}>
                    <s.icon size={24} />
                  </div>
                  <div className="flex items-center gap-1 text-emerald-600 text-xs font-bold bg-emerald-50 px-2 py-1 rounded-full">
                      <TrendingUp size={12} /> +12%
                  </div>
                </div>
                <div className="text-3xl font-extrabold text-slate-900">{s.value ?? 0}</div>
                <div className="text-sm font-semibold text-slate-400 mt-1 uppercase tracking-wider">{s.label}</div>
              </CardContent>
            </Card>
          </Link>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Recent Enrollments / Top Courses */}
        <div className="lg:col-span-2 space-y-6">
            <div className="flex items-center justify-between">
                <h3 className="text-xl font-bold text-slate-900">Öne Çıkan Eğitimler</h3>
                <Link href="/manage-courses" className="text-sm text-primary font-bold flex items-center gap-1 hover:underline">
                    Tümünü Gör <ArrowRight size={14} />
                </Link>
            </div>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                {topCourses?.map(c => (
                    <Card key={c.id} className="border-none bg-white/50 hover:bg-white transition-colors p-4 flex gap-4 items-center rounded-3xl group cursor-pointer shadow-sm">
                        <div className="w-20 h-20 rounded-2xl bg-indigo-100 flex-shrink-0 overflow-hidden">
                            {c.thumbnail_url ? (
                                <img src={c.thumbnail_url} className="w-full h-full object-cover group-hover:scale-110 transition-transform" />
                            ) : (
                                <div className="w-full h-full flex items-center justify-center text-indigo-300 font-bold text-xl">
                                    {c.title[0]}
                                </div>
                            )}
                        </div>
                        <div className="flex-1 min-w-0">
                            <Badge variant="secondary" className="mb-1 text-[10px] uppercase font-bold tracking-tighter bg-indigo-50 text-indigo-500 border-none">{c.category}</Badge>
                            <div className="font-bold text-slate-900 line-clamp-1 group-hover:text-primary transition-colors">{c.title}</div>
                            <div className="text-xs text-slate-400 mt-1">Ayrıntıları düzenlemek için tıkla</div>
                        </div>
                    </Card>
                ))}
            </div>
        </div>

        {/* Recent Students */}
        <div className="space-y-6">
            <div className="flex items-center justify-between">
                <h3 className="text-xl font-bold text-slate-900">Son Kayıtlar</h3>
                <Link href="/whitelist" className="text-sm text-primary font-bold flex items-center gap-1 hover:underline">
                   Yönet <ArrowRight size={14} />
                </Link>
            </div>
            <Card className="border-none glass-card p-2 rounded-[2rem]">
                <CardContent className="p-4 space-y-4">
                    {recentStudents?.length ? recentStudents.map((s, idx) => (
                        <div key={idx} className="flex items-center gap-4 group">
                            <div className="w-10 h-10 rounded-full bg-slate-100 flex items-center justify-center text-slate-500 font-bold group-hover:bg-primary group-hover:text-white transition-all cursor-pointer">
                                {s.full_name?.[0] || 'U'}
                            </div>
                            <div className="flex-1 min-w-0">
                                <div className="text-sm font-bold text-slate-900 truncate">{s.full_name}</div>
                                <div className="text-[10px] text-slate-400 truncate">{s.city || 'Şehir Belirtilmedi'}</div>
                            </div>
                        </div>
                    )) : (
                        <div className="text-center py-8 text-slate-400 text-sm italic">Henüz kayıtlı üye bulunamadı.</div>
                    )}
                </CardContent>
            </Card>
        </div>
      </div>
    </div>
  )
}
