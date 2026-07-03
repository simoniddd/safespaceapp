package com.example.safespace.core.ui.components

import com.example.safespace.core.domain.model.Emotion
import com.example.safespace.core.domain.model.Emotion.ANXIOUS
import com.example.safespace.core.domain.model.Emotion.CALM
import com.example.safespace.core.domain.model.Emotion.GRATEFUL
import com.example.safespace.core.domain.model.Emotion.JOY
import com.example.safespace.core.domain.model.Emotion.TIRED

fun Emotion.toEmoji(): String = when (this) {
    JOY -> "✨"
    CALM -> "🧘"
    TIRED -> "🥱"
    ANXIOUS -> "😰"
    GRATEFUL -> "🫶"
}