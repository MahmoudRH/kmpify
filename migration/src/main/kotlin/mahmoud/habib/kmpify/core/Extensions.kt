package mahmoud.habib.kmpify.core

import mahmoud.habib.kmpify.model.ProcessingChanges


/**
 * Replaces all whole-word occurrences of [old] with [new].
 *
 * Uses word boundaries so partial matches (like substrings inside other words) aren't replaced.
 *
 * Example:
 * `"The cat sat."`.replaceWord("cat", "dog") → `"The dog sat."`
 */
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