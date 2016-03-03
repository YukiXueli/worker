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

import java.util.Date;

import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;

/**
 * Represent a time interval registered to a project.
 */
public class Time extends DomainObject {
    /**
     * Id for the project connected to the time interval.
     */
    private final long mProjectId;

    /**
     * Timestamp for when the time interval starts.
     * <p/>
     * UNIX timestamp, in milliseconds, representing the date and time at
     * which the interval was considered clocked in.
     */
    private final long mStart;

    /**
     * Timestamp for when the time interval ends, or zero if active.
     * <p/>
     * UNIX timestamp, in milliseconds, representing the date and time at
     * which the interval was considered clocked out.
     */
    private long mStop;

    /**
     * Flag for registered time.
     */
    private final boolean mRegistered;

    /**
     * Constructor.
     *
     * @param id         Id for the time interval.
     * @param projectId  Id for the project connected to the time interval.
     * @param start      Timestamp for when the interval starts.
     * @param stop       Timestamp for when the interval ends, or zero if active.
     * @param registered Flag for registered time.
     * @throws ClockOutBeforeClockInException If stop time is before start time, and stop is not zero.
     */
    public Time(
            final Long id,
            final long projectId,
            final long start,
            final long stop,
            boolean registered
    ) throws ClockOutBeforeClockInException {
        super(id);

        mProjectId = projectId;
        mStart = start;

        // Only set the stop time if the time is not active,
        // otherwise an exception will be thrown.
        if (stop > 0) {
            setStop(stop);
        }

        mRegistered = registered;
    }

    /**
     * Getter method for the project id.
     *
     * @return Id for the project connected to the time interval.
     */
    public long getProjectId() {
        return mProjectId;
    }

    /**
     * Getter method for timestamp when the time interval start.
     *
     * @return Timestamp for time interval start, in milliseconds.
     */
    public long getStart() {
        return mStart;
    }

    /**
     * Getter method for timestamp when the time interval ends.
     *
     * @return Timestamp for time interval end, in milliseconds, or zero if active.
     */
    public long getStop() {
        return mStop;
    }

    /**
     * Setter method for timestamp when the time interval ends.
     *
     * @param stop Timestamp for time interval end, in milliseconds.
     * @throws ClockOutBeforeClockInException If value for stop is less than value for start.
     */
    public void setStop(final long stop) throws ClockOutBeforeClockInException {
        // Check that the stop value is lager than the start value,
        // should not be able to clock out before clocked in.
        if (stop < getStart()) {
            throw new ClockOutBeforeClockInException(
                    "Clock out occur before clock in"
            );
        }

        mStop = stop;
    }

    /**
     * Getter method for registered time flag.
     *
     * @return True if time is registered, otherwise false.
     */
    public boolean isRegistered() {
        return mRegistered;
    }

    public Time markAsRegistered() throws ClockOutBeforeClockInException {
        if (isRegistered()) {
            return this;
        }

        return new Time(getId(), getProjectId(), getStart(), getStop(), true);
    }

    public Time unmarkRegistered() throws ClockOutBeforeClockInException {
        if (!isRegistered()) {
            return this;
        }

        return new Time(getId(), getProjectId(), getStart(), getStop(), false);
    }

    /**
     * Set the clock out timestamp at given date.
     *
     * @param date Date at which to clock out.
     * @throws NullPointerException           If date argument is null.
     * @throws ClockOutBeforeClockInException If clock out occur before clock in.
     */
    public Time clockOutAt(final Date date) throws ClockOutBeforeClockInException {
        if (null == date) {
            throw new NullPointerException("Date is not allowed to be null");
        }

        return new Time(getId(), getProjectId(), getStart(), date.getTime(), isRegistered());
    }

    /**
     * Check if the time interval is active.
     *
     * @return True if time interval is active, otherwise false.
     */
    public boolean isActive() {
        return 0 == getStop();
    }

    /**
     * Get the registered time.
     * <p/>
     * The time is only considered registered if the interval is not active,
     * i.e. both the start and stop values must be valid (not zero).
     *
     * @return Registered time in milliseconds, or zero if interval is active.
     */
    public long getTime() {
        long time = 0L;

        if (!isActive()) {
            time = getStop() - getStart();
        }

        return time;
    }

    /**
     * Get the time interval.
     * <p/>
     * If the interval is active, the current time will be used to calculate
     * the time between start and now.
     *
     * @return Interval in milliseconds.
     */
    public long getInterval() {
        long stop = getStop();

        if (isActive()) {
            stop = (new Date()).getTime();
        }

        return stop - getStart();
    }
}
