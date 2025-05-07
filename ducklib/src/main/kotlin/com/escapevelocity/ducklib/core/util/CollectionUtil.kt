package com.escapevelocity.ducklib.core.util

/**
 * Checks if a collection contains any items from another collection.
 */
fun <T> Collection<T>.containsAny(collection: Collection<T>): Boolean {
    for (e in this) {
        if (e in collection) {
            return true;
        }
    }
    return false;
}
