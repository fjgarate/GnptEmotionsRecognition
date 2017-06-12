package gnpt.app.fcomediavilla.com.gnpt;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by Francisco Mediavilla Sala
 *
 * Clase para enunciado de Textmemory1
 * Contiene tanto el enunciado de la tarea Textmemory1 como el texto a recordar.
 */

public class StatementTM1 extends AppCompatActivity {

    LinearLayout layout1;
    RelativeLayout layout2;
    boolean userChooseContinue  = false; //Se pondrá a true cuando el usuario elija continuar y dar paso a las preguntas
    private AsyncTask myTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statement_textm1);

        Log.i("Pruebas2" , "::: onCreate ");
        layout1 = (LinearLayout)findViewById(R.id.statement_tm1);
        layout2 = (RelativeLayout)findViewById(R.id.text_tm1);
    }

    /*
     * Método escuchador de eventos. Botón continuar blanco.
     *
     * Da visibilidad al segundo layout (el que contiene el texto a recordar).
     * Se empieza a ejecutar la hebra que cuenta el tiempo máximo hasta saltar
     * automáticamente a las preguntas
     */
    public void continuar(View view){
        layout2.setVisibility(View.VISIBLE);
        layout1.setVisibility(View.GONE);
        myTask = new Flujo().execute();
    }

    /*
     * Método escuchador de eventos. Botón continuar amarillo.
     *
     * Pone a true el booleano userChooseContinue para ir a las preguntas.
     */
    public void toClock(View v){
        userChooseContinue = true;
    }


    /*
     * Método escuchador de eventos. Botón Salir.
     *
     * Cancela la hebra y salta a los resultados.
     */
    public void toResults(View v){
        myTask.cancel(true);
        final Intent intent = new Intent(StatementTM1.this, ResultsTM.class);
        intent.putExtra("Corrects", 0);  //Pasa el número de respuestas correctas a la Activity ResultsTM
        intent.putExtra("Mistakes", 0);  //Pasa el número de respuestas incorrectas a la Activity ResultsTM
        startActivity(intent);
    }

    /*
     * Método sobreescrito para el botón Back.
     *
     * Se cancela la hebra y vuelves al index.
     */
    @Override
    public void onBackPressed() {
        if(myTask != null){
            myTask.cancel(true);
        }
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }



    /*
     * Clase privada para la creación de procesos en segundo plano
     */
    private class Flujo extends AsyncTask<Void, Void, Integer> {

        double time4continue = 20;

        protected void onPreExecute(){
            Log.i("Pruebas2" , "::: onPreExecute ");
        }

        /*
         * Método que se ejecuta en la hebra en segundo plano.
         *
         * Comprueba si el usuario pulsa continuar antes de que acabe el timer.
         */
        protected Integer doInBackground(Void...params){

            Log.i("Pruebas2" , "::: doInBackground ");
            double seconds = 0;

            while(!isCancelled() && !userChooseContinue && seconds<time4continue){
                try{
                    Thread.sleep(100);
                } catch (InterruptedException e){
                    cancel(true);
                    e.printStackTrace();
                }
                seconds+=0.1;
            }

            return 1;
        }

        /*
         * Método que se ejecuta en primer plano. Se ejecutará después de doInBackground()
         *
         * Una vez terminado el trabajo de la hebra en segundo plano se pasa
         * a la Activity Clock antes de acceder a las preguntas
         */
        protected void onPostExecute(Integer result){
            Log.i("Pruebas2" , "::: onPostExecute ");
            final Intent intent = new Intent(StatementTM1.this, Clock.class);
            startActivity(intent);
        }

    }


}
