/*
 * Copyright (C) 2016 Worker Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 2 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.raatiniemi.worker.presentation.project.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.model.Time;

import static junit.framework.Assert.assertEquals;

@RunWith(Parameterized.class)
public class TimesheetChildModelGetTitleTest {
    private String message;
    private String expected;
    private Time time;

    public TimesheetChildModelGetTitleTest(
            String message,
            String expected,
            Time time
    ) {
        this.message = message;
        this.expected = expected;
        this.time = time;
    }

    @Parameters
    public static Collection<Object[]> getParameters()
            throws ClockOutBeforeClockInException {
        return Arrays.asList(
                new Object[][]{
                        {
                                "active time",
                                "08:00",
                                buildTime(
                                        new GregorianCalendar(2016, 1, 28, 8, 0),
                                        null
                                )
                        },
                        {
                                "inactive time",
                                "08:00 - 11:30",
                                buildTime(
                                        new GregorianCalendar(2016, 1, 28, 8, 0),
                                        new GregorianCalendar(2016, 1, 28, 11, 30)
                                )
                        }
                }
        );
    }

    private static Time buildTime(Calendar start, Calendar stop)
            throws ClockOutBeforeClockInException {
        Time.Builder builder = new Time.Builder(1L)
                .startInMilliseconds(start.getTimeInMillis());

        if (null != stop) {
            builder.stopInMilliseconds(stop.getTimeInMillis());
        }

        return builder.build();
    }

    @Test
    public void getTitle() {
        TimesheetChildModel item = new TimesheetChildModel(time);

        assertEquals(message, expected, item.getTitle());
    }
}
