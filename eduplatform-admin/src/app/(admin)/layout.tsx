"use client"

import Link from "next/link"
import { usePathname, useRouter } from "next/navigation"
import { createBrowserClient } from "@supabase/ssr"
import { 
  LayoutDashboard, 
  BookOpen, 
  Users, 
  Settings, 
  LogOut, 
  Menu, 
  X,
  Bell,
  Trophy
} from "lucide-react"
import { useState } from "react"
import { Button } from "@/components/ui/button"

export default function AdminLayout({ children }: { children: React.ReactNode }) {
  const pathname = usePathname()
  const router = useRouter()
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false)
  
  const supabase = createBrowserClient(
    process.env.NEXT_PUBLIC_SUPABASE_URL!,
    process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!
  )

  const handleLogout = async () => {
    await supabase.auth.signOut()
    router.push("/admin/login")
  }

  const menuItems = [
    { name: "Dashboard", icon: LayoutDashboard, path: "/dashboard" },
    { name: "Eğitimler", icon: BookOpen, path: "/manage-courses" },
    { name: "Beyaz Liste", icon: Users, path: "/whitelist" },
    { name: "Sertifikalar", icon: Trophy, path: "/manage-certificates" },
    { name: "Ayarlar", icon: Settings, path: "/settings" },
  ]

  return (
    <div className="flex h-screen bg-slate-50 overflow-hidden">
      {/* Sidebar - Desktop */}
      <aside className="hidden md:flex flex-col w-72 glass-card rounded-none border-y-0 border-l-0 z-40">
        <div className="p-8 pb-4">
          <Link href="/dashboard" className="flex items-center gap-3">
            <div className="w-10 h-10 premium-gradient rounded-xl flex items-center justify-center text-white font-bold text-xl shadow-lg">E</div>
            <span className="text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-indigo-600 to-purple-600">EduAdmin</span>
          </Link>
        </div>
        
        <nav className="flex-1 px-4 py-8 space-y-2">
          {menuItems.map((item) => {
            const isActive = pathname === item.path
            return (
              <Link 
                key={item.name} 
                href={item.path}
                className={`flex items-center gap-3 px-4 py-3 rounded-2xl transition-all duration-200 group ${
                  isActive 
                    ? "bg-primary text-white shadow-lg shadow-indigo-200" 
                    : "text-slate-500 hover:bg-indigo-50 hover:text-primary"
                }`}
              >
                <item.icon size={20} className={isActive ? "text-white" : "group-hover:scale-110 transition-transform"} />
                <span className="font-semibold">{item.name}</span>
              </Link>
            )
          })}
        </nav>

        <div className="p-4 mt-auto">
          <Button 
            variant="ghost" 
            onClick={handleLogout}
            className="w-full flex items-center justify-start gap-3 px-4 py-6 rounded-2xl text-slate-500 hover:bg-red-50 hover:text-red-600 transition-colors"
          >
            <LogOut size={20} />
            <span className="font-semibold">Çıkış Yap</span>
          </Button>
        </div>
      </aside>

      {/* Main Content Area */}
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        {/* Header */}
        <header className="h-20 flex items-center justify-between px-4 md:px-8 border-b border-slate-100 bg-white/50 backdrop-blur-sm sticky top-0 z-30">
          <button className="md:hidden p-2 text-slate-600" onClick={() => setIsMobileMenuOpen(true)}>
            <Menu size={24} />
          </button>
          
          <div className="hidden md:block">
            <h2 className="text-sm font-medium text-slate-400">Hoş Geldiniz,</h2>
            <p className="text-lg font-bold text-slate-900">Admin Paneli</p>
          </div>

          <div className="flex items-center gap-4">
            <button className="p-2.5 rounded-xl bg-slate-100 text-slate-500 hover:bg-slate-200 transition-colors relative">
              <Bell size={20} />
              <span className="absolute top-2.5 right-2.5 w-2 h-2 bg-red-500 rounded-full border-2 border-white"></span>
            </button>
            <div className="w-10 h-10 rounded-full bg-indigo-100 border-2 border-primary/20 flex items-center justify-center text-primary font-bold shadow-inner">
              A
            </div>
          </div>
        </header>

        {/* Page Content */}
        <main className="flex-1 overflow-y-auto p-4 md:p-8 custom-scrollbar">
          <div className="max-w-7xl mx-auto animate-in fade-in slide-in-from-bottom-4 duration-500">
            {children}
          </div>
        </main>
      </div>

      {/* Mobile Menu Overlay */}
      {isMobileMenuOpen && (
        <div className="fixed inset-0 bg-slate-900/40 backdrop-blur-sm z-50 md:hidden flex justify-end">
          <aside className="w-4/5 h-full bg-white flex flex-col p-6 animate-in slide-in-from-right-full transition-all duration-300">
            <div className="flex justify-between items-center mb-8">
              <span className="font-bold text-xl text-primary">EduAdmin</span>
              <button onClick={() => setIsMobileMenuOpen(false)}><X size={24} /></button>
            </div>
            <nav className="flex-1 space-y-2">
               {menuItems.map((item) => (
                <Link 
                  key={item.name} 
                  href={item.path}
                  onClick={() => setIsMobileMenuOpen(false)}
                  className="flex items-center gap-3 px-4 py-4 rounded-2xl text-slate-600 hover:bg-slate-50"
                >
                  <item.icon size={20} />
                  <span className="font-semibold">{item.name}</span>
                </Link>
              ))}
            </nav>
            <Button variant="ghost" className="mt-auto justify-start gap-3 p-4 text-red-600 hover:bg-red-50" onClick={handleLogout}>
              <LogOut size={20} /> <span className="font-semibold">Çıkış Yap</span>
            </Button>
          </aside>
        </div>
      )}
    </div>
  )
}
