"use client"

// Simplified toast hook for internal use
import * as React from "react"

export type ToastProps = {
  title?: string
  description?: string
  variant?: "default" | "destructive"
}

export function useToast() {
  const toast = React.useCallback(({ title, description, variant }: ToastProps) => {
    // Falls back to simple alert if UI components aren't fully wired
    const message = `${title ? title + ': ' : ''}${description || ''}`
    if (variant === "destructive") {
        console.error("Toast:", message)
        alert("HATA: " + message)
    } else {
        console.log("Toast:", message)
        alert(message)
    }
  }, [])

  return { toast }
}
