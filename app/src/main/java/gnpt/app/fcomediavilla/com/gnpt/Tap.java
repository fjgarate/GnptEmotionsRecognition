package gnpt.app.fcomediavilla.com.gnpt;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fcome on 07/04/2017.
 */

public class Tap extends AppCompatActivity implements CameraDetector.CameraEventListener,Detector.ImageListener{

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap);

        btnword = (Button) findViewById(R.id.button4_tap);
        btn1 = (Button) findViewById(R.id.button1_tap);
        btn2 = (Button) findViewById(R.id.button2_tap);
        btn3 = (Button) findViewById(R.id.button3_tap);
        result = (TextView) findViewById(R.id.textview_result_tap);

        // Affectiva
        cameraPreview = new SurfaceView(this);
        emotionAllFrames = (ArrayList<EmotionDetected>) getIntent().getSerializableExtra("EmotionResult");
        contDetected = getIntent().getIntExtra("FramesDetected", 0);
        contNoFace = getIntent().getIntExtra("FramesNoFace", 0);

        mainLayout = (RelativeLayout) findViewById(R.id.tap);
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
            // info frames in which face was detected/not detected
            intent.putExtra("FramesDetected", contDetected);
            intent.putExtra("FramesNoFace", contNoFace);
            // info related to emotions
            intent.putExtra("EmotionResult", emotionAllFrames);
            startActivity(intent);
        }

        @Override
        protected void onProgressUpdate(Integer... view){
            btnword.setText(palabras[wordPointer]);
            result.setText("");
        }
    }
}
