package com.example.safespace.feature.calendar.data.source

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import com.example.safespace.feature.calendar.domain.source.CalendarSource

class CalendarPreferencesDataStore(
    private val dataStore: DataStore<Preferences>
): CalendarSource {
    companion object {
        private val ENABLED_CALENDAR_IDS = stringSetPreferencesKey("enabled_calendar_ids")
    }

    override val hiddenCalendarIds: Flow<Set<Long>>
        get() = dataStore.data.map { prefs ->
            prefs[ENABLED_CALENDAR_IDS]?.mapNotNull { it.toLongOrNull() }?.toSet() ?: emptySet()
        }

    override suspend fun setHiddenCalendarIds(ids: Set<Long>) {
        dataStore.edit { prefs ->
            prefs[ENABLED_CALENDAR_IDS] = ids.map { it.toString() }.toSet()
        }
    }
}