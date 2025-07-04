package mahmoud.habib.kmpify.utils

import java.io.File
import javax.swing.JFileChooser
import javax.swing.filechooser.FileFilter
import javax.swing.filechooser.FileSystemView


fun browseForDirectoryOrKtFile(title: String): String? {
    val fileChooser = object : JFileChooser(FileSystemView.getFileSystemView().homeDirectory) {
        override fun approveSelection() {
            val file = selectedFile
            if (file != null && (file.isDirectory || file.name.endsWith(".kt"))) {
                super.approveSelection()
            }
        }
    }

    fileChooser.apply {
        dialogTitle = title
        fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
        isAcceptAllFileFilterUsed = false
        fileFilter = object : FileFilter() {
            override fun accept(f: File): Boolean {
                return f.isDirectory || f.name.endsWith(".kt")
            }

            override fun getDescription(): String = "Kotlin Files (*.kt) or Directories"
        }
    }

    val result = fileChooser.showDialog(null, "Select")
    if (result == JFileChooser.APPROVE_OPTION) {
        val selected = fileChooser.selectedFile

        return if (selected.isDirectory) {
            selected.canonicalPath // normalized
        } else {
            selected.absolutePath
        }
    }

    return null
}