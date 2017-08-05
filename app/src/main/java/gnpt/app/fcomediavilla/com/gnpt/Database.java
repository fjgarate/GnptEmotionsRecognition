package gnpt.app.fcomediavilla.com.gnpt;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by alvaro on 10/07/2017.
 */

public class Database extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "gnpt_results";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i("PruebaDatabase", "Llega al constructor del SQLHelper...");
    }
    public void onCreate(SQLiteDatabase db) {
        Log.i("PruebaDatabase", "Llega al onCreate del SQLHelper...");
        db.execSQL(Contract.SQL_CREATE_USER_TABLE);
        db.execSQL(Contract.SQL_CREATE_RESULT_TABLE);
        db.execSQL(Contract.SQL_CREATE_EMOTION_TABLE);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(Contract.SQL_DELETE_RESULT_TABLE);
        db.execSQL(Contract.SQL_DELETE_EMOTION_TABLE);
        db.execSQL(Contract.SQL_CREATE_RESULT_TABLE);
        db.execSQL(Contract.SQL_CREATE_EMOTION_TABLE);
    }

    /*public void byebyeUser (SQLiteDatabase db, int id){
        db.execSQL(String.format());
    }*/

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}