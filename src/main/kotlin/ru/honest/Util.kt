package ru.honest

fun <K, V> Map<K?, V>.mapKeysNotNull(): Map<K, V> {
    return this.filterKeys { it != null }.mapKeys { it.key!! }
}
