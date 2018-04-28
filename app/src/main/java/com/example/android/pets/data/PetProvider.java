package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.android.pets.EditorActivity;

import static com.example.android.pets.data.PetsContract.*;
import static com.example.android.pets.data.PetsContract.PetEntry.*;



public class PetProvider extends ContentProvider {


    private static final int PETS = 100;
    private static final int PETS_ID = 101;

    PetDbHelper mDbHelper ;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PETS,PETS);
        sUriMatcher.addURI(CONTENT_AUTHORITY, PATH_PETS+"/#",PETS_ID);
    }

    @Override
    public boolean onCreate() {

        mDbHelper = new PetDbHelper(getContext());

        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor=null;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                cursor = database.query(TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PETS_ID:
                selection = _ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                cursor = database.query(TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }

    }

    private Uri insertPet(Uri uri, ContentValues values) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        long id = database.insert(TABLE_NAME, null, values);

        getContext().getContentResolver().notifyChange(uri,null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PETS_ID:
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        getContext().getContentResolver().notifyChange(uri,null);
        return database.update(PetEntry.TABLE_NAME, values, selection, selectionArgs);

    }
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
    final int match = sUriMatcher.match(uri);
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        switch (match) {
        case PETS:
            getContext().getContentResolver().notifyChange(uri,null);
            return database.delete(PetEntry.TABLE_NAME,selection,selectionArgs);
        case PETS_ID:
            selection = PetEntry._ID + "=?";
            selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
            getContext().getContentResolver().notifyChange(uri,null);
            return database.delete(PetEntry.TABLE_NAME , selection, selectionArgs);
        default:
            throw new IllegalArgumentException("Update is not supported for " + uri);
    }}
    @Override
    public String getType(Uri uri) {
        return null;
    }
}