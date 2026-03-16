"use client"

import { useState, useEffect } from "react"
import { useParams, useRouter } from "next/navigation"
import { createBrowserClient } from "@supabase/ssr"
import { 
  ChevronRight, 
  ChevronLeft, 
  Clock, 
  Trophy, 
  AlertCircle, 
  CheckCircle2, 
  XCircle,
  ArrowRight,
  Monitor,
  Timer
} from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card, CardContent } from "@/components/ui/card"
import { Progress } from "@/components/ui/progress"
import { Badge } from "@/components/ui/badge"

export default function StudentQuizPage() {
  const { id: courseId } = useParams()
  const router = useRouter()
  
  const [course, setCourse] = useState<any>(null)
  const [quiz, setQuiz] = useState<any>(null)
  const [questions, setQuestions] = useState<any[]>([])
  const [currentQuestionIndex, setCurrentQuestionIndex] = useState(-1) // -1 is the intro screen
  const [answers, setAnswers] = useState<Record<string, string>>({})
  const [loading, setLoading] = useState(true)
  const [timeLeft, setTimeLeft] = useState<number | null>(null)
  const [isFinished, setIsFinished] = useState(false)
  const [score, setScore] = useState(0)
  const [saving, setSaving] = useState(false)

  const supabase = createBrowserClient(
    process.env.NEXT_PUBLIC_SUPABASE_URL!,
    process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!
  )

  useEffect(() => {
    async function loadQuiz() {
      // Load Course info
      const { data: courseData } = await supabase.from("courses").select("*").eq("id", courseId).single()
      setCourse(courseData)

      const { data } = await supabase
        .from("quizzes")
        .select("*, questions(*, options(*))")
        .eq("course_id", courseId)
        .single()

      if (data) {
        setQuiz(data)
        setQuestions(data.questions.sort((a: any, b: any) => a.order_index - b.order_index) || [])
        if (data.time_limit_minutes) {
          setTimeLeft(data.time_limit_minutes * 60)
        }
      }
      setLoading(false)
    }
    loadQuiz()
  }, [courseId])

  useEffect(() => {
    if (currentQuestionIndex >= 0 && !isFinished && timeLeft !== null && timeLeft > 0) {
      const timer = setInterval(() => {
        setTimeLeft(prev => (prev !== null ? prev - 1 : null))
      }, 1000)
      return () => clearInterval(timer)
    } else if (timeLeft === 0 && !isFinished) {
      finishQuiz()
    }
  }, [currentQuestionIndex, timeLeft, isFinished])

  const startQuiz = () => setCurrentQuestionIndex(0)

  const handleOptionSelect = (questionId: string, optionId: string) => {
    if (isFinished) return
    setAnswers({ ...answers, [questionId]: optionId })
  }

  const finishQuiz = async () => {
    setSaving(true)
    let correctCount = 0
    questions.forEach(q => {
      const selectedOption = q.options.find((o: any) => o.id === answers[q.id])
      if (selectedOption?.is_correct) {
        correctCount++
      }
    })
    const finalScore = (correctCount / questions.length) * 100
    setScore(finalScore)
    setIsFinished(true)

    // Save result and check for certificate
    const { data: { session } } = await supabase.auth.getSession()
    if (session?.user) {
      const isPassed = finalScore >= (quiz.pass_score_percent || 70)
      
      await supabase.from("quiz_results").insert({
        profile_id: session.user.id,
        quiz_id: quiz.id,
        score: Math.round(finalScore),
        passed: isPassed
      })

      if (isPassed) {
        // Check if certificate already exists
        const { data: existing } = await supabase
          .from("certificates")
          .select("id")
          .eq("profile_id", session.user.id)
          .eq("course_id", courseId)
          .single()
        
        if (!existing) {
          await supabase.from("certificates").insert({
            profile_id: session.user.id,
            course_id: courseId,
            certificate_number: `ITSO-${Math.random().toString(36).substring(2, 10).toUpperCase()}`
          })
        }
      }
    }
    setSaving(false)
  }

  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60)
    const secs = seconds % 60
    return `${mins}:${secs.toString().padStart(2, '0')}`
  }

  if (loading) return <div className="h-screen flex items-center justify-center font-black animate-pulse text-primary tracking-widest uppercase">Sınav Hazırlanıyor...</div>

  if (!quiz || questions.length === 0) {
    return (
      <div className="h-screen flex items-center justify-center p-8 bg-slate-50">
        <Card className="max-w-md w-full border-none shadow-2xl rounded-[2.5rem] bg-white p-12 text-center space-y-6">
           <div className="w-20 h-20 bg-slate-100 rounded-full flex items-center justify-center mx-auto text-slate-300">
              <AlertCircle size={48} />
           </div>
           <h2 className="text-2xl font-black text-slate-900">Sınav Henüz Hazır Değil</h2>
           <p className="text-slate-500">Bu eğitim için henüz soru eklenmemiş.</p>
           <Button onClick={() => router.back()} className="w-full h-12 rounded-2xl bg-primary">Geri Dön</Button>
         </Card>
      </div>
    )
  }

  // Result Screen
  if (isFinished) {
    const isPassed = score >= (quiz.pass_score_percent || 70)
    return (
      <div className="min-h-screen flex items-center justify-center p-8 bg-[#F8FAFC]">
        <Card className="max-w-xl w-full border-none shadow-2xl rounded-[3rem] bg-white overflow-hidden animate-in zoom-in duration-500">
           <div className={`h-4 ${isPassed ? "bg-emerald-500" : "bg-red-500"}`}></div>
           <CardContent className="p-12 text-center space-y-10">
              <div className={`w-32 h-32 rounded-[2.5rem] flex items-center justify-center mx-auto shadow-2xl ${isPassed ? "bg-emerald-50 text-emerald-500" : "bg-red-50 text-red-500"}`}>
                 {isPassed ? <Trophy size={64} /> : <XCircle size={64} />}
              </div>
              
              <div className="space-y-4">
                 <h2 className="text-4xl font-black text-slate-900 tracking-tight">
                    {isPassed ? "Tebrikler!" : "Biraz Daha Çalışmalısın"}
                 </h2>
                 <p className="text-slate-500 font-medium"><span className="text-slate-900 font-bold">{course?.title}</span> <br /> eğitimini tamamladınız.</p>
              </div>

              <div className="grid grid-cols-2 gap-6">
                 <div className="p-6 rounded-[2rem] bg-slate-50 border border-slate-100">
                    <div className="text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1">Skorunuz</div>
                    <div className={`text-4xl font-black ${isPassed ? "text-emerald-500" : "text-red-500"}`}>%{Math.round(score)}</div>
                 </div>
                 <div className="p-6 rounded-[2rem] bg-slate-50 border border-slate-100">
                    <div className="text-[10px] font-bold text-slate-400 uppercase tracking-widest mb-1">Geçme Notu</div>
                    <div className="text-4xl font-black text-slate-900">%{quiz.pass_score_percent}</div>
                 </div>
              </div>

              <div className="pt-8 flex flex-col gap-4">
                {isPassed ? (
                    <Button onClick={() => router.push(`/certificates`)} className="h-16 rounded-2xl bg-emerald-500 hover:bg-emerald-600 text-white font-black text-lg shadow-xl shadow-emerald-100 gap-2">
                        Sertifikayı Görüntüle <Trophy size={20} />
                    </Button>
                ) : (
                    <Button onClick={() => window.location.reload()} className="h-16 rounded-2xl bg-primary text-white font-black text-lg shadow-xl shadow-indigo-100 gap-2">
                        Tekrar Dene <ArrowRight size={20} />
                    </Button>
                )}
                <Button variant="ghost" onClick={() => router.push(`/courses/${courseId}`)} className="h-14 rounded-2xl text-slate-400 font-bold hover:text-slate-600">
                    Eğitime Geri Dön
                </Button>
              </div>
           </CardContent>
        </Card>
      </div>
    )
  }

  // Intro Screen
  if (currentQuestionIndex === -1) {
    return (
      <div className="min-h-screen flex items-center justify-center p-8 bg-[#F8FAFC]">
        <Card className="max-w-2xl w-full border-none shadow-2xl rounded-[3rem] bg-white overflow-hidden">
            <div className="px-12 pt-16 pb-12 space-y-10">
                <div className="space-y-4 text-center">
                    <Badge className="bg-amber-50 text-amber-600 border-none font-black px-4 py-1.5 rounded-full uppercase tracking-widest text-[10px]">Bilgini Test Et</Badge>
                    <h1 className="text-4xl font-black text-slate-900 tracking-tight">Final Sınavı</h1>
                    <p className="text-slate-500 font-medium">Bilgini ölç ve profesyonel sertifikanı almaya hak kazan.</p>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    <div className="p-6 rounded-3xl bg-slate-50 flex flex-col items-center gap-3 text-center">
                        <div className="w-10 h-10 rounded-xl bg-white flex items-center justify-center text-primary shadow-sm"><Timer size={22} /></div>
                        <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Süre</div>
                        <div className="text-lg font-black text-slate-900">{quiz.time_limit_minutes ? `${quiz.time_limit_minutes} Dakika` : "Sınırsız"}</div>
                    </div>
                    <div className="p-6 rounded-3xl bg-slate-50 flex flex-col items-center gap-3 text-center">
                        <div className="w-10 h-10 rounded-xl bg-white flex items-center justify-center text-emerald-500 shadow-sm"><CheckCircle2 size={22} /></div>
                        <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Geçme Notu</div>
                        <div className="text-lg font-black text-slate-900">%{quiz.pass_score_percent}</div>
                    </div>
                    <div className="p-6 rounded-3xl bg-slate-50 flex flex-col items-center gap-3 text-center">
                        <div className="w-10 h-10 rounded-xl bg-white flex items-center justify-center text-indigo-500 shadow-sm"><Monitor size={22} /></div>
                        <div className="text-[10px] font-bold text-slate-400 uppercase tracking-wider">Soru Sayısı</div>
                        <div className="text-lg font-black text-slate-900">{questions.length} Soru</div>
                    </div>
                </div>

                <div className="bg-indigo-50 rounded-3xl p-6 flex gap-4">
                    <AlertCircle className="text-indigo-400 shrink-0" size={24} />
                    <p className="text-sm text-indigo-700 font-medium leading-relaxed">
                        İnternet bağlantınızın stabil olduğundan emin olun. Sınav başladığında süre geri sayımı başlar ve durdurulamaz.
                    </p>
                </div>

                <div className="flex flex-col sm:flex-row gap-4 pt-4">
                    <Button variant="outline" onClick={() => router.back()} className="h-14 rounded-2xl flex-1 border-slate-200 font-bold text-slate-500">Daha Sonra</Button>
                    <Button onClick={startQuiz} className="h-14 rounded-2xl flex-[2] bg-primary text-white font-black text-lg shadow-xl shadow-indigo-100 gap-2 uppercase tracking-widest">
                        Sınava Başla <ChevronRight size={20} />
                    </Button>
                </div>
            </div>
        </Card>
      </div>
    )
  }

  // Active Quiz Screen
  const currentQuestion = questions[currentQuestionIndex]
  const progressPercent = ((currentQuestionIndex + 1) / questions.length) * 100

  return (
    <div className="min-h-screen bg-[#F8FAFC] flex flex-col">
      {/* Quiz Header */}
      <header className="h-20 bg-white border-b border-slate-100 flex items-center justify-between px-8 sticky top-0 z-50">
        <div className="flex items-center gap-4">
            <h2 className="text-lg font-black text-slate-900 tracking-tight">Final Sınavı</h2>
            <div className="w-[1px] h-6 bg-slate-100 mx-2"></div>
            <div className="text-sm font-bold text-slate-400">{currentQuestionIndex + 1} / {questions.length} Soru</div>
        </div>

        <div className="flex items-center gap-6">
            {timeLeft !== null && (
                <div className={`flex items-center gap-2 font-black text-sm px-4 py-2 rounded-xl ${timeLeft < 60 ? "bg-red-50 text-red-500 animate-pulse" : "bg-slate-50 text-slate-600"}`}>
                    <Clock size={18} /> {formatTime(timeLeft)}
                </div>
            )}
            <Button variant="ghost" className="font-bold text-red-500 hover:bg-red-50" onClick={() => router.back()}>Çıkış</Button>
        </div>
      </header>

      <div className="flex-1 max-w-4xl w-full mx-auto p-8 space-y-8">
        {/* Progress Bar */}
        <div className="space-y-2">
            <div className="flex justify-between text-[10px] font-black uppercase tracking-widest text-slate-400 px-2">
                <span>Sınav İlerlemesi</span>
                <span>%{Math.round(progressPercent)}</span>
            </div>
            <Progress value={progressPercent} className="h-3 rounded-full bg-slate-200" />
        </div>

        {/* Question Card */}
        <Card className="border-none shadow-2xl shadow-indigo-100/30 rounded-[3rem] bg-white overflow-hidden animate-in slide-in-from-bottom-4 duration-500">
            <CardContent className="p-12 space-y-10">
                <h3 className="text-3xl font-black text-slate-900 leading-tight tracking-tight">
                    {currentQuestion.question_text}
                </h3>

                <div className="space-y-4">
                    {currentQuestion.options.map((option: any) => {
                        const isSelected = answers[currentQuestion.id] === option.id
                        return (
                            <button
                                key={option.id}
                                onClick={() => handleOptionSelect(currentQuestion.id, option.id)}
                                className={`w-full p-6 rounded-[2rem] border-2 text-left transition-all flex items-center gap-6 group ${
                                    isSelected 
                                    ? "border-primary bg-indigo-50/50 ring-4 ring-primary/5" 
                                    : "border-slate-50 bg-slate-50 hover:border-slate-200 hover:bg-white"
                                }`}
                            >
                                <div className={`w-8 h-8 rounded-full border-2 flex items-center justify-center shrink-0 transition-all ${
                                    isSelected 
                                    ? "bg-primary border-primary text-white scale-110 shadow-lg shadow-indigo-200" 
                                    : "bg-white border-slate-200 group-hover:border-slate-300"
                                }}`}>
                                    {isSelected && <div className="w-2.5 h-2.5 bg-white rounded-full"></div>}
                                </div>
                                <span className={`text-lg font-bold transition-colors ${isSelected ? "text-slate-900" : "text-slate-500 group-hover:text-slate-700"}`}>
                                    {option.option_text}
                                </span>
                            </button>
                        )
                    })}
                </div>
            </CardContent>
        </Card>

        {/* Navigation Buttons */}
        <div className="flex items-center justify-between pt-4">
            <Button 
                disabled={currentQuestionIndex === 0}
                onClick={() => setCurrentQuestionIndex(prev => prev - 1)}
                className="h-14 rounded-2xl px-8 border-slate-200 bg-white text-slate-500 font-bold hover:bg-slate-50 gap-2"
                variant="outline"
            >
                <ChevronLeft size={20} /> Geri
            </Button>

            {currentQuestionIndex === questions.length - 1 ? (
                <Button 
                    onClick={finishQuiz}
                    disabled={!answers[currentQuestion.id] || saving}
                    className="h-14 rounded-2xl px-12 bg-emerald-500 hover:bg-emerald-600 text-white font-black text-lg shadow-xl shadow-emerald-100 uppercase tracking-widest"
                >
                    {saving ? "Kaydediliyor..." : "Sınavı Bitir"}
                </Button>
            ) : (
                <Button 
                    onClick={() => setCurrentQuestionIndex(prev => prev + 1)}
                    disabled={!answers[currentQuestion.id]}
                    className="h-14 rounded-2xl px-12 bg-primary text-white font-black text-lg shadow-xl shadow-indigo-100 gap-2 uppercase tracking-widest"
                >
                    Sonraki Soru <ChevronRight size={20} />
                </Button>
            )}
        </div>
      </div>
    </div>
  )
}
