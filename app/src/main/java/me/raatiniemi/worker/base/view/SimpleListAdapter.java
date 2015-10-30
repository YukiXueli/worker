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

package me.raatiniemi.worker.base.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Base adapter for working with the RecyclerView.
 *
 * @param <T> Reference type for a single item within the list collection.
 * @param <V> Reference type for the view holder.
 */
abstract public class SimpleListAdapter<T, V extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<V>
        implements ListAdapter<T> {
    /**
     * Tag for logging.
     */
    private static final String TAG = "SimpleListAdapter";

    /**
     * Context used with the adapter.
     */
    private final Context mContext;

    /**
     * Items for the adapter to display.
     */
    private List<T> mItems;

    /**
     * On click listener for list items.
     */
    private OnItemClickListener mOnItemClickListener;

    /**
     * On click listener for views within the SimpleListAdapter.
     */
    private final OnClickListener mOnClickListener = new OnClickListener();

    /**
     * Construct the SimpleListAdapter.
     *
     * @param context Context used with the adapter.
     */
    public SimpleListAdapter(Context context) {
        mContext = context;
    }

    /**
     * Get the adapter context.
     *
     * @return Adapter context.
     */
    protected Context getContext() {
        return mContext;
    }

    /**
     * @inheritDoc
     */
    @Override
    public List<T> getItems() {
        if (null == mItems) {
            mItems = new ArrayList<>();
        }
        return mItems;
    }

    /**
     * @inheritDoc
     */
    @Override
    public void setItems(List<T> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    /**
     * Get the number of items within the adapter.
     *
     * @return Number of items within the adapter.
     */
    @Override
    public int getItemCount() {
        return getItems().size();
    }

    /**
     * Get item from the adapter.
     *
     * @param index Index of the item to get.
     * @return Item at the index.
     */
    @Override
    public T get(int index) {
        return getItems().get(index);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void set(int index, T item) {
        getItems().set(index, item);

        // Notify the adapter that data item have changed.
        notifyItemChanged(index);
    }

    /**
     * Add an item to the adapter.
     *
     * @param item Item to add to the adapter.
     * @return Index of the new item within the adapter.
     */
    public int add(T item) {
        // Retrieve the index for the new item by retrieving the number of
        // items within the adapter before adding the new item.
        int index = getItemCount();
        getItems().add(item);

        // Notify the adapter a new item have been added.
        notifyItemInserted(index);

        // Return the index for the new item.
        return index;
    }

    /**
     * Add collection of items to the adapter.
     *
     * @param items Items to add to the adapter.
     * @return Index at which the new items are being inserted.
     */
    public int add(List<T> items) {
        // Retrieve the current count to have a reference point
        // at which location the new items will be inserted.
        int index = getItemCount();
        getItems().addAll(items);

        // Notify and refresh the new items.
        notifyItemRangeInserted(index, items.size());

        // Return the reference point for the location of the new items.
        return index;
    }

    /**
     * Remove an item from the adapter.
     *
     * @param index Index of the item to remove.
     * @return Item removed from the adapter.
     */
    public T remove(int index) {
        // Remove the item from the internal data container.
        T item = getItems().remove(index);

        // Notify the adapter of the deletion.
        notifyItemRemoved(index);

        // Return the removed item.
        return item;
    }

    /**
     * Retrieve the click listener for list items.
     *
     * @return Click listener for list items, or null if none has been supplied.
     */
    public OnItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    /**
     * Set the click listener for list items.
     *
     * @param onItemClickListener Click listener for list items.
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    /**
     * Retrieve the click listener for list item views.
     *
     * @return Click listener for list item views.
     */
    protected OnClickListener getOnClickListener() {
        return mOnClickListener;
    }

    /**
     * Click listener interface for list items.
     */
    public interface OnItemClickListener {
        /**
         * Handles list item click events.
         *
         * @param view View that has been clicked.
         */
        void onItemClick(View view);
    }

    /**
     * On click listener for list items.
     */
    protected class OnClickListener implements View.OnClickListener {
        /**
         * Handles click events to the list item.
         *
         * @param view List item that have been clicked.
         */
        @Override
        public void onClick(View view) {
            // Check that the OnItemClickListener have been supplied.
            if (null == getOnItemClickListener()) {
                Log.e(TAG, "No OnItemClickListener have been supplied");
                return;
            }

            // Relay the event with the item view to the OnItemClickListener.
            getOnItemClickListener().onItemClick(view);
        }
    }
}
