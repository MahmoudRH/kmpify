package mahmoud.habib.kmpify.model

data class ResourceReferences(
    val drawable: MutableSet<String> = mutableSetOf(),
    val string: MutableSet<String> = mutableSetOf(),
    val font: MutableSet<String> = mutableSetOf()
) {
    fun isEmpty() = drawable.isEmpty() && string.isEmpty() && font.isEmpty()
}