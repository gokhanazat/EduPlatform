import { serve } from "https://deno.land/std@0.208.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"

const CORS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, content-type"
}

serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: CORS })

  try {
    const url = new URL(req.url)
    const code = url.searchParams.get("code")

    if (!code) {
      return new Response(JSON.stringify({ valid: false, error: "Missing verification code" }), {
        status: 400,
        headers: CORS
      })
    }

    const supabase = createClient(
      Deno.env.get("SUPABASE_URL")!,
      Deno.env.get("SUPABASE_ANON_KEY")!
    )

    const { data, error } = await supabase
      .from("certificates")
      .select("*, courses(title), profiles(full_name)")
      .eq("verify_code", code)
      .single()

    if (error || !data) {
      return new Response(JSON.stringify({ valid: false }), { headers: CORS })
    }

    return new Response(JSON.stringify({ valid: true, certificate: data }), {
      headers: { ...CORS, "Content-Type": "application/json" }
    })

  } catch (e: any) {
    return new Response(JSON.stringify({ error: e.message }), { status: 500, headers: CORS })
  }
})
