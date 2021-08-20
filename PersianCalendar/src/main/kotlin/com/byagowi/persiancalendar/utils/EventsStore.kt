package com.byagowi.persiancalendar.utils

import com.byagowi.persiancalendar.entities.CalendarEvent
import io.github.persiancalendar.calendar.AbstractDate
import io.github.persiancalendar.calendar.CivilDate

@JvmInline
value class EventsStore<T : CalendarEvent<out AbstractDate>>
private constructor(private val store: Map<Int, List<T>>) {
    constructor(eventsList: List<T>) : this(eventsList.groupBy { it.date.hash })

    // Year and month equality is checked by hash function and
    // we don't care about year as we expect eventsList to not have
    // any year specific event so we ignore year field.
    private fun getEventsEntry(date: AbstractDate) = store[date.hash] ?: emptyList()

    fun getEvents(date: AbstractDate) = getEventsEntry(date) +
            irregularCalendarEventsStore.getEvents(date)

    fun getEvents(
        date: CivilDate, deviceEvents: DeviceCalendarEventsStore
    ): List<CalendarEvent<*>> = deviceEvents.getEventsEntry(date) + getEvents(date)

    companion object {
        private val AbstractDate.hash get() = this.month * 100 + this.dayOfMonth
        fun <T : CalendarEvent<out AbstractDate>> empty() = EventsStore<T>(emptyMap())
    }
}

typealias PersianCalendarEventsStore = EventsStore<CalendarEvent.PersianCalendarEvent>
typealias IslamicCalendarEventsStore = EventsStore<CalendarEvent.IslamicCalendarEvent>
typealias GregorianCalendarEventsStore = EventsStore<CalendarEvent.GregorianCalendarEvent>
typealias DeviceCalendarEventsStore = EventsStore<CalendarEvent.DeviceCalendarEvent>
