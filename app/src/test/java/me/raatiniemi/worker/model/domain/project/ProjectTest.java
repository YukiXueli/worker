/*
 * Copyright (C) 2015 Worker Project
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

package me.raatiniemi.worker.model.domain.project;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.exception.DomainException;
import me.raatiniemi.worker.exception.domain.ClockActivityException;
import me.raatiniemi.worker.model.domain.time.Time;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ProjectTest {
    @Test
    public void Project_DefaultValueFromDefaultConstructor_True() {
        Project project = new Project();

        assertNull(project.getId());
        assertNull(project.getName());

        assertFalse(project.isArchived());
        assertEquals(Long.valueOf(0L), project.getArchived());
        assertTrue(project.getTime().isEmpty());
    }

    @Test
    public void Project_DefaultValueFromIdNameConstructor_True() {
        Project project = new Project(2L, "Project name");

        assertEquals(Long.valueOf(2L), project.getId());
        assertEquals("Project name", project.getName());

        assertFalse(project.isArchived());
        assertEquals(Long.valueOf(0L), project.getArchived());
        assertTrue(project.getTime().isEmpty());
    }

    @Test
    public void Project_DefaultValueFromNameConstructor_True() {
        Project project = new Project("Project name");

        assertNull(project.getId());
        assertEquals("Project name", project.getName());

        assertFalse(project.isArchived());
        assertEquals(Long.valueOf(0L), project.getArchived());
        assertTrue(project.getTime().isEmpty());
    }

    @Test
    public void getName_DefaultValue_Null() {
        Project project = new Project();

        assertNull(project.getName());
    }

    @Test
    public void getName_ValueFromConstructor_True() {
        Project project = new Project("Project name");

        assertEquals("Project name", project.getName());

        project = new Project(1L, "New project name");

        assertEquals("New project name", project.getName());
    }

    @Test
    public void getName_ValueFromSetter_True() {
        Project project = new Project("Project name");
        project.setName("New project name");

        assertEquals("New project name", project.getName());
    }

    @Test
    public void getDescription_DefaultValue_Null() {
        Project project = new Project();

        assertNull(project.getDescription());
    }

    @Test
    public void getDescription_ValueFromSetter_True() {
        Project project = new Project();
        project.setDescription("Project description");

        assertEquals("Project description", project.getDescription());
    }

    @Test
    public void setDescription_WithEmptyString_Null() {
        Project project = new Project();
        project.setDescription("");

        assertNull(project.getDescription());
    }

    @Test
    public void setDescription_WithNull_Null() {
        Project project = new Project();
        project.setDescription(null);

        assertNull(project.getDescription());
    }

    @Test
    public void isArchived_DefaultValue_False() {
        Project project = new Project();

        assertFalse(project.isArchived());
    }

    @Test
    public void isArchived_ValueFromSetter_True() {
        Project project = new Project();
        project.setArchived(true);

        assertTrue(project.isArchived());

        project.setArchived(false);

        assertFalse(project.isArchived());
    }

    @Test
    public void getArchived_DefaultValue_False() {
        Project project = new Project();

        assertEquals(Long.valueOf(0L), project.getArchived());
    }

    @Test
    public void getArchived_ValueFromSetter_True() {
        Project project = new Project();
        project.setArchived(1L);

        assertEquals(Long.valueOf(1L), project.getArchived());

        project.setArchived(0L);

        assertEquals(Long.valueOf(0L), project.getArchived());
    }

    @Test
    public void getTime_WithoutTime_True() {
        Project project = new Project();

        assertNotNull(project.getTime());
        assertEquals(0, project.getTime().size());
    }

    @Test
    public void getTime_ValueFromAddTimeList_True() {
        Project project = new Project();

        List<Time> times = project.getTime();
        assertEquals(0, times.size());

        Time time = mock(Time.class);
        project.addTime(time);

        times = project.getTime();
        assertEquals(1, times.size());
        assertEquals(time, times.get(0));
    }

    @Test
    public void getTime_ValuesFromAddTimeList_True() {
        Project project = new Project();

        List<Time> times = new ArrayList<>();
        Time time1 = mock(Time.class);
        times.add(time1);
        project.addTime(times);

        times = new ArrayList<>();
        Time time2 = mock(Time.class);
        times.add(time2);
        project.addTime(times);

        times = project.getTime();
        assertEquals(2, times.size());
        assertEquals(time1, times.get(0));
        assertEquals(time2, times.get(1));
    }

    @Test
    public void addTime_WithEmptyTimeList_True() {
        Project project = new Project();

        List<Time> times = new ArrayList<>();
        project.addTime(times);

        assertTrue(project.getTime().isEmpty());
    }

    @Test
    public void summarizeTime_WithoutTime_True() {
        Project project = new Project();

        assertEquals(Long.valueOf(0L), Long.valueOf(project.summarizeTime()));
    }

    @Test
    public void summarizeTime_WithActiveTime_True() {
        Project project = new Project();

        Time time1 = mock(Time.class);
        when(time1.getTime()).thenReturn(60000L);

        Time time2 = mock(Time.class);
        when(time2.getTime()).thenReturn(0L);

        project.addTime(time1);
        project.addTime(time2);

        assertEquals(Long.valueOf(60000L), Long.valueOf(project.summarizeTime()));
    }

    @Test
    public void summarizeTime_WithSingleItem_True() {
        Project project = new Project();

        Time time = mock(Time.class);
        when(time.getTime()).thenReturn(60000L);

        project.addTime(time);

        assertEquals(Long.valueOf(60000L), Long.valueOf(project.summarizeTime()));
    }

    @Test
    public void summarizeTime_WithValueRoundUp_True() {
        Project project = new Project();

        Time time1 = mock(Time.class);
        when(time1.getTime()).thenReturn(60000L);

        Time time2 = mock(Time.class);
        when(time2.getTime()).thenReturn(30000L);

        project.addTime(time1);
        project.addTime(time2);

        assertEquals(Long.valueOf(90000L), Long.valueOf(project.summarizeTime()));
    }

    @Test
    public void summarizeTime_WithValueRoundDown_True() {
        Project project = new Project();

        Time time1 = mock(Time.class);
        when(time1.getTime()).thenReturn(60000L);

        Time time2 = mock(Time.class);
        when(time2.getTime()).thenReturn(29000L);

        project.addTime(time1);
        project.addTime(time2);

        assertEquals(Long.valueOf(89000L), Long.valueOf(project.summarizeTime()));
    }

    @Test
    public void summarizeTime_MultipleItems_True() {
        Project project = new Project();

        Time time1 = mock(Time.class);
        when(time1.getTime()).thenReturn(3600000L);

        Time time2 = mock(Time.class);
        when(time2.getTime()).thenReturn(1800000L);

        project.addTime(time1);
        project.addTime(time2);

        assertEquals(Long.valueOf(5400000L), Long.valueOf(project.summarizeTime()));
    }

    @Test
    public void getElapsed_WithoutTime_True() {
        Project project = new Project();

        assertEquals(Long.valueOf(0L), Long.valueOf(project.getElapsed()));
    }

    @Test
    public void getElapsed_WithoutActiveTime_True() {
        Project project = new Project();

        Time time = mock(Time.class);
        when(time.isActive()).thenReturn(false);

        project.addTime(time);

        assertEquals(Long.valueOf(0L), Long.valueOf(project.getElapsed()));
        verify(time, times(1)).isActive();
    }

    @Test
    public void getElapsed_WithActiveTime_True() {
        Project project = new Project();

        Time time = mock(Time.class);
        when(time.isActive()).thenReturn(true);
        when(time.getInterval()).thenReturn(50000L);

        project.addTime(time);

        assertEquals(Long.valueOf(50000L), Long.valueOf(project.getElapsed()));
        verify(time, times(1)).isActive();
        verify(time, times(1)).getInterval();
    }

    @Test
    public void getClockedInSince_WithoutTime_Null() {
        Project project = new Project();

        assertNull(project.getClockedInSince());
    }

    @Test
    public void getClockedInSince_WithoutActiveTime_Null() {
        Project project = new Project();

        Time time = mock(Time.class);
        when(time.isActive()).thenReturn(false);

        project.addTime(time);

        assertNull(project.getClockedInSince());
        verify(time, times(1)).isActive();
    }

    @Test
    public void getClockedInSince_WithActiveTime_True() {
        Project project = new Project();

        Time time = mock(Time.class);
        when(time.isActive()).thenReturn(true);
        when(time.getStart()).thenReturn(500000L);

        project.addTime(time);

        Date date = project.getClockedInSince();

        assertNotNull(date);
        assertEquals(Long.valueOf(500000L), Long.valueOf(date.getTime()));
        verify(time, times(1)).isActive();
        verify(time, times(1)).getStart();
    }

    @Test(expected = ClockActivityException.class)
    public void clockInAt_WithActiveTime_ThrowException() throws DomainException {
        Project project = new Project();

        // Setup the time object to return true for the `isActive`-call.
        Time time = mock(Time.class);
        when(time.isActive()).thenReturn(true);

        project.addTime(time);

        Date date = mock(Date.class);
        project.clockInAt(date);

        verify(time, times(1)).isActive();
    }

    @Test
    public void clockInAt_WithoutActiveTime_True() throws DomainException {
        Project project = new Project();
        project.setId(1L);

        Date date = mock(Date.class);
        when(date.getTime()).thenReturn(100L);

        Time time = project.clockInAt(date);

        assertNotNull(time);
        assertNull(time.getId());
        assertEquals(Long.valueOf(1L), time.getProjectId());
        assertEquals(Long.valueOf(100L), time.getStart());
        assertEquals(Long.valueOf(0L), time.getStop());
        verify(date, times(1)).getTime();
    }

    @Test(expected = ClockActivityException.class)
    public void clockOutAt_WithoutTime_ThrowException() throws DomainException {
        Project project = new Project();

        Date date = mock(Date.class);
        project.clockOutAt(date);
    }

    @Test(expected = ClockActivityException.class)
    public void clockOutAt_WithoutActiveTime_ThrowException() throws DomainException {
        Project project = new Project();

        // Setup the time object to return true for the `isActive`-call.
        Time time = mock(Time.class);
        when(time.isActive()).thenReturn(false);

        project.addTime(time);

        Date date = mock(Date.class);
        project.clockOutAt(date);

        verify(time, times(1)).isActive();
    }

    @Test
    public void clockOutAt_WithActiveTime_True() throws DomainException {
        Project project = new Project();

        Time time = mock(Time.class);
        when(time.isActive()).thenReturn(true);

        project.addTime(time);

        Date date = mock(Date.class);
        assertEquals(time, project.clockOutAt(date));
        verify(time, times(1)).clockOutAt(date);
    }

    @Test
    public void isActive_WithoutTime_False() {
        Project project = new Project();

        assertFalse(project.isActive());
    }

    @Test
    public void isActive_WithoutActiveTime_False() {
        Project project = new Project();

        // Setup the time object to return false for the `isActive`-call.
        Time time = mock(Time.class);
        when(time.isActive()).thenReturn(false);

        project.addTime(time);

        assertFalse(project.isActive());
        verify(time, times(1)).isActive();
    }

    @Test
    public void isActive_WithActiveTime_True() {
        Project project = new Project();

        // Setup the time object to return true for the `isActive`-call.
        Time time = mock(Time.class);
        when(time.isActive()).thenReturn(true);

        project.addTime(time);

        assertTrue(project.isActive());
        verify(time, times(1)).isActive();
    }
}
