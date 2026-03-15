# EduPlatform - Kurumsal Eğitim Platformu

Bu proje, modern bir kurumsal eğitim platformunun tüm bileşenlerini içeren bir Kotlin Multiplatform (KMP) projesidir. Android uygulaması, Web uygulaması ve bir Admin paneli içermektedir.

## Proje Yapısı

- **shared/**: Tüm platformlar (Android & Web) için ortak kullanılan domain modelleri, repository'ler, API servisleri ve ViewModeller.
- **androidApp/**: Jetpack Compose ve Material3 ile geliştirilmiş native Android uygulaması.
- **webApp/**: Compose Multiplatform for Web kullanılarak geliştirilmiş kullanıcı web arayüzü.
- **eduplatform-admin/**: Next.js 14, Tailwind CSS ve shadcn/ui ile geliştirilmiş bağımsız yönetim paneli.
- **supabase/**: Veritabanı şeması ve Edge Function'lar (Sertifika oluşturma/doğrulama).

## Kurulum

1. **Supabase Kurulumu**: 
   - Bir Supabase projesi oluşturun.
   - `supabase_schema.sql` dosyasındaki tüm kodları SQL Editor'de çalıştırın.
   - `supabase/functions/` altındaki Edge Function'ları deploy edin.
   
2. **Konfigürasyon**:
   - `local.properties.example` dosyasını `local.properties` olarak kopyalayın.
   - `SUPABASE_URL` ve `SUPABASE_ANON_KEY` değerlerini kendi projenize göre güncelleyin.
   - `eduplatform-admin/.env.local` dosyasındaki değerleri güncelleyin.

3. **Android Uygulamasını Çalıştırma**:
   ```bash
   ./gradlew :androidApp:assembleDebug
   ```

4. **Web Uygulamasını Çalıştırma**:
   ```bash
   ./gradlew :webApp:jsBrowserDistribution
   ```

5. **Admin Panelini Çalıştırma**:
   ```bash
   cd eduplatform-admin
   npm install
   npm run dev
   ```

## Test Kullanıcıları

- **Admin**: admin@platform.com / Admin1234!
- **Öğrenci**: test@platform.com / Test1234!

## CI/CD & Deploy

Proje, GitHub Actions ile tam otomatik bir pipeline'a sahiptir:

- **Android**: Her push'ta debug APK oluşturulur ve artifact olarak yüklenir.
- **Web**: Cloudflare Pages üzerine otomatik olarak deploy edilir.
- **Admin**: Vercel üzerine otomatik olarak deploy edilir.

## Gerekli GitHub Secrets

CI/CD'nin çalışması için GitHub deponuza aşağıdaki secret'ları eklemeniz gerekmektedir:
- `SUPABASE_URL`
- `SUPABASE_ANON_KEY`
- `CF_API_TOKEN` (Cloudflare)
- `CF_ACCOUNT_ID`
- `VERCEL_TOKEN`
- `VERCEL_ORG_ID`
- `VERCEL_PROJECT_ID`
