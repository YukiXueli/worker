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

package me.raatiniemi.worker.presentation.projects.model;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import me.raatiniemi.worker.domain.model.Project;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Parameterized.class)
public class ProjectsModelIsActiveTest {
    private boolean expected;
    private Project project;

    public ProjectsModelIsActiveTest(boolean expected, Project project) {
        this.expected = expected;
        this.project = project;
    }

    @Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(
                new Object[][]{
                        {
                                Boolean.FALSE,
                                mockProjectWithActiveIndicator(Boolean.FALSE)
                        },
                        {
                                Boolean.TRUE,
                                mockProjectWithActiveIndicator(Boolean.TRUE)
                        }
                }
        );
    }

    private static Project mockProjectWithActiveIndicator(boolean isProjectActive) {
        Project project = mock(Project.class);
        when(project.isActive()).thenReturn(isProjectActive);

        return project;
    }

    @Test
    public void isActive() {
        ProjectsModel model = new ProjectsModel(project);

        assertTrue(expected == model.isActive());
    }
}
