"use client"

import * as React from "react"
import { cn } from "@/lib/utils"

// Simple basic implementation to avoid Radix dependency
const DropdownMenuContext = React.createContext<any>(null)

const DropdownMenu = ({ children }: { children: React.ReactNode }) => {
  const [open, setOpen] = React.useState(false)
  const containerRef = React.useRef<HTMLDivElement>(null)

  React.useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(event.target as Node)) {
        setOpen(false)
      }
    }
    document.addEventListener("mousedown", handleClickOutside)
    return () => document.removeEventListener("mousedown", handleClickOutside)
  }, [])

  return (
    <DropdownMenuContext.Provider value={{ open, setOpen }}>
      <div className="relative inline-block text-left" ref={containerRef}>
        {children}
      </div>
    </DropdownMenuContext.Provider>
  )
}

const DropdownMenuTrigger = ({ children, asChild }: any) => {
  const { setOpen } = React.useContext(DropdownMenuContext)
  return <div onClick={() => setOpen((prev: boolean) => !prev)} className="cursor-pointer">{children}</div>
}

const DropdownMenuContent = ({ children, className, align = "end" }: any) => {
  const { open } = React.useContext(DropdownMenuContext)
  if (!open) return null
  return (
    <div className={cn(
        "absolute z-50 mt-2 min-w-[12rem] rounded-2xl bg-white p-2 shadow-2xl border border-slate-100 animate-in fade-in slide-in-from-top-2 duration-200", 
        align === "end" ? "right-0" : "left-0",
        className
    )}>
      {children}
    </div>
  )
}

const DropdownMenuItem = ({ children, className, onClick }: any) => {
  const { setOpen } = React.useContext(DropdownMenuContext)
  return (
    <div 
      onClick={(e) => {
        onClick?.(e)
        setOpen(false)
      }} 
      className={cn("flex cursor-pointer items-center rounded-xl px-3 py-2.5 text-sm font-medium transition-colors hover:bg-slate-50 text-slate-600", className)}
    >
      {children}
    </div>
  )
}

const DropdownMenuLabel = ({ children, className }: any) => (
  <div className={cn("px-3 py-2 text-xs font-bold text-slate-400 uppercase tracking-widest", className)}>
    {children}
  </div>
)

const DropdownMenuSeparator = () => <div className="h-px bg-slate-100 my-1 mx-2" />

export {
  DropdownMenu,
  DropdownMenuTrigger,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
}
