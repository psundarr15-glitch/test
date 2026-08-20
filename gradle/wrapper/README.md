# Jeevi Food — Customer Android App

Laravel Food Delivery API-ஐ பயன்படுத்தும் Kotlin + Jetpack Compose Android app.

## முதலில் செய்ய வேண்டியது: API URL மாற்றுங்க

`app/build.gradle.kts` file-ல இந்த வரியை உங்க live server URL-ஆ மாற்றுங்க:

```kotlin
buildConfigField("String", "API_BASE_URL", "\"https://YOUR-SERVER-DOMAIN.com/api/v1/\"")
```

## GitHub-ல push பண்ணி APK-ஐ Actions மூலமா build பண்ண

இந்த repo-வுல `.github/workflows/build.yml` ஏற்கனவே இருக்கு — push பண்ணும்போது GitHub தானா APK-ஐ build பண்ணிடும் (Android Studio local-ல install பண்ண வேண்டிய அவசியம் இல்ல).

### படிகள்

1. **GitHub-ல புது repo உருவாக்குங்க** (github.com → New repository), பெயர்: `jeevi-food-app` (எதுவும் வைக்கலாம்), private/public எதுவும் தேர்ந்தெடுக்கலாம்.

2. **இந்த project folder-ஐ push பண்ணுங்க:**

   ```bash
   cd JeeviFoodApp
   git init
   git add .
   git commit -m "Initial commit - Jeevi Food customer app"
   git branch -M main
   git remote add origin https://github.com/<உங்க-username>/jeevi-food-app.git
   git push -u origin main
   ```

3. **Actions tab-ஐ பாருங்க:** GitHub repo-வுல மேல இருக்கும் **Actions** tab-க்கு போங்க. `Build APK` workflow தானா run ஆகும் (2-4 நிமிடம் ஆகும்).

4. **APK-ஐ download பண்ணுங்க:** Workflow run முடிஞ்சதும், அந்த run-ஐ கிளிக் பண்ணி, கீழ **Artifacts** section-ல `jeevi-food-debug-apk` இருக்கும் — அதை download பண்ணி unzip பண்ணா `app-debug.apk` கிடைக்கும்.

5. **Phone-ல install பண்ண:** அந்த APK-ஐ உங்க Android phone-க்கு அனுப்புங்க (WhatsApp/Drive/USB), "Unknown sources" install-ஐ அனுமதிச்சு open பண்ணுங்க.

### மறுபடி build வேணும்னா

`main` branch-க்கு எந்த code மாற்றமும் push பண்ணும்போதும் automatic-ஆ APK build ஆகும். கைமுறையா trigger பண்ணவும் முடியும்: Actions tab → Build APK → **Run workflow** பட்டன்.

### Local-ல Android Studio-ல ரன் பண்ணவும் முடியும்

Repo-ஐ clone பண்ணி Android Studio-ல "Open Project" செய்யுங்க — Gradle sync ஆகி emulator/device-ல நேரடியா Run ▶️ பண்ணலாம்.
