/*
 * Copyright (C) 2016 The Android Open Source Project
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
package com.example.android.pets;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.pets.data.PetDbHelper;
import com.example.android.pets.data.PetsContract;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements  android.app.LoaderManager.LoaderCallbacks<Cursor> {
    private EditText mNameEditText;
    private EditText mBreedEditText;
    private EditText mWeightEditText;
    private Spinner mGenderSpinner;
    String idFromIntent;


    private int mGender = 0;


    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
// the view, and we change the mPetHasChanged boolean to true.
    private boolean mPetHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle(getIntent().getExtras().getString("title"));//get the title from the extras from the intent
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        try{
            idFromIntent =getIntent().getExtras().getString("id");
        }
        catch(NullPointerException nullPointerException){
            idFromIntent=null;
        }
        if(getIntent().getExtras().getBoolean("execute")){
            getLoaderManager().initLoader(0, null, this);}
            else invalidateOptionsMenu();


        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);

        setupSpinner();
    }

    private void showDialog(String message, String positive , String negative,final boolean b) {
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked "Discard" button, navigate to parent activity.
                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                if(b) getContentResolver().delete(Uri.withAppendedPath(PetsContract.PetEntry.CONTENT_URI, idFromIntent), null, null);
            }
        };
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.setPositiveButton(positive, discardButtonClickListener);
        builder.setNegativeButton(negative, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (!getIntent().getExtras().getBoolean("execute")) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }
    private void setupSpinner() {

        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetsContract.PetEntry.GENDER_MALE;
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetsContract.PetEntry.GENDER_FEMALE;
                    } else {
                        mGender = PetsContract.PetEntry.GENDER_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = PetsContract.PetEntry.GENDER_UNKNOWN;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                savePet();
                finish();
                return true;
            case R.id.action_delete:
                showDialog("Are you sure about that?","Yes","No",true);

                return true;
            case android.R.id.home:
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                showDialog("Discard your changes and quit editing?","Discard","Keep Editing",false);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void savePet() {

          String nameString = mNameEditText.getText().toString().trim();
         String breedString = mBreedEditText.getText().toString().trim();
        String weightString = mWeightEditText.getText().toString().trim();

if (nameString==null||breedString==null||weightString==null||mGender==PetsContract.PetEntry.GENDER_UNKNOWN)
{
    startActivity(new Intent(EditorActivity.this,CatalogActivity.class));
}
else{   int weight = Integer.parseInt(weightString);
        ContentValues values = new ContentValues();
        values.put(PetsContract.PetEntry.COLUMN_PET_NAME, nameString);
        values.put(PetsContract.PetEntry.COLUMN_PET_BREED, breedString);
        values.put(PetsContract.PetEntry.COLUMN_PET_GENDER, mGender);
        values.put(PetsContract.PetEntry.COLUMN_PET_WEIGHT, weight);
        if (getIntent().getExtras().getBoolean("execute")){getContentResolver().update
                (Uri.withAppendedPath(PetsContract.PetEntry.CONTENT_URI,idFromIntent),values,null,null);}
                else{
        getContentResolver().insert(PetsContract.PetEntry.CONTENT_URI,values);}}

    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] selectionArgs ={idFromIntent};

        String[] projection = {
                PetsContract.PetEntry._ID,
                PetsContract.PetEntry.COLUMN_PET_NAME,
                PetsContract.PetEntry.COLUMN_PET_BREED,
                PetsContract.PetEntry.COLUMN_PET_GENDER,
                PetsContract.PetEntry.COLUMN_PET_WEIGHT };
        return new CursorLoader(this, PetsContract.PetEntry.CONTENT_URI, projection, PetsContract.PetEntry._ID +"=?", selectionArgs , null);

    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        String[] projection ={PetsContract.PetEntry._ID};
        if (cursor.moveToFirst()) {

            String name = cursor.getString(cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_NAME));
            String breed = cursor.getString(cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_BREED));
            int gender = cursor.getInt(cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_GENDER));
            int weight = cursor.getInt(cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_WEIGHT));

            mNameEditText.setText(name);
            mBreedEditText.setText(breed);
            mWeightEditText.setText(Integer.toString(weight));

            switch (gender) {
                case PetsContract.PetEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                case PetsContract.PetEntry.GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(0);
                    break;
            }}
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

    }
}