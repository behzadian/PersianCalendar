package com.cepmuvakkit.times.posAlgo

import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * @author mgeden
 */
data class EarthPosition(
    val latitude: Double, val longitude: Double,
    val timezone: Double = round(longitude / 15.0),
    val altitude: Int = 0, val temperature: Int = 10, val pressure: Int = 1010
) {

    fun toEarthHeading(target: EarthPosition): EarthHeading {
        // great circle formula from:
        // https://web.archive.org/web/20161209044600/http://williams.best.vwh.net/avform.htm
        val lat1 = Math.toRadians(latitude) //7155849931833333333e-19 0.71
        val lat2 = Math.toRadians(target.latitude) //3737913479489224943e-19 0.373
        val lon1 = Math.toRadians(-longitude) //-5055637064497558276 e-19 -0.505
        val lon2 = Math.toRadians(-target.longitude) //-69493192920839161e-17  -0.69
        val a = sin((lat1 - lat2) / 2)
        val b = sin((lon1 - lon2) / 2)
        // https://en.wikipedia.org/wiki/Haversine_formula
        val d = 2 * asin(sqrt(a * a + cos(lat1) * cos(lat2) * b * b)) //3774840207564380360e-19
        //d=2*asin(sqrt((sin((lat1-lat2)/2))^2 + cos(lat1)*cos(lat2)*(sin((lon1-lon2)/2))^2))
        // double c=a*a+Math.cos(lat1)*Math.cos(lat2))*b*b
        val tc1 = if (d > 0) {
            //tc1=acos((sin(lat2)-sin(lat1)*cos(d))/(sin(d)*cos(lat1)))
            val x = acos((sin(lat2) - sin(lat1) * cos(d)) / (sin(d) * cos(lat1)))
            /*2646123918118404228e-18*/
            if (sin(lon2 - lon1) < 0) x else 2 * PI - x
        } else 0.0
        //  tc1=2*pi-acos((sin(lat2)-sin(lat1)*cos(d))/(sin(d)*cos(lat1)))
        return EarthHeading(Math.toDegrees(tc1), (d * 6371e3).toLong())
    }

    // Ported from https://www.movable-type.co.uk/scripts/latlong.html MIT License
    fun intermediatePoints(target: EarthPosition, pointsCount: Int): List<EarthPosition> {
        val ??1 = Math.toRadians(latitude)
        val ??1 = Math.toRadians(longitude)
        val ??2 = Math.toRadians(target.latitude)
        val ??2 = Math.toRadians(target.longitude)
        // distance between points
        val ???? = ??2 - ??1
        val ???? = ??2 - ??1
        val cos??1 = cos(??1)
        val cos??2 = cos(??2)
        val cos??1 = cos(??1)
        val cos??2 = cos(??2)
        val sin??1 = sin(??1)
        val sin??2 = sin(??2)
        val sin??1 = sin(??1)
        val sin??2 = sin(??2)
        val a = sin(???? / 2) * sin(???? / 2) + cos??1 * cos??2 * sin(???? / 2) * sin(???? / 2)
        val ?? = 2 * atan2(sqrt(a), sqrt(1 - a))
        val sin?? = sin(??)
        return (0..pointsCount).map {
            val fraction = it.toDouble() / pointsCount
            val A = sin((1 - fraction) * ??) / sin??
            val B = sin(fraction * ??) / sin??
            val x = A * cos??1 * cos??1 + B * cos??2 * cos??2
            val y = A * cos??1 * sin??1 + B * cos??2 * sin??2
            val z = A * sin??1 + B * sin??2
            val ??3 = atan2(z, hypot(x, y))
            val ??3 = atan2(y, x)
            EarthPosition(Math.toDegrees(??3), Math.toDegrees(??3))
        }
    }

    /**
     * rectangular bounds of a certain point
     * @param radius is in km
     */
    fun rectangularBoundsOfRadius(radius: Double): Pair<EarthPosition, EarthPosition> {
        // https://github.com/openstreetmap/openstreetmap-website/blob/e72acac/lib/osm.rb#L452
        val lat = Math.toRadians(latitude)
        val lon = Math.toRadians(longitude)
        val latRadius = 2 * asin(sqrt(sin(radius / (R / 1000) / 2).pow(2)))
        val lonRadius = runCatching {
            2 * asin(sqrt(sin(radius / (R / 1000) / 2).pow(2) / cos(lat).pow(2)))
        }.getOrNull() ?: PI
        return EarthPosition(
            Math.toDegrees(lat - latRadius).coerceAtLeast(-90.0),
            Math.toDegrees(lon - lonRadius).coerceAtLeast(-180.0)
        ) to EarthPosition(
            Math.toDegrees(lat + latRadius).coerceAtMost(90.0),
            Math.toDegrees(lon + lonRadius).coerceAtMost(180.0)
        )
    }

    companion object {
        // https://en.wikipedia.org/wiki/Earth_radius
        const val R = 6_378_137 // Earth radius
    }
}
