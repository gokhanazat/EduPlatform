"use client"

import Link from "next/link"
import { usePathname, useRouter } from "next/navigation"
import { createBrowserClient } from "@supabase/ssr"
import { 
  BookOpen, 
  GraduationCap, 
  Trophy, 
  User, 
  LogOut, 
  Search, 
  Bell,
  Settings,
  Menu,
  X,
  ChevronRight,
  ChevronDown
} from "lucide-react"
import { useState, useEffect, Suspense } from "react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import TopSearch from "@/components/layout/TopSearch"

export default function StudentLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname()
  const router = useRouter()
  const [profile, setProfile] = useState<any>(null)
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false)
  
  const supabase = createBrowserClient(
    process.env.NEXT_PUBLIC_SUPABASE_URL!,
    process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!
  )

  useEffect(() => {
    async function loadProfile() {
      const { data: { session } } = await supabase.auth.getSession()
      if (session) {
        const { data } = await supabase.from('profiles').select('*').eq('id', session.user.id).single()
        setProfile(data)
      }
    }
    loadProfile()
  }, [])

  const handleLogout = async () => {
    await supabase.auth.signOut()
    router.push("/")
  }

  const navLinks = [
    { name: "Keşfet", icon: BookOpen, path: "/home" },
    { name: "Eğitimlerim", icon: GraduationCap, path: "/my-courses" },
    { name: "Sertifikalarım", icon: Trophy, path: "/certificates" },
    { name: "Profilim", icon: User, path: "/profile" },
    { name: "Ayarlar", icon: Settings, path: "/settings" },
  ]

  return (
    <div className="flex h-screen bg-[#F8FAFC] overflow-hidden">
      {/* Sidebar - Desktop */}
      <aside className="hidden lg:flex flex-col w-72 bg-white border-r border-slate-100 p-8 shrink-0">
        <div className="flex items-center gap-3 mb-12">
            <div className="w-10 h-10 bg-primary rounded-xl flex items-center justify-center text-white shadow-lg">
                <BookOpen size={24} />
            </div>
            <span className="text-xl font-bold text-slate-900 tracking-tight">ITSO Akademi</span>
        </div>

        <nav className="flex-1 space-y-2">
            {navLinks.map((link) => {
                const isActive = pathname === link.path
                return (
                    <Link 
                        key={link.path} 
                        href={link.path}
                        className={`flex items-center gap-4 px-6 py-4 rounded-2xl font-bold transition-all group ${
                            isActive 
                            ? "bg-primary text-white shadow-xl shadow-indigo-100" 
                            : "text-slate-400 hover:bg-slate-50 hover:text-slate-900"
                        }`}
                    >
                        <link.icon size={20} className={isActive ? "text-white" : "group-hover:text-primary transition-colors"} />
                        <span>{link.name}</span>
                    </Link>
                )
            })}
        </nav>



        <Button 
            onClick={handleLogout}
            variant="ghost" 
            className="w-full justify-start gap-4 h-14 rounded-2xl font-bold text-red-400 hover:bg-red-50 hover:text-red-500 transition-colors"
        >
            <LogOut size={20} /> Çıkış Yap
        </Button>
      </aside>

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col h-full overflow-hidden">
        {/* Top Header */}
        <header className="h-20 bg-white border-b border-slate-100 flex items-center justify-between px-8 shrink-0">
          <div className="flex items-center gap-4 flex-1">
             <button className="lg:hidden p-2 text-slate-600" onClick={() => setIsMobileMenuOpen(true)}>
                <Menu size={24} />
             </button>
             
              <Suspense fallback={<div className="flex-1 max-w-md h-11 bg-slate-50 animate-pulse rounded-[1rem]" />}>
                <TopSearch />
              </Suspense>
          </div>

          <div className="flex items-center gap-6">
            <button className="p-2.5 rounded-xl text-slate-400 hover:bg-slate-50 transition-colors relative group">
                <Bell size={22} className="group-hover:text-primary transition-colors" />
                <span className="absolute top-2.5 right-2.5 w-2 h-2 bg-primary rounded-full border-2 border-white"></span>
            </button>

            <div className="w-[1px] h-8 bg-slate-100 mx-2"></div>

            <div className="flex items-center gap-3 pl-2">
                <div className="text-right hidden sm:block">
                    <div className="text-sm font-black text-slate-900 leading-none">{profile?.full_name || "John Doe"}</div>
                    <div className="text-[10px] font-bold text-slate-300 mt-1 uppercase tracking-wider">{profile?.role || "Learner"}</div>
                </div>
                <div className="w-11 h-11 rounded-full bg-orange-100 flex items-center justify-center text-orange-600 shadow-inner border-2 border-white overflow-hidden relative">
                    {profile?.full_name?.[0] || <User size={22} />}
                </div>
            </div>
          </div>
        </header>

        {/* Viewport */}
        <main className="flex-1 overflow-y-auto bg-[#F8FAFC]">
            <div className="p-8 max-w-[1400px] mx-auto animate-in fade-in slide-in-from-bottom-4 duration-500">
                {children}
            </div>
        </main>
      </div>

      {/* Mobile Menu Sidebar */}
      {isMobileMenuOpen && (
        <div className="fixed inset-0 bg-slate-900/60 backdrop-blur-sm z-[100] lg:hidden">
          <aside className="w-[85%] h-full bg-white flex flex-col p-8 animate-in slide-in-from-left duration-300">
            <div className="flex justify-between items-center mb-12">
                <div className="flex items-center gap-3">
                    <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center text-white">
                        <BookOpen size={20} />
                    </div>
                    <span className="font-bold text-lg text-slate-900">EduPlatform</span>
                </div>
                <button onClick={() => setIsMobileMenuOpen(false)} className="p-2 bg-slate-50 rounded-full text-slate-400 hover:text-slate-900 transition-colors">
                    <X size={20} />
                </button>
            </div>
            
            <nav className="flex-1 space-y-2">
               {navLinks.map((item) => (
                <Link 
                  key={item.path} 
                  href={item.path}
                  onClick={() => setIsMobileMenuOpen(false)}
                  className={`flex items-center gap-4 px-6 py-4 rounded-2xl font-bold transition-all ${
                    pathname === item.path ? "bg-primary text-white shadow-xl shadow-indigo-100" : "text-slate-400 hover:bg-slate-50"
                  }`}
                >
                  <item.icon size={20} />
                  <span>{item.name}</span>
                </Link>
              ))}
            </nav>

            <div className="mt-auto pt-8 border-t border-slate-100">
                <Button onClick={handleLogout} variant="ghost" className="w-full justify-start gap-4 h-14 rounded-2xl font-bold text-red-500 hover:bg-red-50 hover:text-red-600">
                    <LogOut size={22} /> Logout
                </Button>
            </div>
          </aside>
        </div>
      )}
    </div>
  )
}
