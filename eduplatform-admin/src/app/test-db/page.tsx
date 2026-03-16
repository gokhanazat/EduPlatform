"use client"

import { useState, useEffect } from "react"
import { createBrowserClient } from "@supabase/ssr"

export default function CheckTable() {
    const [status, setStatus] = useState("Checking...")
    const supabase = createBrowserClient(
        process.env.NEXT_PUBLIC_SUPABASE_URL!,
        process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY!
    )

    useEffect(() => {
        async function run() {
            const { error: err1 } = await supabase.from('lesson_completions').select('*').limit(1)
            const { error: err2 } = await supabase.from('quizzes').select('*').limit(1)
            const { error: err3 } = await supabase.from('questions').select('*').limit(1)
            const { error: err4 } = await supabase.from('options').select('*').limit(1)
            
            setStatus(`lesson_completions: ${err1 ? "NO" : "YES"}, quizzes: ${err2 ? "NO" : "YES"}, questions: ${err3 ? "NO" : "YES"}, options: ${err4 ? "NO" : "YES"}`)
        }
        run()
    }, [])

    return <div>{status}</div>
}
