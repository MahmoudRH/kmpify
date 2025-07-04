# KMPify

**KMPify** is a Kotlin-based desktop tool designed to help you **migrate Android Jetpack Compose projects** to **Kotlin Multiplatform (KMP)** with minimal effort.

It automatically scans `.kt` files and applies a series of smart transformations to:
- Replace Android-specific resource imports with their KMP-compatible counterparts
- Convert `R.drawable`, `R.string`, and `R.font` references to `Res.drawable`, `Res.string`, etc.
- Rebuild imports cleanly and inject resource references where needed
- Replace annotations like `@DrawableRes` and `@StringRes` with their KMP alternatives
- Replace previews, viewModels, and other common Compose elements

Whether you're migrating an entire module or just experimenting with KMP, **KMPify** speeds up the process and reduces manual editing.

---
