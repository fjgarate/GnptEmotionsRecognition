package gnpt.app.fcomediavilla.com.gnpt;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by fcome on 24/04/2017.
 */

public class Flexibility extends AppCompatActivity {

    int numbersList[] = {5,1,6,6,5,6,5,3,7,1,5,1,6,5,6,4,5,8,1,7,2,5,6,5,2,7,5,4,2,6,5,6,3,5,9,1,5,3,8,3,6,5,7,2,5,8,2,5,3,8,5,2,7,2,2,9,4,5,9,5};
    int color1 = -143338;
    int color2 = -16731920;
    int randomColor[] = {color1,color2};
    int numberPointer,correct, mistake, omission, recuento = 0;
    double averageTime = 0;
    boolean userChooseSomething, cancelation = false;
    TextView number;
    GradientDrawable backCircle;
    ImageButton leftArrow, rigthArrow, redCircle;
    private AsyncTask myTask;
    private long start, end;
    ArrayList<Double> tReaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flexibility);

        number = (TextView) findViewById(R.id.numbers_flexibility);
        backCircle = (GradientDrawable) number.getBackground();
        leftArrow = (ImageButton) findViewById(R.id.left_arrow_flexibility);
        rigthArrow = (ImageButton) findViewById(R.id.right_arrow_flexibility);
        redCircle = (ImageButton) findViewById(R.id.small_circle_flexibility);

        leftArrow.setTag(false);
        rigthArrow.setTag(false);
        redCircle.setTag(false);

        Log.i("Prueba" , "::: codigo color: " + getResources().getColor(R.color.fillCircle));
        Log.i("Prueba" , "::: codigo color: " + getResources().getColor(R.color.fillCircle2));

        myTask = new Flujo().execute();

    }




    public void buttonPressed (View view){
        if ((boolean)view.getTag()){
            correct++;
        }
        else{ mistake++;}

        leftArrow.setEnabled(false);
        rigthArrow.setEnabled(false);
        redCircle.setEnabled(false);

        userChooseSomething = true;
        Log.i("Prueba" , "::: correctas: " + correct + "incorrectas: " + mistake);
    }

    public void correctButton(int number){
        if (number<5){
            leftArrow.setTag(true);
            rigthArrow.setTag(false);
            redCircle.setTag(false);
        }
        else if (number == 5){
            leftArrow.setTag(false);
            rigthArrow.setTag(false);
            redCircle.setTag(true);
        }
        else{
            leftArrow.setTag(false);
            rigthArrow.setTag(true);
            redCircle.setTag(false);
        }
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
        omission = recuento - correct - mistake;
        end = android.os.SystemClock.uptimeMillis();
        double timer = (end - start)/1000;
        double sum = 0;
        for (Double i : tReaction){
            sum += i;
        }
        averageTime = sum/tReaction.size();
        averageTime = (double) Math.round(averageTime*100)/100;
        Log.i("Pruebas2" , "::: boton exit ");
        final Intent intent = new Intent(Flexibility.this, ResultsFlex.class);
        intent.putExtra("Corrects", correct);
        intent.putExtra("Mistakes", mistake);
        intent.putExtra("Omissions", omission);
        intent.putExtra("Time", (int)timer);
        intent.putExtra("AverageTime", averageTime);
        startActivity(intent);
    }


    private class Flujo extends AsyncTask<Void, Integer, Integer>{

        double time4answer = 3;
        Random r = new Random();
        int colorPointer;

        /*
         * Método que se ejecuta en la hebra principal antes de dar paso a la hebra en segundo plano.
         *
         * Crea un ArrayList de enteros de 8 posiciones que será empleado para seleccionar cada uno de
         * los 8 posibles layouts de preguntas de forma aleatoria
         */
        @Override
        protected void onPreExecute(){
            start = android.os.SystemClock.uptimeMillis();
            tReaction = new ArrayList<>();
        }


        @Override
        protected Integer doInBackground(Void... voids) {
            for(int i = 0; i<numbersList.length; i++){
                if (cancelation) break;
                colorPointer = r.nextInt(2);
                publishProgress(0);
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
                recuento++;
                if (userChooseSomething){
                    tReaction.add(seconds);
                }
                userChooseSomething = false;
                numberPointer++;
                publishProgress(1);
                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e){
                    cancel(true);
                    e.printStackTrace();
                }
            }
            double sum = 0;
            for (Double i : tReaction){
                sum += i;
            }
            averageTime = sum/tReaction.size();
            return null;
        }

        /*
         * Método que se ejecuta en la hebra principal. Su ejecución se empieza al finalizar doInBackground().
         *
         * Llama a la Actividad ResultsTM, pasando los números de respuestas correctas e incorrectas
         */
        @Override
        protected void onPostExecute(Integer result){
            omission = recuento - correct - mistake;
            end = android.os.SystemClock.uptimeMillis();
            double timer = (end - start)/1000;
            averageTime = (double) Math.round(averageTime*100)/100;
            Log.i("Pruebas2" , "::: PostExecute ");
            final Intent intent = new Intent(Flexibility.this, ResultsFlex.class);
            intent.putExtra("Corrects", correct);
            intent.putExtra("Mistakes", mistake);
            intent.putExtra("Omissions", omission);
            intent.putExtra("Time", (int)timer);
            intent.putExtra("AverageTime", averageTime);
            startActivity(intent);
        }

        @Override
        protected void onProgressUpdate(Integer... view){
            switch (view[0]){
                case 0:
                    leftArrow.setEnabled(true);
                    rigthArrow.setEnabled(true);
                    redCircle.setEnabled(true);
                    number.setText(String.valueOf(numbersList[numberPointer]));
                    correctButton(numbersList[numberPointer]);
                    backCircle.setColor(randomColor[colorPointer]);
                    break;
                case 1:
                    number.setText("");
                    backCircle.setColor(getResources().getColor(R.color.BackColorFlexibility));
            }

        }

        /*
         * Método que se ejecuta en primer plano. Se ejecutará después de doInBackground()
         * sustituyendo a onPostExecute(). Solo se ejecuta en el caso de que se haya cancelado
         * el proceso de la hebra.
         */
        protected void onCancelled(Integer cancel){
            Toast toast = Toast.makeText(getApplicationContext(), "Cancelado", Toast.LENGTH_LONG);
            toast.show();

        }
    }


}
