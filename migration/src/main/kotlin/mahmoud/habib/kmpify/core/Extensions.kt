package mahmoud.habib.kmpify.core

import mahmoud.habib.kmpify.model.ProcessingChanges


fun String.replaceWord(old: String, new: String): String {
    val regex = Regex("""\b$old\b""")
    return regex.replace(this, new)
}

inline fun <T> Iterable<T>.sumOf(selector: (T) -> ProcessingChanges): ProcessingChanges {
    var sum: ProcessingChanges = ProcessingChanges()
    for (element in this) {
        sum += selector(element)
    }
    return sum
}