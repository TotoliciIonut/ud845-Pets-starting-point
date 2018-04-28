package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.pets.data.PetsContract.PetEntry;
import com.example.android.pets.data.PetsContract;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    PetCursorAdapter mAdapter;
    ListView listView;
    ArrayList<String> arrayList = new ArrayList<>();

    // These are the Contacts rows that we will retrieve
    String[] projection ={PetEntry._ID ,PetEntry.COLUMN_PET_NAME,PetEntry.COLUMN_PET_BREED,PetEntry.COLUMN_PET_GENDER,PetEntry.COLUMN_PET_WEIGHT};
    String[] idProjection ={PetEntry._ID};
    Uri uri = PetEntry.CONTENT_URI;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        listView = (ListView) findViewById(R.id.listView);

        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(new Intent(CatalogActivity.this, EditorActivity.class));
                intent.putExtra("title","Edit Pet");
                intent.putExtra("id",arrayList.get(position));
                intent.putExtra("execute",true);
                startActivity(intent);
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                intent.putExtra("title","Add pet");
                intent.putExtra("execute",false);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(0, null,this);
        listView.setAdapter(mAdapter);
    }
    private void showDialog(String message, String positive , String negative) {
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getContentResolver().delete(PetsContract.PetEntry.CONTENT_URI,null ,null);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertPet("Toto","Terrier",1,7);
                return true;
            case R.id.action_delete_all_entries:
                showDialog("Are you sure about that?","Yes","No");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void insertPet(String name, String breed, int gender, int weight){

        ContentValues values = new ContentValues();
        values.put(PetEntry.COLUMN_PET_NAME,name);
        values.put(PetEntry.COLUMN_PET_BREED,breed);
        values.put(PetEntry.COLUMN_PET_GENDER,gender);
        values.put(PetEntry.COLUMN_PET_WEIGHT,weight);
        getContentResolver().insert(PetsContract.PetEntry.CONTENT_URI,values);

    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new android.content.CursorLoader(this,uri, projection,null,null,null);

    }


    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        Cursor cursor = getContentResolver().query(uri,idProjection,null,null,null);
        if(cursor.moveToFirst()){
        arrayList.add(cursor.getString(cursor.getColumnIndex(PetEntry._ID)));
        while (cursor.moveToNext()){
            arrayList.add(cursor.getString(cursor.getColumnIndex(PetEntry._ID)));
        }}
        cursor.close();
        if(mAdapter==null){mAdapter= new PetCursorAdapter(this,data);}
        else {mAdapter.swapCursor(data);}
        listView.setAdapter(mAdapter);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(mAdapter==null){return;}
        else {mAdapter.swapCursor(null);}
    }




}









