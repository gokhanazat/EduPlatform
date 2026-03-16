"use client"

import { Input } from "@/components/ui/input"
import { Search } from "lucide-react"
import { useRouter, useSearchParams } from "next/navigation"
import { useTransition } from "react"

export default function SearchInput({ defaultValue }: { defaultValue: string }) {
  const router = useRouter()
  const searchParams = useSearchParams()
  const [isPending, startTransition] = useTransition()

  const handleSearch = (term: string) => {
    const params = new URLSearchParams(searchParams.toString())
    if (term) {
      params.set("search", term)
    } else {
      params.delete("search")
    }
    
    startTransition(() => {
        router.push(`/home?${params.toString()}`)
    })
  }

  return (
    <div className="relative flex-1 lg:w-80">
        <Search className={`absolute left-3 top-3 h-4 w-4 ${isPending ? "text-primary animate-pulse" : "text-slate-400"}`} />
        <Input 
            defaultValue={defaultValue}
            onChange={(e) => handleSearch(e.target.value)}
            placeholder="Eğitim başlığı ile ara..." 
            className="pl-10 h-12 rounded-2xl bg-white border-slate-100 shadow-sm transition-all focus:ring-primary/20" 
        />
    </div>
  )
}
