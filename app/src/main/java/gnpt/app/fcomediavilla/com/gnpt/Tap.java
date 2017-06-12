package gnpt.app.fcomediavilla.com.gnpt;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;

/**
 * Created by fcome on 07/04/2017.
 */

public class Tap extends AppCompatActivity{

    String palabras[] = {"MUELLE", "CARNE", "CEBRA", "CANARIO", "MALETA", "MONO", "MURCIÉLAGO", "PANDA", "PANTERA", "CACEROLA", "PLÁTANO", "CLAVO", "MARTILLO", "CABALLO", "MESA", "PEINE", "MANZANA", "CUBO", "MAPACHE", "PAQUETE", "CIRUELA", "CACAHUETE", "MARISCO", "MANDARINA", "PIRAÑA", "PIMIENTO", "PEPINO", "CISNE", "PATATA"};
    String animales[] = {"CEBRA", "CANARIO", "MONO", "MURCIÉLAGO", "PANDA", "PANTERA", "CABALLO", "MAPACHE", "PIRAÑA", "CISNE"};
    String objetos[] = {"MUELLE", "MALETA", "CACEROLA", "CLAVO", "MARTILLO", "MESA", "PEINE", "CUBO", "PAQUETE"};
    String alimentos[] = {"CARNE", "PLÁTANO", "MANZANA", "CIRUELA", "CACAHUETE", "MARISCO", "MANDARINA", "PIMIENTO", "PEPINO", "PATATA"};

    boolean userChooseSomething, perMistakeOn, cancelation = false;
    public int wordPointer, correlacion, categoriesCompleted = 0;
    int correct, mistake, keptCorrect, perMistake = 0;
    Button btnword, btn1, btn2, btn3;
    TextView result;
    private long start, end;
    private AsyncTask myTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap);

        btnword = (Button) findViewById(R.id.button4_tap);
        btn1 = (Button) findViewById(R.id.button1_tap);
        btn2 = (Button) findViewById(R.id.button2_tap);
        btn3 = (Button) findViewById(R.id.button3_tap);
        result = (TextView) findViewById(R.id.textview_result_tap);

        myTask = new Flujo().execute();
    }

    public void userAnswerbtn1(View v){
        switch (correlacion){
            case 0:
                if (Arrays.asList(animales).contains(palabras[wordPointer])){
                    isCorrect();
                }
                else{isWrong();}
                break;
            case 1:
                if (palabras[wordPointer].startsWith("M")){
                    isCorrect();
                }
                else{isWrong();}
                break;
            case 2:
                if (palabras[wordPointer].endsWith("O")){
                    isCorrect();
                }
                else{isWrong();}
                break;
        }
        userChooseSomething = true;
    }

    public void userAnswerbtn2(View v){
        switch (correlacion){
            case 0:
                if (Arrays.asList(objetos).contains(palabras[wordPointer])){
                    isCorrect();
                }
                else{isWrong();}
                break;
            case 1:
                if (palabras[wordPointer].startsWith("P")){
                    isCorrect();
                }
                else{isWrong();}
                break;
            case 2:
                if (palabras[wordPointer].endsWith("E")){
                    isCorrect();
                }
                else{isWrong();}
                break;
        }
        userChooseSomething = true;
    }

    public void userAnswerbtn3(View v){
        switch (correlacion){
            case 0:
                if (Arrays.asList(alimentos).contains(palabras[wordPointer])){
                    isCorrect();
                }
                else{isWrong();}
                break;
            case 1:
                if (palabras[wordPointer].startsWith("C")){
                    isCorrect();
                }
                else{isWrong();}
                break;
            case 2:
                if (palabras[wordPointer].endsWith("A")){
                    isCorrect();
                }
                else{isWrong();}
                break;
        }
        userChooseSomething = true;
    }


    public void isCorrect(){
        correct++;
        perMistakeOn = false;
        keptCorrect++;
        result.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        result.setText(R.string.correct_word);
    }


    public void isWrong(){
        mistake++;
        if (perMistakeOn){perMistake++;}
        perMistakeOn = true;
        keptCorrect = 0;
        result.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        result.setText(R.string.wrong_word);
    }

    /*
     * Método escuchador de eventos. Botón salir.
     *
     * Cancela la hebra en segundo plano y salta a la Actividad ResultsPH,
     * pasando las respuestas correctas e incorrectas hasta el momento.
     */
    public void toResults(View view){
        cancelation = true;
        myTask.cancel(cancelation);
        end = android.os.SystemClock.uptimeMillis();
        double timer = (end - start)/1000;
        final Intent intent = new Intent(Tap.this, ResultsTap.class);
        intent.putExtra("Corrects", correct);
        intent.putExtra("Mistakes", mistake);
        intent.putExtra("Time", (int)timer);
        intent.putExtra("PerseveringMistakes", perMistake);
        intent.putExtra("CategoriesCompleted", categoriesCompleted);
        startActivity(intent);
    }



    private class Flujo extends AsyncTask<Void, Integer, Integer>{
        double time4answer = 10;
        int correct4change = 5;
        int numRelation = 3;
        int numCategories = 6;

        /*
         * Método que se ejecuta en la hebra principal antes de dar paso a la hebra en segundo plano.
         *
         * Crea un ArrayList de enteros de 8 posiciones que será empleado para seleccionar cada uno de
         * los 8 posibles layouts de preguntas de forma aleatoria
         */
        @Override
        protected void onPreExecute(){
            start = android.os.SystemClock.uptimeMillis();
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            for (int j = 0; j<2; j++){
                wordPointer = 0;
                for (int i = 0; i<29; i++){
                    if (cancelation) break;
                    publishProgress();
                    double seconds = 0;
                    while(!isCancelled() && !userChooseSomething && seconds<time4answer){

                        try{
                            Thread.sleep(100);
                        } catch (InterruptedException e){
                            cancel(true);
                            e.printStackTrace();
                        }
                        seconds+=0.1;
                    }

                    if (userChooseSomething){
                        try{
                            Thread.sleep(2000);
                        } catch (InterruptedException e){
                            cancel(true);
                            e.printStackTrace();
                        }
                        userChooseSomething = false;
                    }

                    wordPointer++;
                    if (keptCorrect>=correct4change){
                        correlacion++;
                        categoriesCompleted++;
                        keptCorrect = 0;
                        if (correlacion>=numRelation){
                            correlacion = 0;
                        }
                    }
                    if (categoriesCompleted>=numCategories) break;
                }
            }

            return null;
        }

        /*
         * Método que se ejecuta en la hebra principal. Su ejecución se empieza al finalizar doInBackground().
         *
         * Llama a la Actividad ResultsTM, pasando los números de respuestas correctas e incorrectas
         */
        @Override
        protected void onPostExecute(Integer result){
            end = android.os.SystemClock.uptimeMillis();
            double timer = (end - start)/1000;
            final Intent intent = new Intent(Tap.this, ResultsTap.class);
            intent.putExtra("Corrects", correct);
            intent.putExtra("Mistakes", mistake);
            intent.putExtra("Time", (int)timer);
            intent.putExtra("PerseveringMistakes", perMistake);
            intent.putExtra("CategoriesCompleted", categoriesCompleted);
            startActivity(intent);
        }

        @Override
        protected void onProgressUpdate(Integer... view){
            btnword.setText(palabras[wordPointer]);
            result.setText("");
        }
    }
}
