import { createServerSupabase } from "@/lib/supabase/server"
import { Card, CardContent, CardHeader, CardTitle, CardDescription } from "@/components/ui/card"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Trophy, Search, FileDown, User, BookOpen, Calendar } from "lucide-react"
import { Input } from "@/components/ui/input"

export default async function ManageCertificatesPage() {
  const supabase = await createServerSupabase()
  
  const { data: certificates } = await supabase
    .from("certificates")
    .select("*, profiles(full_name, email), courses(title)")
    .order("issued_at", { ascending: false })

  return (
    <div className="space-y-8 animate-in fade-in duration-700">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-end gap-6">
        <div className="space-y-1">
          <h1 className="text-3xl font-extrabold tracking-tight text-slate-900">Sertifika Yönetimi</h1>
          <p className="text-slate-500">Kullanıcılara verilen başarı belgelerini buradan takip edebilirsiniz.</p>
        </div>
      </div>

      <Card className="border-none shadow-sm rounded-[2.5rem] bg-white overflow-hidden">
        <CardHeader className="p-8 pb-4">
            <div className="flex flex-col md:flex-row gap-4 items-center justify-between">
                <div className="relative w-full md:w-96">
                    <Search className="absolute left-3 top-3 h-4 w-4 text-slate-400" />
                    <Input placeholder="Öğrenci veya eğitim ara..." className="pl-10 h-11 rounded-xl bg-slate-50/50 border-slate-100" />
                </div>
            </div>
        </CardHeader>
        <CardContent className="p-0">
          <Table>
            <TableHeader className="bg-slate-50/50">
              <TableRow className="hover:bg-transparent border-slate-100">
                <TableHead className="py-5 px-8 font-bold text-slate-400 uppercase tracking-widest text-[10px]">Öğrenci</TableHead>
                <TableHead className="font-bold text-slate-400 uppercase tracking-widest text-[10px]">Eğitim</TableHead>
                <TableHead className="font-bold text-slate-400 uppercase tracking-widest text-[10px]">Tarih</TableHead>
                <TableHead className="font-bold text-slate-400 uppercase tracking-widest text-[10px]">Sertifika No</TableHead>
                <TableHead className="text-right px-8 font-bold text-slate-400 uppercase tracking-widest text-[10px]">İşlemler</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {certificates?.map((cert: any) => (
                <TableRow key={cert.id} className="group hover:bg-slate-50/50 transition-colors border-slate-50">
                  <TableCell className="py-6 px-8">
                    <div className="flex items-center gap-4">
                        <div className="w-10 h-10 rounded-full bg-slate-100 flex items-center justify-center text-slate-400 font-bold shrink-0">
                            {cert.profiles?.full_name?.[0] || "U"}
                        </div>
                        <div>
                            <div className="font-bold text-slate-900">{cert.profiles?.full_name}</div>
                            <div className="text-xs text-slate-400">{cert.profiles?.email}</div>
                        </div>
                    </div>
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center gap-2 text-slate-700 font-bold">
                        <BookOpen size={16} className="text-primary/50" />
                        {cert.courses?.title}
                    </div>
                  </TableCell>
                  <TableCell>
                    <div className="flex items-center gap-2 text-slate-500 text-sm font-medium">
                        <Calendar size={14} />
                        {new Date(cert.issued_at).toLocaleDateString("tr-TR")}
                    </div>
                  </TableCell>
                  <TableCell>
                    <Badge variant="outline" className="rounded-lg border-slate-100 bg-slate-50 text-slate-500 font-mono font-bold">
                        {cert.certificate_number || "EDU-TR-001"}
                    </Badge>
                  </TableCell>
                  <TableCell className="text-right px-8">
                     <button className="p-2 rounded-xl text-slate-300 hover:text-primary hover:bg-indigo-50 transition-all">
                        <FileDown size={20} />
                     </button>
                  </TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>
          
          {(!certificates || certificates.length === 0) && (
            <div className="py-20 text-center space-y-4">
                <div className="w-16 h-16 bg-slate-50 rounded-full flex items-center justify-center mx-auto text-slate-200">
                    <Trophy size={32} />
                </div>
                <div className="text-slate-500 font-medium">Henüz verilmiş bir sertifika bulunmuyor.</div>
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
