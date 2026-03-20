"use client"

import { useState, useEffect, Suspense } from "react"
import { useParams, useRouter, useSearchParams } from "next/navigation"
import { createBrowserClient } from "@supabase/ssr"
import { 
  ChevronLeft, 
  ChevronRight, 
  PlayCircle, 
  FileText, 
  CheckCircle2, 
  Menu,
  ChevronDown,
  ArrowLeft,
  Settings,
  Maximize2,
  Volume2,
  Lock,
  MessageSquare,
  Trophy
} from "lucide-react"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Progress } from "@/components/ui/progress"
import Link from "next/link"
import ReactMarkdown from 'react-markdown'

export default function CourseWatchPage() {
  return (
    <Suspense fallback={<div className="h-screen flex items-center justify-center bg-slate-900 text-white font-black animate-pulse uppercase tracking-[0.3em]">Yükleniyor...</div>}>
      <CourseWatchContent />
    </Suspense>
  )
}

function CourseWatchContent() {
  const { id } = useParams()
  const searchParams = useSearchParams()
  const currentLessonId = searchParams.get("lesson")
  const router = useRouter()
  
  const [course, setCourse] = useState<any>(null)
  const [lessons, setLessons] = useState<any[]>([])
  const [completedLessonIds, setCompletedLessonIds] = useState<string[]>([])
  const [currentLesson, setCurrentLesson] = useState<any>(null)
  const [loading, setLoading] = useState(true)
  const [isSidebarOpen, setIsSidebarOpen] = useState(true)

  const supabase = createBrowserClient(
    process.env.NEXT_PUBLIC_SUPABASE_URL!,
    process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!
  )

  useEffect(() => {
    async function loadData() {
      const { data: { session } } = await supabase.auth.getSession()
      
      // Load Course
      const { data: courseData } = await supabase.from("courses").select("*").eq("id", id).single()
      setCourse(courseData)

      // Load Lessons
      if (session?.user) {
        // 1. Check/Ensure Enrollment (Critical for RLS)
        const { data: enrollment } = await supabase
          .from("enrollments")
          .select("*")
          .eq("course_id", id)
          .eq("profile_id", session.user.id)
          .single()

        if (!enrollment) {
          await supabase.from("enrollments").upsert({
            course_id: id,
            profile_id: session.user.id
          })
        }

        // 2. Now Fetch Lessons (After potential enrollment)
        const { data: lessonsData } = await supabase
          .from("lessons")
          .select("*")
          .eq("course_id", id)
          .order("order_index")
        
        const fetchedLessons = lessonsData || []
        setLessons(fetchedLessons)
        
        // Set Current Lesson
        if (fetchedLessons.length > 0) {
          if (currentLessonId) {
            const match = fetchedLessons.find(l => l.id === currentLessonId)
            setCurrentLesson(match || fetchedLessons[0])
          } else if (!currentLesson) {
            setCurrentLesson(fetchedLessons[0])
          }
        }

        // 3. Load Completions
        const { data: completions } = await supabase
          .from("lesson_completions")
          .select("lesson_id")
          .eq("profile_id", session.user.id)
        
        if (completions) {
          setCompletedLessonIds(completions.map(c => c.lesson_id))
        }
      }
      setLoading(false)
    }
    loadData()
  }, [id, currentLessonId])

  const toggleCompletion = async () => {
    const { data: { session } } = await supabase.auth.getSession()
    if (!session?.user || !currentLesson) return

    const isCompleted = completedLessonIds.includes(currentLesson.id)

    if (isCompleted) {
      const { error } = await supabase
        .from("lesson_completions")
        .delete()
        .eq("profile_id", session.user.id)
        .eq("lesson_id", currentLesson.id)
      
      if (!error) {
        setCompletedLessonIds(prev => prev.filter(id => id !== currentLesson.id))
      }
    } else {
      const { error } = await supabase
        .from("lesson_completions")
        .insert({
          profile_id: session.user.id,
          lesson_id: currentLesson.id
        })
      
      if (!error) {
        setCompletedLessonIds(prev => [...prev, currentLesson.id])
      }
    }
  }

  const handleLessonSelect = (lesson: any) => {
    router.push(`/courses/${id}/watch?lesson=${lesson.id}`)
  }

  const progressPercentage = lessons.length > 0 
    ? Math.round((completedLessonIds.length / lessons.length) * 100) 
    : 0

  if (loading) return <div className="h-screen flex items-center justify-center bg-white text-primary font-black animate-pulse uppercase tracking-[0.3em]">Eğitim Yükleniyor...</div>

  return (
    <div className="flex h-screen bg-white overflow-hidden text-slate-900">
      {/* Main Content Area */}
      <div className="flex-1 flex flex-col h-full overflow-hidden">
        {/* Top Header/Bar */}
        <header className="h-16 flex items-center justify-between px-6 bg-white border-b border-slate-100 shrink-0">
          <div className="flex items-center gap-4">
             <Link href={`/courses/${id}`} className="p-2 hover:bg-slate-50 rounded-xl transition-colors text-slate-400">
                <ArrowLeft size={20} />
             </Link>
             <div className="flex flex-col">
                <span className="text-[10px] uppercase font-black text-slate-400">Şu An İzlenen</span>
                <span className="text-sm font-bold text-slate-900 line-clamp-1">{course?.title}</span>
             </div>
          </div>
          
          <div className="flex items-center gap-4">
             <Link href={`/courses/${id}/quiz`} className="flex items-center gap-2 p-2 px-4 bg-orange-500/10 text-orange-500 hover:bg-orange-500/20 rounded-xl transition-all text-xs font-bold uppercase tracking-widest border border-orange-500/20">
                <Trophy size={16} /> Final Sınavı
             </Link>
             <button onClick={() => setIsSidebarOpen(!isSidebarOpen)} className="lg:hidden p-2 hover:bg-slate-800 rounded-xl transition-colors">
                <Menu size={20} />
             </button>
          </div>
        </header>

        {/* Player / Content View */}
        <main className="flex-1 overflow-y-auto custom-scrollbar p-6 lg:p-10 bg-slate-50">
            <div className="max-w-5xl mx-auto space-y-10">
                {/* Visual Area */}
                <div className="relative aspect-video bg-slate-900 rounded-[2.5rem] overflow-hidden shadow-2xl shadow-indigo-100/50 border border-slate-200 group">
                    {currentLesson?.content_type === "video" ? (
                        <div className="w-full h-full">
                            <iframe 
                                src={currentLesson.video_url?.includes('youtube.com') 
                                    ? currentLesson.video_url.replace('watch?v=', 'embed/') 
                                    : currentLesson.video_url}
                                className="w-full h-full"
                                allowFullScreen
                                title={currentLesson.title}
                            />
                        </div>
                    ) : (
                        <div className="w-full h-full bg-white overflow-hidden flex flex-col">
                            <div className="p-8 border-b border-slate-100 bg-slate-50/50 flex items-center justify-between shrink-0">
                                <div className="flex items-center gap-4">
                                    <div className="w-10 h-10 rounded-xl bg-primary text-white flex items-center justify-center">
                                        <FileText size={22} />
                                    </div>
                                    <div>
                                        <h2 className="text-lg font-black text-slate-900">{currentLesson?.title}</h2>
                                        <p className="text-[10px] uppercase font-bold text-slate-400 tracking-widest">Ders Notları & Okuma Materyali</p>
                                    </div>
                                </div>
                            </div>
                            <div className="flex-1 overflow-y-auto p-12 custom-scrollbar">
                                <article className="max-w-3xl mx-auto">
                                    {currentLesson?.content_markdown ? (
                                        <div className="markdown-content">
                                            <ReactMarkdown>{currentLesson.content_markdown}</ReactMarkdown>
                                        </div>
                                    ) : (
                                        <div className="text-center py-20 text-slate-400 italic">
                                            Bu ders için henüz bir içerik girilmemiş.
                                        </div>
                                    )}
                                </article>
                            </div>
                        </div>
                    )}
                    
                    {/* Floating Controls Overlay Style */}
                    <div className="absolute inset-x-0 bottom-0 p-6 bg-gradient-to-t from-black/80 to-transparent opacity-0 group-hover:opacity-100 transition-opacity">
                        <div className="flex items-center justify-between">
                            <div className="flex items-center gap-4">
                                <button className="p-2 bg-white/10 hover:bg-white/20 rounded-lg backdrop-blur-md transition-all">
                                    <Volume2 size={18} />
                                </button>
                                <span className="text-xs font-bold text-white/60">04:20 / 12:45</span>
                            </div>
                            <button className="p-2 bg-white/10 hover:bg-white/20 rounded-lg backdrop-blur-md transition-all">
                                <Maximize2 size={18} />
                            </button>
                        </div>
                    </div>
                </div>

                {/* Lesson Description */}
                <div className="space-y-6">
                    <div className="flex flex-col md:flex-row md:items-center justify-between gap-6 border-b border-slate-100 pb-8">
                        <div className="space-y-2">
                             <div className="flex items-center gap-3">
                                <Badge className="bg-indigo-50 text-primary border-none font-black text-[10px] px-3 py-1 rounded-full uppercase tracking-widest">
                                    {lessons.length > 0 && currentLesson ? (lessons.indexOf(currentLesson) + 1) : "?"}. Ders
                                </Badge>
                                <div className="text-xs text-slate-400 font-bold uppercase tracking-tighter">İçerik Yayında</div>
                             </div>
                             <h1 className="text-3xl font-black text-slate-900 tracking-tight">{currentLesson?.title || course?.title}</h1>
                        </div>
                        <div className="flex items-center gap-3">
                            <Button 
                                variant="outline" 
                                onClick={() => document.getElementById('lesson-notes')?.scrollIntoView({ behavior: 'smooth' })}
                                className="h-12 border-slate-200 bg-white hover:bg-slate-50 font-bold rounded-2xl px-6 gap-2 text-slate-600"
                            >
                                <FileText size={18} /> Kaynaklar
                            </Button>
                            <Button 
                                onClick={toggleCompletion}
                                className={`h-12 font-black rounded-2xl px-8 shadow-xl gap-2 transition-all ${
                                    completedLessonIds.includes(currentLesson?.id)
                                    ? "bg-emerald-500 hover:bg-emerald-600 text-white shadow-emerald-500/20"
                                    : "bg-primary hover:bg-indigo-600 text-white shadow-indigo-500/20"
                                }`}
                            >
                                {completedLessonIds.includes(currentLesson?.id) ? (
                                    <>Tamamlandı <CheckCircle2 size={18} /></>
                                ) : (
                                    <>Tamamlandı Olarak İşaretle <CheckCircle2 size={18} /></>
                                )}
                            </Button>
                        </div>
                    </div>

                    <article id="lesson-notes" className="max-w-none text-slate-900 leading-relaxed text-lg min-h-[200px]">
                        <div className="flex items-center gap-3 mb-8">
                             <div className="w-1 h-8 bg-primary rounded-full"></div>
                             <h3 className="text-xl font-black text-slate-900">Ders Notları</h3>
                        </div>
                        {currentLesson?.content_markdown ? (
                            <div className="markdown-content bg-white p-8 md:p-12 rounded-[2rem] border border-slate-100 shadow-sm">
                                <ReactMarkdown>{currentLesson.content_markdown}</ReactMarkdown>
                            </div>
                        ) : (
                            <div className="p-12 rounded-3xl bg-slate-50 border border-slate-100 text-slate-400 italic flex items-center justify-center text-center">
                                {lessons.length > 0 
                                    ? "Bu ders için henüz bir yazılı içerik hazırlanmamış." 
                                    : "Bu eğitim kapsamında henüz bir ders içeriği bulunmuyor."}
                            </div>
                        )}
                    </article>
                </div>
            </div>
        </main>

        {/* Footer Navigation */}
        <footer className="h-20 bg-white border-t border-slate-100 flex items-center justify-between px-8 shrink-0">
            <button 
                disabled={lessons.indexOf(currentLesson) === 0}
                onClick={() => handleLessonSelect(lessons[lessons.indexOf(currentLesson) - 1])}
                className="flex items-center gap-3 font-black text-xs uppercase tracking-widest text-slate-500 hover:text-white transition-colors disabled:opacity-30"
            >
                <ChevronLeft size={20} /> Önceki Ders
            </button>
            
            <div className="hidden sm:flex items-center gap-2 group cursor-pointer" onClick={() => setIsSidebarOpen(true)}>
                <span className="text-[10px] font-black uppercase text-slate-500 tracking-widest">Sıradaki:</span>
                <span className="text-sm font-bold text-slate-300 group-hover:text-primary transition-colors">
                    {lessons[lessons.indexOf(currentLesson) + 1]?.title || "Eğitim Tamamlandı!"}
                </span>
            </div>

            <button 
                disabled={lessons.indexOf(currentLesson) === lessons.length - 1}
                onClick={() => handleLessonSelect(lessons[lessons.indexOf(currentLesson) + 1])}
                className="flex items-center gap-3 font-black text-xs uppercase tracking-widest text-primary hover:text-indigo-400 transition-colors disabled:opacity-30"
            >
                Sonraki Ders <ChevronRight size={20} />
            </button>
        </footer>
      </div>

      {/* Sidebar - Curriculum */}
      <aside className={`w-[400px] bg-slate-50 flex flex-col shrink-0 transition-all duration-500 absolute lg:relative inset-y-0 right-0 z-[100] border-l border-slate-100 ${isSidebarOpen ? "translate-x-0" : "translate-x-full lg:w-0 lg:overflow-hidden"}`}>
         <div className="h-16 flex items-center justify-between px-6 bg-white border-b border-slate-100 shrink-0">
             <h3 className="font-black text-sm uppercase tracking-widest text-slate-900">Eğitim Müfredatı</h3>
             <button onClick={() => setIsSidebarOpen(false)} className="p-2 hover:bg-slate-50 rounded-xl transition-colors text-slate-400">
                <ChevronRight size={20} />
             </button>
         </div>

         <div className="p-6 space-y-4">
            <div className="flex justify-between items-center mb-2">
                <span className="text-xs font-bold text-slate-500">Toplam İlerleme</span>
                <span className="text-xs font-black text-primary">%{progressPercentage}</span>
            </div>
            <Progress value={progressPercentage} className="h-2 bg-slate-200" />
         </div>

         <div className="flex-1 overflow-y-auto custom-scrollbar pb-10">
             {lessons.map((lesson, idx) => {
                 const isActive = currentLesson?.id === lesson.id
                 const isCompleted = completedLessonIds.includes(lesson.id)
                 return (
                    <div 
                        key={lesson.id} 
                        onClick={() => handleLessonSelect(lesson)}
                        className={`flex items-start gap-4 p-6 cursor-pointer transition-all border-l-4 ${
                            isActive 
                            ? "bg-indigo-50/50 border-primary" 
                            : "border-transparent bg-transparent hover:bg-slate-50"
                        }`}
                    >
                        <div className={`w-10 h-10 rounded-xl border flex items-center justify-center shrink-0 transition-all ${
                            isActive 
                            ? "bg-primary border-primary text-white shadow-lg shadow-indigo-500/20" 
                            : isCompleted 
                                ? "bg-emerald-50 border-emerald-100 text-emerald-600"
                                : "bg-white border-slate-200 text-slate-400"
                        }`}>
                            {isCompleted ? <CheckCircle2 size={18} /> : lesson.content_type === "video" ? <PlayCircle size={18} /> : <FileText size={18} />}
                        </div>
                        <div className="flex-1 min-w-0">
                            <div className="flex items-center justify-between gap-2 mb-1">
                                <span className={`text-[10px] font-black uppercase tracking-widest ${isActive ? "text-primary" : "text-slate-400"}`}>{idx + 1}. Ders</span>
                                {isCompleted && <CheckCircle2 size={14} className="text-emerald-500" />}
                            </div>
                            <h4 className={`text-sm font-bold leading-snug line-clamp-2 ${isActive ? "text-slate-900" : "text-slate-600"}`}>
                                {lesson.title}
                            </h4>
                            <div className="mt-2 text-[10px] font-bold text-slate-400 uppercase tracking-tighter">{lesson.content_type === "video" ? "VİDEO" : "METİN"}</div>
                        </div>
                    </div>
                 )
             })}
         </div>
      </aside>

      {/* Styles for custom scrollbar */}
      <style jsx global>{`
        .custom-scrollbar::-webkit-scrollbar {
          width: 5px;
        }
        .custom-scrollbar::-webkit-scrollbar-track {
          background: transparent;
        }
        .custom-scrollbar::-webkit-scrollbar-thumb {
          background: #334155;
          border-radius: 10px;
        }
        .custom-scrollbar::-webkit-scrollbar-thumb:hover {
          background: #475569;
        }
      `}</style>
    </div>
  )
}
