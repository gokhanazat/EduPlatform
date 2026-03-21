
import { supabaseAdmin } from "./src/lib/supabase/admin";

async function check() {
    const courseId = "dddd815c-db31-4b22-9541-0e74028c8022";
    
    console.log("Checking Course:", courseId);
    
    const { data: course, error: cErr } = await supabaseAdmin
        .from("courses")
        .select("*")
        .eq("id", courseId)
        .single();
    
    if (cErr) {
        console.error("Course error:", cErr);
        return;
    }
    console.log("Course Found:", course.title);
    
    const { data: lessons, error: lErr } = await supabaseAdmin
        .from("lessons")
        .select("*")
        .eq("course_id", courseId);
        
    if (lErr) {
        console.error("Lessons error:", lErr);
        return;
    }
    
    console.log("Lessons Count:", lessons.length);
    lessons.forEach(l => {
        console.log(`- Lesson: ${l.title}, Type: ${l.content_type}, MD Length: ${l.markdown_content?.length || 0}`);
    });

    const { data: rlsCheck, error: rlsErr } = await supabaseAdmin.rpc('get_policies'); // This might not work depending on DB permissions
    // Alternatively, I'll just check if I can fetch as a regular user would.
}

check();
