/*
 * Copyright (c) 2016, Gayan Weerakutti <gayan@linuxdeveloper.space>
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package space.linuxdeveloper.osum.forecast;


import java.util.Calendar;


class TimeCalc {

    private static Calendar sCalendar = Calendar.getInstance();

    /**
     * Calculate the time elapsed since the beginning of the month
     * @return the number of milliseconds elapsed since the beginning of the month
     */
    public static long getTimeElapsed() {
        setCalendarToBeginning();

        return System.currentTimeMillis() - sCalendar.getTimeInMillis();
    }

    /**
     * Calculate the remaining time in milliseconds
     * @return the time remaining in milliseconds
     */
    public static long getTimeRemaining() {
        setCalendarToEnd();

        return sCalendar.getTimeInMillis() - System.currentTimeMillis();
    }

    public static int getRemainingDays() {
        long millis = getTimeRemaining();

        // Convert millis to days
        // pre calculated: 24 * 60 * 60 * 1000
        return Math.round(millis / 86400000.0f);
    }

    /**
     * @return the number of milliseconds in the month
     */
    public static long getMillisInMonth() {
        setCalendarToBeginning();
        long startOfMonth = sCalendar.getTimeInMillis();

        setCalendarToEnd();
        long endOfMonth = sCalendar.getTimeInMillis();

        return endOfMonth - startOfMonth;
    }

    private static void setCalendarToBeginning() {
        sCalendar.set(Calendar.DATE, 1);
        sCalendar.set(Calendar.HOUR_OF_DAY, 0);
        sCalendar.set(Calendar.MINUTE, 0);
        sCalendar.set(Calendar.SECOND, 0);
        sCalendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setCalendarToEnd() {
        sCalendar.set(Calendar.DATE, sCalendar.getActualMaximum(Calendar.DATE));
        sCalendar.set(Calendar.HOUR_OF_DAY, 23);
        sCalendar.set(Calendar.MINUTE, 59);
        sCalendar.set(Calendar.SECOND, 59);
        sCalendar.set(Calendar.MILLISECOND, 999);
    }
}
