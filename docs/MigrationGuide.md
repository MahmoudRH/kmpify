# Migration Steps: How KMPify Transforms Your Code

KMPify automates the migration of Android Compose projects to align with **Compose Multiplatform (KMP)** resource conventions.

Each `.kt` file goes through the following transformation steps:

---

## 1. File Validation

- Accepts individual `.kt` files or scans directories recursively for `.kt` files.
- Skips non-Kotlin files automatically.

---

## 2. Package Detection

- Extracts the `package` declaration at the top of the file to preserve it during reconstruction.

---

## 3. Import Replacements

The tool replaces Android-specific imports with their KMP equivalents:

| Android Import                                      | KMP Replacement                                     |
|----------------------------------------------------|-----------------------------------------------------|
| `androidx.compose.ui.res.painterResource`          | `org.jetbrains.compose.resources.painterResource`   |
| `androidx.compose.ui.res.stringResource`           | `org.jetbrains.compose.resources.stringResource`    |
| `androidx.compose.ui.text.font.Font`               | `org.jetbrains.compose.resources.Font`              |
| `androidx.annotation.DrawableRes`                 | `org.jetbrains.compose.resources.DrawableResource`  |
| `androidx.annotation.StringRes`                   | `org.jetbrains.compose.resources.StringResource`    |
| `androidx.compose.ui.tooling.preview.Preview`     | `org.jetbrains.compose.ui.tooling.preview.Preview`  |
| `org.koin.androidx.compose.koinViewModel`         | `org.koin.compose.viewmodel.koinViewModel`          |

---

## 4. Resource References Replacement

- Replaces resource references like `R.drawable.icon` with `Res.drawable.icon`.
- Supports `drawable`, `string`, and `font` types.
- Tracks which resources are used to add proper import statements later.

---

## 5. Annotation Migration

Replaces Android-specific annotations with KMP equivalents.

### Example:

```kotlin
@DrawableRes icon: Int
@StringRes label: Int
```

Becomes:

```kotlin
icon: DrawableResource
label: StringResource
```

The import lines gets replaced as in [3. Import Replacements](#3-import-replacements).

---

## 6. Preview Cleanup

  - **What**: Removes parameters from `@Preview(...)` annotations, replacing them with a simple `@Preview`.
      - Optionally replaces custom preview class and their imports (e.g. `@MyCustomPrev`) with standard `@Preview`.
  - **Why**: KMP’s preview system doesn’t accept Android-specific parameters.
  - **Example**:
      ```kotlin
      // Before
      @Preview(showBackground = true, name = "Light")
      // After
      @Preview
      ```
---

## 7. Rewrite `id =` to `resource =`

Only inside supported functions like:
- `painterResource(...)`
- `stringResource(...)`
- `Font(...)`

### Example:

```kotlin
painterResource(id = R.drawable.logo)
```

Becomes:

```kotlin
painterResource(resource = Res.drawable.logo)
```

---

## 8. Import Reconstruction

- Removes all previous imports.
- Collects:
  - Remaining original imports
  - New imports for used resources (e.g. `Res.drawable.logo`)
- Sorts and rebuilds import block.

---

## 9. Output File Writing

- If `dryRun = true`: you get a `MigrationSummary` and a `MigrationReport` but no files gets changed.
- Otherwise:
  - if no `OutputPath` was specified
    - Writes to the original file with a new name (`*.kmp.kt`)  
  - else writes to the `OutputPath`, preserving folder structure.

---

## Final Report (in-app)

For each file, KMPify tracks:
- Whether the file changed
- Number of changes per category:
  - `R Import`: whether R class got Replaced or not 
  - `Res Imports`: number of individual resource imports added to the file 
  - `Drawable`: number of `painterResource` replacements
  - `Strings`: number of `stringResource` replacements
  - `Imports Added`: number of imports replacements as in [3. Import Replacements](#3-import-replacements).
    - doesn't include the number of individual resource imports.
    - click on any row to toggle between number of imports and the actual imports
- You can click on any title to sort the files based on it.
