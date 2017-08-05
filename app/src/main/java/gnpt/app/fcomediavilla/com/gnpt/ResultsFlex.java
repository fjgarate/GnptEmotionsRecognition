package gnpt.app.fcomediavilla.com.gnpt;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

/**
 * Created by fcome on 25/04/2017.
 */

public class ResultsFlex extends AppCompatActivity {

    TextView percentage, corrects, mistakes, omissions, scores, totalTime, averageTimes, percentageGraphic;
    String percentage_str, corrects_str, mistakes_str, omissions_str, score_str, time_str, averageTime_str, idUser;
    int correct, mistake, omission, score, timer, percentageInt, framesDetected, framesNoFace;
    double averageTime;
    ProgressBar pbPercentage;
    private AsyncTask mTask;
    ArrayList<EmotionDetected> emotion = new ArrayList<>();
    double att_average, val_average, eng_average;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_flex);

        percentage = (TextView) findViewById(R.id.percentage_Flex);
        corrects = (TextView) findViewById(R.id.corrects_Flex);
        mistakes = (TextView) findViewById(R.id.mistakes_Flex);
        omissions = (TextView) findViewById(R.id.omissions_Flex);
        totalTime = (TextView) findViewById(R.id.total_time_Flex);
        scores = (TextView) findViewById(R.id.score_Flex);
        averageTimes = (TextView) findViewById(R.id.average_time_Flex);
        pbPercentage = (ProgressBar) findViewById(R.id.pbPercentage_Flex);
        percentageGraphic = (TextView) findViewById(R.id.percentage_graphic_Flex);

        correct = getIntent().getIntExtra("Corrects", 0);                          //Recibimos el número de respuestas correctas
        mistake = getIntent().getIntExtra("Mistakes", 0);                          //Recibimos el número de respuestas incorrectas
        omission = getIntent().getIntExtra("Omissions", 0);
        score = correct - mistake;
        if(score<0){score = 0;}
        timer = getIntent().getIntExtra("Time", 0);                                //Recibimos el tiempo total empleado
        averageTime = getIntent().getDoubleExtra("AverageTime", 0);                //Recibimos el tiempo medio de reacción empleado
        emotion = (ArrayList<EmotionDetected>) getIntent().getSerializableExtra("EmotionResult");
        framesDetected = getIntent().getIntExtra("FramesDetected", 0);
        framesNoFace = getIntent().getIntExtra("FramesNoFace", 0);

        percentage_str = getResources().getString(R.string.percentage) + " " + getPercentage(correct);     //Cadena para el texto del porcentaje
        corrects_str = getResources().getString(R.string.corrects) + " " + correct;                        //Cadena para el texto de aciertos
        mistakes_str = getResources().getString(R.string.mistakes) + " " + mistake;                        //Cadena para el texto de errores
        omissions_str = getResources().getString(R.string.omissions) + " " + omission;                     //Cadena para el texto de omisiones
        score_str = getResources().getString(R.string.score) + " " + score;          //Cadena para el texto de puntuación
        time_str = getResources().getString(R.string.time) + " " + timer + " s";                           //Cadena para el texto del tiempo total
        averageTime_str = getResources().getString(R.string.average_time) + " " + averageTime + " s";      //Cadena para el texto del tiempo promedio de reacción

        for (EmotionDetected i : emotion) {
            att_average = (double) i.attention + att_average;
            eng_average = (double) i.engagement + eng_average;
            val_average = (double) i.valence + val_average;
        }
        att_average = Math.round(att_average/(framesDetected+framesNoFace));
        eng_average = Math.round(eng_average/framesDetected);
        val_average = Math.round(val_average/framesDetected);

        percentage.setText(percentage_str);
        corrects.setText(corrects_str);
        mistakes.setText(mistakes_str);
        omissions.setText(omissions_str);
        scores.setText(score_str);
        totalTime.setText(time_str);
        averageTimes.setText(averageTime_str);

        /*Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        String date = d.toString();*/

        if(Register.justRegistered == true){
            idUser = Register.idUser;
        }else{
            idUser = Login.idUser;
        }

        Results newResults = new Results(idUser, getCurrentTimeStamp(), Login.idSesion, Login.numSesion, "Flexibility", percentageInt, correct, mistake, omission, score, timer, averageTime, -1, -1, framesDetected, framesNoFace, emotion);
        sendNewResults(newResults);
    }

    public String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }
    /*
     * Método que recibe el número entero de respuestas correcta
     * y devuelve una String con el porcentaje de aciertos
     */
    public String getPercentage(int correct){
        percentageInt = Math.round(((float)correct/(correct+mistake+omission))*100);
        return String.valueOf(percentageInt) + " %";
    }

    /*
     * Método escuchador de eventos. Botón salir.
     *
     * LLama a la actividad principal, es decir, nos lleva al índice inicial
     */
    public void toIndex(View view){
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /*
     * Método sobreescrito para el botón Back.
     *
     * Se cancela la hebra y vuelves al index.
     */
    @Override
    public void onBackPressed() {
        if(mTask != null){
            mTask.cancel(true);
        }
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }


    /*
     * Método que prepara los resultados para el envío.
     *
     * Se transforman los resultados a datos JSON y se define la URL del servidor.
     * A continuación se inicia la hebra en segundo plano que se encarga del envío
     * de datos y de la creación de la barra de progeso de resultados
     */
    public void sendNewResults(Results newResults){
        //Obtenemos los datos del Articles en formato JSON
        String strJson = newResults.toJSON();
        //Se define la URL del servidor a la cual se enviarán lso datos
        //DEPARTAMENTO
        //String baseUrl = "http://138.4.10.143:8443/saveresults";
        //NECN
        //String baseUrl = "http://192.168.0.189:8443/saveresults";
        //MOVIL
        String baseUrl = "http://192.168.43.119:8443/saveresults";
        //Se ejecuta la peticion Http POST empleando AsyncTAsk
        new ProgressFlex().execute(baseUrl, strJson);
    }



    /*
     * Método que indica cuál ha sido el resultado del envío.
     *
     * Si la respuesta del servidor es correcta escribe en
     * pantalla "Envío correcto", en caso contrario, "Error"
     */
    public void processResult(String result) {
        if (result.equals("OK")) {
            Toast.makeText(getApplicationContext(), "Envío correcto", Toast.LENGTH_LONG).show();

            // Si el resultado se ha guardado bien, enviamos lo que este guardado en local, si hay algo
            Database mDbHelper = new Database(getApplicationContext());

            // Gets the data repository in write mode
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            if (db != null) {
                try {
                    String queryResults = String.format("SELECT * FROM %s", Contract.ResultsTable.TABLE_NAME);
                    Cursor cursorResults = db.rawQuery(queryResults, null);
                    int contadorResults = cursorResults.getCount();
                    if (contadorResults > 0) {
                        Toast.makeText(getApplicationContext(), "Se están enviando datos desde la base de datos local.", Toast.LENGTH_LONG).show();
                        ArrayList<String> envio = new ArrayList<>();
                        cursorResults.moveToFirst();
                        Log.i("PruebaDatabase", "Columnas ResultsTable: " + contadorResults);
                        Log.i("PruebaDatabase", "Position cursorResults: " + cursorResults.getPosition());
                        while (cursorResults.isAfterLast() == false) {
                            Log.i("PruebaDatabase", "Primer campo de results (user_name): " + cursorResults.getString(0));
                            //Log.i("PruebaDatabase", "Primer campo de results (user_name): " + cursorResults.getColumnIndex(Contract.ResultsTable._ID));
                            String user_name_db = cursorResults.getString(0);
                            String date_db = cursorResults.getString(1);
                            String session_id_db = cursorResults.getString(2);
                            int session_num_db = cursorResults.getInt(3);
                            String task_db = cursorResults.getString(4);
                            int percentage_db = cursorResults.getInt(5);
                            int correct_db = cursorResults.getInt(6);
                            int mistaken_db = cursorResults.getInt(7);
                            int omitted_db = cursorResults.getInt(8);
                            int score_db = cursorResults.getInt(9);
                            int time_total_db = cursorResults.getInt(10);
                            double time_average_db = cursorResults.getDouble(11);
                            int persevering_mistakes_db = cursorResults.getInt(12);
                            int completed_category_db = cursorResults.getInt(13);
                            int frames_detected_db = cursorResults.getInt(14);
                            int frames_noface_db = cursorResults.getInt(15);
                            int rowid = (int) cursorResults.getLong(cursorResults.getColumnIndex(Contract.ResultsTable._ID));
                            ArrayList<EmotionDetected> emotion_db = new ArrayList<>();

                            String queryEmotion = String.format("SELECT * FROM %s WHERE %s = %2d", Contract.EmotionTable.TABLE_NAME, Contract.EmotionTable.RESULT_ID, rowid);
                            Log.i("PruebaDatabase", queryEmotion);
                            Cursor cursorEmotion = db.rawQuery(queryEmotion, null);
                            int contadorEmotion = cursorEmotion.getCount();
                            cursorEmotion.moveToFirst();
                            Log.i("PruebaDatabase", "Columnas EmotionTable: " + contadorEmotion);
                            Log.i("PruebaDatabase", "Position cursorEmotion: " + cursorEmotion.getPosition());

                            while (cursorEmotion.isAfterLast() == false) {
                                EmotionDetected eachEmotion = new EmotionDetected();
                                eachEmotion.attention = cursorEmotion.getFloat(1);
                                eachEmotion.anger = cursorEmotion.getFloat(2);
                                eachEmotion.contempt = cursorEmotion.getFloat(3);
                                eachEmotion.disgust = cursorEmotion.getFloat(4);
                                eachEmotion.engagement = cursorEmotion.getFloat(5);
                                eachEmotion.fear = cursorEmotion.getFloat(6);
                                eachEmotion.joy = cursorEmotion.getFloat(7);
                                eachEmotion.sadness = cursorEmotion.getFloat(8);
                                eachEmotion.surprise = cursorEmotion.getFloat(9);
                                eachEmotion.valence = cursorEmotion.getFloat(10);
                                eachEmotion.view = cursorEmotion.getInt(11);
                                emotion_db.add(eachEmotion);
                                cursorEmotion.moveToNext();
                            }
                            Results resultFromDB = new Results(user_name_db, date_db, session_id_db, session_num_db, task_db, percentage_db, correct_db, mistaken_db, omitted_db, score_db, time_total_db, time_average_db, persevering_mistakes_db, completed_category_db, frames_detected_db, frames_noface_db, emotion_db);
                            String resultFromDBJson = resultFromDB.toJSON();
                            envio.add(resultFromDBJson);

                            cursorResults.moveToNext();
                        }

                        String resultFromDBJson = "";
                        for (String i : envio) {
                            resultFromDBJson += String.format("%s,", i);
                        }
                        resultFromDBJson = "[" + resultFromDBJson.substring(0, resultFromDBJson.length() - 1) + "]";
                        Log.i("PruebaDatabase", resultFromDBJson);

                        //DEPARTAMENTO
                        //String baseUrl = "http://138.4.10.143:8443/enviofromlocal";
                        //NECN
                        //String baseUrl = "http://192.168.0.189:8443/enviofromlocal";
                        //CASA
                        //String baseUrl = "http://192.168.0.161:8443/enviofromlocal";
                        //MOVIL
                        String baseUrl = "http://192.168.43.119:8443/enviofromlocal";

                        new ProgressFlex().execute(baseUrl, resultFromDBJson);
                    }
                }catch (Exception e) {
                    Log.e("ResultsPhotos ", "Error while saving result: " + e.toString());
                    Log.i("PruebaDatabase ", "Error while upgrading database: " + e.toString());
                    e.printStackTrace();
                }
                db.close();
            }

        } else if (result.equals("OK2")){
            Toast.makeText(getApplicationContext(), "Envío desde base de datos local correcto.", Toast.LENGTH_LONG).show();

            // Si el resultado se ha guardado bien, enviamos lo que este guardado en local, si hay algo
            Database mDbHelper = new Database(getApplicationContext());

            // Gets the data repository in write mode
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            if (db != null) {
                try{
                    mDbHelper.onUpgrade(db, 1, 2);
                } catch (Exception e) {
                    Log.e("ResultsPhotos ", "Error while saving result: " + e.toString());
                    Log.i("PruebaDatabase ", "Error while upgrading database: " + e.toString());
                    e.printStackTrace();
                }
                db.close();
            }
        }else{
            long resultId;
            Database mDbHelper = new Database(getApplicationContext());

            // Gets the data repository in write mode
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            if (db != null) {
                //mDbHelper.onUpgrade(db,1,2);
                try {
                    //mDbHelper.onUpgrade(db,1,2);
                    // Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put(Contract.ResultsTable.USER_NAME, idUser);
                    values.put(Contract.ResultsTable.DATE, getCurrentTimeStamp());
                    values.put(Contract.ResultsTable.SESSION_ID, Login.idSesion);
                    values.put(Contract.ResultsTable.SESSION_NUM, Login.numSesion);
                    values.put(Contract.ResultsTable.TASK, "Flexibility");
                    values.put(Contract.ResultsTable.PERCENTAGE, percentageInt);
                    values.put(Contract.ResultsTable.CORRECT, correct);
                    values.put(Contract.ResultsTable.MISTAKEN, mistake);
                    values.put(Contract.ResultsTable.OMITTED, omission);
                    values.put(Contract.ResultsTable.SCORE, score);
                    values.put(Contract.ResultsTable.TIME_TOTAL, timer);
                    values.put(Contract.ResultsTable.TIME_AVERAGE, averageTime);
                    values.put(Contract.ResultsTable.PERSEVERING_MISTAKES, -1);
                    values.put(Contract.ResultsTable.COMPLETED_CATEGORY, -1);
                    values.put(Contract.ResultsTable.FRAMES_DETECTED, framesDetected);
                    values.put(Contract.ResultsTable.FRAMES_NOFACE, framesNoFace);

                    // Insert the new row, returning the primary key value of the new row
                    resultId = db.insert(Contract.ResultsTable.TABLE_NAME, null, values);
                    if (resultId != -1) {
                        Toast.makeText(getApplicationContext(), "Se ha encontrado un problema en su conexión. Envío correcto a base de datos local.", Toast.LENGTH_SHORT).show();
                        Log.i("PruebaDatabase ", "New result inserted in results table with id " + resultId);
                    } else {
                        Log.i("PruebaDatabase ", "Error when inserting result in results table");
                    }

                } catch (Exception e) {
                    Log.e("ResultsPhotos ", "Error while saving result: " + e.toString());
                    Log.i("PruebaDatabase ", "Error while saving result: " + e.toString());
                    e.printStackTrace();
                    resultId = 0;
                }
                for (EmotionDetected i : emotion) {
                    try {
                        // Create a new map of values, where column names are the keys
                        ContentValues values = new ContentValues();
                        values.put(Contract.EmotionTable.RESULT_ID, resultId);
                        values.put(Contract.EmotionTable.ATTENTION, i.attention);
                        values.put(Contract.EmotionTable.ANGER, i.anger);
                        values.put(Contract.EmotionTable.CONTEMPT, i.contempt);
                        values.put(Contract.EmotionTable.DISGUST, i.disgust);
                        values.put(Contract.EmotionTable.ENGAGEMENT, i.engagement);
                        values.put(Contract.EmotionTable.FEAR, i.fear);
                        values.put(Contract.EmotionTable.JOY, i.joy);
                        values.put(Contract.EmotionTable.SADNESS, i.sadness);
                        values.put(Contract.EmotionTable.SURPRISE, i.surprise);
                        values.put(Contract.EmotionTable.VALENCE, i.valence);
                        values.put(Contract.EmotionTable.VIEW, i.view);

                        // Insert the new row, returning the primary key value of the new row
                        long newRowId = db.insert(Contract.EmotionTable.TABLE_NAME, null, values);
                        if (newRowId != -1) {
                            Log.i("PruebaDatabase ", "New emotion inserted in emotion_result table with id " + newRowId);
                        } else {
                            Log.i("PruebaDatabase ", "Error while saving emotion result");
                        }

                    } catch (Exception e) {
                        Log.e("PruebaDatabase ", "Error while saving emotion result: " + e.toString());
                        Log.i("PruebaDatabase ", "Error while saving emotion result: " + e.toString());
                        e.printStackTrace();
                    }
                }
                db.close();
            }
        }
    }


    /*
     * Clase privada para la creación de procesos en segundo plano.
     *
     * La utilizamos para la creación de la barra de progreso de los resultados
     */
    private class ProgressFlex extends AsyncTask<String, Integer, String> {
        int contador = 0;

        /*
         * Método que se ejecuta en la hebra principal antes de dar paso a la hebra en segundo plano.
         *
         * Inicializa el progreso en 0
         */
        protected void onPreExecute(){
            Log.i("Pruebas2" , "::: onPreExecute ");
            pbPercentage.setProgress(0);
        }

        /*
         * Método que se ejecuta en segundo plano tras onPreExcute().
         *
         * Se encarga de llenar la barra de progreso hasta el porcentaje
         * de aciertos que se ha conseguido con las respuestas
         */
        protected String doInBackground(String...params) {
            String baseUrl = params[0];
            String jsonData = params[1];
            BufferedReader br = null;
            JSONObject jsonObj = new JSONObject();

            Log.i("Pruebas2", "::: doInBackground ");
            if (contador == 0) {
                for (int i = 0; i < percentageInt/2+1; i++) {
                    try {
                        Log.i("ResultsTRLlega:" , " 3");
                        Thread.sleep(50);
                        publishProgress(i*2);                   //LLamada al método onProgressUpdate()
                    } catch (InterruptedException e) {
                        cancel(true);
                        e.printStackTrace();
                    }
                }
            }

            try{
                Log.i("PruebaDatabase:" , "Llega a try");
                //Send Http PUT request to: "http://some.url" with request header:

                URL url = new URL(baseUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(10000);
                con.setConnectTimeout(15000);
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("resultados", jsonData);
                String query = builder.build().getEncodedQuery();

                OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                wr.write(query);
                wr.flush();

                StringBuilder sb = new StringBuilder();
                int HttpResult = con.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    br = new BufferedReader(
                            new InputStreamReader(con.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    jsonObj = new JSONObject(sb.toString());
                    //con.disconnect();
                    Log.i("PruebaDatabase" , "Resultado servirdor "+sb.toString());
                    System.out.println("" + sb.toString());
                } else {
                    Log.i("PruebaDatabase" , "Problema servidor "+con.getResponseMessage());
                    System.out.println(con.getResponseMessage());
                }
                contador++;
                return jsonObj.getString("msg");

            } catch (Exception e) {
                Log.i("PruebaDatabase" , "Error servidor "+e.getMessage());
                return "Exception happened: " + e.getMessage();
            }
        }

        /*
         * Método que se ejecuta en la hebra principal. Su ejecución se empieza al finalizar doInBackground().
         *
         * Establece el texto con el porcentaje que se encuentra en medio de la barra de progreso
         */
        protected void onPostExecute(String result){
            Log.i("Pruebas2" , "::: onPostExecute ");
            percentageGraphic.setText(getPercentage(correct));
            //Se obtiene el resultado de la peticion Asincrona
            processResult(result);
        }

        /*
         * Método que se ejecuta en primer plano. Puede ser llamado desde doInBackground() en cualquier momento.
         *
         * Rellena nuestra vista de la barra de progreso en cada llamada hasta el entero que le pasan como parámetro.
         */
        protected void onProgressUpdate(Integer... view){
            pbPercentage.setProgress(view[0]);
        }
    }


}


