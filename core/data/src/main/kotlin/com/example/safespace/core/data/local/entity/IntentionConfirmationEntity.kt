package com.example.safespace.core.data.local.entity

import androidx.room.Entity

@Entity(tableName = "intention_confirmations", primaryKeys = ["eventId", "instanceStart"])
data class IntentionConfirmationEntity(
    val eventId: String,
    val instanceStart: Long,
    val status: String,
    val answeredAt: Long? = null,
)