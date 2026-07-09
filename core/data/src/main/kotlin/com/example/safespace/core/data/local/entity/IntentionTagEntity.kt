package com.example.safespace.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "intention_tags")
data class IntentionTagEntity(
    @PrimaryKey
    val eventId: String
)