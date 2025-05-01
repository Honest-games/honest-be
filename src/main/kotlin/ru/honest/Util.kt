package ru.honest

fun <K, V> Map<K?, V>.filterKeysNotNull(): Map<K, V> {
    return this.filterKeys { it != null }.mapKeys { it.key!! }
}

fun String.toUtf8(): String {
    return String(this.toByteArray(Charsets.ISO_8859_1), Charsets.UTF_8)
}
