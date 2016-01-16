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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import me.raatiniemi.worker.data.WorkerContract.ProjectColumns;
import me.raatiniemi.worker.data.WorkerContract.Tables;
import me.raatiniemi.worker.data.WorkerContract.TimeColumns;
import me.raatiniemi.worker.util.Worker;

/**
 * A helper class to manage database creation and version management.
 */
public class WorkerDatabase extends SQLiteOpenHelper {
    /**
     * Constructor.
     *
     * @param context Context used with the database.
     */
    public WorkerDatabase(Context context) {
        super(context, Worker.DATABASE_NAME, null, Worker.DATABASE_VERSION);
    }

    /**
     * Check if column exists in table.
     *
     * @param db     The database.
     * @param table  Name of the table.
     * @param column Name of the column.
     * @return true if column exists, otherwise false.
     */
    private boolean columnExists(SQLiteDatabase db, String table, String column) {
        boolean exists = false;

        // get the table structure and check if the column exists.
        Cursor cursor = db.rawQuery("pragma table_info(" + table + ")", null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex("name"));
            if (name.equalsIgnoreCase(column)) {
                exists = true;
                break;
            }
        }
        cursor.close();

        return exists;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the structure for the `project`-table.
        db.execSQL("CREATE TABLE " + Tables.PROJECT + " ( " +
                ProjectColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ProjectColumns.NAME + " TEXT NOT NULL, " +
                ProjectColumns.DESCRIPTION + " TEXT NULL, " +
                ProjectColumns.ARCHIVED + " INTEGER DEFAULT 0, " +
                "UNIQUE (" + ProjectColumns.NAME + ") ON CONFLICT ROLLBACK)");

        // Create the structure for the `time`-table.
        db.execSQL("CREATE TABLE " + Tables.TIME + " ( " +
                TimeColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TimeColumns.PROJECT_ID + " INTEGER NOT NULL, " +
                TimeColumns.START + " INTEGER NOT NULL, " +
                TimeColumns.STOP + " INTEGER DEFAULT 0, " +
                TimeColumns.REGISTERED + " INTEGER NOT NULL DEFAULT 0)");
    }

    /**
     * Upgrade the database.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     * @throws IllegalArgumentException If oldVersion is less than 1.
     * @throws IllegalArgumentException If newVersion is more than `Worker.DATABASE_VERSION`.
     * @throws IllegalArgumentException If newVersion is less than oldVersion, i.e. downgrade.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Since the first version of the structure was 1, upgrading from any
        // value less than 1 is not allowed.
        if (1 > oldVersion) {
            throw new IllegalArgumentException(
                    "oldVersion cannot be less than 1"
            );
        }

        // Check the state of the newVersion, we cannot allow to upgrade past
        // the latest available version (i.e. `Worker.DATABASE_VERSION`).
        if (Worker.DATABASE_VERSION < newVersion) {
            throw new IllegalArgumentException(
                    "newVersion cannot be more than " + Worker.DATABASE_VERSION
            );
        }

        // Check the relation between oldVersion and newVersion, downgrade via
        // the `onUpgrade`-method is not allowed.
        if (oldVersion > newVersion) {
            throw new IllegalArgumentException(
                    "newVersion cannot be less than oldVersion"
            );
        }

        // since sqlite is unable to remove columns we need to check if the
        // column already exists before adding it.
        if (!columnExists(db, Tables.TIME, TimeColumns.REGISTERED)) {
            // Add the `registered`-column to the `time`-table.
            db.execSQL("ALTER TABLE " + Tables.TIME +
                    " ADD COLUMN " + TimeColumns.REGISTERED +
                    " INTEGER NOT NULL DEFAULT 0");
        }
    }

    /**
     * Downgrade the database.
     *
     * @param db         The database.
     * @param oldVersion The old database version.
     * @param newVersion The new database version.
     * @throws IllegalArgumentException If newVersion is less than 1.
     * @throws IllegalArgumentException If oldVersion is less than newVersion, i.e. upgrade.
     */
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Since the first version of the structure was 1, downgrading to any
        // value less than 1 is not allowed.
        if (1 > newVersion) {
            throw new IllegalArgumentException(
                    "newVersion cannot be less than 1"
            );
        }

        // Check the relation between oldVersion and newVersion, upgrade via
        // the `onDowngrade`-method is not allowed.
        if (oldVersion < newVersion) {
            throw new IllegalArgumentException(
                    "oldVersion cannot be less than newVersion"
            );
        }
    }
}