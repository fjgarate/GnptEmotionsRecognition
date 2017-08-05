package gnpt.app.fcomediavilla.com.gnpt;

import android.provider.BaseColumns;

/**
 * Created by alvaro on 10/07/2017.
 */

public final class Contract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private Contract() {}

    /* Inner class that defines the table contents */
    public static class UserTable implements BaseColumns {
        public static final String TABLE_NAME = "user";
        public static final String _ID = "_id";
        public static final String USER_NAME = "user_name";
    }
    /* Inner class that defines the table contents */
    public static class ResultsTable implements BaseColumns {
        public static final String TABLE_NAME = "results";
        public static final String USER_NAME = "user_name";
        public static final String DATE = "date";
        public static final String SESSION_ID = "session_id";
        public static final String SESSION_NUM = "session_num";
        public static final String TASK = "task";
        public static final String PERCENTAGE = "percentage";
        public static final String CORRECT = "correct";
        public static final String MISTAKEN = "mistaken";
        public static final String OMITTED = "omitted";
        public static final String SCORE = "score";
        public static final String TIME_TOTAL = "time_total";
        public static final String TIME_AVERAGE = "time_average";
        public static final String PERSEVERING_MISTAKES = "persevering_mistakes";
        public static final String COMPLETED_CATEGORY = "completed_category";
        public static final String FRAMES_DETECTED = "frames_detected";
        public static final String FRAMES_NOFACE = "frames_noface";
        public static final String _ID = "_id";
    }
    /* Inner class that defines the table contents */
    public static class EmotionTable implements BaseColumns {
        public static final String TABLE_NAME = "emotion_result";
        public static final String RESULT_ID = "result_id";
        public static final String ATTENTION = "attention";
        public static final String ANGER = "anger";
        public static final String CONTEMPT = "contempt";
        public static final String DISGUST = "disgust";
        public static final String ENGAGEMENT = "engagement";
        public static final String FEAR = "fear";
        public static final String JOY = "joy";
        public static final String SADNESS = "sadness";
        public static final String SURPRISE = "surprise";
        public static final String VALENCE = "valence";
        public static final String VIEW = "view";
        public static final String _ID = "_id";
    }

    // consultas para crear y eliminar la tabla user
    public static final String SQL_CREATE_USER_TABLE =
            "CREATE TABLE " + UserTable.TABLE_NAME + " (" +
                    UserTable._ID + "  INTEGER PRIMARY KEY AUTOINCREMENT," +
                    UserTable.USER_NAME + " TEXT)";

    public static final String SQL_DELETE_USER_TABLE =
            "DROP TABLE IF EXISTS " + UserTable.TABLE_NAME;

    public static final String SQL_SELECT_USER_TABLE =
            "SELECT * FROM TABLE " + UserTable.TABLE_NAME;

    // consultas para crear y eliminar la tabla results
    public static final String SQL_CREATE_RESULT_TABLE =
            "CREATE TABLE " + ResultsTable.TABLE_NAME + " (" +
                    ResultsTable.USER_NAME + " TEXT," +
                    ResultsTable.DATE + " TEXT," +
                    ResultsTable.SESSION_ID + " TEXT," +
                    ResultsTable.SESSION_NUM + " INTEGER," +
                    ResultsTable.TASK + " TEXT," +
                    ResultsTable.PERCENTAGE + " INTEGER," +
                    ResultsTable.CORRECT + " INTEGER," +
                    ResultsTable.MISTAKEN + " INTEGER," +
                    ResultsTable.OMITTED + " INTEGER," +
                    ResultsTable.SCORE + " INTEGER," +
                    ResultsTable.TIME_TOTAL + " INTEGER," +
                    ResultsTable.TIME_AVERAGE + " REAL," +
                    ResultsTable.PERSEVERING_MISTAKES + " INTEGER," +
                    ResultsTable.COMPLETED_CATEGORY + " INTEGER," +
                    ResultsTable.FRAMES_DETECTED + " INTEGER," +
                    ResultsTable.FRAMES_NOFACE + " INTEGER," +
                    ResultsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT)";

    public static final String SQL_DELETE_RESULT_TABLE =
            "DROP TABLE IF EXISTS " + ResultsTable.TABLE_NAME;

    public static final String SQL_SELECT_RESULT_TABLE =
            "SELECT * FROM TABLE " + ResultsTable.TABLE_NAME;

    // consultas para crear y eliminar la tabla emotion_result
    public static final String SQL_CREATE_EMOTION_TABLE =
            "CREATE TABLE " + EmotionTable.TABLE_NAME + " (" +
                    EmotionTable.RESULT_ID + " INTEGER," +
                    EmotionTable.ATTENTION + " REAL," +
                    EmotionTable.ANGER + " REAL," +
                    EmotionTable.CONTEMPT + " REAL," +
                    EmotionTable.DISGUST + " REAL," +
                    EmotionTable.ENGAGEMENT + " REAL," +
                    EmotionTable.FEAR + " REAL," +
                    EmotionTable.JOY + " REAL," +
                    EmotionTable.SADNESS + " REAL," +
                    EmotionTable.SURPRISE + " REAL," +
                    EmotionTable.VALENCE + " REAL," +
                    EmotionTable.VIEW + " INTEGER," +
                    EmotionTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT)";

    public static final String SQL_DELETE_EMOTION_TABLE =
            "DROP TABLE IF EXISTS " + EmotionTable.TABLE_NAME;

}