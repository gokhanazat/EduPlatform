"use server"

import { createClient } from "@supabase/supabase-js"
import { revalidatePath } from "next/cache"

const supabaseAdmin = createClient(
  process.env.NEXT_PUBLIC_SUPABASE_URL!,
  process.env.SUPABASE_SERVICE_ROLE_KEY!,
  {
    auth: {
      autoRefreshToken: false,
      persistSession: false
    }
  }
)

export async function adminSaveCourse(payload: any, id: string, isNew: boolean) {
  try {
    let res
    if (isNew) {
      res = await supabaseAdmin.from("courses").insert(payload).select().single()
    } else {
      res = await supabaseAdmin.from("courses").update(payload).eq("id", id).select().single()
    }
    
    if (res.error) throw new Error(res.error.message)
    revalidatePath("/manage-courses")
    return { data: res.data, error: null }
  } catch (error: any) {
    console.error("Course save action error:", error)
    return { data: null, error: error.message }
  }
}

export async function adminChangeUserPassword(userId: string, newPassword: string) {
  try {
    const { data, error } = await supabaseAdmin.auth.admin.updateUserById(
      userId,
      { password: newPassword }
    )

    if (error) {
      return { success: false, error: error.message }
    }

    return { success: true }
  } catch (error: any) {
    return { success: false, error: error.message || "Bilinmeyen bir hata oluştu" }
  }
}
