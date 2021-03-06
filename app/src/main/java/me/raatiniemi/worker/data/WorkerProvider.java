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

package me.raatiniemi.worker.data;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import java.util.ArrayList;

import me.raatiniemi.worker.data.WorkerContract.ProjectContract;
import me.raatiniemi.worker.data.WorkerContract.Tables;
import me.raatiniemi.worker.data.WorkerContract.TimeColumns;
import me.raatiniemi.worker.data.WorkerContract.TimeContract;
import me.raatiniemi.worker.data.util.SelectionBuilder;

public class WorkerProvider extends ContentProvider {
    private static final int PROJECTS = 100;

    private static final int PROJECTS_ID = 101;

    private static final int PROJECTS_TIME = 102;

    private static final int PROJECTS_TIMESHEET = 103;

    private static final int TIME = 200;

    private static final int TIME_ID = 201;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    WorkerDatabase openHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WorkerContract.AUTHORITY;

        matcher.addURI(authority, "projects", PROJECTS);
        matcher.addURI(authority, "projects/#", PROJECTS_ID);
        matcher.addURI(authority, "projects/#/time", PROJECTS_TIME);
        matcher.addURI(authority, "projects/#/timesheet", PROJECTS_TIMESHEET);

        matcher.addURI(authority, "time", TIME);
        matcher.addURI(authority, "time/#", TIME_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        openHelper = new WorkerDatabase(getContext());
        return true;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        String mimeType;

        final int match = uriMatcher.match(uri);
        switch (match) {
            case PROJECTS:
                mimeType = ProjectContract.STREAM_TYPE;
                break;
            case PROJECTS_ID:
                mimeType = ProjectContract.ITEM_TYPE;
                break;
            case PROJECTS_TIME:
            case PROJECTS_TIMESHEET:
            case TIME:
                mimeType = TimeContract.STREAM_TYPE;
                break;
            case TIME_ID:
                mimeType = TimeContract.ITEM_TYPE;
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return mimeType;
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Build the limit section of the query, with the offset.
        // TODO: Add proper validation and additional controls.
        // TODO: Simplify the process of retrieving offset and limit.
        String limit = null;
        if (null != uri.getQueryParameter(WorkerContract.QUERY_PARAMETER_LIMIT)) {
            limit = "";
            if (null != uri.getQueryParameter(WorkerContract.QUERY_PARAMETER_OFFSET)) {
                limit = uri.getQueryParameter(WorkerContract.QUERY_PARAMETER_OFFSET) + ",";
            }
            limit = limit + uri.getQueryParameter(WorkerContract.QUERY_PARAMETER_LIMIT);
        }

        return buildSelection(uri)
                .where(selection, selectionArgs)
                .query(openHelper.getReadableDatabase(), projection, sortOrder, limit);
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Uri createdResourceUri;

        final int match = uriMatcher.match(uri);
        switch (match) {
            case PROJECTS:
                createdResourceUri = insertProject(values);
                break;
            case TIME:
                createdResourceUri = insertTime(values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
        }

        return createdResourceUri;
    }

    private Uri insertProject(ContentValues values) {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        long id = db.insertOrThrow(Tables.PROJECT, null, values);
        return ProjectContract.getItemUri(id);
    }

    private Uri insertTime(ContentValues values) {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        long id = db.insertOrThrow(Tables.TIME, null, values);
        return TimeContract.getItemUri(id);
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return buildSelection(uri)
                .where(selection, selectionArgs)
                .update(openHelper.getWritableDatabase(), values);
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        return buildSelection(uri)
                .where(selection, selectionArgs)
                .delete(openHelper.getWritableDatabase());
    }

    @Override
    @NonNull
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {

        final SQLiteDatabase db = openHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numberOfOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numberOfOperations];
            for (int i = 0; i < numberOfOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();

            return results;
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Build the selection based on the URI.
     *
     * @param uri URI for building the selection.
     * @return Selection ready to be queried.
     */
    private static SelectionBuilder buildSelection(Uri uri) {
        SelectionBuilder builder;

        final int match = uriMatcher.match(uri);
        switch (match) {
            case PROJECTS:
                builder = ProjectsSelection.build();
                break;
            case PROJECTS_ID:
                builder = ProjectSelection.build(uri);
                break;
            case PROJECTS_TIME:
                builder = ProjectTimeSelection.build(uri);
                break;
            case PROJECTS_TIMESHEET:
                builder = ProjectTimesheetSelection.build(uri);
                break;
            case TIME_ID:
                builder = TimeSelection.build(uri);
                break;
            default:
                throw new UnsupportedOperationException(
                        "Unknown uri for selection: " + uri
                );
        }

        return builder;
    }

    private static class ProjectsSelection {
        private ProjectsSelection() {
        }

        private static SelectionBuilder build() {
            return new SelectionBuilder()
                    .table(Tables.PROJECT);
        }
    }

    private static class ProjectSelection {
        private ProjectSelection() {
        }

        private static SelectionBuilder build(Uri uri) {
            return new SelectionBuilder()
                    .table(Tables.PROJECT)
                    .where(
                            BaseColumns._ID + "=?",
                            ProjectContract.getItemId(uri)
                    );
        }
    }

    private static class ProjectTimeSelection {
        private ProjectTimeSelection() {
        }

        private static SelectionBuilder build(Uri uri) {
            return new SelectionBuilder()
                    .table(Tables.TIME)
                    .where(
                            TimeColumns.PROJECT_ID + "=?",
                            ProjectContract.getItemId(uri)
                    );
        }
    }

    private static class ProjectTimesheetSelection {
        private ProjectTimesheetSelection() {
        }

        private static SelectionBuilder build(Uri uri) {
            return new SelectionBuilder()
                    .table(Tables.TIME)
                    .where(
                            TimeColumns.PROJECT_ID + "=?",
                            ProjectContract.getItemId(uri)
                    )
                    .groupBy(ProjectContract.GROUP_BY_TIMESHEET);
        }
    }

    private static class TimeSelection {
        private TimeSelection() {
        }

        private static SelectionBuilder build(Uri uri) {
            return new SelectionBuilder()
                    .table(Tables.TIME)
                    .where(
                            BaseColumns._ID + "=?",
                            TimeContract.getItemId(uri)
                    );
        }
    }
}
