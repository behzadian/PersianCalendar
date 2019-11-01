package com.byagowi.persiancalendar.utils

import androidx.annotation.StyleRes
import com.byagowi.persiancalendar.*
import com.byagowi.persiancalendar.entities.CityItem
import com.byagowi.persiancalendar.entities.ShiftWorkRecord
import io.github.persiancalendar.praytimes.Coordinate
import io.github.persiancalendar.praytimes.PrayTimes
import java.util.concurrent.TimeUnit

const val TAG = "Utils"
val twoSeconds = TimeUnit.SECONDS.toMillis(2)
//
//
//
// Service
//
const val DAY_IN_SECOND: Long = 86400
const val CHANGE_DATE_TAG = "changeDate"
const val UPDATE_TAG = "update"
val monthNameEmptyList = (1..12).map { "" }.toList()
var persianMonths = monthNameEmptyList
var islamicMonths = monthNameEmptyList
var gregorianMonths = monthNameEmptyList
val weekDaysEmptyList = (1..7).map { "" }.toList()
var weekDays = weekDaysEmptyList
var weekDaysInitials = weekDaysEmptyList
var preferredDigits = PERSIAN_DIGITS
var clockIn24 = DEFAULT_WIDGET_IN_24
var iranTime = DEFAULT_IRAN_TIME
var isNotifyDateOnLockScreen = DEFAULT_NOTIFY_DATE_LOCK_SCREEN
var isWidgetClock = DEFAULT_WIDGET_CLOCK
var isNotifyDate = DEFAULT_NOTIFY_DATE
var notificationAthan = DEFAULT_NOTIFICATION_ATHAN
var selectedWidgetTextColor: String = DEFAULT_SELECTED_WIDGET_TEXT_COLOR
var selectedWidgetBackgroundColor: String = DEFAULT_SELECTED_WIDGET_BACKGROUND_COLOR
//    static private String islamicOffset = DEFAULT_ISLAMIC_OFFSET;
var calculationMethod: String = DEFAULT_PRAY_TIME_METHOD
var language: String = DEFAULT_APP_LANGUAGE
var coordinate: Coordinate? = null
var mainCalendar = CalendarType.SHAMSI
var otherCalendars = listOf(CalendarType.GREGORIAN, CalendarType.ISLAMIC)
var spacedComma = "، "
var showWeekOfYear: Boolean = false
var isCenterAlignWidgets: Boolean = false
var weekStartOffset: Int = 0
var weekEnds = booleanArrayOf()
var showDeviceCalendarEvents: Boolean = false
var whatToShowOnWidgets: Set<String> = emptySet()
var isAstronomicalFeaturesEnabled: Boolean = false
@StyleRes
var appTheme = R.style.LightTheme
var isTalkBackEnabled = false
var prayTimes: PrayTimes? = null
var cachedCityKey = ""
var cachedCity: CityItem? = null
var sShiftWorkTitles: Map<String, String> = emptyMap()
var sShiftWorkStartingJdn: Long = -1
var sShiftWorkRecurs = true
var sShiftWorks: List<ShiftWorkRecord> = emptyList()
var sIsIranHolidaysEnabled = true
var sShiftWorkPeriod = 0
var sAM = DEFAULT_AM
var sPM = DEFAULT_PM
var latestToastShowTime: Long = -1
var numericalDatePreferred = false
var calendarTypesTitleAbbr = emptyList<String>()