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
import org.junit.runners.JUnit4;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.model.Time;

import static junit.framework.Assert.assertTrue;

@RunWith(JUnit4.class)
public class TimesheetChildModelTest {
    @Test
    public void asTime() throws ClockOutBeforeClockInException {
        Time time = new Time.Builder(1L).build();
        TimesheetChildModel model = new TimesheetChildModel(time);

        assertTrue(time == model.asTime());
    }

    @Test
    public void getId() throws ClockOutBeforeClockInException {
        Time time = new Time.Builder(1L)
                .id(2L)
                .build();
        TimesheetChildModel model = new TimesheetChildModel(time);

        assertTrue(time.getId() == model.getId());
    }
}
