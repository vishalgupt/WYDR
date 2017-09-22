package wydr.sellers.slider;

/**
 * Created by Deepesh_pc on 16-07-2015.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import wydr.sellers.syncadap.AlteredCatalog;
import wydr.sellers.syncadap.FeedCatalog;

public class MyContentProvider extends ContentProvider
{
    public static final int contact = 1;
    public static final int contactitem = 2;
    public static final int login = 3;
    public static final int loginitem = 4;
    public static final int connections = 5;
    public static final int connectionsitem = 6;
    public static final int loggedin = 7;
    public static final int loggedinitem = 8;
    public static final int category = 9;
    public static final int categoryitem = 10;
    public static final int altercatalog = 11;
    public static final int altercatalogitem = 12;
    public static final int mycatalog = 13;
    public static final int mycatalogitem = 14;
    public static final int mycategory = 15;
    public static final int mycategoryitem = 16;
    public static final int authentication = 17;
    public static final int authenticationitem = 18;
    // authority is the symbolic name of your provider
    // To avoid conflicts with other providers, you should use
    // Internet domain ownership (in reverse) as the basis of your provider authority.
    public static final String AUTHORITY = "com.wydr.contacts";
    // create content URIs from the authority by appending path to database table
    public static final Uri CONTENT_URI_Contacts =
            Uri.parse("content://" + AUTHORITY + "/contacts");
    public static final Uri CONTENT_URI_Login =
            Uri.parse("content://" + AUTHORITY + "/login");
    public static final Uri CONTENT_URI_Connections =
            Uri.parse("content://" + AUTHORITY + "/connections");
    public static final Uri CONTENT_URI_Loggedin =
            Uri.parse("content://" + AUTHORITY + "/loggedin");
    public static final Uri CONTENT_URI_Category =
            Uri.parse("content://" + AUTHORITY + "/category");
    public static final Uri CONTENT_URI_ALTER =
            Uri.parse("content://" + AUTHORITY + "/altercatalog");
    public static final Uri CONTENT_URI_FEED =
            Uri.parse("content://" + AUTHORITY + "/mycatalog");
    public static final Uri CONTENT_URI_MYCATEGORY =
            Uri.parse("content://" + AUTHORITY + "/mycategory");
    public static final Uri CONTENT_URI_AUTHENTICATION =
            Uri.parse("content://" + AUTHORITY + "/authentication");
    // a content URI pattern matches content URIs using wildcard characters:
    // *: Matches a string of any valid characters of any length.
    // #: Matches a string of numeric characters of any length.
    public static final UriMatcher uriMatcher;

    static
    {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "contacts", contact);
        uriMatcher.addURI(AUTHORITY, "contacts/#", contactitem);
        uriMatcher.addURI(AUTHORITY, "login", login);
        uriMatcher.addURI(AUTHORITY, "login/#", loginitem);
        uriMatcher.addURI(AUTHORITY, "connections", connections);
        uriMatcher.addURI(AUTHORITY, "connections/#", connectionsitem);
        uriMatcher.addURI(AUTHORITY, "loggedin", loggedin);
        uriMatcher.addURI(AUTHORITY, "loggedin/#", loggedinitem);
        uriMatcher.addURI(AUTHORITY, "category", category);
        uriMatcher.addURI(AUTHORITY, "category/#", categoryitem);
        uriMatcher.addURI(AUTHORITY, "altercatalog", altercatalog);
        uriMatcher.addURI(AUTHORITY, "altercatalog/#", altercatalogitem);
        uriMatcher.addURI(AUTHORITY, "mycatalog", mycatalog);
        uriMatcher.addURI(AUTHORITY, "mycatalog/#", mycatalogitem);
        uriMatcher.addURI(AUTHORITY, "mycategory", mycategory);
        uriMatcher.addURI(AUTHORITY, "mycategory/#", mycategoryitem);
        uriMatcher.addURI(AUTHORITY, "authentication", authentication);
        uriMatcher.addURI(AUTHORITY, "authentication/#", authenticationitem);

    }

    public MyDatabaseHelper dbHelper;
    public Context c;

    // system calls onCreate() when it starts up the provider.
    @Override
    public boolean onCreate()
    {
        // get access to the database helper
        dbHelper = new MyDatabaseHelper(this.getContext());
        return false;
    }

    //Return the MIME type corresponding to a content URI
    @Override
    public String getType(Uri uri)
    {
        switch (uriMatcher.match(uri))
        {
            case contact:
                return "vnd.android.cursor.dir/contacts";
            case login:
                return "vnd.android.cursor.dir/login";
            case connections:
                return "vnd.android.cursor.dir/connections";
            case loggedin:
                return "vnd.android.cursor.dir/loggedin";
            case category:
                return "vnd.android.cursor.dir/category";
            case altercatalog:
                return "vnd.android.cursor.dir/altercatalog";
            case mycatalog:
                return "vnd.android.cursor.dir/mycatalog";
            case mycategory:
                return "vnd.android.cursor.dir/mycategory";
            case authentication:
                return "vnd.android.cursor.dir/mycategory";
            default:
                return null;

        }
    }

    // The insert() method adds a new row to the appropriate table, using the values
    // in the ContentValues argument. If a column name is not in the ContentValues argument,
    // you may want to provide a default value for it either in your provider code or in
    // your database schema.
    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        if (uriMatcher.match(uri) != contact && uriMatcher.match(uri) != authentication
                && uriMatcher.match(uri) != login && uriMatcher.match(uri) != connections && uriMatcher.match(uri) != loggedin
                && uriMatcher.match(uri) != category && uriMatcher.match(uri) != altercatalog && uriMatcher.match(uri) != mycatalog && uriMatcher.match(uri) != mycategory) {
            throw new IllegalArgumentException(
                    "Unsupported URI for insertion: " + uri);
        }
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            if (uriMatcher.match(uri) == authentication) {
                long id = db.insertOrThrow(Authentication.TABLE_CONTACTS, null, values);
                return getUriForId(id, uri);
            } else if (uriMatcher.match(uri) == contact) {
                long id = db.insertOrThrow(ContactsDb.TABLE_CONTACTS, null, values);

                return getUriForId(id, uri);
            } else if (uriMatcher.match(uri) == login) {
                long id = db.insertOrThrow(LoginDB.TABLE_LOGIN, null, values);

                return getUriForId(id, uri);
            } else if (uriMatcher.match(uri) == connections) {
                long id = db.insertOrThrow(ProductTable.TABLE_CONNECTIONS, null, values);

                return getUriForId(id, uri);
            } else if (uriMatcher.match(uri) == loggedin) {
                long id = db.insertOrThrow(LoggedIn.TABLE_LOGGEDIN, null, values);

                return getUriForId(id, uri);
            } else if (uriMatcher.match(uri) == category) {
                long id = db.insertOrThrow(CategoryTable.TABLE_CONTACTS, null, values);

                return getUriForId(id, uri);
            } else if (uriMatcher.match(uri) == altercatalog) {
                long id = db.insertOrThrow(AlteredCatalog.TABLE_NAME, null, values);

                return getUriForId(id, uri);
            } else if (uriMatcher.match(uri) == mycatalog) {
                long id = db.insertOrThrow(FeedCatalog.TABLE_NAME, null, values);

                return getUriForId(id, uri);
            } else if (uriMatcher.match(uri) == mycategory) {
                long id = db.insertOrThrow(MyCategoryTable.TABLE_CONTACTS, null, values);

                return getUriForId(id, uri);
            }
        } catch (android.database.sqlite.SQLiteConstraintException e) {
            Log.e("MyContentProvider", "SQLiteConstraintException:" + e.getMessage());
        } catch (android.database.sqlite.SQLiteException e) {
            Log.e("MyContentProvider", "SQLiteException:" + e.getMessage());
        } catch (Exception e) {
            Log.e("MyContentProvider", "Exception:" + e.getMessage());
        }
        getContext().getContentResolver().notifyChange(uri, null);
        //db.close();
        return uri;
    }


    // The query() method must return a Cursor object, or if it fails,
    // throw an Exception. If you are using an SQLite database as your data storage,
    // you can simply return the Cursor returned by one of the query() methods of the
    // SQLiteDatabase class. If the query does not match any rows, you should return a
    // Cursor instance whose getCount() method returns 0. You should return null only
    // if an internal error occurred during the query process.
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder)
    {
        Cursor cursor = null;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            if (uriMatcher.match(uri) == contact) {
                queryBuilder.setTables(ContactsDb.TABLE_CONTACTS);
            } else if (uriMatcher.match(uri) == login) {
                queryBuilder.setTables(LoginDB.TABLE_LOGIN);
            } else if (uriMatcher.match(uri) == connections) {
                queryBuilder.setTables(ProductTable.TABLE_CONNECTIONS);
            } else if (uriMatcher.match(uri) == loggedin) {
                queryBuilder.setTables(LoggedIn.TABLE_LOGGEDIN);
            } else if (uriMatcher.match(uri) == category) {
                queryBuilder.setTables(CategoryTable.TABLE_CONTACTS);
            } else if (uriMatcher.match(uri) == altercatalog) {
                queryBuilder.setTables(AlteredCatalog.TABLE_NAME);
            } else if (uriMatcher.match(uri) == mycatalog) {
                queryBuilder.setTables(FeedCatalog.TABLE_NAME);
            } else if (uriMatcher.match(uri) == mycategory) {
                queryBuilder.setTables(MyCategoryTable.TABLE_CONTACTS);
            } else if (uriMatcher.match(uri) == authentication) {
                queryBuilder.setTables(Authentication.TABLE_CONTACTS);
            }

       /* String id = uri.getPathSegments().get(1);
        queryBuilder.appendWhere(ContactsDb.KEY_ID + "=" + id);
*/

            cursor = queryBuilder.query(db, projection, selection,
                    selectionArgs, null, null, sortOrder);
            cursor.setNotificationUri(
                    getContext().getContentResolver(),
                    uri);
        } catch (android.database.sqlite.SQLiteConstraintException e) {
            Log.e("MyContentProvider", "SQLiteConstraintException:" + e.getMessage());
        } catch (android.database.sqlite.SQLiteException e) {
            Log.e("MyContentProvider", "SQLiteException:" + e.getMessage());
        } catch (Exception e) {
            Log.e("MyContentProvider", "Exception:" + e.getMessage());
        }
        return cursor;

    }

    // The delete() method deletes rows based on the seletion or if an id is
    // provided then it deleted a single row. The methods returns the numbers
    // of records delete from the database. If you choose not to delete the data
    // physically then just update a flag here.
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deleteCount = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            if (uriMatcher.match(uri) == contact) {
                deleteCount = db.delete(ContactsDb.TABLE_CONTACTS, selection, selectionArgs);
            } else if (uriMatcher.match(uri) == login) {
                deleteCount = db.delete(LoginDB.TABLE_LOGIN, selection, selectionArgs);
            } else if (uriMatcher.match(uri) == connections) {
                deleteCount = db.delete(ProductTable.TABLE_CONNECTIONS, selection, selectionArgs);
            } else if (uriMatcher.match(uri) == loggedin) {
                deleteCount = db.delete(LoggedIn.TABLE_LOGGEDIN, selection, selectionArgs);

            } else if (uriMatcher.match(uri) == category) {
                deleteCount = db.delete(CategoryTable.TABLE_CONTACTS, selection, selectionArgs);

            } else if (uriMatcher.match(uri) == altercatalog) {
                deleteCount = db.delete(AlteredCatalog.TABLE_NAME, selection, selectionArgs);

            } else if (uriMatcher.match(uri) == mycatalog) {
                deleteCount = db.delete(FeedCatalog.TABLE_NAME, selection, selectionArgs);
            } else if (uriMatcher.match(uri) == mycategory) {
                deleteCount = db.delete(MyCategoryTable.TABLE_CONTACTS, selection, selectionArgs);
            } else if (uriMatcher.match(uri) == authentication) {
                deleteCount = db.delete(Authentication.TABLE_CONTACTS, selection, selectionArgs);
            }
        } catch (android.database.sqlite.SQLiteConstraintException e) {
            Log.e("MyContentProvider", "SQLiteConstraintException:" + e.getMessage());
        } catch (android.database.sqlite.SQLiteException e) {
            Log.e("MyContentProvider", "SQLiteException:" + e.getMessage());
        } catch (Exception e) {
            Log.e("MyContentProvider", "Exception:" + e.getMessage());
        }
        getContext().getContentResolver().notifyChange(uri, null);
        //  db.close();
        return deleteCount;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int updateCount = 0;
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
            if (uriMatcher.match(uri) == contact) {

                updateCount = db.update(ContactsDb.TABLE_CONTACTS, values, selection, selectionArgs);

            } else if (uriMatcher.match(uri) == login) {
                updateCount = db.update(LoginDB.TABLE_LOGIN, values, selection, selectionArgs);

            } else if (uriMatcher.match(uri) == connections) {
                updateCount = db.update(ProductTable.TABLE_CONNECTIONS, values, selection, selectionArgs);

            } else if (uriMatcher.match(uri) == loggedin) {
                updateCount = db.update(LoggedIn.TABLE_LOGGEDIN, values, selection, selectionArgs);

            } else if (uriMatcher.match(uri) == category) {
                updateCount = db.update(CategoryTable.TABLE_CONTACTS, values, selection, selectionArgs);

            } else if (uriMatcher.match(uri) == altercatalog) {
                updateCount = db.update(AlteredCatalog.TABLE_NAME, values, selection, selectionArgs);

            } else if (uriMatcher.match(uri) == mycatalog) {
                updateCount = db.update(FeedCatalog.TABLE_NAME, values, selection, selectionArgs);
            } else if (uriMatcher.match(uri) == mycategory) {
                updateCount = db.update(MyCategoryTable.TABLE_CONTACTS, values, selection, selectionArgs);
            } else if (uriMatcher.match(uri) == authentication) {
                updateCount = db.update(Authentication.TABLE_CONTACTS, values, selection, selectionArgs);
            }
        } catch (android.database.sqlite.SQLiteConstraintException e) {
            Log.e("MyContentProvider", "SQLiteConstraintException:" + e.getMessage());
        } catch (android.database.sqlite.SQLiteException e) {
            Log.e("MyContentProvider", "SQLiteException:" + e.getMessage());
        } catch (Exception e) {
            Log.e("MyContentProvider", "Exception:" + e.getMessage());
        }
        getContext().getContentResolver().notifyChange(uri, null);

        // db.close();
        return updateCount;
    }

    private Uri getUriForId(long id, Uri uri) {
        if (id > 0L) {
            Uri itemUri = ContentUris.withAppendedId(uri, id);
            // notify all listeners of changes:
            getContext().
                    getContentResolver().
                    notifyChange(itemUri, null);
            return itemUri;
        }
        // s.th. went wrong:
        throw new SQLException(
                "Problem while inserting into uri: " + uri);
    }

}