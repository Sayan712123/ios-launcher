# iOS Launcher for Android

An Android home-screen replacement that looks and behaves like iOS, written in
Kotlin with Jetpack Compose. It lists every app installed on the phone, opens
them, and ships its own iOS-style app set so the phone feels complete out of the
box. Tuned to stay light on low-end devices such as a POCO M4.

## What's inside

**Launcher UI**

- Home screen: 4-column iOS grid, page dots, horizontal paging
- Glass dock with four apps
- App Library: built-in apps, then every installed app grouped A-Z
- Spotlight-style search over built-in and installed apps
- iOS folders with a 2x2 icon preview
- Long-press menu: Add to Dock / Hide from Home Screen / App Info
- Status bar: time, cellular bars, Wi-Fi, battery with charging bolt
- Control Centre: connectivity toggles, media block, brightness, volume,
  flashlight, fake-blur frosted glass
- Notification Centre: large clock plus real device notifications
- Lock screen: big date/time, live notification cards, swipe up to unlock
- Own navigation bar (buttons or gesture pill) replacing the system 3-button bar
- Own recents panel with swipe-to-dismiss and Clear All

**14 built-in apps**

Phone (keypad + call log), Messages, Contacts, Camera, Photos, Calculator,
Notes, Clock (World / Alarms / Stopwatch / Timer), Calendar, Files, Music,
Weather, Reminders, Settings.

**Integration**

- Every installed app is listed automatically with its real icon
- Can be set as the default home app (HOME intent filter)
- Real notifications via a NotificationListenerService
- Dialer / messaging / camera / gallery hand off to the system apps
- Third-party icon pack support (ADW / Nova / Lawnchair style)
- Wallpaper: four gradients or any photo from the gallery

## Low-end / POCO tuning

- Icons rasterised to 132px and held in a 6 MB byte-bounded LruCache, so the
  footprint stays flat with 200+ apps installed
- Icon loading happens on `Dispatchers.IO`; the UI thread never touches the
  package manager
- Wallpaper is drawn as a gradient by default - no full-screen bitmap decode
- `LazyVerticalGrid` / `LazyColumn` everywhere
- App list read once and cached
- No database and no JSON parser: layout and app data are line-based text in
  SharedPreferences
- Notifications keep only title / text / time - no bitmaps
- `largeHeap="false"`, R8 and resource shrinking in release
- No background services
- Fake blur instead of a real `RenderEffect` blur, which would re-sample the
  whole window every frame

## Build the APK

### Option A - GitHub Actions (no local toolchain needed)

1. Push to `main` (or open the **Actions** tab and run **Build APK** manually).
2. Wait 5-8 minutes for the run to go green.
3. Open the run, scroll to **Artifacts**, download `ios-launcher-debug-apk`.
4. Unzip it and install `app-debug.apk` on the phone.

The debug APK is signed with the standard debug key, so it installs directly.

### Option B - Android Studio

1. Open the project folder in Android Studio (Ladybug or newer).
2. Let Gradle sync.
3. Connect the phone over USB with USB debugging on.
4. Press Run.

### Option C - command line

```bash
gradle :app:assembleDebug
# output: app/build/outputs/apk/debug/app-debug.apk
```

## Install and set up on the phone

1. Install `app-debug.apk` (allow install from unknown sources if asked).
2. Open the launcher, then **Settings -> Set as default launcher** and pick it.
3. **Settings -> Notification access** and enable the launcher, so the
   notification centre and lock screen can show your notifications.
4. Optional: **Settings -> Icon Pack** to apply an installed icon pack.
5. Optional: **Settings -> Wallpaper -> Choose from Gallery**.

### POCO / MIUI / HyperOS notes

- Settings -> Apps -> Manage apps -> Launcher -> **Autostart**: on
- Battery saver -> **No restrictions**
- Developer options -> animation scales to **0.5x** for a snappier feel
- If the launcher is killed in the background, lock it in the recents view

## Honest limitations

These are platform limits, not bugs - no Android launcher can do them:

- **The system notification shade cannot be disabled.** Android exposes no API
  for it. The launcher hides the system status and navigation bars via
  immersive mode, but a swipe from the very top edge can still pull the system
  shade down. The launcher's own Control Centre is what the in-app gestures
  open.
- **The system recents panel cannot be replaced.** `getRunningTasks` is
  deprecated and `UsageStatsManager` needs a special grant, so the launcher
  keeps its own recents list of the apps it launched. Dismissing a card calls
  `killBackgroundProcesses`, which the platform honours for background
  processes only.
- **Uninstalling the system dialer / messaging app breaks calling and SMS.**
  The built-in Phone and Messages apps are iOS-styled UI; the actual call and
  SMS stacks belong to the system apps. Keep them installed.
- **The camera viewfinder is the system camera.** A launcher cannot hold a
  camera preview without its own CAMERA permission flow, so the shutter hands
  off to the system camera app.
- **Contacts and Messages show sample data.** Reading the real ones needs
  `READ_CONTACTS` / `READ_SMS` runtime permissions.
- **Weather is static.** Live data needs an API key.
- **Music is a UI demo** - no playback engine.
- Built-in app icons are glyph-based; Apple's copyrighted artwork is not used.

## Project layout

```
app/src/main/java/com/ioslauncher/app/
  MainActivity.kt              entry point, immersive mode, re-lock on resume
  data/                        app list, icon cache, icon packs, prefs, recents
  service/                     notification listener
  ui/                          home, library, search, folders, control centre,
                               notification centre, lock screen, recents
  ui/apps/                     the 14 built-in apps
  ui/components/               status bar, wallpaper, nav bar, shared widgets
  ui/theme/                    iOS palette and typography
  util/                        system intent helpers
```

## License

Provided as-is for personal use. Apple's iOS design language and artwork are
Apple's property; this project reimplements the look with original code and
glyph icons.
