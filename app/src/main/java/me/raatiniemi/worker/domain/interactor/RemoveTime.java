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

import me.raatiniemi.worker.domain.model.Time;
import me.raatiniemi.worker.domain.repository.TimeRepository;

/**
 * Use case for removing registered time.
 */
public class RemoveTime {
    /**
     * Time repository.
     */
    private final TimeRepository mTimeRepository;

    /**
     * Constructor.
     *
     * @param timeRepository Time repository.
     */
    public RemoveTime(TimeRepository timeRepository) {
        mTimeRepository = timeRepository;
    }

    /**
     * Remove registered time.
     *
     * @param time Time to remove.
     */
    public void execute(final Time time) {
        mTimeRepository.remove(time.getId());
    }
}
