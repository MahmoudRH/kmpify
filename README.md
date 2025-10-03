# KMPify

**KMPify** is a Kotlin tool that helps **migrate Android Jetpack Compose projects** to **Kotlin Multiplatform (KMP)** with minimal effort. Available as both a **CLI tool** and **Desktop GUI**.
![KMPify in Action](docs/screenshots/main_window.png)

## Why Use KMPify?

Migrating to Kotlin Multiplatform allows you to share code across multiple platforms. KMPify automates the repetitive tasks, It automatically scans `.kt` files and applies a series of smart transformations to:

- Replace Android-specific resource imports with their KMP-compatible counterparts
- Convert `R.drawable`, `R.string`, and `R.font` references to `Res.drawable`, `Res.string`, etc.
- Rebuild imports cleanly and inject resource references where needed
- Replace annotations like `@DrawableRes` and `@StringRes` with their KMP alternatives
- Replace previews, viewModels, and other common Compose elements

for more about migration steps see [MigrationSteps](docs/MigrationGuide.md)

---
### Example
**Before** (Android):
```kotlin
import com.example.android.R
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

@Composable
fun MainScreen() {
    Image(painter = painterResource(id = R.drawable.icon), contentDescription = null)
    Text(stringResource(id = R.string.app_name))
}
```

**After** (KMP):
```kotlin
import my_kmp_project.composeapp.generated.resources.Res
import my_kmp_project.composeapp.generated.resources.icon
import my_kmp_project.composeapp.generated.resources.app_name
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainScreen() {
    Image(painter = painterResource(resource = Res.drawable.icon), contentDescription = null)
    Text(stringResource(resource = Res.string.app_name))
}
```

## Features

- **Dual Interface**: Use the CLI for quick, scriptable migrations or the Compose Desktop UI for an interactive experience.
- **Flexible Processing**: Supports single `.kt` files or entire directories with recursive processing.
- **Dry Run Mode**: Preview changes without modifying files.
- **User-Friendly UI**: Simple desktop interface for inputting paths and configuration (optional).
- **Reports**: Generates a `MigrationSummary` and `MigrationReport` detailing processed files and changes.


## Usage

```bash
Usage: kmpify [<options>]

Options:
  -i, --input=<path>    The directory containing your Android .kt files, typically your main/java/ folder.
  -o, --output=<path>   The directory where the migrated files will be saved. Usually points to your KMP commonMain/kotlin/ directory.
  -p, --project=<text>  The name of your multiplatform project (used to build resource paths).
  -s, --shared=<text>   The name of your shared module. Defaults to 'composeapp' if left empty.
  --preview=<text>      If you're using a custom @Preview annotation, you can specify it here.
  -d, --dry-run         If true, KMPify will simulate the migration and show what would be changed, but won't overwrite any files.
  -h, --help            Show this message and exit
```
---

## Installation

### For macOS Users

Download the latest release from the [Releases page](https://github.com/MahmoudRH/kmpify/releases):

1. GUI Version (Desktop App):
    - Download `KMPify-1.1.0.dmg`
    - Double-click to mount and drag KMPify to your Applications folder

2. CLI Version (Terminal):
    - Download `kmpify-cli-macOs.zip`
    - Unzip the downloaded file
      ```bash
      #Unzip
        unzip kmpify-cli-macOs.zip

      # Make executable
        chmod +x kmpify
        
      # Run 
        ./kmpify --help
      ```
  
### For Other Platforms (Linux/Windows)
1. GUI Version (Desktop App):

    ```bash
    # Clone the repository
    git clone https://github.com/MahmoudRH/kmpify.git
    cd kmpify
    
    # Run Desktop GUI
    ./gradlew :composeApp:run
    ```
 2. CLI Version (Terminal):
    - clone the repo
    - go to the cli module and run `Main.kt`
---
## 📸 Screenshots
![KMPify Migration Summary](docs/screenshots/migration_summary.png)
![KMPify Report](docs/screenshots/report_blured.png)
![KMPify Report Expanded](docs/screenshots/report_blured_expanded.png)

---

## Roadmap

- [x] Support for custom `@Preview` annotations.
- [x] Command-line interface as an alternative to the GUI.
- [ ] Hilt to Koin migration option

---

## Contributing

We welcome contributions to make KMPify better!
- Report bugs or request features at [GitHub Issues](https://github.com/MahmoudRH/kmpify/issues).
- Open a PR with a clear description of changes and reference any related issues.

---

## Contact

- **Email**: Reach out to [mhmoudrhabib@gmail.com](mailto:mhmoudrhabib@gmail.com).
- **Follow**: Stay updated on [LinkedIn](https://www.linkedin.com/in/mahmoudhabib/).

Star ⭐ this repository to support the project

---
## License

KMPify is licensed under the Apache License 2.0. See the [LICENSE](https://github.com/MahmoudRH/KMPify?tab=Apache-2.0-1-ov-file) file for details.

