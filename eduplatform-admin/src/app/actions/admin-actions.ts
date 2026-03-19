"use server"

import { supabaseAdmin } from "@/lib/supabase/admin"
import { revalidatePath } from "next/cache"

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

export async function adminDeleteCourse(courseId: string) {
  try {
    const { error } = await supabaseAdmin.from("courses").delete().eq("id", courseId)
    if (error) throw error
    revalidatePath("/manage-courses")
    return { success: true }
  } catch (error: any) {
    console.error("Delete course error:", error)
    return { success: false, error: error.message }
  }
}

export async function adminGetCourse(courseId: string) {
  try {
    const { data, error } = await supabaseAdmin.from("courses").select("*").eq("id", courseId).single()
    if (error) throw error
    return { data, error: null }
  } catch (error: any) {
    console.error("Get course error:", error)
    return { data: null, error: error.message }
  }
}

export async function adminGetLessons(courseId: string) {
  try {
    const { data, error } = await supabaseAdmin.from("lessons").select("*").eq("course_id", courseId).order("order_index")
    if (error) throw error
    return { data, error: null }
  } catch (error: any) {
    console.error("Get lessons error:", error)
    return { data: null, error: error.message }
  }
}

export async function adminSaveLesson(payload: any) {
  try {
    const { data, error } = await supabaseAdmin.from("lessons").upsert(payload).select().single()
    if (error) throw error
    return { data, error: null }
  } catch (error: any) {
    console.error("Save lesson error:", error)
    return { data: null, error: error.message }
  }
}
