import { createServerSupabase } from "@/lib/supabase/server"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

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

  // Top 5 courses by enrollment
  const { data: topCourses } = await supabase
    .from("courses")
    .select("id, title, category")
    .eq("is_published", true)
    .limit(5)

  // Recent 5 students
  const { data: recentStudents } = await supabase
    .from("profiles")
    .select("id, full_name, email, city, created_at")
    .eq("role", "student")
    .order("created_at", { ascending: false })
    .limit(5)

  return (
    <div className="space-y-6">
      <h1 className="text-2xl font-semibold">Dashboard</h1>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        {[
          { label: "Toplam Üye", value: totalStudents, color: "blue" },
          { label: "Aktif Eğitim", value: publishedCourses, color: "green" },
          { label: "Kayıt Sayısı", value: totalEnrollments, color: "purple" },
          { label: "Sertifika", value: totalCerts, color: "amber" },
        ].map(s => (
          <Card key={s.label}>
            <CardContent className="pt-6">
              <div className="text-3xl font-bold">{s.value ?? 0}</div>
              <div className="text-sm text-slate-500 mt-1">{s.label}</div>
            </CardContent>
          </Card>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <Card>
          <CardHeader><CardTitle>Öne Çıkan Eğitimler</CardTitle></CardHeader>
          <CardContent>
            <table className="w-full text-sm">
              <thead><tr className="border-b"><th className="text-left py-2">Eğitim</th><th className="text-left">Kategori</th></tr></thead>
              <tbody>
                {topCourses?.map(c => (
                  <tr key={c.id} className="border-b last:border-0">
                    <td className="py-2">{c.title}</td>
                    <td><span className="bg-slate-100 text-slate-700 px-2 py-0.5 rounded text-xs">{c.category}</span></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>

        <Card>
          <CardHeader><CardTitle>Son Kayıt Olan Üyeler</CardTitle></CardHeader>
          <CardContent>
            <table className="w-full text-sm">
              <thead><tr className="border-b"><th className="text-left py-2">İsim</th><th className="text-left">Şehir</th></tr></thead>
              <tbody>
                {recentStudents?.map(s => (
                  <tr key={s.id} className="border-b last:border-0">
                    <td className="py-2">
                      <div className="font-medium">{s.full_name}</div>
                      <div className="text-slate-400 text-xs">{s.email}</div>
                    </td>
                    <td className="text-slate-500">{s.city}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
