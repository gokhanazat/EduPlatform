import { createServerSupabase } from "@/lib/supabase/server"
import { redirect } from "next/navigation"
import AdminLoginForm from "@/components/auth/AdminLoginForm"

export default async function IndexPage() {
  const supabase = await createServerSupabase()
  const { data: { session } } = await supabase.auth.getSession()

  // If already logged in, redirect to appropriate dashboard
  if (session) {
    const { data: profile } = await supabase
      .from('profiles')
      .select('role')
      .eq('id', session.user.id)
      .single()
    
    if (profile?.role === 'admin') {
      redirect("/dashboard")
    } else {
      redirect("/home")
    }
  }

  return (
    <div className="min-h-screen bg-white flex flex-col items-center justify-center p-4">
      <div className="w-full max-w-[400px] space-y-12">
        <div className="text-center space-y-2">
            <h1 className="text-4xl font-black text-slate-900 tracking-tight">Admin Girişi</h1>
            <p className="text-slate-400 font-medium">Lütfen yönetim panelini kullanmak için giriş yapın.</p>
        </div>
        
        <AdminLoginForm />
        
        <div className="text-center">
            <p className="text-[10px] text-slate-300 font-black uppercase tracking-[0.2em] mt-20">
                © 2026 ITSO AKADEMİ • TÜM HAKLARI SAKLIDIR
            </p>
        </div>
      </div>
    </div>
  )
}
