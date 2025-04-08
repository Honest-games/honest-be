package ru.honest

fun <K, V> Map<K?, V>.filterKeysNotNull(): Map<K, V> {
    return this.filterKeys { it != null }.mapKeys { it.key!! }
}
