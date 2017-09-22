/*
 * Copyright 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wydr.sellers.syncadap;

public class FeedProvider {
    /*FeedDatabase mDatabaseHelper;

    private static final String AUTHORITY = FeedCatalog.CONTENT_AUTHORITY;
    public static final int ROUTE_ENTRIES = 1;
    public static final int ROUTE_ENTRIES_ID = 2;
   *//* public static final int ROUTE_ALTERED = 3;
    public static final int ROUTE_ALTERED_ID = 4;*//*
    *//**
     * UriMatcher, used to decode incoming URIs.
     *//*
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, "entries", ROUTE_ENTRIES);
        sUriMatcher.addURI(AUTHORITY, "entries*//*", ROUTE_ENTRIES_ID);
        *//*sUriMatcher.addURI(AUTHORITY, "edited_entries", ROUTE_ALTERED);
        sUriMatcher.addURI(AUTHORITY, "edited_entries*//**//*", ROUTE_ALTERED_ID);*//*
    }

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new FeedDatabase(getContext());
        return true;
    }

    *//**
     * Determine the mime type for entries returned by a given URI.
     *//*
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ROUTE_ENTRIES:
                return FeedCatalog.Entry.CONTENT_TYPE;
            case ROUTE_ENTRIES_ID:
                return FeedCatalog.Entry.CONTENT_ITEM_TYPE;
          *//*  case ROUTE_ALTERED:
                return AlteredCatalog.Entry.CONTENT_TYPE;
            case ROUTE_ALTERED_ID:
                return AlteredCatalog.Entry.CONTENT_ITEM_TYPE;*//*
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    *//**
     * Perform a database query by URI.
     *
     * <p>Currently supports returning all entries (/entries) and individual entries by ID
     * (/entries/{ID}).
     *//*
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase();
        SelectionBuilder builder = new SelectionBuilder();
        int uriMatch = sUriMatcher.match(uri);
        switch (uriMatch) {
            case ROUTE_ENTRIES_ID:
                // Return a single entry, by ID.
                String id = uri.getLastPathSegment();
                builder.where(FeedCatalog.Entry._ID + "=?", id);
            case ROUTE_ENTRIES:
                // Return all known entries.
                builder.table(FeedCatalog.Entry.TABLE_NAME)
                       .where(selection, selectionArgs);
                Cursor c = builder.query(db, projection, sortOrder);
                // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                Context ctx = getContext();
                assert ctx != null;
                c.setNotificationUri(ctx.getContentResolver(), uri);
                return c;
           *//* case ROUTE_ALTERED:
                // Return a single entry, by ID.
                String id2 = uri.getLastPathSegment();
                builder.where(AlteredCatalog.Entry._ID + "=?", id2);
            case ROUTE_ALTERED_ID:
                // Return all known entries.
                builder.table(AlteredCatalog.Entry.TABLE_NAME)
                        .where(selection, selectionArgs);
                Cursor c2 = builder.query(db, projection, sortOrder);
                // Note: Notification URI must be manually set here for loaders to correctly
                // register ContentObservers.
                Context ctx2 = getContext();
                assert ctx2 != null;
                c2.setNotificationUri(ctx2.getContentResolver(), uri);
                return c2;*//*
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    *//**
     * Insert a new entry into the database.
     *//*
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        assert db != null;
        final int match = sUriMatcher.match(uri);
        Uri result;
        switch (match) {
            case ROUTE_ENTRIES:
                long id = db.insertOrThrow(FeedCatalog.Entry.TABLE_NAME, null, values);
                result = Uri.parse(FeedCatalog.Entry.CONTENT_URI + "/" + id);
                break;
            case ROUTE_ENTRIES_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
            *//*case ROUTE_ALTERED:
                long id2 = db.insertOrThrow(AlteredCatalog.Entry.TABLE_NAME, null, values);
                result = Uri.parse(AlteredCatalog.Entry.CONTENT_URI + "/" + id2);
                break;
            case ROUTE_ALTERED_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);*//*
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return result;
    }

    *//**
     * Delete an entry by database by URI.
     *//*
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case ROUTE_ENTRIES:
                count = builder.table(FeedCatalog.Entry.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_ENTRIES_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(FeedCatalog.Entry.TABLE_NAME)
                       .where(FeedCatalog.Entry._ID + "=?", id)
                       .where(selection, selectionArgs)
                       .delete(db);
                break;
            *//*case ROUTE_ALTERED:
                count = builder.table(AlteredCatalog.Entry.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;
            case ROUTE_ALTERED_ID:
                String id2 = uri.getLastPathSegment();
                count = builder.table(AlteredCatalog.Entry.TABLE_NAME)
                        .where(AlteredCatalog.Entry._ID + "=?", id2)
                        .where(selection, selectionArgs)
                        .delete(db);
                break;*//*
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    *//**
     * Update an etry in the database by URI.
     *//*
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int count;
        switch (match) {
            case ROUTE_ENTRIES:
                count = builder.table(FeedCatalog.Entry.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_ENTRIES_ID:
                String id = uri.getLastPathSegment();
                count = builder.table(FeedCatalog.Entry.TABLE_NAME)
                        .where(FeedCatalog.Entry._ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            *//*case ROUTE_ALTERED:
                count = builder.table(AlteredCatalog.Entry.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_ALTERED_ID:
                String id2 = uri.getLastPathSegment();
                count = builder.table(AlteredCatalog.Entry.TABLE_NAME)
                        .where(AlteredCatalog.Entry._ID + "=?", id2)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;*//*
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Context ctx = getContext();
        assert ctx != null;
        ctx.getContentResolver().notifyChange(uri, null, false);
        return count;
    }

    *//**
     * SQLite backend for @{link FeedProvider}.
     *
     * Provides access to an disk-backed, SQLite datastore which is utilized by FeedProvider. This
     * database should never be accessed by other parts of the application directly.
     *//*
    static class FeedDatabase extends SQLiteOpenHelper {
        *//** Schema version. *//*
        public static final int DATABASE_VERSION = 1;
        *//** Filename for SQLite file. *//*
        public static final String DATABASE_NAME = "catalog.db";

        private static final String TYPE_TEXT = " TEXT";
        private static final String COMMA_SEP = ",";
        *//** SQL statement to create "entry" table. *//*
        private static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + FeedCatalog.Entry.TABLE_NAME + " (" +
                        FeedCatalog.Entry._ID + " INTEGER PRIMARY KEY," +
                        FeedCatalog.Entry.COLUMN_TITLE + TYPE_TEXT + COMMA_SEP +
                        FeedCatalog.Entry.COLUMN_CODE + TYPE_TEXT + COMMA_SEP +
                        FeedCatalog.Entry.COLUMN_IMAGEPATH + TYPE_TEXT + COMMA_SEP +
                        FeedCatalog.Entry.COLUMN_GRANDPARENTCAT + TYPE_TEXT + COMMA_SEP +
                        FeedCatalog.Entry.COLUMN_PARENTCAT + TYPE_TEXT + COMMA_SEP +
                        FeedCatalog.Entry.COLUMN_CHILDCAT + TYPE_TEXT + COMMA_SEP +
                        FeedCatalog.Entry.COLUMN_MRP + TYPE_TEXT + COMMA_SEP +
                        FeedCatalog.Entry.COLUMN_SP + TYPE_TEXT + COMMA_SEP +
                        FeedCatalog.Entry.COLUMN_QTY + TYPE_TEXT + COMMA_SEP +
                        FeedCatalog.Entry.COLUMN_MINQTY + TYPE_TEXT + COMMA_SEP +
                        FeedCatalog.Entry.COLUMN_DESC + TYPE_TEXT + COMMA_SEP +
                        FeedCatalog.Entry.COLUMN_VISIBILTY + TYPE_TEXT + COMMA_SEP +
                        FeedCatalog.Entry.COLUMN_CREATEDAT + TYPE_TEXT + COMMA_SEP +
                        FeedCatalog.Entry.COLUMN_UPDATEDAT + TYPE_TEXT  + COMMA_SEP +
                        FeedCatalog.Entry.COLUMN_STATUS + TYPE_TEXT  + COMMA_SEP +
                        FeedCatalog.Entry.COLUMN_PRODUCT_ID + TYPE_TEXT +
        ")";

        *//** SQL statement to drop "entry" table. *//*
        private static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + FeedCatalog.Entry.TABLE_NAME;

       *//* private static final String SQL_CREATE_ENTRIES2 =
                "CREATE TABLE " + AlteredCatalog.Entry.TABLE_NAME + " (" +
                        AlteredCatalog.Entry._ID + " INTEGER PRIMARY KEY," +
                        AlteredCatalog.Entry.COLUMN_TITLE + TYPE_TEXT + COMMA_SEP +
                        AlteredCatalog.Entry.COLUMN_CODE + TYPE_TEXT + COMMA_SEP +
                        AlteredCatalog.Entry.COLUMN_IMAGEPATH + TYPE_TEXT + COMMA_SEP +
                        AlteredCatalog.Entry.COLUMN_GRANDPARENTCAT + TYPE_TEXT + COMMA_SEP +
                        AlteredCatalog.Entry.COLUMN_PARENTCAT + TYPE_TEXT + COMMA_SEP +
                        AlteredCatalog.Entry.COLUMN_CHILDCAT + TYPE_TEXT + COMMA_SEP +
                        AlteredCatalog.Entry.COLUMN_MRP + TYPE_TEXT + COMMA_SEP +
                        AlteredCatalog.Entry.COLUMN_SP + TYPE_TEXT + COMMA_SEP +
                        AlteredCatalog.Entry.COLUMN_QTY + TYPE_TEXT + COMMA_SEP +
                        AlteredCatalog.Entry.COLUMN_MINQTY + TYPE_TEXT + COMMA_SEP +
                        AlteredCatalog.Entry.COLUMN_DESC + TYPE_TEXT + COMMA_SEP +
                        AlteredCatalog.Entry.COLUMN_VISIBILTY + TYPE_TEXT + COMMA_SEP +
                        AlteredCatalog.Entry.COLUMN_CREATEDAT + TYPE_TEXT + COMMA_SEP +
                        AlteredCatalog.Entry.COLUMN_UPDATED + TYPE_TEXT  + COMMA_SEP +
                        AlteredCatalog.Entry.COLUMN_LOCAL_FLAG + TYPE_TEXT  + COMMA_SEP +
                        AlteredCatalog.Entry.COLUMN_DEFAULT_POSITION + TYPE_TEXT  + COMMA_SEP +
                        AlteredCatalog.Entry.COLUMN_COMPANY_ID + TYPE_TEXT  + COMMA_SEP +
                        AlteredCatalog.Entry.COLUMN_REQUEST_STATUS + TYPE_TEXT  + COMMA_SEP +
                        AlteredCatalog.Entry.COLUMN_PRODUCT_ID + TYPE_TEXT +
                        ")";

        *//**//** SQL statement to drop "entry" table. *//**//*
        private static final String SQL_DELETE_ENTRIES2 =
                "DROP TABLE IF EXISTS " + AlteredCatalog.Entry.TABLE_NAME;*//*



        public FeedDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ENTRIES);
          //  db.execSQL(SQL_CREATE_ENTRIES2);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
          //  db.execSQL(SQL_DELETE_ENTRIES2);
            onCreate(db);
        }
    }*/
}
