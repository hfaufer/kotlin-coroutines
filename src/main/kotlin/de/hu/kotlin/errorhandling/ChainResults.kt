package de.hu.kotlin.errorhandling

// Demonstrates that `kotlin.Result` can be chained if the missing `flatMap` is added.
fun main() {
    println("ChainResults")
    val result = toInteger("17")
        .flatMap { plus("4", it) }
        .flatMap { times("2", it) }
        .getOrThrow()
    println("Result: $result")
}

private fun plus(s: String, x: Int): Result<Int> = toInteger(s).map { it + x }

private fun times(s: String, x: Int): Result<Int> = toInteger(s).map { it * x }

private fun toInteger(s: String): Result<Int> = runCatching { s.toInt() }
