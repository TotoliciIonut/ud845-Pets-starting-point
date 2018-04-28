package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kakat on 04.04.2018.
 */

public class PetDbHelper extends SQLiteOpenHelper {
    public static final int DB_VERSION = 1;
    public static final String DB_NAME="Pets.db";
    public static final String SQL_CREATE_PETS_TABLE =  "CREATE TABLE " + PetsContract.PetEntry.TABLE_NAME + " ("+
    PetsContract.PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
    PetsContract.PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL, "+
    PetsContract.PetEntry.COLUMN_PET_BREED + " TEXT, "+
    PetsContract.PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "+
    PetsContract.PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";
    public static final String SQL_DELETE_ENTERIES = "DELETE FROM "+ PetsContract.PetEntry.TABLE_NAME;

    public PetDbHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
     db.execSQL(SQL_CREATE_PETS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
      db.execSQL(SQL_DELETE_ENTERIES);
    }
}
