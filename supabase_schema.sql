-- 1. UZANTILAR VE TEMEL TABLOLAR
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Whitelist Tablosu
CREATE TABLE IF NOT EXISTS public.whitelist (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email TEXT UNIQUE NOT NULL,
    sicil_no TEXT,
    city TEXT NOT NULL,
    notes TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    added_at TIMESTAMPTZ DEFAULT NOW()
);

-- Profiller
CREATE TABLE IF NOT EXISTS public.profiles (
    id UUID PRIMARY KEY REFERENCES auth.users(id) ON DELETE CASCADE,
    full_name TEXT NOT NULL,
    email TEXT NOT NULL,
    sicil_no TEXT,
    city TEXT,
    avatar_url TEXT,
    role TEXT NOT NULL DEFAULT 'student' CHECK (role IN ('student','admin')),
    created_at TIMESTAMPTZ DEFAULT NOW(),
    updated_at TIMESTAMPTZ DEFAULT NOW()
);

-- Kurslar ve Dersler
CREATE TABLE IF NOT EXISTS public.courses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title TEXT NOT NULL,
    description TEXT,
    category TEXT NOT NULL,
    city TEXT,
    instructor_name TEXT NOT NULL,
    duration_minutes INT DEFAULT 0,
    has_certificate BOOLEAN DEFAULT FALSE,
    is_published BOOLEAN DEFAULT FALSE,
    thumbnail_url TEXT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.lessons (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id UUID NOT NULL REFERENCES public.courses(id) ON DELETE CASCADE,
    title TEXT NOT NULL,
    content_type TEXT NOT NULL CHECK (content_type IN ('text','video')),
    content_markdown TEXT,
    video_url TEXT,
    order_index INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- Kayıt ve İlerleme Takibi
CREATE TABLE IF NOT EXISTS public.enrollments (
    profile_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    course_id UUID NOT NULL REFERENCES public.courses(id) ON DELETE CASCADE,
    enrolled_at TIMESTAMPTZ DEFAULT NOW(),
    progress_percent INT DEFAULT 0,
    PRIMARY KEY (profile_id, course_id)
);

CREATE TABLE IF NOT EXISTS public.lesson_completions (
    profile_id UUID NOT NULL REFERENCES auth.users(id) ON DELETE CASCADE,
    lesson_id UUID NOT NULL REFERENCES public.lessons(id) ON DELETE CASCADE,
    completed_at TIMESTAMPTZ DEFAULT NOW(),
    PRIMARY KEY (profile_id, lesson_id)
);

-- SINAV SİSTEMİ
CREATE TABLE IF NOT EXISTS public.quizzes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    course_id UUID NOT NULL REFERENCES public.courses(id) ON DELETE CASCADE,
    pass_score_percent INT DEFAULT 70,
    time_limit_minutes INT,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    quiz_id UUID NOT NULL REFERENCES public.quizzes(id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    question_type TEXT NOT NULL CHECK (question_type IN ('multiple_choice','true_false')),
    order_index INT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS public.options (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    question_id UUID NOT NULL REFERENCES public.questions(id) ON DELETE CASCADE,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE
);

-- Başarı Kayıtları ve Sertifikalar
CREATE TABLE IF NOT EXISTS public.quiz_results (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    profile_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    quiz_id UUID REFERENCES public.quizzes(id) ON DELETE CASCADE,
    score INTEGER NOT NULL,
    passed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS public.certificates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    profile_id UUID REFERENCES auth.users(id) ON DELETE CASCADE,
    course_id UUID REFERENCES public.courses(id) ON DELETE CASCADE,
    certificate_number TEXT UNIQUE NOT NULL,
    created_at TIMESTAMPTZ DEFAULT NOW()
);

-- 2. GÜVENLİK (RLS)
ALTER TABLE public.profiles ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.whitelist ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.courses ENABLE ROW LEVEL SECURITY;

-- Politikaları Temizle ve Yeniden Kur
DROP POLICY IF EXISTS "Admins can manage whitelist" ON public.whitelist;
CREATE POLICY "Admins can manage whitelist" ON public.whitelist FOR ALL TO authenticated
USING (EXISTS (SELECT 1 FROM public.profiles WHERE profiles.id = auth.uid() AND profiles.role = 'admin'));

DROP POLICY IF EXISTS "Anyone can view published courses" ON public.courses;
CREATE POLICY "Anyone can view published courses" ON public.courses FOR SELECT USING (is_published = true);

DROP POLICY IF EXISTS "Users can view own profile" ON public.profiles;
CREATE POLICY "Users can view own profile" ON public.profiles FOR SELECT USING (auth.uid() = id);

DROP POLICY IF EXISTS "Users can update own profile" ON public.profiles;
CREATE POLICY "Users can update own profile" ON public.profiles FOR UPDATE USING (auth.uid() = id);

-- 3. DERSLER VE İÇERİK POLİTİKALARI
ALTER TABLE public.lessons ENABLE ROW LEVEL SECURITY;
DROP POLICY IF EXISTS "Anyone can view lessons" ON public.lessons;
CREATE POLICY "Anyone can view lessons" ON public.lessons FOR SELECT USING (true);

-- Admin tüm dersleri yönetebilir
DROP POLICY IF EXISTS "Admins can manage lessons" ON public.lessons;
CREATE POLICY "Admins can manage lessons" ON public.lessons FOR ALL TO authenticated
USING (EXISTS (SELECT 1 FROM public.profiles WHERE profiles.id = auth.uid() AND profiles.role = 'admin'));

-- 4. SINAV VE SORU POLİTİKALARI
ALTER TABLE public.quizzes ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.questions ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.options ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Anyone can view quizzes" ON public.quizzes;
CREATE POLICY "Anyone can view quizzes" ON public.quizzes FOR SELECT USING (true);

DROP POLICY IF EXISTS "Anyone can view questions" ON public.questions;
CREATE POLICY "Anyone can view questions" ON public.questions FOR SELECT USING (true);

DROP POLICY IF EXISTS "Anyone can view options" ON public.options;
CREATE POLICY "Anyone can view options" ON public.options FOR SELECT USING (true);

-- 5. KAYIT VE İLERLEME POLİTİKALARI
ALTER TABLE public.enrollments ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.lesson_completions ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Users can manage own enrollments" ON public.enrollments;
CREATE POLICY "Users can manage own enrollments" ON public.enrollments FOR ALL TO authenticated
USING (auth.uid() = profile_id);

DROP POLICY IF EXISTS "Users can manage own completions" ON public.lesson_completions;
CREATE POLICY "Users can manage own completions" ON public.lesson_completions FOR ALL TO authenticated
USING (auth.uid() = profile_id);

-- 6. SINAV SONUÇLARI VE SERTİFİKALAR
ALTER TABLE public.quiz_results ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.certificates ENABLE ROW LEVEL SECURITY;

DROP POLICY IF EXISTS "Users can view own results" ON public.quiz_results;
CREATE POLICY "Users can view own results" ON public.quiz_results FOR SELECT TO authenticated
USING (auth.uid() = profile_id);

DROP POLICY IF EXISTS "Users can view own certificates" ON public.certificates;
CREATE POLICY "Users can view own certificates" ON public.certificates FOR SELECT TO authenticated
USING (auth.uid() = profile_id);

-- Whitelist sorgulama (Public - Kayıt anı için)
DROP POLICY IF EXISTS "Whitelist sorgulama (Public)" ON public.whitelist;
CREATE POLICY "Whitelist sorgulama (Public)" ON public.whitelist FOR SELECT TO anon USING (is_active = true);

-- 3. AKILLI KAYIT SİSTEMİ (Trigger)
CREATE OR REPLACE FUNCTION public.handle_new_user()
RETURNS TRIGGER AS $$
DECLARE
    matched_city TEXT;
    user_sicil TEXT;
BEGIN
    user_sicil := COALESCE(NEW.raw_user_meta_data->>'sicil_no', SPLIT_PART(NEW.email, '@', 1));
    
    SELECT city INTO matched_city FROM public.whitelist 
    WHERE sicil_no = user_sicil OR email = NEW.email LIMIT 1;

    INSERT INTO public.profiles (id, full_name, email, sicil_no, city, role)
    VALUES (
        NEW.id,
        COALESCE(NEW.raw_user_meta_data->>'full_name', 'İsimsiz Kullanıcı'),
        NEW.email,
        user_sicil,
        COALESCE(matched_city, 'Belirtilmemiş'),
        'student' 
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql SECURITY DEFINER;

DROP TRIGGER IF EXISTS on_auth_user_created ON auth.users;
CREATE TRIGGER on_auth_user_created
  AFTER INSERT ON auth.users
  FOR EACH ROW EXECUTE FUNCTION public.handle_new_user();
