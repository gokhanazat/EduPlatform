import Link from "next/link"
import { redirect } from "next/navigation"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { 
  BookOpen, 
  Search, 
  Bell, 
  User, 
  ChevronRight, 
  Code, 
  Palette, 
  Briefcase, 
  Megaphone, 
  Music, 
  Camera,
  Star,
  ShoppingCart,
  Heart,
  Clock
} from "lucide-react"
import { Input } from "@/components/ui/input"
import { createServerSupabase } from "@/lib/supabase/server"

export default async function LandingPage() {
  const supabase = await createServerSupabase()
  const { data: { session } } = await supabase.auth.getSession()

  if (session) {
    const { data: profile } = await supabase.from('profiles').select('role').eq('id', session.user.id).single()
    if (profile?.role === 'admin') redirect("/dashboard")
    else redirect("/home")
  }
  
  // Real course fetching
  const { data: realCourses } = await supabase
    .from("courses")
    .select("*")
    .eq("is_published", true)
    .limit(5)
    .order("created_at", { ascending: false })

  const categories = [
    { name: "Programlama", icon: Code, color: "text-blue-500", bg: "bg-blue-50" },
    { name: "Tasarım", icon: Palette, color: "text-purple-500", bg: "bg-purple-50" },
    { name: "İş Dünyası", icon: Briefcase, color: "text-emerald-500", bg: "bg-emerald-50" },
    { name: "Pazarlama", icon: Megaphone, color: "text-orange-500", bg: "bg-orange-50" },
    { name: "Müzik", icon: Music, color: "text-red-500", bg: "bg-red-50" },
    { name: "Fotoğrafçılık", icon: Camera, color: "text-teal-500", bg: "bg-teal-50" },
  ]

  return (
    <div className="flex flex-col min-h-screen bg-slate-50/50">
      {/* Navbar - Image style */}
      <header className="px-8 h-20 flex items-center bg-white border-b border-slate-100 sticky top-0 z-50">
        <div className="flex items-center gap-8 w-full">
          <Link className="flex items-center gap-2" href="/">
            <div className="w-10 h-10 bg-primary rounded-xl flex items-center justify-center text-white font-bold text-xl shadow-lg">
              <BookOpen size={24} />
            </div>
            <span className="text-xl font-bold text-slate-900 tracking-tight">ITSO Akademi</span>
          </Link>

          {/* Search Bar - Center */}
          <div className="hidden md:flex flex-1 max-w-xl relative">
            <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" />
            <Input 
              placeholder="Bugün ne öğrenmek istersiniz?" 
              className="w-full pl-11 h-11 bg-slate-100/50 border-none rounded-2xl focus-visible:ring-primary/20 text-sm font-medium"
            />
          </div>

          {/* Navigation Links */}
          <nav className="ml-auto flex items-center gap-8">
            <Link className="text-sm font-bold text-slate-600 hover:text-primary transition-colors" href="/home">Eğitimler</Link>
            <Link className="text-sm font-bold text-slate-600 hover:text-primary transition-colors" href="#">Keşfet</Link>
            <Link className="text-sm font-bold text-slate-600 hover:text-primary transition-colors" href="/my-courses">Eğitimlerim</Link>
          </nav>

          {/* Auth/Profile Actions */}
          <div className="flex items-center gap-4 border-l border-slate-100 pl-8">
            <button className="p-2.5 rounded-xl text-slate-400 hover:bg-slate-50 transition-colors">
              <Bell size={20} />
            </button>
            <Link href="/login" className="w-10 h-10 rounded-full bg-orange-100 flex items-center justify-center text-orange-600 shadow-sm border-2 border-white hover:scale-105 transition-transform">
              <User size={20} />
            </Link>
          </div>
        </div>
      </header>

      <main className="flex-1 px-8 py-8 space-y-12">
        {/* Android Style Hero Banner */}
        <section className="relative w-full h-[320px] rounded-[2.5rem] overflow-hidden group">
          <img 
            src="/landing_hero_banner_1773665519997.png" 
            alt="Landing Hero" 
            className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-105"
          />
          <div className="absolute inset-0 bg-gradient-to-r from-primary/80 via-primary/40 to-transparent"></div>
          
          <div className="absolute inset-0 flex flex-col justify-center px-16 max-w-2xl space-y-6">
            <h1 className="text-5xl font-black text-white leading-[1.1] tracking-tight">
              Uzmanlardan <br /> Yeni Yetkinlikler Edinin
            </h1>
            <p className="text-indigo-50 text-lg font-medium opacity-90">
              Milyonlarca öğrenciye katılın. Programlama, iş dünyası, tasarım ve daha fazlasında 5.000'den fazla eğitime erişin.
            </p>
            <div className="flex items-center gap-4 pt-4">
              <Button className="h-14 px-8 rounded-2xl bg-white text-primary font-bold hover:bg-indigo-50 shadow-xl shadow-black/10" asChild>
                <Link href="/register">Hemen Başla</Link>
              </Button>
              <Button variant="outline" className="h-14 px-8 rounded-2xl border-white/40 bg-white/10 backdrop-blur-md text-white font-bold hover:bg-white/20" asChild>
                <Link href="/home">Kütüphaneyi Keşfet</Link>
              </Button>
            </div>
          </div>
        </section>

        {/* Top Categories */}
        <section className="space-y-6">
          <div className="flex items-center justify-between">
            <h2 className="text-2xl font-black text-slate-900">Popüler Kategoriler</h2>
            <Link href="#" className="text-sm font-bold text-primary hover:underline">Tümünü Gör</Link>
          </div>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-6">
            {categories.map((cat, i) => (
              <Card key={i} className="border-none shadow-sm hover:shadow-xl hover:shadow-indigo-100 transition-all cursor-pointer rounded-3xl group">
                <CardContent className="p-8 flex flex-col items-center gap-4">
                  <div className={`w-12 h-12 rounded-2xl ${cat.bg} ${cat.color} flex items-center justify-center group-hover:scale-110 transition-transform`}>
                    <cat.icon size={24} />
                  </div>
                  <span className="text-sm font-bold text-slate-700">{cat.name}</span>
                </CardContent>
              </Card>
            ))}
          </div>
        </section>

        {/* Popular Courses */}
        <section className="space-y-6 pb-20">
          <div className="flex items-center justify-between">
            <h2 className="text-2xl font-black text-slate-900">Popüler Eğitimler</h2>
            <div className="flex gap-2">
               <button className="w-10 h-10 rounded-xl bg-white border border-slate-100 flex items-center justify-center text-slate-400 hover:text-primary transition-colors shadow-sm">
                <ChevronRight size={20} className="rotate-180" />
               </button>
               <button className="w-10 h-10 rounded-xl bg-white border border-slate-100 flex items-center justify-center text-slate-400 hover:text-primary transition-colors shadow-sm">
                <ChevronRight size={20} />
               </button>
            </div>
          </div>
          
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-6">
            {realCourses?.map((course) => (
              <Link href={`/login`} key={course.id}>
                <Card className="border-none shadow-sm hover:shadow-2xl transition-all rounded-[2rem] overflow-hidden group cursor-pointer bg-white h-full">
                    <div className="relative h-44 overflow-hidden bg-slate-100">
                    {course.thumbnail_url ? (
                        <img src={course.thumbnail_url} className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-500" />
                    ) : (
                        <div className="w-full h-full flex items-center justify-center text-slate-300">
                            <BookOpen size={48} />
                        </div>
                    )}
                    <div className="absolute top-4 left-4">
                        <Badge className="bg-primary/90 text-white border-none rounded-lg font-bold text-[10px] px-2 py-0.5 uppercase tracking-wider">
                        {course.category}
                        </Badge>
                    </div>
                    </div>
                    <CardContent className="p-6 space-y-3">
                    <h3 className="font-bold text-slate-900 text-sm line-clamp-2 h-10 leading-snug">
                        {course.title}
                    </h3>
                    <div className="flex items-center gap-2 text-[10px] font-bold text-slate-400 uppercase tracking-widest">
                        <Clock size={12} /> {course.duration_minutes || 0} dk
                    </div>
                    
                    <div className="flex items-center gap-1">
                        <div className="flex text-amber-400">
                        {[...Array(5)].map((_, j) => <Star key={j} size={12} fill={j < 4 ? "currentColor" : "none"} />)}
                        </div>
                        <span className="text-xs font-bold text-slate-700">4.8</span>
                    </div>

                    <div className="flex items-center justify-between pt-2">
                        <div className="text-lg font-black text-slate-900">Ücretsiz</div>
                        <div className="w-9 h-9 rounded-xl bg-slate-50 text-primary flex items-center justify-center group-hover:bg-primary group-hover:text-white transition-all">
                        <ChevronRight size={18} />
                        </div>
                    </div>
                    </CardContent>
                </Card>
              </Link>
            ))}

            {!realCourses?.length && (
                <div className="col-span-full py-20 text-center bg-white rounded-[2rem] border-2 border-dashed border-slate-100">
                    <p className="text-slate-400 font-bold">Henüz eğitim eklenmemiş.</p>
                </div>
            )}
          </div>
        </section>
      </main>

      <footer className="px-8 py-10 border-t border-slate-100 text-center">
        <p className="text-xs text-slate-400 font-medium">© 2026 ITSO Akademi Eğitim Sistemleri. Tüm Hakları Saklıdır.</p>
      </footer>
    </div>
  )
}
