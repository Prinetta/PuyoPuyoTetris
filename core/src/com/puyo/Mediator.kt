package com.puyo

class Mediator {
    
    fun <K, V> Map<K, V>.inverseMap() = map { Pair(it.value, it.key) }.toMap()
}