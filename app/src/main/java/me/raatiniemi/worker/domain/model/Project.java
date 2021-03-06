/*
 * Copyright (C) 2015-2016 Worker Project
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import me.raatiniemi.worker.domain.exception.ClockActivityException;
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.exception.InvalidProjectNameException;

/**
 * Represent a project.
 */
public class Project extends DomainObject {
    /**
     * Name for the project.
     */
    private String name;

    /**
     * Time registered for the project.
     */
    private final List<Time> time;

    /**
     * Constructor.
     *
     * @param id   Id for the project.
     * @param name Name of the project.
     * @throws InvalidProjectNameException If project name is null or empty.
     */
    private Project(final Long id, final String name)
            throws InvalidProjectNameException {
        super(id);

        setName(name);

        // Set default value for non-constructor arguments.
        time = new ArrayList<>();
    }

    /**
     * Getter method for the project name.
     *
     * @return Project name.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method for the project name.
     *
     * @param name Project name.
     * @throws InvalidProjectNameException If project name is null or empty.
     */
    private void setName(final String name) throws InvalidProjectNameException {
        if (null == name || 0 == name.length()) {
            throw new InvalidProjectNameException();
        }

        this.name = name;
    }

    /**
     * Getter method for the project time.
     *
     * @return Project time.
     */
    public List<Time> getTime() {
        return time;
    }

    /**
     * Add time for the project from a list.
     *
     * @param time Time to add to the project.
     */
    public void addTime(final List<Time> time) {
        if (null == time) {
            throw new NullPointerException("Time is not allowed to be null");
        }

        // If the list with items are empty, there's no
        // need to attempt to add them.
        if (time.isEmpty()) {
            return;
        }

        getTime().addAll(time);
    }

    /**
     * Retrieve the elapsed time for an active project.
     *
     * @return Elapsed time in milliseconds, zero if project is not active.
     */
    public long getElapsed() {
        long elapsed = 0;

        // Retrieve the interval for the active time.
        Time time = getActiveTime();
        if (null != time) {
            elapsed = time.getInterval();
        }

        return elapsed;
    }

    /**
     * Retrieve the active time, if available.
     *
     * @return Active time, or null if project is not active.
     */
    private Time getActiveTime() {
        // If no time is registered, the project can't be active.
        List<Time> list = getTime();
        if (list.isEmpty()) {
            return null;
        }

        // If the first item is not active, the project is not active.
        Time time = list.get(0);
        if (!time.isActive()) {
            return null;
        }

        return time;
    }

    /**
     * Retrieve the date when the project was clocked in.
     *
     * @return Date when project was clocked in, or null if project is not active.
     */
    public Date getClockedInSince() {
        // Retrieve the last time, i.e. the active time session.
        Time time = getActiveTime();
        if (null == time) {
            return null;
        }

        // TODO: Do not instantiate inside method, return value from get start?
        return new Date(time.getStartInMilliseconds());
    }

    /**
     * Clock in project at a given date and time.
     *
     * @param date Date and time for when to clock in the project.
     * @return The clocked in Time.
     * @throws ClockActivityException         If the project is active.
     * @throws ClockOutBeforeClockInException If clock in occur after clock out.
     */
    public Time clockInAt(final Date date)
            throws ClockActivityException, ClockOutBeforeClockInException {
        if (null == date) {
            throw new NullPointerException("Date is not allowed to be null");
        }

        // If the project is already active, we can't clock in.
        if (isActive()) {
            throw new ClockActivityException("Unable to clock in, project is already active");
        }

        // Instantiate the Time domain object with the project
        // and clock in with the supplied date.
        return new Time.Builder(getId())
                .startInMilliseconds(date.getTime())
                .build();
    }

    /**
     * Clock out project at a given date and time.
     *
     * @param date Date and time for when to clock out the project.
     * @return The clocked out Time.
     * @throws ClockActivityException         If the project is not active.
     * @throws ClockOutBeforeClockInException If clock out occur before clock in.
     */
    public Time clockOutAt(final Date date)
            throws ClockActivityException, ClockOutBeforeClockInException {
        if (null == date) {
            throw new NullPointerException("Date is not allowed to be null");
        }

        // Retrieve the active Time domain object, and clock
        // out with the supplied date.
        //
        // If none is available, i.e. we have not clocked in,
        // we can't clock out.
        Time time = getActiveTime();
        if (null == time) {
            throw new ClockActivityException("Unable to clock out, project is not active");
        }

        time = time.clockOutAt(date);
        this.time.set(0, time);

        return time;
    }

    /**
     * Check if the project is active.
     *
     * @return True if the project is active, otherwise false.
     */
    public boolean isActive() {
        return null != getActiveTime();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Project)) {
            return false;
        }

        Project project = (Project) o;
        return Objects.equals(getId(), project.getId())
                && getName().equals(project.getName())
                && getTime().equals(project.getTime());
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + Objects.hashCode(getId());
        result = 31 * result + getName().hashCode();
        result = 31 * result + getTime().hashCode();
        return result;
    }

    public static class Builder {
        private final String projectName;
        private Long id;

        public Builder(String projectName) {
            this.projectName = projectName;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Project build() throws InvalidProjectNameException {
            return new Project(id, projectName);
        }
    }
}
