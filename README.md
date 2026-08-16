# MaterialColorPicker

[![JitPack](https://jitpack.io/v/AliBinkhani/MaterialColorPicker.svg)](https://jitpack.io/#AliBinkhani/MaterialColorPicker)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)
[![Kotlin](https://img.shields.io/badge/Kotlin-100%25-purple.svg)](https://kotlinlang.org)

A Material 3 color picker for Android, offering both a ready-to-use `AlertDialog` and a standalone, embeddable `View`.

The UI and interaction model are inspired by the **Samsung OneUI Color Picker** and adapted from the [OneUI-Design-Library](https://github.com/OneUIProject/OneUI-Design-Library) project, rebuilt from the ground up on top of AndroidX and Material Components so it fits naturally into any Material 3-themed app.

## Screenshots

| Swatches | Spectrum | Dialog |
|---|---|---|
| _add screenshot_ | _add screenshot_ | _add screenshot_ |

> Replace the placeholders above with actual screenshots (light/dark) once available.

## Features

- Material 3 look and feel that follows your app's theme (light/dark, dynamic color)
- Two selection modes: a **Swatches** grid and an **HSV Spectrum** wheel with a hue/saturation gradient slider
- Optional **opacity (alpha)** slider
- Direct **Hex** and **RGB** input fields, kept in sync with every other control
- **Recently used colors** row with automatic slot management
- **Current vs. New** color comparison
- Ships as both a `MaterialColorPickerDialogBuilder` (drop-in `AlertDialog.Builder`-style API) and a `MaterialColorPickerView` (embed anywhere in your layout)
- Full RTL support and layouts tuned for phones, tablets, and landscape orientation
- Localized into 70+ languages
- No third-party color-picker dependency — built purely on AndroidX + Material Components

## Requirements

- `minSdk 24` (Android 7.0) or higher
- A Material Components / Material 3 theme (`Theme.Material3.*` or a `MaterialComponents` descendant)
- Kotlin project (Java consumers can use the library as well, since the public API is plain Kotlin/Java-interop friendly)

## Installation

The library is distributed via [JitPack](https://jitpack.io).

**1. Add the JitPack repository** in your project's `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

**2. Add the dependency** to your module's `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.AliBinkhani.MaterialColorPicker:materialColorPicker:<version>")
}
```

Replace `<version>` with the latest [release tag](https://github.com/AliBinkhani/MaterialColorPicker/releases) (or a commit hash for a specific snapshot). See the JitPack badge above for the latest resolvable version.

> **Maven Central:** the library is not yet published to Maven Central. This section will be updated with `mavenCentral()` coordinates once a release is available there — JitPack works out of the box in the meantime.

## Usage

### 1. `MaterialColorPickerDialogBuilder`

The quickest way to let users pick a color — mirrors the familiar `MaterialAlertDialogBuilder` API.

```kotlin
MaterialColorPickerDialogBuilder(context)
    .setTitle("Choose a color")
    .setRecentColorEnabled(true)
    .setRecentColors(Color.RED, Color.GREEN, Color.BLUE)
    .setOpacityBarEnabled(true)
    .setNewColor(currentColor)
    .setOnColorChangeListener { color ->
        // Called live, on every change while the dialog is open
    }
    .setPositiveButton("OK") { _, _, color ->
        // Called with the final selected color
        applyColor(color)
    }
    .setNegativeButton("Cancel", null)
    .show()
```

#### Builder API reference

| Method | Description |
|---|---|
| `setTitle(...)` / `setCustomTitle(...)` | Dialog title, forwarded to the underlying `MaterialAlertDialogBuilder` |
| `setMessage(...)` | Optional message shown above the picker |
| `setIcon(...)` / `setIconAttribute(...)` | Dialog icon |
| `setBackground(...)`, `setBackgroundInset*(...)` | Dialog background/insets |
| `setNewColor(color)` | Preselects the color shown as "New" |
| `setRecentColorEnabled(enabled)` | Show/hide the recently-used colors row |
| `setRecentColors(vararg colors)` | Populate the recently-used colors row |
| `setRecentColor(color)` | Sets the "Current" color shown for comparison |
| `setOpacityBarEnabled(enabled)` | Show/hide the alpha slider |
| `setOnlySpectrumMode()` | Hides the Swatches tab and locks the picker to Spectrum mode |
| `setOnColorChangeListener { color -> ... }` | Fired on every live color change |
| `setPositiveButton(...)` / `setNegativeButton(...)` / `setNeutralButton(...)` | Standard dialog buttons; their listeners additionally receive the last selected color |
| `setOnCancelListener(...)` / `setOnDismissListener(...)` | Fired with the last selected color |
| `setCancelable(...)` | Whether the dialog is cancelable |
| `create()` / `show()` | Build or build-and-show the underlying `AlertDialog` |

### 2. `MaterialColorPickerView`

Embed the picker directly in a layout — useful for settings screens, bottom sheets, or a custom dialog/fragment of your own.

**XML:**

```xml
<com.hooshkar.materialcolorpicker.views.MaterialColorPickerView
    android:id="@+id/colorPickerView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```

**Kotlin:**

```kotlin
colorPickerView.setOpacityBarEnabled(true)
colorPickerView.setRecentColorEnabled(true)
colorPickerView.setRecentColors(intArrayOf(Color.RED, Color.GREEN, Color.BLUE))
colorPickerView.setNewColor(currentColor)

colorPickerView.onColorChangedListener =
    MaterialColorPickerView.OnColorChangedListener { color ->
        applyColor(color)
    }
```

You can also instantiate it purely in code, exactly as `MaterialColorPickerDialogBuilder` does internally:

```kotlin
val colorPickerView = MaterialColorPickerView(context)
container.addView(colorPickerView)
```

## Theming

The dialog uses `R.style.MaterialColoPickerAlertDialog` by default (a `ThemeOverlay.Material3.MaterialAlertDialog` descendant that reads `?colorSurfaceContainerLow` for its background). To use a custom theme overlay, pass it explicitly:

```kotlin
MaterialColorPickerDialogBuilder(context, R.style.YourCustomDialogTheme)
```

Since both the dialog and the view are built on standard Material Components widgets, they automatically pick up your app's Material 3 color scheme (including dynamic color) without any extra configuration.

## Localization

String resources are already translated into 70+ locales. Contributions that fix or improve a translation are welcome — see [Contributing](#contributing).

## Contributing

Contributions are welcome:

1. Fork the repository and create a feature branch
2. Make your changes (keep the existing code style)
3. Open a pull request describing the change and, for UI changes, include before/after screenshots

Please open an issue first for larger changes so the approach can be discussed.

## License

```
Copyright 2026 Ali Binkhani

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

See the [LICENSE](LICENSE) file for the full text.

## Acknowledgements

- [OneUI-Design-Library](https://github.com/OneUIProject/OneUI-Design-Library) — this project's UI, interaction patterns, and much of its resources are adapted from this excellent recreation of Samsung's OneUI design system. Huge thanks to its authors and contributors.
- Samsung **OneUI** — for the original Color Picker design this library is modeled after.
- [Material Components for Android](https://github.com/material-components/material-components-android) — the underlying `MaterialAlertDialogBuilder`, buttons, and theming primitives used throughout.
