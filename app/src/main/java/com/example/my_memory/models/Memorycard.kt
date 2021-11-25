package com.example.my_memory.models

data class Memorycard(
    val identifier: Int,
    var isFaceup: Boolean = false,
    var isMatched: Boolean = false
)