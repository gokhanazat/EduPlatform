import { serve } from "https://deno.land/std@0.208.0/http/server.ts"
import { createClient } from "https://esm.sh/@supabase/supabase-js@2"
import { PDFDocument, StandardFonts, rgb } from "https://cdn.skypack.dev/pdf-lib@1.17.1"

const CORS = {
  "Access-Control-Allow-Origin": "*",
  "Access-Control-Allow-Headers": "authorization, content-type"
}

serve(async (req) => {
  if (req.method === "OPTIONS") return new Response("ok", { headers: CORS })

  try {
    const authHeader = req.headers.get("Authorization")
    const supabase = createClient(
      Deno.env.get("SUPABASE_URL")!,
      Deno.env.get("SUPABASE_SERVICE_ROLE_KEY")!,
      { global: { headers: { Authorization: authHeader! } } }
    )

    const { data: { user } } = await supabase.auth.getUser()
    if (!user) return new Response(JSON.stringify({ error: "Unauthorized" }), { status: 401, headers: CORS })

    const { certificateId } = await req.json()

    const { data: cert } = await supabase
      .from("certificates")
      .select("*")
      .eq("id", certificateId)
      .eq("user_id", user.id)
      .single()

    if (!cert) return new Response(JSON.stringify({ error: "Not found" }), { status: 404, headers: CORS })

    // Generate PDF — A4 landscape
    const doc = await PDFDocument.create()
    const page = doc.addPage([842, 595])
    const bold = await doc.embedFont(StandardFonts.HelveticaBold)
    const regular = await doc.embedFont(StandardFonts.Helvetica)

    // Dark blue background
    page.drawRectangle({ x: 0, y: 0, width: 842, height: 595,
      color: rgb(0.118, 0.227, 0.373) })
    // Gold border
    page.drawRectangle({ x: 20, y: 20, width: 802, height: 555,
      borderColor: rgb(1, 0.843, 0), borderWidth: 2, opacity: 0 })

    const center = (text: string, y: number, font: any, size: number, color: any) => {
      const w = font.widthOfTextAtSize(text, size)
      page.drawText(text, { x: (842 - w) / 2, y, font, size, color })
    }

    center("EduPlatform",     500, bold,    36, rgb(1, 0.843, 0))
    center("Başarı Sertifikası", 458, regular, 18, rgb(1,1,1))
    center("Bu belge aşağıdaki kişinin başarıyla tamamladığını onaylar:", 410, regular, 12, rgb(0.8,0.8,0.8))
    center(cert.user_name,    368, bold,    30, rgb(1, 0.843, 0))
    center(cert.course_title, 330, bold,    20, rgb(1,1,1))

    const date = new Date(cert.issued_at).toLocaleDateString("tr-TR", {
      year: "numeric", month: "long", day: "numeric"
    })
    page.drawText("Tarih: " + date, { x: 60, y: 60, font: regular, size: 11,
      color: rgb(0.7,0.7,0.7) })
    page.drawText("Dogrulama: " + cert.verify_code.substring(0, 8).toUpperCase(),
      { x: 60, y: 42, font: regular, size: 10, color: rgb(0.5,0.5,0.5) })

    const pdfBytes = await doc.save()
    const path = user.id + "/" + certificateId + ".pdf"

    await supabase.storage.from("certificates")
      .upload(path, pdfBytes, { contentType: "application/pdf", upsert: true })

    const { data: { publicUrl } } = supabase.storage.from("certificates").getPublicUrl(path)

    // Save URL to certificates table
    await supabase.from("certificates").update({ pdf_url: publicUrl }).eq("id", certificateId)

    return new Response(JSON.stringify({ pdfUrl: publicUrl }),
      { headers: { ...CORS, "Content-Type": "application/json" } })

  } catch (e: any) {
    return new Response(JSON.stringify({ error: e.message }), { status: 500, headers: CORS })
  }
})
