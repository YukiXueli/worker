package me.raatiniemi.worker.settings;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import de.greenrobot.event.EventBus;
import me.raatiniemi.worker.BuildConfig;
import me.raatiniemi.worker.model.backup.Backup;
import me.raatiniemi.worker.model.event.BackupSuccessfulEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class SettingsPresenterTest {
    @Test
    public void testRegisterEventBusWhenAttachView() {
        Context context = mock(Context.class);
        EventBus eventBus = mock(EventBus.class);
        SettingsView view = mock(SettingsView.class);

        SettingsPresenter presenter = new SettingsPresenter(context, eventBus);
        presenter.attachView(view);

        verify(eventBus, times(1)).register(presenter);
    }

    @Test
    public void testUnregisterEventBusWhenDetachView() {
        Context context = mock(Context.class);
        EventBus eventBus = mock(EventBus.class);

        SettingsPresenter presenter = new SettingsPresenter(context, eventBus);
        presenter.detachView();

        verify(eventBus, times(1)).unregister(presenter);
    }

    @Test
    public void testBackupSuccessfulEvent() {
        Context context = mock(Context.class);
        EventBus eventBus = mock(EventBus.class);
        SettingsView view = mock(SettingsView.class);

        Backup backup = mock(Backup.class);
        BackupSuccessfulEvent event = mock(BackupSuccessfulEvent.class);
        when(event.getBackup()).thenReturn(backup);

        SettingsPresenter presenter = new SettingsPresenter(context, eventBus);
        presenter.attachView(view);
        presenter.onEventMainThread(event);

        verify(view, times(1)).setLatestBackup(backup);
    }

    @Test
    public void testBackupSuccessfulEventWithoutView() {
        Context context = mock(Context.class);
        EventBus eventBus = mock(EventBus.class);
        SettingsView view = mock(SettingsView.class);

        Backup backup = mock(Backup.class);
        BackupSuccessfulEvent event = mock(BackupSuccessfulEvent.class);
        when(event.getBackup()).thenReturn(backup);

        SettingsPresenter presenter = new SettingsPresenter(context, eventBus);
        presenter.onEventMainThread(event);

        verify(view, never()).setLatestBackup(backup);
    }
}
