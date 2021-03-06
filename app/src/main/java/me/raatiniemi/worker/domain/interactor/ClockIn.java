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

package me.raatiniemi.worker.domain.interactor;

import java.util.Date;

import me.raatiniemi.worker.domain.exception.ClockActivityException;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.TimeRepository;

/**
 * Use case for clocking in.
 */
public class ClockIn {
    private final TimeRepository timeRepository;

    public ClockIn(TimeRepository timeRepository) {
        this.timeRepository = timeRepository;
    }

    public void execute(long projectId, Date date)
            throws DomainException {
        Time time = timeRepository.getActiveTimeForProject(projectId);
        if (null != time) {
            throw new ClockActivityException("Project is active");
        }

        timeRepository.add(
                new Time.Builder(projectId)
                        .startInMilliseconds(date.getTime())
                        .build()
        );
    }
}
