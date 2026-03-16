"use client"

import { useState } from "react"
import { createBrowserClient } from "@supabase/ssr"
import { Button } from "@/components/ui/button"
import { CheckCircle, Play } from "lucide-react"
import { useRouter } from "next/navigation"

export default function EnrollButton({ 
  courseId, 
  userId, 
  isEnrolled,
  isFree 
}: { 
  courseId: string, 
  userId?: string, 
  isEnrolled: boolean,
  isFree: boolean
}) {
  const [loading, setLoading] = useState(false)
  const router = useRouter()
  const supabase = createBrowserClient(
    process.env.NEXT_PUBLIC_SUPABASE_URL!,
    process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!
  )

  const handleEnroll = async () => {
    if (!userId) {
        router.push("/login")
        return
    }
    setLoading(true)
    const { error } = await supabase.from('enrollments').insert({
        course_id: courseId,
        profile_id: userId
    })

    if (error) {
        alert("Kayıt hatası: " + error.message)
    } else {
        router.refresh()
    }
    setLoading(false)
  }

  if (isEnrolled) {
    return (
        <Button 
            onClick={() => router.push(`/courses/${courseId}/watch`)}
            className="w-full h-14 rounded-2xl btn-primary font-bold text-lg shadow-xl shadow-indigo-100/50"
        >
            <Play size={20} className="mr-2" fill="currentColor" /> Eğitime Başla
        </Button>
    )
  }

  return (
    <Button 
        onClick={handleEnroll}
        disabled={loading}
        className="w-full h-14 rounded-2xl bg-emerald-500 hover:bg-emerald-600 text-white font-bold text-lg shadow-xl shadow-emerald-200/50"
    >
        {loading ? "Kaydediliyor..." : <><CheckCircle size={20} className="mr-2" /> Şimdi Ücretsiz Kaydol</>}
    </Button>
  )
}
