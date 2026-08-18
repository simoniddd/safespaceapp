package com.example.safespace.core.domain.model.notification

interface BankNotificationParser {
    fun canHandle(packageName: String): Boolean
    fun parse(payload: NotificationPayload): ParseResult
}