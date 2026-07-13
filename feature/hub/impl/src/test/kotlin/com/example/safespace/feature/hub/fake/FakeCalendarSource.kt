package com.example.safespace.feature.hub.fake

import com.example.safespace.feature.calendar.domain.source.CalendarSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeCalendarSource : CalendarSource {

    private val _hiddenIds = MutableStateFlow<Set<Long>>(emptySet())
    override val hiddenCalendarIds: Flow<Set<Long>> = _hiddenIds

    override suspend fun setHiddenCalendarIds(ids: Set<Long>) {
        _hiddenIds.value = ids
    }
}
