# Mayo — Android App

نسخة أندرويد لتطبيق تسجيل بيانات الطلاب (Dental Student Record Automation).
نسخة تعمل بالكامل دون اتصال بالإنترنت (Offline) بأولوية قصوى.

## المتطلبات

- **Android 12+ (API 31)** للتعرف على الصوت دون اتصال — يفضل تفعيل حزمة اللغة العربية
  في إعدادات التعرف على الصوت (Settings → System → Languages & input → Speech recognition).
- إصدار APK جاهز للتثبيت: `dist/Mayo-v1.0.apk`

## الميزات

- نموذج تسجيل سريع: اسم المدرسة، الفصل، الشعبة، اسم الطالب، التشخيص.
- **إدخال صوتي** لاسم الطالب عبر خدمة التعرف على الصوت في النظام (تعمل دون إنترنت
  إذا كانت حزمة اللغة العربية مثبتة، وإلا تعمل تلقائياً عبر الإنترنت).
- عرض السجلات مع تعديل وحذف.
- توليد **بطاقة ولي الأمر PDF** لكل سجل مع إمكانية **المشاركة** (WhatsApp / Telegram / ...).
- توليد **ملخص PDF** لكل السجلات (3 بطاقات في كل صفحة).
- **الطباعة** مباشرة عبر نظام أندرويد (Print Manager).
- **تصدير واستيراد CSV** متوافق تماماً مع النسخة المكتبية
  (نفس الأعمدة: `Record_ID,Date,School_Name,Student_Name,Class_No,Department,Diagnosis`).
- واجهة عربية كاملة (RTL) مع خط Noto Naskh Arabic المدمج.
- تخزين محلي عبر SQLite (Room) يعمل دون إنترنت.

## البناء

المسار: `android/`

```bash
cd android
./gradlew :app:assembleRelease
```

الناتج:
- `app/build/outputs/apk/release/app-release.apk` (مصدق لتثبيت جانبي)
- نسخة جاهزة: `dist/Mayo-v1.0.apk`

### تكوين التوقيع

التوقيع يقرأ من `keystore.properties` (غير مرفوع إلى المستودع):
```
storeFile=keystore/mayo-release.jks
storePassword=***
keyAlias=mayo
keyPassword=***
```

> **مهم:** احتفظ بـ `android/keystore/mayo-release.jks` في مكان آمن ولا تخسره —
> هو المفتاح الوحيد لتحديث التطبيق مستقبلاً (نفس التوقيع).

معرّف الحزمة: `com.musno.mayo` (جاهز للنشر على Google Play مستقبلاً).

## بنية المشروع

```
app/src/main/java/com/musno/mayo/
├── MayoApplication.kt       # تهيئة التطبيق
├── MainActivity.kt          # النشاط الرئيسي
├── data/                    # قاعدة البيانات (Room)
│   ├── StudentRecord.kt
│   ├── StudentDao.kt
│   ├── AppDatabase.kt
│   └── StudentRepository.kt
├── ui/
│   ├── theme/               # الثيم والخط العربي
│   ├── navigation/          # التنقل بين الشاشات
│   ├── MainViewModel.kt
│   ├── screens/             # شاشة الإدخال + شاشة السجلات
│   └── components/          # بطاقة السجل + نافذة التعديل
└── util/
    ├── PdfGenerator.kt      # توليد PDF (بطاقة + ملخص)
    ├── CsvManager.kt        # تصدير/استيراد CSV
    ├── SpeechHelper.kt      # التعرف على الصوت (offline-first)
    ├── FileSharer.kt        # المشاركة عبر أي تطبيق
    └── PrintHelper.kt       # الطباعة عبر نظام أندرويد
```