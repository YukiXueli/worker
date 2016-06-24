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

package me.raatiniemi.worker.presentation.view.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;

import me.raatiniemi.worker.R;
import me.raatiniemi.worker.domain.comparator.ProjectComparator;
import me.raatiniemi.worker.domain.model.Project;
import me.raatiniemi.worker.presentation.base.view.adapter.SimpleListAdapter;
import me.raatiniemi.worker.presentation.model.ProjectsModel;
import me.raatiniemi.worker.presentation.util.HintedImageButtonListener;

/**
 * Adapter for listing available projects.
 */
public class ProjectsAdapter extends SimpleListAdapter<ProjectsModel, ProjectsAdapter.ItemViewHolder> {
    /**
     * Listener for project actions.
     */
    private final OnProjectActionListener mOnProjectActionListener;
    private final Resources mResources;

    /**
     * Listener for hinting images.
     */
    private HintedImageButtonListener mHintedImageButtonListener;

    /**
     * Construct the ProjectsAdapter.
     *
     * @param context               Context to use.
     * @param projectActionListener Listener for project actions.
     */
    public ProjectsAdapter(
            Context context,
            OnProjectActionListener projectActionListener
    ) {
        super(context);

        mOnProjectActionListener = projectActionListener;
        mResources = getContext().getResources();
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.fragment_projects_item;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(viewType, viewGroup, false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ItemViewHolder vh, int index) {
        final ProjectsModel item = get(index);
        final Project project = item.asProject();

        vh.mName.setText(item.getTitle());
        vh.mTime.setText(item.getTimeSummary());

        vh.mClockedInSince.setText(item.getClockedInSince(mResources));
        item.setVisibilityForClockedInSinceView(vh.mClockedInSince);

        vh.itemView.setOnClickListener(getOnClickListener());

        vh.mClockActivityToggle.setContentDescription(
                item.getHelpTextForClockActivityToggle(mResources)
        );
        vh.mClockActivityToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnProjectActionListener.onClockActivityToggle(project);
            }
        });
        vh.mClockActivityToggle.setOnLongClickListener(mHintedImageButtonListener);
        vh.mClockActivityToggle.setActivated(project.isActive());

        vh.mClockActivityAt.setContentDescription(
                item.getHelpTextForClockActivityAt(mResources)
        );
        vh.mClockActivityAt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnProjectActionListener.onClockActivityAt(project);
            }
        });
        vh.mClockActivityAt.setOnLongClickListener(mHintedImageButtonListener);

        vh.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOnProjectActionListener.onDelete(project);
            }
        });
        vh.mDelete.setOnLongClickListener(mHintedImageButtonListener);
    }

    public int findProject(final Project project) {
        // TODO: Clean up the comparator.
        final ProjectComparator comparator = new ProjectComparator();

        ProjectsModel item = new ProjectsModel(project);
        return Collections.binarySearch(getItems(), item, new Comparator<ProjectsModel>() {
            @Override
            public int compare(ProjectsModel lhs, ProjectsModel rhs) {
                return comparator.compare(
                        lhs.asProject(),
                        rhs.asProject()
                );
            }
        });
    }

    /**
     * Set the listener for hinting images.
     *
     * @param hintedImageButtonListener Listener for hinting images.
     */
    public void setHintedImageButtonListener(HintedImageButtonListener hintedImageButtonListener) {
        mHintedImageButtonListener = hintedImageButtonListener;
    }

    /**
     * View holder for the project item view.
     */
    class ItemViewHolder extends RecyclerView.ViewHolder {
        /**
         * Field for the project name.
         */
        private final TextView mName;

        /**
         * Field for the registered project time.
         */
        private final TextView mTime;

        /**
         * Icon for toggling the project activity.
         */
        private final ImageButton mClockActivityToggle;

        /**
         * Icon for toggling the project activity, with date and time.
         */
        private final ImageButton mClockActivityAt;

        /**
         * Icon for deleting project.
         */
        private final ImageButton mDelete;

        /**
         * Field for the time when the project was clocked in.
         */
        private final TextView mClockedInSince;

        private ItemViewHolder(View view) {
            super(view);

            mName = (TextView) view.findViewById(R.id.fragment_projects_item_name);
            mTime = (TextView) view.findViewById(R.id.fragment_projects_item_time);
            mClockActivityToggle = (ImageButton) view.findViewById(R.id.fragment_projects_item_action_clock_activity_toggle);
            mClockActivityAt = (ImageButton) view.findViewById(R.id.fragment_projects_item_action_clock_activity_at);
            mDelete = (ImageButton) view.findViewById(R.id.fragment_projects_item_action_delete);
            mClockedInSince = (TextView) view.findViewById(R.id.fragment_projects_item_clocked_in_since);
        }
    }

    /**
     * Interface for project actions.
     */
    public interface OnProjectActionListener {
        /**
         * Toggle the clock activity change.
         *
         * @param project Project to change the clock activity.
         */
        void onClockActivityToggle(@NonNull Project project);

        /**
         * Toggle the clock activity change, with date and time.
         *
         * @param project Project to change the clock activity.
         */
        void onClockActivityAt(@NonNull Project project);

        /**
         * Handle project delete action from user.
         *
         * @param project Project to delete.
         */
        void onDelete(@NonNull Project project);
    }
}
