import { createServerSupabase } from "@/lib/supabase/server"
import { Button } from "@/components/ui/button"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import Link from "next/link"
import { Edit, BookOpen, Trash2, Eye, EyeOff } from "lucide-react"

export default async function CoursesPage() {
  const supabase = await createServerSupabase()
  const { data: courses } = await supabase
    .from("courses")
    .select("*")
    .order("created_at", { ascending: false })

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-semibold">Eğitimler</h1>
        <Button asChild>
          <Link href="/courses/new">Yeni Eğitim</Link>
        </Button>
      </div>

      <div className="border rounded-lg overflow-hidden bg-white">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Başlık</TableHead>
              <TableHead>Kategori</TableHead>
              <TableHead>Şehir</TableHead>
              <TableHead>Eğitmen</TableHead>
              <TableHead>Süre</TableHead>
              <TableHead>Sertifika</TableHead>
              <TableHead>Durum</TableHead>
              <TableHead className="text-right">İşlemler</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {courses?.map((course) => (
              <TableRow key={course.id}>
                <TableCell className="font-medium">{course.title}</TableCell>
                <TableCell>{course.category}</TableCell>
                <TableCell>{course.city || "Tüm Şehirler"}</TableCell>
                <TableCell>{course.instructor_name}</TableCell>
                <TableCell>{course.duration_minutes} dk</TableCell>
                <TableCell>
                  {course.has_certificate ? (
                    <Badge variant="default">Evet</Badge>
                  ) : (
                    <Badge variant="secondary">Hayır</Badge>
                  )}
                </TableCell>
                <TableCell>
                  {course.is_published ? (
                    <Badge className="bg-green-100 text-green-700 hover:bg-green-100">Yayında</Badge>
                  ) : (
                    <Badge variant="outline">Taslak</Badge>
                  )}
                </TableCell>
                <TableCell className="text-right">
                  <div className="flex justify-end gap-2">
                    <Button variant="ghost" size="icon" asChild title="Düzenle">
                      <Link href={`/courses/${course.id}`}>
                        <Edit size={16} />
                      </Link>
                    </Button>
                    <Button variant="ghost" size="icon" asChild title="Sınav">
                      <Link href={`/courses/${course.id}/quiz`}>
                        <BookOpen size={16} />
                      </Link>
                    </Button>
                    <Button variant="ghost" size="icon" className="text-red-500 hover:text-red-600 hover:bg-red-50">
                      <Trash2 size={16} />
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>
    </div>
  )
}
