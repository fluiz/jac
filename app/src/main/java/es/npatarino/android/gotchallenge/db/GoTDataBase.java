package es.npatarino.android.gotchallenge.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import es.npatarino.android.gotchallenge.model.GoTCharacter;
import es.npatarino.android.gotchallenge.model.GoTEntity;

public class GoTDataBase extends SQLiteOpenHelper {
    private static final String TAG = "GoTDataBase";

    private static GoTDataBase instance;

    // Database info
    private static final String DATABASE_NAME = "GoTDatabase.db";
    private static final int DATABASE_VERSION = 1;
    private static SQLiteDatabase db;

    // Table Names
    private static final String TABLE_NAME_CHARACTERS = "GoTCharacters";
    private static final String TABLE_NAME_HOUSES = "GoTHouses";

    // Character Table Columns
    private static final String KEY_CHARS_ID = "_id";
    private static final String KEY_CHARS_DESC = "description";
    private static final String KEY_CHARS_HOUSEID = "houseId";
    private static final String KEY_CHARS_IMGURL = "imageUrl";
    private static final String KEY_CHARS_NAME = "name";

    // House Table Columns
    private static final String KEY_HOUSE_ID = "_id";
    private static final String KEY_HOUSE_NAME = "houseName";
    private static final String KEY_HOUSE_IMGURL = "houseImageUrl";


    final String create_table_characters_sql =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_CHARACTERS +
            "([_id] INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, [description] TEXT, [houseId] TEXT,[imageUrl] TEXT,[name] TEXT)";
    final String create_table_houses_sql =
            "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_HOUSES +
            "([_id] TEXT PRIMARY KEY NOT NULL UNIQUE, [houseName] TEXT, [houseImageUrl] TEXT)";

    public static synchronized GoTDataBase getInstance(Context context) {
        if (instance == null) {
            instance = new GoTDataBase(context.getApplicationContext());
        }
        return instance;
    }

    private GoTDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(create_table_houses_sql);
        db.execSQL(create_table_characters_sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_CHARACTERS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_HOUSES);
            onCreate(db);
        }
    }

    public void saveCharactersList(List<GoTEntity> list) {
        for (GoTEntity character : list) {
            addOrUpdateCharacter((GoTCharacter) character);
        }
    }

    public void saveHousesList(List<GoTEntity> list) {
        for (GoTEntity house : list) {
            GoTCharacter.GoTHouse gotHouse = (GoTCharacter.GoTHouse) house;
            addOrUpdateHouse(gotHouse.getHouseId(), gotHouse.getHouseName(), gotHouse.getHouseImageUrl());
        }
    }

    public long addOrUpdateCharacter(GoTCharacter character) {
        SQLiteDatabase db = getWritableDatabase();
        long charId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_CHARS_NAME, character.getName());
            values.put(KEY_CHARS_DESC, character.getDescription());
            values.put(KEY_CHARS_IMGURL, character.getImageUrl());
            values.put(KEY_CHARS_HOUSEID, character.getHouseId());

            addOrUpdateHouse(character.getHouseId(), character.getHouseName(), character.getHouseImageUrl());

            int rows = db.update(TABLE_NAME_CHARACTERS, values, KEY_CHARS_NAME + "= ?", new String[]{ character.getName() });

            if (rows == 1) {
                String characterSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?", KEY_CHARS_ID, TABLE_NAME_CHARACTERS, KEY_CHARS_NAME);
                Cursor cursor = db.rawQuery(characterSelectQuery, new String[]{ String.valueOf(character.getName()) });

                try {
                    if (cursor.moveToFirst()) {
                        charId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                charId = db.insertOrThrow(TABLE_NAME_CHARACTERS, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating or adding character");
        } finally {
            db.endTransaction();
        }
        return charId;
    }

    public long addOrUpdateHouse(String houseId, String houseName, String houseImgUrl) {
        SQLiteDatabase db = getWritableDatabase();
        long id = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_HOUSE_ID, houseId);
            values.put(KEY_HOUSE_NAME, houseName);
            values.put(KEY_HOUSE_IMGURL, houseImgUrl);

            int rows = db.update(TABLE_NAME_HOUSES, values, KEY_HOUSE_NAME + "= ?", new String[]{ houseName });

            if (rows == 1) {
                String houseSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?", KEY_HOUSE_ID, TABLE_NAME_HOUSES, KEY_HOUSE_NAME);
                Cursor cursor = db.rawQuery(houseSelectQuery, new String[]{ String.valueOf(houseName) });

                try {
                    if (cursor.moveToFirst()) {
                        id = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                id = db.insertOrThrow(TABLE_NAME_HOUSES, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating or adding house");
        } finally {
            db.endTransaction();
        }
        return id;
    }

    public List<GoTEntity> getAllCharacters() {
        List<GoTEntity> characters = new ArrayList<>();

        String CHARACTERS_SELECT_QUERY = String.format("SELECT * FROM %s ", TABLE_NAME_CHARACTERS);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(CHARACTERS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                     GoTCharacter character = new GoTCharacter();
                     character.setName(cursor.getString(cursor.getColumnIndex(KEY_CHARS_NAME)));
                     character.setDescription(cursor.getString(cursor.getColumnIndex(KEY_CHARS_DESC)));
                     character.setHouseId(cursor.getString(cursor.getColumnIndex(KEY_CHARS_HOUSEID)));
                     character.setImageUrl(cursor.getString(cursor.getColumnIndex(KEY_CHARS_IMGURL)));
                     
                     characters.add(character);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to get Characters from Database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return characters;
    }

    public List<GoTEntity> getAllHouses() {
        List<GoTEntity> houses = new ArrayList<>();

        String HOUSES_SELECT_QUERY = String.format("SELECT * FROM %s ", TABLE_NAME_HOUSES);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(HOUSES_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    GoTCharacter.GoTHouse house = new GoTCharacter.GoTHouse();
                    house.setHouseId(cursor.getString(cursor.getColumnIndex(KEY_HOUSE_ID)));
                    house.setHouseName(cursor.getString(cursor.getColumnIndex(KEY_HOUSE_NAME)));
                    house.setHouseImageUrl(cursor.getString(cursor.getColumnIndex(KEY_HOUSE_IMGURL)));

                    houses.add(house);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to get Characters from Database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return houses;
    }
}
