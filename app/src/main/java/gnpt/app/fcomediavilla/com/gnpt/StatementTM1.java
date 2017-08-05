package gnpt.app.fcomediavilla.com.gnpt;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Francisco Mediavilla Sala
 *
 * Clase para enunciado de Textmemory1
 * Contiene tanto el enunciado de la tarea Textmemory1 como el texto a recordar.
 */

public class StatementTM1 extends AppCompatActivity implements CameraDetector.CameraEventListener,Detector.ImageListener {

    LinearLayout layout1;
    RelativeLayout layout2;
    boolean userChooseContinue  = false; //Se pondrá a true cuando el usuario elija continuar y dar paso a las preguntas
    private AsyncTask myTask;

    /// /----- Affectiva
    int previewWidth = 0;
    int previewHeight = 0;
    LinearLayout mainLayout;
    CameraDetector detector;
    SurfaceView cameraPreview;

    // detecting attention and all emotions
    int contDetected = 0;
    int contNoFace = 0;
    ArrayList<EmotionDetected> emotionAllFrames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statement_textm1);

        // Affectiva
        cameraPreview = new SurfaceView(this);
         //cameraPreview = (SurfaceView) findViewById(R.id.camera_preview1);

        mainLayout = (LinearLayout) findViewById(R.id.statement_tm1_total);
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
        // info frames in which face was detected/not detected
        intent.putExtra("FramesDetected", contDetected);
        intent.putExtra("FramesNoFace", contNoFace);
        // info related to emotions
        intent.putExtra("EmotionResult", emotionAllFrames);
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
            if(layout1.isShown()){
                emotion.view = 0;
            }else{
                emotion.view = 1;
            }


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
            // info frames in which face was detected/not detected
            intent.putExtra("FramesDetected", contDetected);
            intent.putExtra("FramesNoFace", contNoFace);
            // info related to emotions
            intent.putExtra("EmotionResult", emotionAllFrames);
            startActivity(intent);
        }
    }
}
