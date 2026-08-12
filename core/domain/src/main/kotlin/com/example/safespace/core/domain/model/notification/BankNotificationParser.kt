package com.example.safespace.core.domain.model.notification

import com.example.safespace.core.domain.model.ExpensesItem

interface BankNotificationParser {
    fun canHandle(packageName: String): Boolean
    fun parse(payload: NotificationPayload): ExpensesItem?
}