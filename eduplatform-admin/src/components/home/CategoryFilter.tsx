"use client"

import { useRouter, useSearchParams } from "next/navigation"

export default function CategoryFilter({ 
    categories, 
    currentCategory 
}: { 
    categories: string[], 
    currentCategory: string 
}) {
  const router = useRouter()
  const searchParams = useSearchParams()

  const handleCategoryClick = (category: string) => {
    const params = new URLSearchParams(searchParams.toString())
    if (category === "Hepsi") {
      params.delete("category")
    } else {
      params.set("category", category)
    }
    router.push(`/home?${params.toString()}`)
  }

  return (
    <div className="flex items-center gap-2 overflow-x-auto pb-2 scrollbar-hide no-scrollbar w-full lg:w-auto">
        {categories.map((cat) => (
            <button 
                key={cat} 
                onClick={() => handleCategoryClick(cat)}
                className={`px-5 py-2.5 rounded-2xl text-sm font-bold whitespace-nowrap transition-all border ${
                    currentCategory === cat ? "bg-primary text-white border-primary shadow-lg shadow-indigo-100" : "bg-white text-slate-500 border-slate-100 hover:border-slate-300"
                }`}
            >
                {cat}
            </button>
        ))}
    </div>
  )
}
