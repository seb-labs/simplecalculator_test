# Simple Calculator

A small, privacy-friendly Android calculator built with Kotlin and Jetpack Compose.

## Features
- Basic arithmetic: addition, subtraction, multiplication, division
- Clean, minimal interface
- Offline only
- No tracking, ads, or Google services

## Build
```bash
./gradlew assembleDebug
```

The debug APK will be created at:
`app/build/outputs/apk/debug/app-debug.apk`

## Ready-made APK
A copy of the current APK is also kept in the repo here:
`APK/simplecalculator-debug.apk`

## Install
- Copy the APK to your phone and install it manually.
- Or use `adb install -r app/build/outputs/apk/debug/app-debug.apk`.

## Notes
- License: MIT
- Minimum SDK: 24
- Intended to stay F-Droid compatible
