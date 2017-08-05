package gnpt.app.fcomediavilla.com.gnpt;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Francisco Mediavilla Sala
 *
 * Clase para las preguntas a contestar de Textmemory
 */

public class Questions extends AppCompatActivity implements CameraDetector.CameraEventListener,Detector.ImageListener{

    LinearLayout layout1, layout2, layout3, layout4, layout5, layout6, layout7, layout8;
    ImageButton btn11, btn12, btn13, btn14, btn21, btn22, btn23, btn24, btn31, btn32, btn33, btn34,
    btn41, btn42, btn43, btn44, btn51, btn52, btn53, btn54, btn61, btn62, btn63, btn64, btn71,
            btn72, btn73, btn74, btn81, btn82, btn83, btn84, next, exit;
    public boolean userChooseSomething = false;
    int correct = 0;
    int mistake = 0;
    private AsyncTask myTask;
    boolean cancelation = false;
    String tarea;

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
        setContentView(R.layout.questions);

        // Affectiva

        cameraPreview = new SurfaceView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        cameraPreview.setLayoutParams(params);
        cameraPreview.getLayoutParams().width = 1;
        cameraPreview.getLayoutParams().height = 1;
        mainLayout = (LinearLayout) findViewById(R.id.prueba1234);
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

        layout1 = (LinearLayout) findViewById(R.id.question_layout_1);
        layout2 = (LinearLayout) findViewById(R.id.question_layout_2);
        layout3 = (LinearLayout) findViewById(R.id.question_layout_3);
        layout4 = (LinearLayout) findViewById(R.id.question_layout_4);
        layout5 = (LinearLayout) findViewById(R.id.question_layout_5);
        layout6 = (LinearLayout) findViewById(R.id.question_layout_6);
        layout7 = (LinearLayout) findViewById(R.id.question_layout_7);
        layout8 = (LinearLayout) findViewById(R.id.question_layout_8);

        btn11 = (ImageButton) findViewById(R.id.checkTM_11);
        btn12 = (ImageButton) findViewById(R.id.checkTM_12);
        btn13 = (ImageButton) findViewById(R.id.checkTM_13);
        btn14 = (ImageButton) findViewById(R.id.checkTM_14);
        btn21 = (ImageButton) findViewById(R.id.checkTM_21);
        btn22 = (ImageButton) findViewById(R.id.checkTM_22);
        btn23 = (ImageButton) findViewById(R.id.checkTM_23);
        btn24 = (ImageButton) findViewById(R.id.checkTM_24);
        btn31 = (ImageButton) findViewById(R.id.checkTM_31);
        btn32 = (ImageButton) findViewById(R.id.checkTM_32);
        btn33 = (ImageButton) findViewById(R.id.checkTM_33);
        btn34 = (ImageButton) findViewById(R.id.checkTM_34);
        btn41 = (ImageButton) findViewById(R.id.checkTM_41);
        btn42 = (ImageButton) findViewById(R.id.checkTM_42);
        btn43 = (ImageButton) findViewById(R.id.checkTM_43);
        btn44 = (ImageButton) findViewById(R.id.checkTM_44);
        btn51 = (ImageButton) findViewById(R.id.checkTM_51);
        btn52 = (ImageButton) findViewById(R.id.checkTM_52);
        btn53 = (ImageButton) findViewById(R.id.checkTM_53);
        btn54 = (ImageButton) findViewById(R.id.checkTM_54);
        btn61 = (ImageButton) findViewById(R.id.checkTM_61);
        btn62 = (ImageButton) findViewById(R.id.checkTM_62);
        btn63 = (ImageButton) findViewById(R.id.checkTM_63);
        btn64 = (ImageButton) findViewById(R.id.checkTM_64);
        btn71 = (ImageButton) findViewById(R.id.checkTM_71);
        btn72 = (ImageButton) findViewById(R.id.checkTM_72);
        btn73 = (ImageButton) findViewById(R.id.checkTM_73);
        btn74 = (ImageButton) findViewById(R.id.checkTM_74);
        btn81 = (ImageButton) findViewById(R.id.checkTM_81);
        btn82 = (ImageButton) findViewById(R.id.checkTM_82);
        btn83 = (ImageButton) findViewById(R.id.checkTM_83);
        btn84 = (ImageButton) findViewById(R.id.checkTM_84);
        next = (ImageButton) findViewById(R.id.button_continue);
        exit = (ImageButton) findViewById(R.id.button_exit);


        tarea = getIntent().getStringExtra("Tarea");
        emotionAllFrames = (ArrayList<EmotionDetected>) getIntent().getSerializableExtra("EmotionResult");
        contDetected = getIntent().getIntExtra("FramesDetected", 0);
        contNoFace = getIntent().getIntExtra("FramesNoFace", 0);

        myTask = new Flujo().execute();

    }


    /*
     * Método escuchador de eventos. Botones primera pregunta.
     *
     * Cambia el background del botón pulsado, aumenta errores o aciertos
     * y pone userChooseSomething a true para pasar a la siguiente pregunta
     */
    public void checkQuestion1(View view){

        Log.i("Pruebas2" , "::: Estoy en el onClick1 ");

        switch (view.getId()){
            case R.id.checkTM_11: btn11.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
            case R.id.checkTM_12: btn12.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
            case R.id.checkTM_13: btn13.setBackgroundResource(R.drawable.checkbox_on);
                correct++;
                break;
            case R.id.checkTM_14: btn14.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
        }
        userChooseSomething = true;
    }

    /*
     * Método escuchador de eventos. Botones segunda pregunta.
     *
     * Cambia el background del botón pulsado, aumenta errores o aciertos
     * y pone userChooseSomething a true para pasar a la siguiente pregunta
     */
    public void checkQuestion2(View view){

        Log.i("Pruebas2" , "::: Estoy en el onClick2 ");

        switch (view.getId()){
            case R.id.checkTM_21: btn21.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
            case R.id.checkTM_22: btn22.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
            case R.id.checkTM_23: btn23.setBackgroundResource(R.drawable.checkbox_on);
                correct++;
                break;
            case R.id.checkTM_24: btn24.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
        }
        userChooseSomething = true;
    }

    /*
     * Método escuchador de eventos. Botones tercera pregunta.
     *
     * Cambia el background del botón pulsado, aumenta errores o aciertos
     * y pone userChooseSomething a true para pasar a la siguiente pregunta.
     */
    public void checkQuestion3(View view){

        Log.i("Pruebas2" , "::: Estoy en el onClick3 ");

        switch (view.getId()){
            case R.id.checkTM_31: btn31.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
            case R.id.checkTM_32: btn32.setBackgroundResource(R.drawable.checkbox_on);
                correct++;
                break;
            case R.id.checkTM_33: btn33.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
            case R.id.checkTM_34: btn34.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
        }
        userChooseSomething = true;
    }

    /*
     * Método escuchador de eventos. Botones cuarta pregunta.
     *
     * Cambia el background del botón pulsado, aumenta errores o aciertos
     * y pone userChooseSomething a true para pasar a la siguiente pregunta.
     */
    public void checkQuestion4(View view){

        Log.i("Pruebas2" , "::: Estoy en el onClick4 ");

        switch (view.getId()){
            case R.id.checkTM_41: btn41.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
            case R.id.checkTM_42: btn42.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
            case R.id.checkTM_43: btn43.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
            case R.id.checkTM_44: btn44.setBackgroundResource(R.drawable.checkbox_on);
                correct++;
                break;
        }
        userChooseSomething = true;
    }

    /*
     * Método escuchador de eventos. Botones quinta pregunta.
     *
     * Cambia el background del botón pulsado, aumenta errores o aciertos
     * y pone userChooseSomething a true para pasar a la siguiente pregunta.
     */
    public void checkQuestion5(View view){

        Log.i("Pruebas2" , "::: Estoy en el onClick5 ");

        switch (view.getId()){
            case R.id.checkTM_51: btn51.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
            case R.id.checkTM_52: btn52.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
            case R.id.checkTM_53: btn53.setBackgroundResource(R.drawable.checkbox_on);
                correct++;
                break;
            case R.id.checkTM_54: btn54.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
        }
        userChooseSomething = true;
    }

    /*
     * Método escuchador de eventos. Botones sexta pregunta.
     *
     * Cambia el background del botón pulsado, aumenta errores o aciertos
     * y pone userChooseSomething a true para pasar a la siguiente pregunta.
     */
    public void checkQuestion6(View view){

        Log.i("Pruebas2" , "::: Estoy en el onClick6 ");

        switch (view.getId()){
            case R.id.checkTM_61: btn61.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
            case R.id.checkTM_62: btn62.setBackgroundResource(R.drawable.checkbox_on);
                correct++;
                break;
            case R.id.checkTM_63: btn63.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
            case R.id.checkTM_64: btn64.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
        }
        userChooseSomething = true;
    }

    /*
     * Método escuchador de eventos. Botones séptima pregunta.
     *
     * Cambia el background del botón pulsado, aumenta errores o aciertos
     * y pone userChooseSomething a true para pasar a la siguiente pregunta.
     */
    public void checkQuestion7(View view){

        Log.i("Pruebas2" , "::: Estoy en el onClick7 ");

        switch (view.getId()){
            case R.id.checkTM_71: btn71.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
            case R.id.checkTM_72: btn72.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
            case R.id.checkTM_73: btn73.setBackgroundResource(R.drawable.checkbox_on);
                correct++;
                break;
            case R.id.checkTM_74: btn74.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
        }
        userChooseSomething = true;
    }

    /*
     * Método escuchador de eventos. Botones octava pregunta.
     *
     * Cambia el background del botón pulsado, aumenta errores o aciertos
     * y pone userChooseSomething a true para pasar a la siguiente pregunta.
     */
    public void checkQuestion8(View view){

        Log.i("Pruebas2" , "::: Estoy en el onClick8 ");

        switch (view.getId()){
            case R.id.checkTM_81: btn81.setBackgroundResource(R.drawable.checkbox_on);
                correct++;
                break;
            case R.id.checkTM_82: btn82.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
            case R.id.checkTM_83: btn83.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
            case R.id.checkTM_84: btn84.setBackgroundResource(R.drawable.checkbox_on);
                mistake++;
                break;
        }
        userChooseSomething = true;
    }

    /*
     * Método escuchador de eventos. Botón continuar.
     *
     * Pasa a la siguiente pregunta (poniendo userChooseSomething a true) sin ninguna
     * elección de respuesta, produciéndose por tanto una omisión.
     */
    public void nextQuestion(View view){
        userChooseSomething = true;
    }

    /*
     * Método escuchador de eventos. Botón salir.
     *
     * Cancela la hebra en segundo plano y salta a la Actividad ResultsTM,
     * pasando las respuestas correctas e incorrectas hasta el momento.
     */
    public void toResults(View view){
        cancelation = true;
        myTask.cancel(cancelation);
        final Intent intent = new Intent(Questions.this, ResultsTM.class);
        intent.putExtra("Corrects", correct);
        intent.putExtra("Mistakes", mistake);
        intent.putExtra("Tarea", tarea);
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
        cancelation = true;
        myTask.cancel(cancelation);
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
            emotion.view = 3;

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
    private class Flujo extends AsyncTask<Void, Integer, Integer>{

        double time4answer = 6; //Tiempo de espera antes de pasar a la siguiente pregunta
        ArrayList<Integer> indexViews;

        /*
         * Método que se ejecuta en la hebra principal antes de dar paso a la hebra en segundo plano.
         *
         * Crea un ArrayList de enteros de 8 posiciones que será empleado para seleccionar cada uno de
         * los 8 posibles layouts de preguntas de forma aleatoria
         */
        protected void onPreExecute(){
            Log.i("Pruebas2" , "::: onPreExecute ");
            indexViews = new ArrayList<>();
            indexViews.add(0);
            indexViews.add(1);
            indexViews.add(2);
            indexViews.add(3);
            indexViews.add(4);
            indexViews.add(5);
            indexViews.add(6);
            indexViews.add(7);
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

            Random r = new Random();


            for (int i=0; i<7; i++) {
                int indexDinamico = r.nextInt(indexViews.size());
                publishProgress(indexViews.get(indexDinamico));      //Llamada al método onProgressUpdate()
                indexViews.remove(indexDinamico);                    //Se elimina la posición del Array que se acaba de usar
                Log.i("Pruebas2" , "::: for() vuelta "+ i);
                if(cancelation) break;                               //Si se cancela la hebra, salimos del bucle
                                                                     //para terminar cuanto antes con este método


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

                if (userChooseSomething=true){
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        cancel(true);
                        e.printStackTrace();
                    }
                }
                userChooseSomething=false;

            }

            return 1;
        }

        /*
         * Método que se ejecuta en la hebra principal. Su ejecución se empieza al finalizar doInBackground().
         *
         * Llama a la Actividad ResultsTM, pasando los números de respuestas correctas e incorrectas
         */
        protected void onPostExecute(Integer result){
            Log.i("Pruebas2" , "::: PostExecute ");
            final Intent intent = new Intent(Questions.this, ResultsTM.class);
            intent.putExtra("Corrects", correct);
            intent.putExtra("Mistakes", mistake);
            intent.putExtra("Tarea", tarea);
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
                    layout1.setVisibility(View.VISIBLE);
                    layout2.setVisibility(View.GONE);
                    layout3.setVisibility(View.GONE);
                    layout4.setVisibility(View.GONE);
                    layout5.setVisibility(View.GONE);
                    layout6.setVisibility(View.GONE);
                    layout7.setVisibility(View.GONE);
                    layout8.setVisibility(View.GONE);
                    break;
                case 1:
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.VISIBLE);
                    layout3.setVisibility(View.GONE);
                    layout4.setVisibility(View.GONE);
                    layout5.setVisibility(View.GONE);
                    layout6.setVisibility(View.GONE);
                    layout7.setVisibility(View.GONE);
                    layout8.setVisibility(View.GONE);
                    break;
                case 2:
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    layout3.setVisibility(View.VISIBLE);
                    layout4.setVisibility(View.GONE);
                    layout5.setVisibility(View.GONE);
                    layout6.setVisibility(View.GONE);
                    layout7.setVisibility(View.GONE);
                    layout8.setVisibility(View.GONE);
                    break;
                case 3:
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    layout3.setVisibility(View.GONE);
                    layout4.setVisibility(View.VISIBLE);
                    layout5.setVisibility(View.GONE);
                    layout6.setVisibility(View.GONE);
                    layout7.setVisibility(View.GONE);
                    layout8.setVisibility(View.GONE);
                    break;
                case 4:
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    layout3.setVisibility(View.GONE);
                    layout4.setVisibility(View.GONE);
                    layout5.setVisibility(View.VISIBLE);
                    layout6.setVisibility(View.GONE);
                    layout7.setVisibility(View.GONE);
                    layout8.setVisibility(View.GONE);
                    break;
                case 5:
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    layout3.setVisibility(View.GONE);
                    layout4.setVisibility(View.GONE);
                    layout5.setVisibility(View.GONE);
                    layout6.setVisibility(View.VISIBLE);
                    layout7.setVisibility(View.GONE);
                    layout8.setVisibility(View.GONE);
                    break;
                case 6:
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    layout3.setVisibility(View.GONE);
                    layout4.setVisibility(View.GONE);
                    layout5.setVisibility(View.GONE);
                    layout6.setVisibility(View.GONE);
                    layout7.setVisibility(View.VISIBLE);
                    layout8.setVisibility(View.GONE);
                    break;
                case 7:
                    layout1.setVisibility(View.GONE);
                    layout2.setVisibility(View.GONE);
                    layout3.setVisibility(View.GONE);
                    layout4.setVisibility(View.GONE);
                    layout5.setVisibility(View.GONE);
                    layout6.setVisibility(View.GONE);
                    layout7.setVisibility(View.GONE);
                    layout8.setVisibility(View.VISIBLE);
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

