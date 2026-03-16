"use client"

import { Input } from "@/components/ui/input"
import { Search } from "lucide-react"
import { useRouter, useSearchParams } from "next/navigation"

export default function TopSearch() {
  const router = useRouter()
  const searchParams = useSearchParams()

  const handleSearch = (term: string) => {
    const params = new URLSearchParams(searchParams.toString())
    if (term) {
      params.set("search", term)
    } else {
      params.delete("search")
    }
    
    // If not on home page, go to home page with the search param
    if (window.location.pathname !== "/home") {
        router.push(`/home?${params.toString()}`)
    } else {
        router.push(`/home?${params.toString()}`, { scroll: false })
    }
  }

  return (
    <div className="relative flex-1 max-w-md hidden md:block">
        <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-300" />
        <Input 
            placeholder="Eğitim, mentor veya konu ara..." 
            onChange={(e) => handleSearch(e.target.value)}
            className="pl-11 h-11 bg-slate-50 border-none rounded-[1rem] focus:bg-white focus:ring-primary/10 transition-all font-medium text-sm"
        />
    </div>
  )
}
