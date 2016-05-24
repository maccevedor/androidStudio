package maccevedor.maveru;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by mauricio.acevedo on 23/05/16.
 */
public class MainDBAdapter {

    public static  final String COL_ID = "_id";
    public static  final String COL_CONTENT = "content";
    public static  final String COL_IMPORTANT = "important";


    public static  final int INDEX_ID = 0;
    public static  final int INDEX_CONTENT = INDEX_ID + 1 ;
    public static  final int INDEX_IMPORTANT = INDEX_ID + 2;

    private static final String TAG = "MainDbAdapter";

    private DatabaseHelper  mDbhelper;
    private SQLiteDatabase mDb;

    public static  final String DATABASE_NAME = "dba_remdrs";
    public static  final String TABLE_NAME = "tbl_remdrs";
    public static  final int DATABASE_VERSION = 1;


    private final Context mCtx;


    private static final String DATABASE_CREATE =
            //"CREATE TABLE if no exists " + TABLE_NAME + " ( " +
            "CREATE TABLE " + TABLE_NAME + " ( " +
                    COL_ID + " INTEGER PRIMARY KEY autoincrement, " +
                    COL_CONTENT + " TEXT, " +
                    COL_IMPORTANT + " INTEGER );" ;

    public MainDBAdapter(Context ctx){
        this.mCtx = ctx;
    }


    public void open() throws SQLException{
        mDbhelper = new DatabaseHelper(mCtx);
        mDb = mDbhelper.getReadableDatabase();
    }

    public void close(){
        if(mDbhelper != null){
            mDbhelper.close();
        }
    }

    public void createReminder(String name, boolean important){
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT, name);
        values.put(COL_IMPORTANT, important ? 1 : 0 );
        mDb.insert(TABLE_NAME, null, values);
    }

    public long createReminder(Main reminder){
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT, reminder.getContent());
        values.put(COL_IMPORTANT, reminder.getImportant());

        return mDb.insert(TABLE_NAME, null, values);

    }

    public Main fetchReminderById(int id){
        Cursor cursor = mDb.query(TABLE_NAME, new String[]{COL_ID,
                        COL_CONTENT,COL_IMPORTANT}, COL_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null
        );
        if(cursor !=null)
            cursor.moveToFirst();

        return new Main(
                cursor.getInt(INDEX_ID),
                cursor.getString(INDEX_CONTENT),
                cursor.getInt(INDEX_IMPORTANT)
        );
    }

    public Cursor fetchAllReminders(){
        Cursor mCursor = mDb.query(TABLE_NAME, new String[]{COL_ID,
                        COL_CONTENT,COL_IMPORTANT},
                null, null, null, null, null
        );

        if(mCursor != null){
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public void updateReminder(Main reminder){
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT, reminder.getContent());
        values.put(COL_IMPORTANT, reminder.getImportant());
        mDb.update(TABLE_NAME, values,
                COL_ID + "=?", new String[]{String.valueOf(reminder.getId())});

    }

    public void deleteReminderById(int nId){
        mDb.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(nId)});
    }

    public void deleteAllReminders() {
        mDb.delete(TABLE_NAME, null, null);
    }




    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old date");
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
}
