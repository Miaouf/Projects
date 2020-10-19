package com.example.quickmatch.utils

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*

/**
 * Hashing Utils
 * @author Sam Clarke <www.samclarke.com>
 * @license MIT
 */

object HashUtils {

    fun sha512(input: String) = hashString("SHA-512", input)

    private fun hashString(type: String, input: String): String {
        val HEX_CHARS = "0123456789abcdef"
        val bytes = MessageDigest
                .getInstance(type)
                .digest(input.toByteArray())

        /* Convert hash into string value */

        val result = StringBuilder(bytes.size * 2)

        bytes.forEach {
            val i = it.toInt()
            result.append(HEX_CHARS[i shr 4 and 0x0f])
            result.append(HEX_CHARS[i and 0x0f])
        }

        return result.toString()
    }

}

object FormatUtils {

    /* team sizes */
    const val MINIMUM_TEAM_SIZE = 1
    const val MAXIMUM_TEAM_SIZE = 11

    /* repetitions */
    const val MINIMUM_REPETITIONS = 0
    const val MAXIMUM_REPETITIONS = 4

    /* sizes to respect to store in database */
    const val MAIL_SIZE = 50
    const val BASIC_SIZE = 20
    const val MIN_PASSWORD_SIZE = 8
    const val BIO_SIZE = 80
    const val AVATAR_SIZE = 60
    const val CLUB_NAME_SIZE = 40

    /* timer settings */
    const val MINIMUM_SECONDS = 0
    const val MAXIMUM_SECONDS = 59
    const val MINIMUM_MINUTES = 0
    const val MAXIMUM_MINUTES = 60

    /* Regexs */
    val mailPattern = Regex("[a-zA-Z0-9._-]+@[a-z0-9-]+\\.[a-z]+")
    val passwordPattern = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{$MIN_PASSWORD_SIZE,$BASIC_SIZE}$")
    val phoneNumberPattern = Regex("^(?:(?:\\+|00)33|0)\\s*[1-9](?:[\\s.-]*\\d{2}){4}\$")
    val emptyFieldPattern = Regex("\\s*")

    /* Database date format */
    const val DATE_FORMAT = "yyyy-MM-dd"

    /* Parse phone number to keek the difits only */
    fun parsePhoneNumber(phoneNumber: String) : String? {
        if(phoneNumber != "") {
            var parsedPhoneNumber = phoneNumber.replace("+33", "0")
            Timber.i(phoneNumber)
            return parsedPhoneNumber.filterNot { it in ".- " }
        }
        return null
    }

    /* parse a date string from database */
    fun parseDbDateToJJMMAAAA(date: String?) : String {
        date?.let {
            val splitDate = date.split("-", "T")
            val day = splitDate[2]
            val month = splitDate[1]
            val year = splitDate[0]
            return "$day/$month/$year"
        }
        return "(non déterminé)"
    }
    /* parse a date string from database */
    fun parseDbTimeToHHMM(time: String?) : String {
        time?.let {
            val splitTime = time.split(":")
            val hour = splitTime[0]
            val minute = splitTime[1]
            return "$hour:$minute"
        }
        return "(non déterminé)"
    }

    /* get the string for a day of the week given as integer */
    /* week starts at Sunday (1) and ends at Saturday (7) */
    fun toStringDay(day: Int?) : String {
        day?.let {
            return when(day) {
                Calendar.SUNDAY     -> "Sunday"
                Calendar.MONDAY     -> "Monday"
                Calendar.TUESDAY    -> "Tuesday"
                Calendar.WEDNESDAY  -> "Wednesday"
                Calendar.THURSDAY   -> "Thursday"
                Calendar.FRIDAY     -> "Friday"
                Calendar.SATURDAY   -> "Saturday"
                else                -> "Wrong day"
            }
        }
        return ""
    }
}



