package com.example.bumb.miser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "item_database";
    private static final String TABLE_NAME = "item_table";
    private static final int DATABASE_VERSION = 1;

    private static final String ID = "_id";
    private static final String NAME = "name";
    private static final String PRICE = "price";
    private static final String CURRENTPRICE = "currentprice";
    private static final String URL = "url";
    private static final String ALARMON = "alarmon";
    private static final String PERCENTAGE = "percentage";
    private static final String FAVORITE = "favorite";
    private static final String IMAGE = "image";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_table =
                "CREATE TABLE " + TABLE_NAME + " (" +
                NAME + " TEXT PRIMARY KEY UNIQUE, " +
                PRICE + " DOUBLE, " +
                CURRENTPRICE + " DOUBLE, " +
                URL + " TEXT, " +
                ALARMON + " INTEGER, " +
                PERCENTAGE + " INTEGER, " +
                FAVORITE + " INTEGER, " +
                IMAGE + ");";

        db.execSQL(create_table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(NAME, item.getItemName());
        cv.put(PRICE, item.getItemPrice());
        cv.put(CURRENTPRICE, item.getItemPrice());
        cv.put(URL, item.getItemURL());
        cv.put(ALARMON, item.isAlarmOn() ? 1 : 0);
        cv.put(PERCENTAGE, item.getAlarmPercentage());
        cv.put(FAVORITE, item.isFavorite() ? 1 : 0);
        cv.put(IMAGE, item.getItemImage());

        long id = db.insert(TABLE_NAME,null, cv);

        this.getReadableDatabase();

        db.close();
        return id;
    }

    public void deleteItem(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, NAME + " = ?",
                new String[] { name });

        db.close();
    }


    public void updateItemFav(String name, Boolean isfavorite) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(FAVORITE, isfavorite ? 1 : 0);

        db.update(TABLE_NAME, cv ,NAME + " = ?",
                new String[] { name });

        db.close();
    }

    public void updateItemPrice(String name, double price) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(CURRENTPRICE, price);

        db.update(TABLE_NAME, cv ,NAME + " = ?",
                new String[] { name });

        db.close();
    }

    public List<Item> getAllItems() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.query(TABLE_NAME, null, null, null, null, null, null);

        int name_no = c.getColumnIndex(NAME);
        int price_no = c.getColumnIndex(PRICE);
        int curprice_no = c.getColumnIndex(CURRENTPRICE);
        int url_no = c.getColumnIndex(URL);
        int alarmon_no = c.getColumnIndex(ALARMON);
        int percentage_no = c.getColumnIndex(PERCENTAGE);
        int favorite_no = c.getColumnIndex(FAVORITE);
        int image_no = c.getColumnIndex(IMAGE);

        List<Item> itemList = new ArrayList<Item>();

        if (c.moveToFirst()) {
            while (!c.isAfterLast()) {
                Item item = new Item();

                item.setItemName(c.getString(name_no));
                item.setItemPrice(c.getDouble(price_no));
                item.setItemPriceUpdated(c.getDouble(curprice_no));
                item.setItemURL(c.getString(url_no));
                item.setAlarmOn(c.getInt(alarmon_no) == 1);
                item.setAlarmPercentage(c.getInt(percentage_no));
                item.setFavorite(c.getInt(favorite_no) == 1);
                item.setItemImage(c.getString(image_no));

                itemList.add(item);

                c.moveToNext();
            }
        }

        c.close();
        db.close();

        return itemList;
    }
}

