package gnpt.app.fcomediavilla.com.gnpt;

import android.content.Intent;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by fcome on 20/04/2017.
 */

public class ResultsTap extends AppCompatActivity {

    TextView percentage, corrects, mistakes, omissions, totalTime, scores, perseveringMistakes, categoriesCompleted, percentageGraphic;
    String percentage_str, corrects_str, mistakes_str, omissions_str, time_str, score_str, perseveringMistakes_str, categoriesCompleted_str;
    int correct, mistake, omission, timer, score, perseveringMistake, categoryCompleted, percentageInt;
    ProgressBar pbPercentage;
    private AsyncTask mTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_tap);

        percentage = (TextView) findViewById(R.id.percentage_Tap);
        corrects = (TextView) findViewById(R.id.corrects_Tap);
        mistakes = (TextView) findViewById(R.id.mistakes_Tap);
        omissions = (TextView) findViewById(R.id.omissions_Tap);
        totalTime = (TextView) findViewById(R.id.total_time_Tap);
        scores = (TextView) findViewById(R.id.score_Tap);
        perseveringMistakes = (TextView) findViewById(R.id.persevering_mistakes_Tap);
        pbPercentage = (ProgressBar) findViewById(R.id.pbPercentage_Tap);
        percentageGraphic = (TextView) findViewById(R.id.percentage_graphic_Tap);
        categoriesCompleted = (TextView) findViewById(R.id.completed_categories_Tap);

        correct = getIntent().getIntExtra("Corrects", 0);                          //Recibimos el número de respuestas correctas
        mistake = getIntent().getIntExtra("Mistakes", 0);                          //Recibimos el número de respuestas incorrectas
        omission = 0;
        score = correct - mistake;
        if(score<0){score = 0;}
        timer = getIntent().getIntExtra("Time", 0);                                //Recibimos el tiempo total empleado
        perseveringMistake = getIntent().getIntExtra("PerseveringMistakes", 0);
        categoryCompleted = getIntent().getIntExtra("CategoriesCompleted", 0);


        percentage_str = getResources().getString(R.string.percentage) + " " + getPercentage(correct);     //Cadena para el texto del porcentaje
        corrects_str = getResources().getString(R.string.corrects) + " " + correct;                        //Cadena para el texto de aciertos
        mistakes_str = getResources().getString(R.string.mistakes) + " " + mistake;                        //Cadena para el texto de errores
        omissions_str = getResources().getString(R.string.omissions) + " " + omission;                     //Cadena para el texto de omisiones
        time_str = getResources().getString(R.string.time) + " " + timer + " s";                           //Cadena para el texto del tiempo total
        score_str = getResources().getString(R.string.score) + " " + score;                              //Cadena para el texto de puntuación
        perseveringMistakes_str = getResources().getString(R.string.persevering_mistakes) + " " + perseveringMistake;      //Cadena para el texto del tiempo promedio de reacción
        categoriesCompleted_str = getResources().getString(R.string.completed_categories) + " " + categoryCompleted;

        percentage.setText(percentage_str);
        corrects.setText(corrects_str);
        mistakes.setText(mistakes_str);
        omissions.setText(omissions_str);
        totalTime.setText(time_str);
        scores.setText(score_str);
        perseveringMistakes.setText(perseveringMistakes_str);
        categoriesCompleted.setText(categoriesCompleted_str);


        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        String date = d.toString();

        Results newResults = new Results(Login.idUser, date, Login.idSesion, Login.numSesion, "Tap", percentageInt, correct, mistake, omission, score, timer, -1, perseveringMistake, categoryCompleted);
        sendNewResults(newResults);

    }

    /*
     * Método que recibe el número entero de respuestas correcta
     * y devuelve una String con el porcentaje de aciertos
     */
    public String getPercentage(int correct){
        percentageInt = Math.round(((float)correct/(correct+mistake))*100);
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
        String baseUrl = "http://192.168.1.115:8080/Servidor/Resultados";
        //Se ejecuta la peticion Http POST empleando AsyncTAsk
        new ProgressTap().execute(baseUrl, strJson);
    }


    /*
     * Método que indica cuál ha sido el resultado del envío.
     *
     * Si la respuesta del servidor es correcta escribe en
     * pantalla "Envío correcto", en caso contrario, "Error"
     */
    public void processResult(String result) {
        if (result.contains("OK")) {
            Toast.makeText(getApplicationContext(), "Envío correcto", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Error" + result, Toast.LENGTH_LONG).show();
        }
    }


    /*
     * Clase privada para la creación de procesos en segundo plano.
     *
     * La utilizamos para la creación de la barra de progreso de los resultados
     */
    private class ProgressTap extends AsyncTask<String, Integer, String> {


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
            BufferedReader in = null;

            Log.i("Pruebas2", "::: doInBackground ");
            for (int i = 0; i < percentageInt/2+1; i++) {
                try {
                    Thread.sleep(50);
                    publishProgress(i*2);                   //LLamada al método onProgressUpdate()
                } catch (InterruptedException e) {
                    cancel(true);
                    e.printStackTrace();
                }
            }

            try{
                //Creamos un objeto Cliente HTTP para manejar la peticion al servidor
                HttpClient httpClient = new DefaultHttpClient();
                //Creamos objeto para armar peticion de tipo HTTP POST
                HttpPost post = new HttpPost(baseUrl);
                //Configuramos los parametos que vaos a enviar con la peticion HTTP POST
                List<NameValuePair> nvp = new ArrayList<>(2);
                nvp.add(new BasicNameValuePair("resultados",jsonData));
                //post.setHeader("Content-type", "application/json");
                post.setEntity(new UrlEncodedFormEntity(nvp));
                //Se ejecuta el envio de la peticion y se espera la respuesta de la misma.
                HttpResponse response = httpClient.execute(post);
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                StringBuffer sb = new StringBuffer("");
                String line = "";
                String NL = System.getProperty("line.separator");
                while ((line = in.readLine()) != null) {
                    sb.append(line + NL);
                }
                in.close();
                return sb.toString();
            } catch (Exception e) {
                return "Exception happened: " + e.getMessage();
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
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
