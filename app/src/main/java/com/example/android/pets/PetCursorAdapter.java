package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetsContract;


public class PetCursorAdapter extends CursorAdapter {


    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 );
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }

    /**
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView name = (TextView) view.findViewById(R.id.name);
        TextView summary = (TextView) view.findViewById(R.id.summary);

        String currentName = cursor.getString(cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_NAME));
        String currentSummary =cursor.getString(cursor.getColumnIndex(PetsContract.PetEntry.COLUMN_PET_BREED));

        name.setText(currentName);
        summary.setText(currentSummary);

    }
}
