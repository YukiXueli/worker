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

package me.raatiniemi.worker.presentation.presenter;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import org.greenrobot.eventbus.EventBus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContextImpl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.RxSchedulerRule;
import me.raatiniemi.worker.Worker;
import me.raatiniemi.worker.domain.exception.ClockOutBeforeClockInException;
import me.raatiniemi.worker.domain.exception.DomainException;
import me.raatiniemi.worker.domain.interactor.ClockActivityChange;
import me.raatiniemi.worker.domain.interactor.GetProjects;
import me.raatiniemi.worker.domain.interactor.RemoveProject;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.model.OngoingNotificationActionEvent;
import me.raatiniemi.worker.presentation.model.ProjectsModel;
import me.raatiniemi.worker.presentation.view.ProjectsView;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ProjectsPresenterTest {
    @Rule
    public final RxSchedulerRule mRxSchedulersRule = new RxSchedulerRule();

    private Context mContext = RuntimeEnvironment.application.getBaseContext();
    private EventBus mEventBus;
    private GetProjects mGetProjects;
    private ClockActivityChange mClockActivityChange;
    private RemoveProject mRemoveProject;
    private ProjectsPresenter mPresenter;
    private ProjectsView mView;
    private NotificationManager mNotificationManager;

    @Before
    public void setUp() {
        mEventBus = mock(EventBus.class);
        mGetProjects = mock(GetProjects.class);
        mClockActivityChange = mock(ClockActivityChange.class);
        mRemoveProject = mock(RemoveProject.class);
        mPresenter = new ProjectsPresenter(
                mContext,
                mEventBus,
                mGetProjects,
                mClockActivityChange,
                mRemoveProject
        );
        mView = mock(ProjectsView.class);

        setupNotificationManager();
    }

    private void setupNotificationManager() {
        mNotificationManager = mock(NotificationManager.class);
        ShadowContextImpl shadowContext = (ShadowContextImpl) Shadows.shadowOf(mContext);
        shadowContext.setSystemService(Context.NOTIFICATION_SERVICE, mNotificationManager);
    }

    @Test
    public void attachView_registerEventBus() {
        mPresenter.attachView(mView);

        verify(mEventBus).register(mPresenter);
    }

    @Test
    public void detachView_unregisterEventBus() {
        mPresenter.detachView();

        verify(mEventBus).unregister(mPresenter);
    }

    @Test
    public void getProjects() throws DomainException {
        List<Project> projects = new ArrayList<>();
        projects.add(
                new Project.Builder("Name")
                        .build()
        );
        when(mGetProjects.execute()).thenReturn(projects);
        mPresenter.attachView(mView);

        mPresenter.getProjects();

        verify(mView).addProjects(anyListOf(ProjectsModel.class));
    }

    @Test
    public void getProjects_withoutAttachedView() throws DomainException {
        List<Project> projects = new ArrayList<>();
        when(mGetProjects.execute()).thenReturn(projects);

        mPresenter.getProjects();

        verify(mView, never()).addProjects(anyListOf(ProjectsModel.class));
    }

    @Test
    public void getProjects_withError() throws DomainException {
        when(mGetProjects.execute()).thenThrow(new RuntimeException());
        mPresenter.attachView(mView);

        mPresenter.getProjects();

        verify(mView).showGetProjectsErrorMessage();
    }

    @Test
    public void getProjects_withErrorAndWithoutAttachedView() throws DomainException {
        when(mGetProjects.execute()).thenThrow(new ClockOutBeforeClockInException());

        mPresenter.getProjects();

        verify(mView, never()).showGetProjectsErrorMessage();
    }

    @Test
    public void deleteProject() throws DomainException {
        Project project = new Project.Builder("Name")
                .build();
        List<Project> projects = new ArrayList<>();
        projects.add(project);
        when(mView.getProjects()).thenReturn(projects);
        mPresenter.attachView(mView);

        mPresenter.deleteProject(project);

        verify(mView).deleteProjectAtPosition(0);
        verify(mView).showDeleteProjectSuccessMessage();
    }

    @Test
    public void deleteProject_withError() throws DomainException {
        Project project = new Project.Builder("Name")
                .build();
        List<Project> projects = new ArrayList<>();
        projects.add(project);
        when(mView.getProjects()).thenReturn(projects);
        doThrow(new RuntimeException()).when(mRemoveProject).execute(project);
        mPresenter.attachView(mView);

        mPresenter.deleteProject(project);

        verify(mView).deleteProjectAtPosition(0);
        verify(mView).restoreProjectAtPreviousPosition(0, project);
        verify(mView).showDeleteProjectErrorMessage();
    }

    @Test
    public void clockActivityChange_clockOut() throws DomainException {
        Project project = new Project.Builder("Name")
                .id(1L)
                .build();
        when(mClockActivityChange.execute(eq(project), any(Date.class)))
                .thenReturn(project);
        mPresenter.attachView(mView);

        mPresenter.clockActivityChange(project, new Date());

        verify(mNotificationManager)
                .cancel("1", Worker.NOTIFICATION_ON_GOING_ID);
        verify(mNotificationManager, never()).notify(
                eq("1"),
                eq(Worker.NOTIFICATION_ON_GOING_ID),
                isA(Notification.class)
        );
        verify(mView).updateProject(project);
    }

    @Test
    public void clockActivityChange_withoutAttachedView() throws DomainException {
        Project project = new Project.Builder("Name")
                .id(1L)
                .build();
        when(mClockActivityChange.execute(eq(project), any(Date.class)))
                .thenReturn(project);

        mPresenter.clockActivityChange(project, new Date());

        verify(mNotificationManager).cancel("1", Worker.NOTIFICATION_ON_GOING_ID);
        verify(mView, never()).updateProject(project);
    }

    @Test
    public void clockActivityChange_withClockInError() throws DomainException {
        Project project = new Project.Builder("Name")
                .build();
        when(mClockActivityChange.execute(eq(project), any(Date.class)))
                .thenThrow(new ClockOutBeforeClockInException());
        mPresenter.attachView(mView);

        mPresenter.clockActivityChange(project, new Date());

        verify(mView, never()).showClockOutErrorMessage();
        verify(mView).showClockInErrorMessage();
    }

    @Test
    public void clockActivityChange_withClockOutError() throws DomainException {
        Project project = mock(Project.class);
        when(project.isActive()).thenReturn(true);
        when(mClockActivityChange.execute(eq(project), any(Date.class)))
                .thenThrow(new ClockOutBeforeClockInException());
        mPresenter.attachView(mView);

        mPresenter.clockActivityChange(project, new Date());

        verify(mView).showClockOutErrorMessage();
        verify(mView, never()).showClockInErrorMessage();
    }

    @Test
    public void clockActivityChange_withErrorAndWithoutAttachedView() throws DomainException {
        Project project = mock(Project.class);
        when(project.isActive()).thenReturn(true);
        when(mClockActivityChange.execute(eq(project), any(Date.class)))
                .thenThrow(new ClockOutBeforeClockInException());

        mPresenter.clockActivityChange(project, new Date());

        verify(mView, never()).showClockOutErrorMessage();
        verify(mView, never()).showClockInErrorMessage();
    }

    @Test
    public void onEventMainThread_ongoingNotification() {
        mPresenter.attachView(mView);

        mPresenter.onEventMainThread(new OngoingNotificationActionEvent(1));

        verify(mView).reloadProjects();
    }

    @Test
    public void onEventMainThread_ongoingNotificationWithoutAttachedView() {
        mPresenter.onEventMainThread(new OngoingNotificationActionEvent(1));

        verify(mView, never()).reloadProjects();
    }
}
