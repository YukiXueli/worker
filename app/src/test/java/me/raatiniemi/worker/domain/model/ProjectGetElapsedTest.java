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

package me.raatiniemi.worker.domain.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class ProjectGetElapsedTest {
    private String message;
    private long expected;
    private Time[] times;

    public ProjectGetElapsedTest(String message, long expected, Time... times) {
        this.message = message;
        this.expected = expected;
        this.times = times;
    }

    @Parameters
    public static Collection<Object[]> getParameters()
            throws DomainException {
        return Arrays.asList(
                new Object[][]{
                        {
                                "without items",
                                0L,
                                new Time[]{}
                        },
                        {
                                "without active item",
                                0L,
                                new Time[]{
                                        new Time.Builder(1L)
                                                .stopInMilliseconds(1L)
                                                .build()
                                }
                        },
                        {
                                // Due to the implementation of the elapsed calculation
                                // (i.e. it creates a new Date instance as reference),
                                // a mock is needed required to test the behaviour.
                                "with active item",
                                50000L,
                                new Time[]{
                                        createActiveTimeWithElapsedTimeInMilliseconds(50000L)
                                }
                        }
                }
        );
    }

    private static Time createActiveTimeWithElapsedTimeInMilliseconds(
            long elapsedTimeInMilliseconds
    ) {
        Time time = mock(Time.class);
        when(time.isActive()).thenReturn(true);
        when(time.getInterval()).thenReturn(elapsedTimeInMilliseconds);

        return time;
    }

    @Test
    public void getElapsed() throws InvalidProjectNameException {
        Project project = new Project.Builder("Project name")
                .build();
        project.addTime(Arrays.asList(times));

        assertEquals(message, expected, project.getElapsed());
    }
}
