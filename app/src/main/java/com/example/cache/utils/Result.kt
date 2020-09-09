package com.example.cache.utils

sealed class Result<A, E> {
    fun <B> map(mapping: (A) -> B): Result<B, E> =
        when (this) {
            is Success -> Success(
                mapping(value)
            )
            is Failure -> Failure(
                reason
            )
        }
    fun <B> bind(mapping: (A) -> Result<B, E>): Result<B, E> =
        when (this) {
            is Success -> mapping(value)
            is Failure -> Failure(
                reason
            )
        }
    fun <F> mapFailure(mapping: (E) -> F): Result<A, F> =
        when (this) {
            is Success -> Success(
                value
            )
            is Failure -> Failure(
                mapping(reason)
            )
        }
    fun bindFailure(mapping: (E) -> Result<A, E>): Result<A, E> =
        when (this) {
            is Success -> Success(
                value
            )
            is Failure -> mapping(reason)
        }
    fun orElse(other: A): A =
        when (this) {
            is Success -> value
            is Failure -> other
        }
    fun orElse(function: (E) -> A): A =
        when (this) {
            is Success -> value
            is Failure -> function(reason)
        }
    fun orNull(): A? =
        when (this) {
            is Success -> value
            is Failure -> null
        }
}

data class Success<A, E>(val value: A) : Result<A, E>()
data class Failure<A, E>(val reason: E) : Result<A, E>()