package gnpt.app.fcomediavilla.com.gnpt;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by fcome on 24/04/2017.
 */

public class Flexibility extends AppCompatActivity implements CameraDetector.CameraEventListener,Detector.ImageListener{

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

        // Affectiva
        emotionAllFrames = (ArrayList<EmotionDetected>) getIntent().getSerializableExtra("EmotionResult");
        contDetected = getIntent().getIntExtra("FramesDetected", 0);
        contNoFace = getIntent().getIntExtra("FramesNoFace", 0);

        cameraPreview = new SurfaceView(this);

        mainLayout = (RelativeLayout) findViewById(R.id.flex);
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

            /*if(face.expressions.getEyeClosure()>60){
                countFramesEye++;
                if(countFramesEye==5){
                    countFramesEye = 0;
                    countEye++;
                }
            }
            if(face.expressions.getMouthOpen()>60){
                countFramesMouth++;
                if(countFramesMouth==5){
                    countFramesMouth = 0;
                    countMouth++;
                }
            }*/
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
            // info frames in which face was detected/not detected
            intent.putExtra("FramesDetected", contDetected);
            intent.putExtra("FramesNoFace", contNoFace);
            // info related to emotions
            intent.putExtra("EmotionResult", emotionAllFrames);
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
