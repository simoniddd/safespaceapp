package com.example.safespace.core.domain.model.notification

data class NotificationPayload(
    val packageName: String,
    val title: String?,
    val text: String?,
    val bigText: String?,
    val postTime: Long
)