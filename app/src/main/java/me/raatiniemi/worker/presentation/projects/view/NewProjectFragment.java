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

package me.raatiniemi.worker.presentation.projects.view;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.data.mapper.ProjectContentValuesMapper;
import me.raatiniemi.worker.data.mapper.ProjectCursorMapper;
import me.raatiniemi.worker.data.repository.ProjectResolverRepository;
import me.raatiniemi.worker.domain.interactor.CreateProject;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.domain.repository.ProjectRepository;
import me.raatiniemi.worker.presentation.projects.presenter.NewProjectPresenter;
import me.raatiniemi.worker.presentation.util.Keyboard;

public class NewProjectFragment extends DialogFragment implements NewProjectView, DialogInterface.OnShowListener {
    private static final String TAG = "NewProjectFragment";

    /**
     * Presenter for creating new projects.
     */
    private NewProjectPresenter presenter;

    /**
     * Text field for the project name.
     */
    private EditText projectName;

    /**
     * Callback handler for the "OnCreateProjectListener".
     */
    private OnCreateProjectListener onCreateProjectListener;

    /**
     * Retrieve the presenter instance, create if none is available.
     *
     * @return Presenter instance.
     */
    private NewProjectPresenter getPresenter() {
        if (null == presenter) {
            ProjectRepository projectRepository = new ProjectResolverRepository(
                    getActivity().getContentResolver(),
                    new ProjectCursorMapper(),
                    new ProjectContentValuesMapper()
            );

            presenter = new NewProjectPresenter(
                    getActivity(),
                    new CreateProject(projectRepository)
            );
        }
        return presenter;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // Check that we actually have a listener available, otherwise we
        // should not attempt to create new projects.
        if (null == onCreateProjectListener) {
            // The real reason for failure is to technical to display to the
            // user, hence the unknown error message.
            //
            // And, the listener should always be available in the production
            // version, i.e. this should just be seen as developer feedback.
            Snackbar.make(
                    getActivity().findViewById(android.R.id.content),
                    R.string.error_message_unknown,
                    Snackbar.LENGTH_SHORT
            ).show();

            Log.w(TAG, "No OnCreateProjectListener have been supplied");
            dismiss();

            return;
        }

        getPresenter().attachView(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        getPresenter().detachView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_new_project, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieve and set the title for the dialog.
        getDialog().setTitle(getString(R.string.fragment_new_project_title));

        // Retrieve the text field for project name.
        projectName = (EditText) view.findViewById(R.id.fragment_new_project_name);

        // Add the click listener for the create button.
        TextView create = (TextView) view.findViewById(R.id.fragment_new_project_create);
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String projectName = NewProjectFragment.this.projectName.getText().toString();
                getPresenter().createNewProject(projectName);
            }
        });

        // Add the click listener for the cancel button.
        TextView cancel = (TextView) view.findViewById(R.id.fragment_new_project_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        getDialog().setOnShowListener(this);
    }

    @Override
    public void onShow(DialogInterface dialog) {
        // We might have dismissed the dialog, we have to make sure that the
        // dialog and activity are still available before we can continue.
        if (null == dialog || null == getActivity()) {
            Log.d(TAG, "No dialog/activity available, exiting...");
            return;
        }

        // Force the keyboard to show when the dialog is showing.
        Keyboard.show(getActivity());
    }

    /**
     * @inheritDoc
     */
    @Override
    public void createProjectSuccessful(final Project project) {
        onCreateProjectListener.onCreateProject(project);

        dismiss();
    }

    /**
     * @inheritDoc
     */
    @Override
    public void showInvalidNameError() {
        projectName.setError(getString(R.string.error_message_project_name_missing));
    }

    /**
     * @inheritDoc
     */
    @Override
    public void showDuplicateNameError() {
        projectName.setError(getString(R.string.error_message_project_name_already_exists));
    }

    /**
     * @inheritDoc
     */
    @Override
    public void showUnknownError() {
        projectName.setError(getString(R.string.error_message_unknown));
    }

    public void setOnCreateProjectListener(OnCreateProjectListener onCreateProjectListener) {
        this.onCreateProjectListener = onCreateProjectListener;
    }

    /**
     * Public interface for the "OnCreateProjectListener".
     */
    public interface OnCreateProjectListener {
        /**
         * When a new project have been created the project is sent to this method.
         *
         * @param project The newly created project.
         */
        void onCreateProject(Project project);
    }
}
