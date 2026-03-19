"use client"

import { Button } from "@/components/ui/button"
import { Trash2 } from "lucide-react"
import { adminDeleteCourse } from "@/app/actions/admin-actions"
import { useRouter } from "next/navigation"
import { useToast } from "@/components/ui/use-toast"
import { useState } from "react"

export function DeleteCourseButton({ courseId }: { courseId: string }) {
  const router = useRouter()
  const { toast } = useToast()
  const [loading, setLoading] = useState(false)

  async function handleDelete() {
    if (!confirm("Bu kursu silmek istediğinizden emin misiniz?")) return
    
    setLoading(true)
    try {
      const res = await adminDeleteCourse(courseId)
      if (res.success) {
        toast({ title: "Başarılı", description: "Kurs silindi." })
        router.refresh()
      } else {
        toast({ title: "Hata", description: res.error || "Silme işlemi başarısız.", variant: "destructive" })
      }
    } catch (err: any) {
      toast({ title: "Hata", description: err.message, variant: "destructive" })
    } finally {
      setLoading(false)
    }
  }

  return (
    <Button 
      variant="outline" 
      size="icon" 
      className="h-9 w-9 rounded-xl border-slate-100 text-red-300 hover:text-red-500 hover:bg-red-50" 
      onClick={handleDelete}
      disabled={loading}
    >
      <Trash2 size={16} />
    </Button>
  )
}
