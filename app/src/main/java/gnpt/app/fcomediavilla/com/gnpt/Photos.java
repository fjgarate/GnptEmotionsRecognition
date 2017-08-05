package gnpt.app.fcomediavilla.com.gnpt;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SalmonManin on 09/03/2017.
 */

public class Photos extends AppCompatActivity implements CameraDetector.CameraEventListener,Detector.ImageListener{

    ImageButton rectangle10, rectangle11, rectangle12, rectangle13, rectangle14, rectangle15, rectangle16, rectangle17, rectangle18, rectangle19;
    ImageView image;
    boolean userChooseSomething, cancelation = false;
    public int index = 0;
    public int correct, mistake = 0;
    private AsyncTask myTask;
    private long start, end;

    //----- Affectiva
    int previewWidth = 0;
    int previewHeight = 0;
    RelativeLayout mainLayout;
    CameraDetector detector;
    SurfaceView cameraPreview;

    // detecting attention and all emotions
    int contDetected = 0;
    int contNoFace = 0;
    ArrayList<EmotionDetected> emotionAllFrames = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos);


        rectangle10 = (ImageButton)findViewById(R.id.rectangle_button1);
        rectangle11 = (ImageButton)findViewById(R.id.rectangle_button2);
        rectangle12 = (ImageButton)findViewById(R.id.rectangle_button3);
        rectangle13 = (ImageButton)findViewById(R.id.rectangle_button4);
        rectangle14 = (ImageButton)findViewById(R.id.rectangle_button5);
        rectangle15 = (ImageButton)findViewById(R.id.rectangle_button6);
        rectangle16 = (ImageButton)findViewById(R.id.rectangle_button7);
        rectangle17 = (ImageButton)findViewById(R.id.rectangle_button8);
        rectangle18 = (ImageButton)findViewById(R.id.rectangle_button9);
        rectangle19 = (ImageButton)findViewById(R.id.rectangle_button10);

        rectangle10.setOnClickListener(listener);
        rectangle11.setOnClickListener(listener);
        rectangle12.setOnClickListener(listener);
        rectangle13.setOnClickListener(listener);
        rectangle14.setOnClickListener(listener);
        rectangle15.setOnClickListener(listener);
        rectangle16.setOnClickListener(listener);
        rectangle17.setOnClickListener(listener);
        rectangle18.setOnClickListener(listener);
        rectangle19.setOnClickListener(listener);

        rectangle10.setVisibility(View.GONE);
        rectangle11.setVisibility(View.GONE);
        rectangle12.setVisibility(View.GONE);
        rectangle13.setVisibility(View.GONE);
        rectangle14.setVisibility(View.GONE);
        rectangle15.setVisibility(View.GONE);
        rectangle16.setVisibility(View.GONE);
        rectangle17.setVisibility(View.GONE);
        rectangle18.setVisibility(View.GONE);
        rectangle19.setVisibility(View.GONE);

        ImageButton buttonExit = (ImageButton)findViewById(R.id.button_exit_photos);
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        Log.i("Pruebas2" , "::: altura: " +dpHeight+ "anchura: " +dpWidth);


        if(dpHeight>550){
            buttonExit.setImageResource(R.drawable.button_exit_large_selector);
        }

        image = (ImageView) findViewById(R.id.image_photos);

        // Affectiva
        cameraPreview = new SurfaceView(this);
        emotionAllFrames = (ArrayList<EmotionDetected>) getIntent().getSerializableExtra("EmotionResult");
        contDetected = getIntent().getIntExtra("FramesDetected", 0);
        contNoFace = getIntent().getIntExtra("FramesNoFace", 0);

        mainLayout = (RelativeLayout) findViewById(R.id.photos);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        cameraPreview.setLayoutParams(params);
        cameraPreview.getLayoutParams().width = 1;
        cameraPreview.getLayoutParams().height = 1;
        mainLayout.addView(cameraPreview,0);

        detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, cameraPreview);
        detector.setDetectAttention(true);
        detector.setDetectAllEmotions(true);

        detector.setImageListener(this);
        detector.setOnCameraEventListener(this);
        try {
            detector.start();
        } catch (Exception e) {
            Log.i("Prueba" , "::: Pinta:"+e.getMessage());

        }

        myTask = new Flujo().execute();

    }


    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            userChooseSomething = true;
            chooseWidth(v, android.R.color.holo_green_dark);
            correct++;
        }
    };


    public boolean onTouchEvent(MotionEvent event){
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(index == 0){
                return true;
            }
            userChooseSomething = true;
            chooseWidth(getButtonView(), android.R.color.holo_red_dark);
            mistake++;
            return true;
        }
        return false;
    }




    public void chooseWidth(View v, int color){
        GradientDrawable drawable = new GradientDrawable();
        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;

        if (dpHeight<350){
            drawable.setStroke(6,getResources().getColor(color));
            v.setBackgroundDrawable(drawable);
        }
        else if (dpHeight<550){
            drawable.setStroke(9,getResources().getColor(color));
            v.setBackgroundDrawable(drawable);
        }
        else{
            drawable.setStroke(12,getResources().getColor(color));
            v.setBackgroundDrawable(drawable);
        }
    }


    public View getButtonView(){
        View v;
        switch (index){
            case 1: v = rectangle10;
                break;
            case 2: v = rectangle11;
                break;
            case 3: v = rectangle12;
                break;
            case 4: v = rectangle13;
                break;
            case 5: v = rectangle14;
                break;
            case 6: v = rectangle15;
                break;
            case 7: v = rectangle16;
                break;
            case 8: v = rectangle17;
                break;
            case 9: v = rectangle18;
                break;
            case 10: v = rectangle19;
                break;
            default: v = rectangle10;
        }
        return v;
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
        Log.d("MYTAG", "Execution time: " + end + " ms");
        double timer = (end - start)/1000;
        final Intent intent = new Intent(Photos.this, ResultsPH.class);
        intent.putExtra("Corrects", correct);
        intent.putExtra("Mistakes", mistake);
        intent.putExtra("Time", (int)timer);
        // info frames in which face was detected/not detected
        intent.putExtra("FramesDetected", contDetected);
        intent.putExtra("FramesNoFace", contNoFace);
        // info related to emotions
        intent.putExtra("EmotionResult", emotionAllFrames);
        startActivity(intent);
    }

    //Affectiva
    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public void onCameraSizeSelected(int width, int height, Frame.ROTATE rotate) {
        Log.i("Prueba" , "::: onCameraSizeSelected:");
        if (rotate == Frame.ROTATE.BY_90_CCW || rotate == Frame.ROTATE.BY_90_CW) {
            previewWidth = height;
            previewHeight = width;
        } else {
            previewHeight = height;
            previewWidth = width;
        }
        cameraPreview.requestLayout();
    }

    @Override
    public void onImageResults(List<Face> list, Frame frame, float v) {
        if (list == null)
            return;
        if (list.size() == 0) {
            Log.i("TimeReaction" , ": NO FACE");
            contNoFace++;
        } else {
            Face face = list.get(0);
            EmotionDetected emotion = new EmotionDetected();

            emotion.attention = face.expressions.getAttention();
            emotion.anger = face.emotions.getAnger();
            emotion.contempt = face.emotions.getContempt();
            emotion.disgust = face.emotions.getDisgust();
            emotion.engagement = face.emotions.getEngagement();
            emotion.fear = face.emotions.getFear();
            emotion.joy = face.emotions.getJoy();
            emotion.sadness = face.emotions.getSadness();
            emotion.surprise = face.emotions.getSurprise();
            emotion.valence = face.emotions.getValence();
            emotion.view = 1;

            emotionAllFrames.add(emotion);

            contDetected++;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (detector.isRunning()) {
            try {
                detector.stop();
            } catch (Exception e) {
                Log.e("Login", e.getMessage());
            }
        }
        detector.setDetectAttention(false);
        detector.setDetectAllEmotions(false);
        detector.setDetectEyeClosure(false);
        detector.setDetectMouthOpen(false);

    }



    private class Flujo extends AsyncTask<Void, Integer, Integer> {

        double time4answer = 4; //Tiempo de espera antes de pasar a la siguiente pregunta

        /*
         * Método que se ejecuta en la hebra principal antes de dar paso a la hebra en segundo plano.
         *
         * Crea un ArrayList de enteros de 8 posiciones que será empleado para seleccionar cada uno de
         * los 8 posibles layouts de preguntas de forma aleatoria
         */
        protected void onPreExecute(){

            Log.i("Pruebas2" , "::: onPreExecute ");
            start = android.os.SystemClock.uptimeMillis();
            Log.d("MYTAG", "Execution time: " + start + " ms");
        }


        /*
         * Método que se ejecuta en segundo plano tras onPreExcute().
         *
         * Elige aleatoriamente el orden de 7 de los 8 layouts de preguntas que aparecerán en pantalla.
         * La visualización de cada uno de ellos acabará cuando el usuario seleccione una respuesta y,
         * por tanto, userChooseSomenthing pase a true, o cuando termine el tiempo time4answer.
         * Además, si es el usuario el que selecciona una respuesta, mantendrá el estado ON del botón
         * pulsado durante medio segundo antes de dar paso al siguiente layout de preguntas.
         */
        protected Integer doInBackground(Void...params){
            Log.i("Pruebas2" , "::: doInBackground ");


            for (int i=0; i<10; i++) {
                if(cancelation) break;
                publishProgress(i);      //Llamada al método onProgressUpdate()

                Log.i("Pruebas2" , "::: for() vuelta "+ i);
                //if(cancelation) break;                               //Si se cancela la hebra, salimos del bucle
                //para terminar cuanto antes con este método


                try{
                    Thread.sleep(7000);
                } catch (InterruptedException e){
                    cancel(true);
                    e.printStackTrace();
                }
                Log.i("Pruebas2" , "::: después del primer sleep, vuelta "+ i);
                publishProgress(20);
                if(cancelation) break;

                try{
                    Thread.sleep(500);
                } catch (InterruptedException e){
                    cancel(true);
                    e.printStackTrace();
                }
                Log.i("Pruebas2" , "::: después del segundo sleep, vuelta "+ i);
                publishProgress(i+10);
                if(cancelation) break;

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
                if(cancelation) break;
                Log.i("Pruebas2" , "::: después del tercer sleep, vuelta "+ i);
                if(seconds>=time4answer) {
                    publishProgress(21);
                }
                userChooseSomething=false;
                publishProgress(i);

                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    cancel(true);
                    e.printStackTrace();
                }
                Log.i("Pruebas2" , "::: después del cuarto sleep, vuelta "+ i);


            }

            return 1;
        }

        /*
         * Método que se ejecuta en la hebra principal. Su ejecución se empieza al finalizar doInBackground().
         *
         * Llama a la Actividad ResultsTM, pasando los números de respuestas correctas e incorrectas
         */
        protected void onPostExecute(Integer result){
            end = android.os.SystemClock.uptimeMillis();
            Log.d("MYTAG", "Execution time: " + end + " ms");
            double timer = (end - start)/1000;
            Log.i("Pruebas2" , "::: PostExecute ");
            final Intent intent = new Intent(Photos.this, ResultsPH.class);
            intent.putExtra("Corrects", correct);
            intent.putExtra("Mistakes", mistake);
            intent.putExtra("Time", (int)timer);
            // info frames in which face was detected/not detected
            intent.putExtra("FramesDetected", contDetected);
            intent.putExtra("FramesNoFace", contNoFace);
            // info related to emotions
            intent.putExtra("EmotionResult", emotionAllFrames);
            startActivity(intent);

        }

        /*
         * Método que se ejecuta en primer plano. Puede ser llamado desde doInBackground() en cualquier momento.
         *
         * Recibe un entero correspondiente al número de layout que ha sido elegido
         * para proporcinarle visibilidad. El resto se dejan invisibles en pantalla.
         */
        protected void onProgressUpdate(Integer... view){

            switch (view[0]) {
                case 0:
                    rectangle10.setClickable(false);
                    image.setImageResource(R.drawable.photo_10);
                    index = 0;
                    break;
                case 1:
                    rectangle10.setVisibility(View.GONE);
                    rectangle11.setClickable(false);
                    image.setImageResource(R.drawable.photo_11);
                    index = 0;
                    break;
                case 2:
                    rectangle11.setVisibility(View.GONE);
                    rectangle12.setClickable(false);
                    image.setImageResource(R.drawable.photo_12);
                    index = 0;
                    break;
                case 3:
                    rectangle12.setVisibility(View.GONE);
                    rectangle13.setClickable(false);
                    image.setImageResource(R.drawable.photo_13);
                    index = 0;
                    break;
                case 4:
                    rectangle13.setVisibility(View.GONE);
                    rectangle14.setClickable(false);
                    image.setImageResource(R.drawable.photo_14);
                    index = 0;
                    break;
                case 5:
                    rectangle14.setVisibility(View.GONE);
                    rectangle15.setClickable(false);
                    image.setImageResource(R.drawable.photo_15);
                    index = 0;
                    break;
                case 6:
                    rectangle15.setVisibility(View.GONE);
                    rectangle16.setClickable(false);
                    image.setImageResource(R.drawable.photo_16);
                    index = 0;
                    break;
                case 7:
                    rectangle16.setVisibility(View.GONE);
                    rectangle17.setClickable(false);
                    image.setImageResource(R.drawable.photo_17);
                    index = 0;
                    break;
                case 8:
                    rectangle17.setVisibility(View.GONE);
                    rectangle18.setClickable(false);
                    image.setImageResource(R.drawable.photo_18);
                    index = 0;
                    break;
                case 9:
                    rectangle18.setVisibility(View.GONE);
                    rectangle19.setClickable(false);
                    image.setImageResource(R.drawable.photo_19);
                    index = 0;
                    break;
                case 10:
                    rectangle10.setVisibility(View.VISIBLE);
                    rectangle10.setClickable(true);
                    image.setImageResource(R.drawable.photo_20);
                    index = 1;
                    break;
                case 11:
                    rectangle11.setVisibility(View.VISIBLE);
                    rectangle11.setClickable(true);
                    image.setImageResource(R.drawable.photo_21);
                    index = 2;
                    break;
                case 12:
                    rectangle12.setVisibility(View.VISIBLE);
                    rectangle12.setClickable(true);
                    image.setImageResource(R.drawable.photo_22);
                    index = 3;
                    break;
                case 13:
                    rectangle13.setVisibility(View.VISIBLE);
                    rectangle13.setClickable(true);
                    image.setImageResource(R.drawable.photo_23);
                    index = 4;
                    break;
                case 14:
                    rectangle14.setVisibility(View.VISIBLE);
                    rectangle14.setClickable(true);
                    image.setImageResource(R.drawable.photo_24);
                    index = 5;
                    break;
                case 15:
                    rectangle15.setVisibility(View.VISIBLE);
                    rectangle15.setClickable(true);
                    image.setImageResource(R.drawable.photo_25);
                    index = 6;
                    break;
                case 16:
                    rectangle16.setVisibility(View.VISIBLE);
                    rectangle16.setClickable(true);
                    image.setImageResource(R.drawable.photo_26);
                    index = 7;
                    break;
                case 17:
                    rectangle17.setVisibility(View.VISIBLE);
                    rectangle17.setClickable(true);
                    image.setImageResource(R.drawable.photo_27);
                    index = 8;
                    break;
                case 18:
                    rectangle18.setVisibility(View.VISIBLE);
                    rectangle18.setClickable(true);
                    image.setImageResource(R.drawable.photo_28);
                    index = 9;
                    break;
                case 19:
                    rectangle19.setVisibility(View.VISIBLE);
                    rectangle19.setClickable(true);
                    image.setImageResource(R.drawable.photo_29);
                    index = 10;
                    break;
                case 20:
                    image.setImageResource(android.R.color.transparent);
                    break;
                case 21:
                    chooseWidth(getButtonView(), android.R.color.holo_red_dark);
                    break;

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
