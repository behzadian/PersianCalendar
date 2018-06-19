package com.byagowi.persiancalendar.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.RawRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.byagowi.persiancalendar.R;
import com.byagowi.persiancalendar.adapter.ShapedArrayAdapter;
import com.byagowi.persiancalendar.entity.CityEntity;
import com.byagowi.persiancalendar.entity.DayEntity;
import com.byagowi.persiancalendar.entity.EventEntity;
import com.byagowi.persiancalendar.enums.CalendarTypeEnum;
import com.byagowi.persiancalendar.enums.SeasonEnum;
import com.byagowi.persiancalendar.service.BroadcastReceivers;
import com.github.praytimes.CalculationMethod;
import com.github.praytimes.Clock;
import com.github.praytimes.Coordinate;
import com.github.praytimes.PrayTime;
import com.github.praytimes.PrayTimesCalculator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import calendar.AbstractDate;
import calendar.CivilDate;
import calendar.DateConverter;
import calendar.DayOutOfRangeException;
import calendar.IslamicDate;
import calendar.PersianDate;

import static com.byagowi.persiancalendar.Constants.AM_IN_PERSIAN;
import static com.byagowi.persiancalendar.Constants.ARABIC_DIGITS;
import static com.byagowi.persiancalendar.Constants.BROADCAST_ALARM;
import static com.byagowi.persiancalendar.Constants.BROADCAST_RESTART_APP;
import static com.byagowi.persiancalendar.Constants.DARK_THEME;
import static com.byagowi.persiancalendar.Constants.DAYS_ICONS;
import static com.byagowi.persiancalendar.Constants.DAYS_ICONS_AR;
import static com.byagowi.persiancalendar.Constants.DEFAULT_ALTITUDE;
import static com.byagowi.persiancalendar.Constants.DEFAULT_APP_LANGUAGE;
import static com.byagowi.persiancalendar.Constants.DEFAULT_ATHAN_VOLUME;
import static com.byagowi.persiancalendar.Constants.DEFAULT_CITY;
import static com.byagowi.persiancalendar.Constants.DEFAULT_IRAN_TIME;
import static com.byagowi.persiancalendar.Constants.DEFAULT_ISLAMIC_OFFSET;
import static com.byagowi.persiancalendar.Constants.DEFAULT_LATITUDE;
import static com.byagowi.persiancalendar.Constants.DEFAULT_LONGITUDE;
import static com.byagowi.persiancalendar.Constants.DEFAULT_NOTIFY_DATE;
import static com.byagowi.persiancalendar.Constants.DEFAULT_NOTIFY_DATE_LOCK_SCREEN;
import static com.byagowi.persiancalendar.Constants.DEFAULT_PERSIAN_DIGITS;
import static com.byagowi.persiancalendar.Constants.DEFAULT_PRAY_TIME_METHOD;
import static com.byagowi.persiancalendar.Constants.DEFAULT_SELECTED_WIDGET_TEXT_COLOR;
import static com.byagowi.persiancalendar.Constants.DEFAULT_WIDGET_CLOCK;
import static com.byagowi.persiancalendar.Constants.DEFAULT_WIDGET_IN_24;
import static com.byagowi.persiancalendar.Constants.KEY_EXTRA_PRAYER_KEY;
import static com.byagowi.persiancalendar.Constants.LIGHT_THEME;
import static com.byagowi.persiancalendar.Constants.PERSIAN_COMMA;
import static com.byagowi.persiancalendar.Constants.PERSIAN_DIGITS;
import static com.byagowi.persiancalendar.Constants.PM_IN_PERSIAN;
import static com.byagowi.persiancalendar.Constants.PREF_ALTITUDE;
import static com.byagowi.persiancalendar.Constants.PREF_APP_LANGUAGE;
import static com.byagowi.persiancalendar.Constants.PREF_ATHAN_ALARM;
import static com.byagowi.persiancalendar.Constants.PREF_ATHAN_GAP;
import static com.byagowi.persiancalendar.Constants.PREF_ATHAN_VOLUME;
import static com.byagowi.persiancalendar.Constants.PREF_GEOCODED_CITYNAME;
import static com.byagowi.persiancalendar.Constants.PREF_IRAN_TIME;
import static com.byagowi.persiancalendar.Constants.PREF_ISLAMIC_OFFSET;
import static com.byagowi.persiancalendar.Constants.PREF_LATITUDE;
import static com.byagowi.persiancalendar.Constants.PREF_LONGITUDE;
import static com.byagowi.persiancalendar.Constants.PREF_NOTIFY_DATE;
import static com.byagowi.persiancalendar.Constants.PREF_NOTIFY_DATE_LOCK_SCREEN;
import static com.byagowi.persiancalendar.Constants.PREF_PERSIAN_DIGITS;
import static com.byagowi.persiancalendar.Constants.PREF_PRAY_TIME_METHOD;
import static com.byagowi.persiancalendar.Constants.PREF_SELECTED_LOCATION;
import static com.byagowi.persiancalendar.Constants.PREF_SELECTED_WIDGET_TEXT_COLOR;
import static com.byagowi.persiancalendar.Constants.PREF_THEME;
import static com.byagowi.persiancalendar.Constants.PREF_WIDGET_CLOCK;
import static com.byagowi.persiancalendar.Constants.PREF_WIDGET_IN_24;

/**
 * Common utilities that needed for this calendar
 *
 * @author ebraminio
 */

public class Utils {

    static private final String TAG = Utils.class.getName();

    static private List<EventEntity>[] events;

    static private String[] persianMonths;
    static private String[] islamicMonths;
    static private String[] gregorianMonths;
    static private String[] weekDays;

    static public void setActivityTitleAndSubtitle(Activity activity, String title, String subtitle) {
        //noinspection ConstantConditions
        ActionBar supportActionBar = ((AppCompatActivity) activity).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(title);
            supportActionBar.setSubtitle(subtitle);
        }
    }

    static public Coordinate getCoordinate(Context context) {
        CityEntity cityEntity = getCityFromPreference(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        if (cityEntity != null) {
            return cityEntity.getCoordinate();
        }

        try {
            Coordinate coord = new Coordinate(
                    Double.parseDouble(prefs.getString(PREF_LATITUDE, DEFAULT_LATITUDE)),
                    Double.parseDouble(prefs.getString(PREF_LONGITUDE, DEFAULT_LONGITUDE)),
                    Double.parseDouble(prefs.getString(PREF_ALTITUDE, DEFAULT_ALTITUDE))
            );

            // If latitude or longitude is zero probably preference is not set yet
            if (coord.getLatitude() == 0 && coord.getLongitude() == 0) {
                return null;
            }

            return coord;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    static private char[] preferredDigits = PERSIAN_DIGITS;
    static private boolean clockIn24 = DEFAULT_WIDGET_IN_24;
    static private boolean iranTime = DEFAULT_IRAN_TIME;
    static private boolean notifyInLockScreen = DEFAULT_NOTIFY_DATE_LOCK_SCREEN;
    static private boolean widgetClock = DEFAULT_WIDGET_CLOCK;
    static private boolean notifyDate = DEFAULT_NOTIFY_DATE;
    static private String selectedWidgetTextColor = DEFAULT_SELECTED_WIDGET_TEXT_COLOR;
    static private String islamicOffset = DEFAULT_ISLAMIC_OFFSET;
    static private String calculationMethod = DEFAULT_PRAY_TIME_METHOD;
    static private String language = DEFAULT_APP_LANGUAGE;
    static private Coordinate coordinate;

    static public void updateStoredPreference(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        preferredDigits = prefs.getBoolean(PREF_PERSIAN_DIGITS, DEFAULT_PERSIAN_DIGITS)
                ? PERSIAN_DIGITS
                : ARABIC_DIGITS;

        clockIn24 = prefs.getBoolean(PREF_WIDGET_IN_24, DEFAULT_WIDGET_IN_24);
        iranTime = prefs.getBoolean(PREF_IRAN_TIME, DEFAULT_IRAN_TIME);
        notifyInLockScreen = prefs.getBoolean(PREF_NOTIFY_DATE_LOCK_SCREEN,
                DEFAULT_NOTIFY_DATE_LOCK_SCREEN);
        widgetClock = prefs.getBoolean(PREF_WIDGET_CLOCK, DEFAULT_WIDGET_CLOCK);
        notifyDate = prefs.getBoolean(PREF_NOTIFY_DATE, DEFAULT_NOTIFY_DATE);
        selectedWidgetTextColor = prefs.getString(PREF_SELECTED_WIDGET_TEXT_COLOR,
                DEFAULT_SELECTED_WIDGET_TEXT_COLOR);
        islamicOffset = prefs.getString(PREF_ISLAMIC_OFFSET, DEFAULT_ISLAMIC_OFFSET);
        // We were using "Jafari" method but later found out Tehran is nearer to time.ir and others
        // so switched to "Tehran" method as default calculation algorithm
        calculationMethod = prefs.getString(PREF_PRAY_TIME_METHOD, DEFAULT_PRAY_TIME_METHOD);
        language = prefs.getString(PREF_APP_LANGUAGE, DEFAULT_APP_LANGUAGE);
        coordinate = getCoordinate(context);
    }

    static public boolean isIranTime() {
        return iranTime;
    }

    static public boolean isPersianDigitSelected() {
        return preferredDigits == PERSIAN_DIGITS;
    }

    static public void setTheme(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String key = prefs.getString(PREF_THEME, "");

        int theme = R.style.LightTheme; // default theme

        if (key.equals(LIGHT_THEME)) {
            theme = R.style.LightTheme;
        } else if (key.equals(DARK_THEME)) {
            theme = R.style.DarkTheme;
        }

        context.setTheme(theme);
    }

    static public boolean isWidgetClock() {
        return widgetClock;
    }

    static public boolean isNotifyDate() {
        return notifyDate;
    }

    static public int getAthanVolume(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getInt(PREF_ATHAN_VOLUME, DEFAULT_ATHAN_VOLUME);
    }

    static public boolean isNotifyDateOnLockScreen() {
        return notifyInLockScreen;
    }

    static public CalculationMethod getCalculationMethod() {
        return CalculationMethod.valueOf(calculationMethod);
    }

    static public int getIslamicOffset() {
        return Integer.parseInt(islamicOffset.replace("+", ""));
    }

    static public String getAppLanguage() {
        // If is empty for whatever reason (pref dialog bug, etc), return Persian at least
        return TextUtils.isEmpty(language) ? DEFAULT_APP_LANGUAGE : language;
    }

    static public String getTheme(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        return prefs.getString(PREF_THEME, LIGHT_THEME);
    }

    static public String getSelectedWidgetTextColor() {
        return selectedWidgetTextColor;
    }

    static public PersianDate getToday() {
        return DateConverter.civilToPersian(new CivilDate(makeCalendarFromDate(new Date())));
    }

    static public Calendar makeCalendarFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        if (iranTime) {
            calendar.setTimeZone(TimeZone.getTimeZone("Asia/Tehran"));
        }
        calendar.setTime(date);
        return calendar;
    }

    static private String clockToString(int hour, int minute) {
        return formatNumber(String.format(Locale.ENGLISH, "%d:%02d", hour, minute));
    }

    static private Map<PrayTime, Clock> prayTimes;
    static public String getNextOghatTime(Context context, Clock clock, boolean dateHasChanged) {
        if (coordinate == null) return null;

        if (prayTimes == null || dateHasChanged) {
            prayTimes = new PrayTimesCalculator(getCalculationMethod())
                    .calculate(new Date(), coordinate);
        }

        if (prayTimes.get(PrayTime.FAJR).getInt() > clock.getInt()) {
            return context.getString(R.string.azan1) + ": " + getPersianFormattedClock(prayTimes.get(PrayTime.FAJR));

        } else if (prayTimes.get(PrayTime.SUNRISE).getInt() > clock.getInt()) {
            return context.getString(R.string.aftab1) + ": " + getPersianFormattedClock(prayTimes.get(PrayTime.SUNRISE));

        } else if (prayTimes.get(PrayTime.DHUHR).getInt() > clock.getInt()) {
            return context.getString(R.string.azan2) + ": " + getPersianFormattedClock(prayTimes.get(PrayTime.DHUHR));

        } else if (prayTimes.get(PrayTime.ASR).getInt() > clock.getInt()) {
            return context.getString(R.string.azan3) + ": " + getPersianFormattedClock(prayTimes.get(PrayTime.ASR));

        } else if (prayTimes.get(PrayTime.SUNSET).getInt() > clock.getInt()) {
            return context.getString(R.string.aftab2) + ": " + getPersianFormattedClock(prayTimes.get(PrayTime.SUNSET));

        } else if (prayTimes.get(PrayTime.MAGHRIB).getInt() > clock.getInt()) {
            return context.getString(R.string.azan4) + ": " + getPersianFormattedClock(prayTimes.get(PrayTime.MAGHRIB));

        } else if (prayTimes.get(PrayTime.ISHA).getInt() > clock.getInt()) {
            return context.getString(R.string.azan5) + ": " + getPersianFormattedClock(prayTimes.get(PrayTime.ISHA));

        } else if (prayTimes.get(PrayTime.MIDNIGHT).getInt() > clock.getInt()) {
            return context.getString(R.string.aftab3) + ": " + getPersianFormattedClock(prayTimes.get(PrayTime.MIDNIGHT));

        } else {
            return context.getString(R.string.azan1) + ": " + getPersianFormattedClock(prayTimes.get(PrayTime.FAJR)); //this is today & not tomorrow
        }
    }

    static public String getPersianFormattedClock(Clock clock) {
        String timeText = null;

        int hour = clock.getHour();
        if (!clockIn24) {
            if (hour >= 12) {
                timeText = PM_IN_PERSIAN;
                hour -= 12;
            } else {
                timeText = AM_IN_PERSIAN;
            }
        }

        String result = clockToString(hour, clock.getMinute());
        if (!clockIn24) {
            result = result + " " + timeText;
        }
        return result;
    }

    static public String getPersianFormattedClock(Calendar calendar) {
        String timeText = null;

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (!clockIn24) {
            if (calendar.get(Calendar.HOUR_OF_DAY) >= 12) {
                timeText = PM_IN_PERSIAN;
                hour -= 12;
            } else {
                timeText = AM_IN_PERSIAN;
            }
        }

        String result = clockToString(hour, calendar.get(Calendar.MINUTE));
        if (!clockIn24) {
            result = result + " " + timeText;
        }
        return result;
    }

    static public String formatNumber(int number) {
        return formatNumber(Integer.toString(number));
    }

    static public String formatNumber(String number) {
        if (preferredDigits == ARABIC_DIGITS)
            return number;

        char[] result = number.toCharArray();
        for (int i = 0; i < result.length; ++i) {
            char c = number.charAt(i);
            if (Character.isDigit(c))
                result[i] = preferredDigits[Character.getNumericValue(c)];
        }
        return String.valueOf(result);
    }

    static public String dateToString(Context context, AbstractDate date) {
        return formatNumber(date.getDayOfMonth()) + ' ' + getMonthName(context, date) + ' ' +
                formatNumber(date.getYear());
    }

    static public String dayTitleSummary(Context context, PersianDate persianDate) {
        return getWeekDayName(context, persianDate) + PERSIAN_COMMA + " " + dateToString(context, persianDate);
    }

    static private String[] monthsNamesOfCalendar(Context context, AbstractDate date) {
        // the next step would be using them so lets check if they have initialized already
        if (persianMonths == null || gregorianMonths == null || islamicMonths == null)
            loadLanguageResource(context);

        if (date instanceof PersianDate)
            return persianMonths;
        else if (date instanceof IslamicDate)
            return islamicMonths;
        else
            return gregorianMonths;
    }

    static public String getMonthName(Context context, AbstractDate date) {
        return monthsNamesOfCalendar(context, date)[date.getMonth() - 1];
    }

    static public String getWeekDayName(Context context, AbstractDate date) {
        if (date instanceof IslamicDate)
            date = DateConverter.islamicToCivil((IslamicDate) date);
        else if (date instanceof PersianDate)
            date = DateConverter.persianToCivil((PersianDate) date);

        if (weekDays == null)
            loadLanguageResource(context);

        return weekDays[date.getDayOfWeek() % 7];
    }

    static public int getDayIconResource(int day) {
        try {
            return preferredDigits == ARABIC_DIGITS ? DAYS_ICONS_AR[day] : DAYS_ICONS[day];
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "No such field is available");
            return 0;
        }
    }

    static private String readStream(InputStream is) {
        // http://stackoverflow.com/a/5445161
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    static public String readRawResource(Context context, @RawRes int res) {
        return readStream(context.getResources().openRawResource(res));
    }

    static private String persianStringToArabic(String text) {
        return text
                .replaceAll("ی", "ي")
                .replaceAll("ک", "ك")
                .replaceAll("گ", "كی")
                .replaceAll("ژ", "زی")
                .replaceAll("چ", "جی")
                .replaceAll("پ", "بی");
    }

    static private <T> Iterable<T> iteratorToIterable(final Iterator<T> iterator) {
        return () -> iterator;
    }

    static public List<CityEntity> getAllCities(Context context, boolean needsSort) {
        List<CityEntity> result = new ArrayList<>();
        try {
            JSONObject countries = new JSONObject(readRawResource(context, R.raw.cities));

            for (String countryCode : iteratorToIterable(countries.keys())) {
                JSONObject country = countries.getJSONObject(countryCode);

                String countryEn = country.getString("en");
                String countryFa = country.getString("fa");

                JSONObject cities = country.getJSONObject("cities");

                for (String key : iteratorToIterable(cities.keys())) {
                    JSONObject city = cities.getJSONObject(key);

                    String en = city.getString("en");
                    String fa = city.getString("fa");

                    Coordinate coordinate = new Coordinate(
                            city.getDouble("latitude"),
                            city.getDouble("longitude"),
                            0 // city.getDouble("elevation")
                    );

                    result.add(new CityEntity(key, en, fa, countryCode, countryEn, countryFa, coordinate));
                }
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }

        if (!needsSort) {
            return result;
        }

        CityEntity[] cities = result.toArray(new CityEntity[result.size()]);
        // Sort first by country code then city
        Arrays.sort(cities, (l, r) -> {
            if (l.getKey().equals("")) {
                return -1;
            }
            if (r.getKey().equals(DEFAULT_CITY)) {
                return 1;
            }
            int compare = r.getCountryCode().compareTo(l.getCountryCode());
            if (compare != 0) return compare;
            if (language.equals("en")) {
                return l.getEn().compareTo(r.getEn());
            } else {
                return persianStringToArabic(l.getFa())
                        .compareTo(persianStringToArabic(r.getFa()));
            }
        });

        return Arrays.asList(cities);
    }

    static private String cachedCityKey = "";
    static private CityEntity cachedCity;

    static private CityEntity getCityFromPreference(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String key = prefs.getString(PREF_SELECTED_LOCATION, "");

        if (TextUtils.isEmpty(key) || key.equals(DEFAULT_CITY))
            return null;

        if (key.equals(cachedCityKey))
            return cachedCity;

        // cache last query even if no city available under the key, useful in case invalid
        // value is somehow inserted on the preference
        cachedCityKey = key;

        for (CityEntity cityEntity : getAllCities(context, false))
            if (cityEntity.getKey().equals(key))
                return cachedCity = cityEntity;

        return cachedCity = null;
    }

    static public String formatCoordinate(Context context, Coordinate coordinate, String separator) {
        return String.format(Locale.getDefault(), "%s: %.4f%s%s: %.4f",
                context.getString(R.string.latitude), coordinate.getLatitude(), separator,
                context.getString(R.string.longitude), coordinate.getLongitude());
    }

    static public String getCityName(Context context, boolean fallbackToCoord) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        CityEntity cityEntity = getCityFromPreference(context);
        if (cityEntity != null)
            return language.equals("en") ? cityEntity.getEn() : cityEntity.getFa();

        String geocodedCityName = prefs.getString(PREF_GEOCODED_CITYNAME, "");
        if (!TextUtils.isEmpty(geocodedCityName))
            return geocodedCityName;

        if (fallbackToCoord)
            if (coordinate != null)
                return formatCoordinate(context, coordinate, PERSIAN_COMMA + " ");

        return "";
    }

    static private void loadEvents(Context context) {
        List<EventEntity>[] events = new ArrayList[32];
        for (int i = 0; i < 32; ++i)
            events[i] = new ArrayList<>();
        try {
            JSONArray days = new JSONObject(readRawResource(context, R.raw.events)).getJSONArray("events");

            int length = days.length();
            for (int i = 0; i < length; ++i) {
                JSONObject event = days.getJSONObject(i);

                int year = event.getInt("year");
                int month = event.getInt("month");
                int day = event.getInt("day");
                String title = event.getString("title");
                boolean holiday = event.getBoolean("holiday");

                events[day].add(new EventEntity(new PersianDate(year, month, day), title, holiday));
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
        Utils.events = events;
    }

    static private int maxSupportedYear = -1;
    static private int minSupportedYear = -1;
    static private boolean isYearWarnGivenOnce = false;

    static public void checkYearAndWarnIfNeeded(Context context, int selectedYear) {
        // once is enough, see #clearYearWarnFlag() also
        if (isYearWarnGivenOnce)
            return;

        if (maxSupportedYear == -1 || minSupportedYear == -1)
            loadMinMaxSupportedYear(context);

        if (selectedYear < minSupportedYear) {
            Toast.makeText(context, context.getString(R.string.holidaysIncompletenessWarning), Toast.LENGTH_LONG).show();
            isYearWarnGivenOnce = true;
        }

        if (selectedYear > maxSupportedYear) {
            Toast.makeText(context, context.getString(getToday().getYear() > maxSupportedYear
                    ? R.string.shouldBeUpdated
                    : R.string.holidaysIncompletenessWarning), Toast.LENGTH_LONG).show();

            isYearWarnGivenOnce = true;
        }
    }

    // called from CalendarFragment to make it once per calendar view
    static public void clearYearWarnFlag() {
        isYearWarnGivenOnce = false;
    }

    static private void loadMinMaxSupportedYear(Context context) {
        if (events == null) {
            loadEvents(context);
        }

        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (List<EventEntity> eventsList : events)
            for (EventEntity eventEntity : eventsList) {
                int year = eventEntity.getDate().getYear();

                if (min > year && year != -1) {
                    min = year;
                }

                if (max < year) {
                    max = year;
                }
            }

        minSupportedYear = min;
        maxSupportedYear = max;
    }

    static private List<EventEntity> getEvents(Context context, PersianDate day) {
        if (events == null) {
            loadEvents(context);
        }

        List<EventEntity> result = new ArrayList<>();
        for (EventEntity eventEntity : events[day.getDayOfMonth()]) {
            if (eventEntity.getDate().equals(day)) {
                result.add(eventEntity);
            }
        }
        return result;
    }

    static public String getEventsTitle(Context context, PersianDate day, boolean holiday) {
        StringBuilder titles = new StringBuilder();
        boolean first = true;
        List<EventEntity> dayEvents = getEvents(context, day);

        for (EventEntity event : dayEvents) {
            if (event.isHoliday() == holiday) {
                if (first) {
                    first = false;

                } else {
                    titles.append("\n");
                }
                titles.append(event.getTitle());
            }
        }
        return titles.toString();
    }

    static public void loadApp(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 0);
        startTime.set(Calendar.MINUTE, 1);
        Intent intent = new Intent(context, BroadcastReceivers.class);
        intent.setAction(BROADCAST_RESTART_APP);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC, startTime.getTimeInMillis(), pendingIntent);
    }

    static public boolean isServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    static public void loadAlarms(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String prefString = prefs.getString(PREF_ATHAN_ALARM, "");
        Log.d(TAG, "reading and loading all alarms from prefs: " + prefString);
        CalculationMethod calculationMethod = getCalculationMethod();

        if (calculationMethod != null && coordinate != null && !TextUtils.isEmpty(prefString)) {
            PrayTimesCalculator calculator = new PrayTimesCalculator(calculationMethod);
            Map<PrayTime, Clock> prayTimes = calculator.calculate(new Date(), coordinate);
            // convert comma separated string to a set
            Set<String> alarmTimesSet = new HashSet<>(Arrays.asList(TextUtils.split(prefString, ",")));
            // in the past IMSAK was used but now we figured out FAJR was what we wanted
            if (alarmTimesSet.remove("IMSAK"))
                alarmTimesSet.add("FAJR");

            String[] alarmTimesNames = alarmTimesSet.toArray(new String[alarmTimesSet.size()]);
            for (int i = 0; i < alarmTimesNames.length; i++) {
                PrayTime prayTime = PrayTime.valueOf(alarmTimesNames[i]);

                Clock alarmTime = prayTimes.get(prayTime);

                if (alarmTime != null)
                    setAlarm(context, prayTime, alarmTime, i);
            }
        }
    }

    static private void setAlarm(Context context, PrayTime prayTime, Clock clock, int id) {
        Calendar triggerTime = Calendar.getInstance();
        triggerTime.set(Calendar.HOUR_OF_DAY, clock.getHour());
        triggerTime.set(Calendar.MINUTE, clock.getMinute());
        setAlarm(context, prayTime, triggerTime.getTimeInMillis(), id);
    }

    static private void setAlarm(Context context, PrayTime prayTime, long timeInMillis, int id) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        String valAthanGap = prefs.getString(PREF_ATHAN_GAP, "0");
        long athanGap;
        try {
            athanGap = (long) (Double.parseDouble(valAthanGap) * 60);
        } catch (NumberFormatException e) {
            athanGap = 0;
        }

        Calendar triggerTime = Calendar.getInstance();
        triggerTime.setTimeInMillis(timeInMillis - TimeUnit.SECONDS.toMillis(athanGap));
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // don't set an alarm in the past
        if (!triggerTime.before(Calendar.getInstance())) {
            Log.d(TAG, "setting alarm for: " + triggerTime.getTime());

            Intent intent = new Intent(context, BroadcastReceivers.class);
            intent.setAction(BROADCAST_ALARM);
            intent.putExtra(KEY_EXTRA_PRAYER_KEY, prayTime.name());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                SetExactAlarm.setExactAlarm(alarmManager,
                        AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis(), pendingIntent);
            }
        }
    }

    static private class SetExactAlarm {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        public static void setExactAlarm(AlarmManager alarmManager,
                                         int type, long triggerAtMillis, PendingIntent pendingIntent) {
            alarmManager.setExact(type, triggerAtMillis, pendingIntent);
        }
    }

    static public Uri getAthanUri(Context context) {
        String defaultSoundUri = "android.resource://" + context.getPackageName() + "/" + R.raw.abdulbasit;
        return Uri.parse(defaultSoundUri);
    }

    // Context preferably should be activity context not application
    static public void changeAppLanguage(Context context) {
        String localeCode = language.replaceAll("-(IR|AF)", "");
        Locale locale = new Locale(localeCode);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.locale = locale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLayoutDirection(config.locale);
        }
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    static public void loadLanguageResource(Context context) {
        @RawRes int messagesFile;
        switch (language) {
            case "fa-AF":
                messagesFile = R.raw.messages_fa_af;
                break;
            case "ps":
                messagesFile = R.raw.messages_ps;
                break;
            default:
                messagesFile = R.raw.messages_fa;
                break;
        }

        persianMonths = new String[12];
        islamicMonths = new String[12];
        gregorianMonths = new String[12];
        weekDays = new String[7];

        try {
            JSONObject messages = new JSONObject(readRawResource(context, messagesFile));

            JSONArray persianMonthsArray = messages.getJSONArray("PersianCalendarMonths");
            for (int i = 0; i < 12; ++i)
                persianMonths[i] = persianMonthsArray.getString(i);

            JSONArray islamicMonthsArray = messages.getJSONArray("IslamicCalendarMonths");
            for (int i = 0; i < 12; ++i)
                islamicMonths[i] = islamicMonthsArray.getString(i);

            JSONArray gregorianMonthsArray = messages.getJSONArray("GregorianCalendarMonths");
            for (int i = 0; i < 12; ++i)
                gregorianMonths[i] = gregorianMonthsArray.getString(i);

            JSONArray weekDaysArray = messages.getJSONArray("WeekDays");
            for (int i = 0; i < 7; ++i)
                weekDays[i] = weekDaysArray.getString(i);

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    static public void copyToClipboard(Context context, View view) {
        // if it is older than this, the view is also shaped which is not good for copying, so just
        // nvm about backup solution for older Androids
        CharSequence text = ((TextView) view).getText();
        ((ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE))
                .setPrimaryClip(ClipData.newPlainText("converted date", text));
        Toast.makeText(context, "«" + text + "»\n" + context.getString(R.string.date_copied_clipboard), Toast.LENGTH_SHORT).show();
    }

    static public SeasonEnum getSeason() {
        int month = getToday().getMonth();

        if (month < 4) {
            return SeasonEnum.SPRING;

        } else if (month < 7) {
            return SeasonEnum.SUMMER;

        } else if (month < 10) {
            return SeasonEnum.FALL;

        } else {
            return SeasonEnum.WINTER;
        }
    }

    static public List<DayEntity> getDays(Context context, int offset) {
        List<DayEntity> days = new ArrayList<>();
        PersianDate persianDate = getToday();
        int month = persianDate.getMonth() - offset;
        month -= 1;
        int year = persianDate.getYear();

        year = year + (month / 12);
        month = month % 12;
        if (month < 0) {
            year -= 1;
            month += 12;
        }
        month += 1;
        persianDate.setMonth(month);
        persianDate.setYear(year);
        persianDate.setDayOfMonth(1);

        int dayOfWeek = DateConverter.persianToCivil(persianDate).getDayOfWeek() % 7;

        try {
            PersianDate today = getToday();
            for (int i = 1; i <= 31; i++) {
                persianDate.setDayOfMonth(i);

                DayEntity dayEntity = new DayEntity();
                dayEntity.setNum(formatNumber(i));
                dayEntity.setDayOfWeek(dayOfWeek);

                if (dayOfWeek == 6 || !TextUtils.isEmpty(getEventsTitle(context, persianDate, true))) {
                    dayEntity.setHoliday(true);
                }

                if (getEvents(context, persianDate).size() > 0) {
                    dayEntity.setEvent(true);
                }

                dayEntity.setPersianDate(persianDate.clone());

                if (persianDate.equals(today)) {
                    dayEntity.setToday(true);
                }

                days.add(dayEntity);
                dayOfWeek++;
                if (dayOfWeek == 7) {
                    dayOfWeek = 0;
                }
            }
        } catch (DayOutOfRangeException e) {
            // okay, it was expected
        }

        return days;
    }

    // based on R.array.calendar_type order
    static public CalendarTypeEnum calendarTypeFromPosition(int position) {
        switch (position) {
            case 0:
                return CalendarTypeEnum.SHAMSI;
            case 1:
                return CalendarTypeEnum.ISLAMIC;
            default:
                return CalendarTypeEnum.GREGORIAN;
        }
    }

    @LayoutRes
    static public final int DROPDOWN_LAYOUT = R.layout.select_dialog_item;

    static public int fillYearMonthDaySpinners(Context context, Spinner calendarTypeSpinner,
                                               Spinner yearSpinner, Spinner monthSpinner,
                                               Spinner daySpinner) {
        AbstractDate date;
        PersianDate newDatePersian = getToday();
        CivilDate newDateCivil = DateConverter.persianToCivil(newDatePersian);
        IslamicDate newDateIslamic = DateConverter.persianToIslamic(newDatePersian);

        date = newDateCivil;
        switch (calendarTypeFromPosition(calendarTypeSpinner.getSelectedItemPosition())) {
            case GREGORIAN:
                date = newDateCivil;
                break;

            case ISLAMIC:
                date = newDateIslamic;
                break;

            case SHAMSI:
                date = newDatePersian;
                break;
        }

        // years spinner init.
        String[] years = new String[200];
        int startingYearOnYearSpinner = date.getYear() - years.length / 2;
        for (int i = 0; i < years.length; ++i) {
            years[i] = formatNumber(i + startingYearOnYearSpinner);
        }
        yearSpinner.setAdapter(new ShapedArrayAdapter<>(context, DROPDOWN_LAYOUT, years));
        yearSpinner.setSelection(years.length / 2);
        //

        // month spinner init.
        String[] months = monthsNamesOfCalendar(context, date).clone();
        for (int i = 0; i < months.length; ++i) {
            months[i] = months[i] + " / " + formatNumber(i + 1);
        }
        monthSpinner.setAdapter(new ShapedArrayAdapter<>(context, DROPDOWN_LAYOUT, months));
        monthSpinner.setSelection(date.getMonth() - 1);
        //

        // days spinner init.
        String[] days = new String[31];
        for (int i = 0; i < days.length; ++i) {
            days[i] = formatNumber(i + 1);
        }
        daySpinner.setAdapter(new ShapedArrayAdapter<>(context, DROPDOWN_LAYOUT, days));
        daySpinner.setSelection(date.getDayOfMonth() - 1);
        //

        return startingYearOnYearSpinner;
    }
}
