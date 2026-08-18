package com.example.safespace.core.domain.model.notification

sealed interface ParseResult {
    data class Success(val tx: ParsedTransaction): ParseResult //распознали трату
    data object NotATransaction: ParseResult // кэшбек, промо, пополнение
    data class Unrecognized(val reason: String): ParseResult // формат не распознан
}