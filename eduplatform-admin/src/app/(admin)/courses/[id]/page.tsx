"use client"

import { useEffect, useState } from "react"
import { useRouter, useParams } from "next/navigation"
import { supabase } from "@/lib/supabase/client"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Textarea } from "@/components/ui/textarea"
import { Switch } from "@/components/ui/switch"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { useToast } from "@/components/ui/use-toast"
import { Sheet, SheetContent, Header as SheetHeader, SheetTitle, SheetTrigger } from "@/components/ui/sheet"
import { Trash2, Plus, GripVertical, FileText, Video as VideoIcon } from "lucide-react"

export default function CourseEditPage() {
  const { id } = useParams()
  const isNew = id === "new"
  const router = useRouter()
  const { toast } = useToast()

  const [loading, setLoading] = useState(false)
  const [course, setCourse] = useState({
    title: "",
    description: "",
    category: "Programlama",
    city: "",
    instructor_name: "",
    duration_minutes: 0,
    has_certificate: false,
    is_published: false,
    thumbnail_url: ""
  })

  const [lessons, setLessons] = useState<any[]>([])
  const [editingLesson, setEditingQuestion] = useState<any>(null)
  const [showLessonForm, setShowLessonForm] = useState(false)

  useEffect(() => {
    if (!isNew) {
      loadCourse()
      loadLessons()
    }
  }, [id])

  async function loadCourse() {
    const { data } = await supabase.from("courses").select("*").eq("id", id).single()
    if (data) setCourse(data)
  }

  async function loadLessons() {
    const { data } = await supabase.from("lessons").select("*").eq("course_id", id).order("order_index")
    setLessons(data || [])
  }

  async function saveCourse() {
    setLoading(true)
    console.log("Saving course:", course)
    const payload = { ...course, city: course.city || null }

    try {
      let res
      if (isNew) {
        res = await supabase.from("courses").insert(payload).select().single()
      } else {
        res = await supabase.from("courses").update(payload).eq("id", id).select().single()
      }

      if (res.error) {
        console.error("Supabase Error:", res.error)
        toast({ title: "Hata", description: res.error.message, variant: "destructive" })
      } else {
        console.log("Save successful:", res.data)
        toast({ title: "Başarılı", description: "Kurs kaydedildi." })
        if (isNew && res.data) {
          router.push(`/courses/${res.data.id}`)
        } else {
          router.refresh()
        }
      }
    } catch (e: any) {
      console.error("General Error:", e)
      toast({ title: "Beklenmedik Hata", description: e.message, variant: "destructive" })
    } finally {
      setLoading(false)
    }
  }

  async function saveLesson(lessonData: any) {
    const payload = {
      ...lessonData,
      course_id: id,
      order_index: lessonData.order_index || lessons.length
    }
    const { error } = await supabase.from("lessons").upsert(payload)
    if (!error) {
      setShowLessonForm(false)
      loadLessons()
    }
  }

  return (
    <div className="max-w-4xl mx-auto space-y-8 pb-20">
      <div className="flex items-center justify-between">
        <h1 className="text-2xl font-bold">{isNew ? "Yeni Eğitim Oluştur" : "Eğitimi Düzenle"}</h1>
        <Button onClick={saveCourse} disabled={loading}>
          {loading ? "Kaydediliyor..." : "Kaydet"}
        </Button>
      </div>

      <Card>
        <CardHeader><CardTitle>Temel Bilgiler</CardTitle></CardHeader>
        <CardContent className="grid gap-4">
          <div className="grid gap-2">
            <Label htmlFor="title">Eğitim Başlığı</Label>
            <Input id="title" value={course.title} onChange={e => setCourse({...course, title: e.target.value})} />
          </div>
          <div className="grid gap-2">
            <Label htmlFor="desc">Açıklama</Label>
            <Textarea id="desc" value={course.description} onChange={e => setCourse({...course, description: e.target.value})} />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div className="grid gap-2">
              <Label>Kategori</Label>
              <Select value={course.category} onValueChange={v => setCourse({...course, category: v})}>
                <SelectTrigger><SelectValue /></SelectTrigger>
                <SelectContent>
                  {["Programlama", "Tasarım", "İş Geliştirme", "Liderlik", "Diğer"].map(c => (
                    <SelectItem key={c} value={c}>{c}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>
            <div className="grid gap-2">
              <Label>Şehir (Tüm şehirler için boş bırakın)</Label>
              <Input value={course.city || ""} onChange={e => setCourse({...course, city: e.target.value})} />
            </div>
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div className="grid gap-2">
              <Label>Eğitmen İsmi</Label>
              <Input value={course.instructor_name} onChange={e => setCourse({...course, instructor_name: e.target.value})} />
            </div>
            <div className="grid gap-2">
              <Label>Toplam Süre (Dakika)</Label>
              <Input type="number" value={course.duration_minutes} onChange={e => setCourse({...course, duration_minutes: parseInt(e.target.value)})} />
            </div>
          </div>
          <div className="flex items-center gap-8 pt-2">
            <div className="flex items-center gap-2">
              <Switch checked={course.has_certificate} onCheckedChange={v => setCourse({...course, has_certificate: v})} />
              <Label>Sertifika Verilsin</Label>
            </div>
            <div className="flex items-center gap-2">
              <Switch checked={course.is_published} onCheckedChange={v => setCourse({...course, is_published: v})} />
              <Label>Yayında</Label>
            </div>
          </div>
        </CardContent>
      </Card>

      {!isNew && (
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle>Dersler</CardTitle>
            <Button size="sm" onClick={() => { setEditingQuestion({ title: "", content_type: "text", content_markdown: "", video_url: "" }); setShowLessonForm(true) }}>
              <Plus size={16} className="mr-2" /> Ders Ekle
            </Button>
          </CardHeader>
          <CardContent>
            <div className="space-y-2">
              {lessons.map((lesson, idx) => (
                <div key={lesson.id} className="flex items-center gap-3 p-3 border rounded-lg bg-slate-50">
                  <GripVertical size={16} className="text-slate-400" />
                  <div className="w-6 h-6 rounded-full bg-slate-200 flex items-center justify-center text-xs font-bold">{idx + 1}</div>
                  <div className="flex-1 font-medium">{lesson.title}</div>
                  {lesson.content_type === "video" ? <VideoIcon size={16} /> : <FileText size={16} />}
                  <Button variant="ghost" size="sm" onClick={() => { setEditingQuestion(lesson); setShowLessonForm(true) }}>Düzenle</Button>
                  <Button variant="ghost" size="sm" className="text-red-500"><Trash2 size={16} /></Button>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      )}

      <Sheet open={showLessonForm} onOpenChange={setShowLessonForm}>
        <SheetContent className="sm:max-w-xl overflow-y-auto">
          <div className="space-y-6 pt-6">
            <h2 className="text-lg font-bold">Ders Düzenle</h2>
            <div className="grid gap-4">
              <div className="grid gap-2">
                <Label>Ders Başlığı</Label>
                <Input value={editingLesson?.title} onChange={e => setEditingQuestion({...editingLesson, title: e.target.value})} />
              </div>
              <div className="grid gap-2">
                <Label>İçerik Tipi</Label>
                <div className="flex gap-4">
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input type="radio" checked={editingLesson?.content_type === "text"} onChange={() => setEditingQuestion({...editingLesson, content_type: "text"})} /> Metin
                  </label>
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input type="radio" checked={editingLesson?.content_type === "video"} onChange={() => setEditingQuestion({...editingLesson, content_type: "video"})} /> Video
                  </label>
                </div>
              </div>
              {editingLesson?.content_type === "text" ? (
                <div className="grid gap-2">
                  <Label>İçerik (Markdown)</Label>
                  <Textarea rows={10} value={editingLesson?.content_markdown} onChange={e => setEditingQuestion({...editingLesson, content_markdown: e.target.value})} />
                </div>
              ) : (
                <div className="grid gap-2">
                  <Label>Video URL</Label>
                  <Input value={editingLesson?.video_url} onChange={e => setEditingQuestion({...editingLesson, video_url: e.target.value})} />
                </div>
              )}
              <Button onClick={() => saveLesson(editingLesson)}>Kaydet</Button>
            </div>
          </div>
        </SheetContent>
      </Sheet>
    </div>
  )
}
