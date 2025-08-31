package de.hu.kotlin.errorhandling

fun <T, R> Result<T>.flatMap(transform: (T) -> Result<R>): Result<R> = fold(
    onSuccess = { value -> transform(value) },
    onFailure = { throwable -> Result.failure(throwable) },
)

fun List<Result<Any>>.resultExceptions(): List<Throwable> =
    filter { it.isFailure }.map { requireNotNull(it.exceptionOrNull()) }

fun <T> List<Result<T>>.resultValues(): List<T> =
    filter { it.isSuccess }.map { it.getOrThrow() }

inline fun <T> List<Result<T>>.alsoResultValues(block: (List<T>) -> Unit): List<Result<T>> =
    also { block(it.filter { r -> r.isSuccess }.map { r -> r.getOrThrow() }) }

inline fun <T> List<Result<T>>.filterResult(predicate: (T) -> Boolean): List<Result<T>> =
    filter { result -> result.map { predicate(it) }.getOrDefault(true) }

inline fun <T, R> List<Result<T>>.flatMapResult(transform: (T) -> List<Result<R>>): List<Result<R>> =
    flatMap { result ->
        result.fold(
            onSuccess = { value -> transform(value) },
            onFailure = { throwable -> listOf(Result.failure(throwable)) },
        )
    }

inline fun <T, R> List<Result<T>>.mapNotNullResult(crossinline transform: (T) -> R?): List<Result<R>> =
    mapNotNull { result ->
        result.fold(
            onSuccess = { value -> transform(value).let { if (it == null) null else Result.success(it) } },
            onFailure = { throwable -> Result.failure(throwable) },
        )
    }
