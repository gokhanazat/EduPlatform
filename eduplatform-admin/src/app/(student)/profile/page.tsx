import { createServerSupabase } from "@/lib/supabase/server"
import { 
  User, 
  MapPin, 
  Calendar, 
  BookOpen, 
  Trophy, 
  ChevronRight, 
  Play, 
  CheckCircle2, 
  Eye,
  Edit2,
  Clock
} from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import Link from "next/link"
import { redirect } from "next/navigation"

export const dynamic = "force-dynamic"

export default async function ProfilePage() {
  const supabase = await createServerSupabase()
  const { data: { session } } = await supabase.auth.getSession()

  if (!session) {
    return (
      <div className="flex flex-col items-center justify-center min-h-[60vh] text-center space-y-4">
        <h3 className="text-xl font-bold text-slate-900">Giriş Yapmalısınız</h3>
        <p className="text-slate-500 max-w-xs mx-auto text-sm">Profilinizi görmek için lütfen hesabınıza giriş yapın.</p>
        <Button asChild className="rounded-2xl h-12 px-8 font-bold mt-4">
           <Link href="/login">Giriş Yap</Link>
        </Button>
      </div>
    )
  }

  // Fetch actual profile data
  const { data: profile } = await supabase
    .from('profiles')
    .select('*')
    .eq('id', session.user.id)
    .single()

  // Fetch actual counts
  const { count: enrollmentsCount } = await supabase
    .from('enrollments')
    .select('*', { count: 'exact', head: true })
    .eq('profile_id', session.user.id)

  const { count: certificatesCount } = await supabase
    .from('certificates')
    .select('*', { count: 'exact', head: true })
    .eq('profile_id', session.user.id)

  // Fetch last 3 enrollments
  const { data: enrollments } = await supabase
    .from('enrollments')
    .select('*, courses(*)')
    .eq('profile_id', session.user.id)
    .order('enrolled_at', { ascending: false })
    .limit(3)

  const recentEnrollments = enrollments ? await Promise.all(
    enrollments.map(async (item: any) => {
        const { count: totalLessons } = await supabase
            .from("lessons")
            .select("*", { count: "exact", head: true })
            .eq("course_id", item.course_id)
        
        const { data: completedLessons } = await supabase
            .from("lesson_completions")
            .select("lesson_id, lessons!inner(course_id)")
            .eq("profile_id", session.user.id)
            .eq("lessons.course_id", item.course_id)
        
        const finishedCount = completedLessons?.length || 0
        const totalCount = totalLessons || 0
        const progress = totalCount > 0 ? Math.floor((finishedCount / totalCount) * 100) : 0
        return { ...item, progress }
    })
  ) : []

  // Fetch certificates
  const { data: certificates } = await supabase
    .from('certificates')
    .select('*, courses(title, category)')
    .eq('profile_id', session.user.id)
    .order('created_at', { ascending: false })

  return (
    <div className="space-y-8 pb-20">
      {/* User Banner Card */}
      <Card className="border-none shadow-sm rounded-[2rem] overflow-hidden bg-white">
        <CardContent className="p-8">
            <div className="flex flex-col md:flex-row items-center gap-8">
                {/* Profile Image & Status */}
                <div className="relative">
                    <div className="w-32 h-32 rounded-full border-4 border-slate-50 bg-slate-100 flex items-center justify-center text-4xl font-black text-slate-300 overflow-hidden shadow-inner">
                        {profile?.full_name?.[0] || session.user.email?.[0]?.toUpperCase()}
                        {profile?.avatar_url && <img src={profile.avatar_url} className="absolute inset-0 w-full h-full object-cover" />}
                    </div>
                    {/* Status indicator */}
                    <div className="absolute bottom-1 right-1 w-7 h-7 bg-emerald-500 border-4 border-white rounded-full"></div>
                </div>

                {/* Profile Text Info */}
                <div className="flex-1 text-center md:text-left space-y-3">
                    <div className="space-y-1">
                        <h1 className="text-3xl font-black text-slate-900 tracking-tight">{profile?.full_name || "Öğrenci"}</h1>
                        <p className="text-primary font-bold uppercase text-xs tracking-widest">{profile?.role || "Öğrenci"}</p>
                    </div>
                    
                    <div className="flex flex-wrap items-center justify-center md:justify-start gap-6 pt-2">
                        <div className="flex items-center gap-2 text-slate-400 text-sm font-medium">
                            <MapPin size={16} /> 
                            <span>{profile?.city || "Şehir Belirtilmemiş"}</span>
                        </div>
                        <div className="flex items-center gap-2 text-slate-400 text-sm font-medium">
                            <Calendar size={16} /> 
                            <span>{new Date(profile?.created_at).toLocaleDateString('tr-TR', { month: 'long', year: 'numeric' })} Katıldı</span>
                        </div>
                    </div>
                </div>

                {/* Edit Button */}
                <Button variant="outline" className="h-12 px-6 rounded-2xl border-slate-100 bg-slate-50 font-bold hover:bg-white transition-all gap-2" asChild>
                    <Link href="/settings">
                        Profil Ayarları
                    </Link>
                </Button>
            </div>
        </CardContent>
      </Card>

      {/* Stats Row */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <Card className="border-none shadow-sm rounded-[2rem] bg-white hover:shadow-xl hover:shadow-indigo-50 transition-all group">
            <CardContent className="p-8 flex items-center justify-between">
                <div className="space-y-2">
                    <div className="text-xs font-bold text-slate-400 uppercase tracking-widest">Kayıtlı Eğitimler</div>
                    <div className="text-4xl font-black text-slate-900 leading-tight">{enrollmentsCount || 0}</div>
                </div>
                <div className="w-14 h-14 rounded-2xl bg-indigo-50 text-primary flex items-center justify-center group-hover:scale-110 transition-transform">
                    <BookOpen size={28} />
                </div>
            </CardContent>
        </Card>

        <Card className="border-none shadow-sm rounded-[2rem] bg-white hover:shadow-xl hover:shadow-amber-50 transition-all group">
            <CardContent className="p-8 flex items-center justify-between">
                <div className="space-y-2">
                    <div className="text-xs font-bold text-slate-400 uppercase tracking-widest">Sertifikalar</div>
                    <div className="text-4xl font-black text-slate-900 leading-tight">{certificatesCount || 0}</div>
                </div>
                <div className="w-14 h-14 rounded-2xl bg-amber-50 text-amber-500 flex items-center justify-center group-hover:scale-110 transition-transform">
                    <Trophy size={28} />
                </div>
            </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          {/* My Courses Section */}
          <div className="space-y-6">
              <div className="flex items-center justify-between border-b border-slate-100 pb-4">
                <h2 className="text-2xl font-black text-slate-900">Eğitimlerim</h2>
                <Link href="/my-courses" className="text-primary font-bold text-sm hover:underline">Tümünü Gör</Link>
              </div>

              <div className="space-y-4">
                  {recentEnrollments?.map((item: any) => {
                      const course = item.courses
                      const progress = item.progress
                      
                      return (
                        <Card key={item.id} className="border-none shadow-sm rounded-[2rem] bg-white overflow-hidden group hover:shadow-xl transition-all">
                            <CardContent className="p-6 flex items-center gap-6">
                                <div className="w-14 h-14 rounded-2xl bg-primary/10 flex items-center justify-center shrink-0 text-primary">
                                    <BookOpen size={24} />
                                </div>
                                <div className="flex-1 space-y-4">
                                    <div className="flex flex-col md:flex-row md:items-center justify-between gap-2">
                                        <div className="space-y-1">
                                            <h3 className="font-bold text-slate-900 text-lg leading-tight line-clamp-1">{course.title}</h3>
                                            <p className="text-xs text-slate-400 font-bold uppercase tracking-wider">%{progress} Tamamlandı</p>
                                        </div>
                                    </div>
                                    <div className="w-full h-2 bg-slate-50 rounded-full overflow-hidden">
                                        <div className="h-full bg-primary transition-all duration-1000" style={{ width: `${progress}%` }}></div>
                                    </div>
                                </div>
                                <Link href={`/courses/${course.id}/watch`}>
                                    <button className="w-12 h-12 rounded-2xl bg-primary text-white flex items-center justify-center hover:scale-110 transition-transform shadow-lg shadow-indigo-100">
                                        <Play size={20} className="fill-current" />
                                    </button>
                                </Link>
                            </CardContent>
                        </Card>
                      )
                  })}
                  {!recentEnrollments?.length && (
                      <div className="bg-slate-50/50 rounded-[2rem] py-12 text-center border-2 border-dashed border-slate-100">
                          <p className="text-slate-400 font-medium italic">Henüz bir eğitime kayıtlı değilsiniz.</p>
                      </div>
                  )}
              </div>
          </div>

          {/* Certificates Section */}
          <div className="space-y-6">
              <div className="flex items-center justify-between border-b border-slate-100 pb-4">
                <h2 className="text-2xl font-black text-slate-900">Sertifikalarım</h2>
                <span className="text-slate-400 text-sm font-bold">{certificates?.length || 0} Belge</span>
              </div>

              <div className="space-y-4">
                {certificates?.map((cert: any) => (
                    <Card key={cert.id} className="border-none shadow-sm rounded-[2rem] bg-white overflow-hidden group hover:shadow-xl transition-all">
                        <CardContent className="p-6 flex items-center gap-6">
                            <div className="w-14 h-14 rounded-2xl bg-amber-50 flex items-center justify-center shrink-0 text-amber-500">
                                <Trophy size={24} />
                            </div>
                            <div className="flex-1">
                                <h3 className="font-bold text-slate-900 text-lg leading-tight line-clamp-1">{cert.courses?.title}</h3>
                                <div className="flex items-center gap-3 mt-1">
                                    <p className="text-[10px] text-slate-400 font-black uppercase tracking-widest">{cert.certificate_number}</p>
                                    <span className="w-1 h-1 bg-slate-200 rounded-full"></span>
                                    <p className="text-[10px] text-slate-400 font-black uppercase tracking-widest">
                                        {new Date(cert.created_at).toLocaleDateString('tr-TR')}
                                    </p>
                                </div>
                            </div>
                            <Link href={`/certificates/${cert.id}`}>
                                <button className="w-12 h-12 rounded-2xl bg-slate-900 text-white flex items-center justify-center hover:scale-110 transition-transform">
                                    <Eye size={20} />
                                </button>
                            </Link>
                        </CardContent>
                    </Card>
                ))}
                {!certificates?.length && (
                    <div className="bg-slate-50/50 rounded-[2rem] py-12 text-center border-2 border-dashed border-slate-100">
                        <p className="text-slate-400 font-medium italic">Henüz sertifikanız bulunmuyor.</p>
                    </div>
                )}
              </div>
          </div>
      </div>
    </div>
  )
}
