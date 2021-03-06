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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by SalmonManin on 27/02/2017.
 */

public class TimeReaction extends AppCompatActivity implements CameraDetector.CameraEventListener,Detector.ImageListener{

    ImageButton circle1, circle2, circle3, circle4, circle5, circle6;
    GradientDrawable backCircle1, backCircle2, backCircle3, backCircle4, backCircle5, backCircle6;
    int correct, mistake, omission, recuento;
    double averageTime = 0;
    double timer = 0;
    public boolean userChooseCircle = false;
    TextView points;
    private AsyncTask myTask;
    boolean cancelation = false;
    ArrayList<Double> tReaction;

    //----- Affectiva
    int previewWidth = 0;
    int previewHeight = 0;
    RelativeLayout mainLayout;
    CameraDetector detector;
    SurfaceView cameraPreview;
    FrameLayout window;

    // detecting expressions: eye closure and mouth widen
    /*int countFramesEye = 0;
    int countFramesMouth = 0;
    int countMouth = 0;
    int countEye = 0;*/

    // detecting attention and all emotions
    int contDetected = 0;
    int contNoFace = 0;
    ArrayList<EmotionDetected> emotionAllFrames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_reaction);

        circle1 = (ImageButton) findViewById(R.id.circle1_timereaction);
        circle2 = (ImageButton) findViewById(R.id.circle2_timereaction);
        circle3 = (ImageButton) findViewById(R.id.circle3_timereaction);
        circle4 = (ImageButton) findViewById(R.id.circle4_timereaction);
        circle5 = (ImageButton) findViewById(R.id.circle5_timereaction);
        circle6 = (ImageButton) findViewById(R.id.circle6_timereaction);

        circle1.setEnabled(false);
        circle2.setEnabled(false);
        circle3.setEnabled(false);
        circle4.setEnabled(false);
        circle5.setEnabled(false);
        circle6.setEnabled(false);

        backCircle1 = (GradientDrawable)circle1.getBackground();
        backCircle2 = (GradientDrawable)circle2.getBackground();
        backCircle3 = (GradientDrawable)circle3.getBackground();
        backCircle4 = (GradientDrawable)circle4.getBackground();
        backCircle5 = (GradientDrawable)circle5.getBackground();
        backCircle6 = (GradientDrawable)circle6.getBackground();

        points = (TextView) findViewById(R.id.text_points_TR);

        // Affectiva
        cameraPreview = new SurfaceView(this);
        //cameraPreview = (SurfaceView) findViewById(R.id.camera_preview);
        emotionAllFrames = (ArrayList<EmotionDetected>) getIntent().getSerializableExtra("EmotionResult");
        contDetected = getIntent().getIntExtra("FramesDetected", 0);
        contNoFace = getIntent().getIntExtra("FramesNoFace", 0);
        /*{
            @Override
            public void onMeasure(int widthSpec, int heightSpec) {
                // int measureWidth = MeasureSpec.getSize(widthSpec);
                // int measureHeight = MeasureSpec.getSize(heightSpec);
                int width = 220;
                int height = 220;

                setMeasuredDimension(width,height);
            }
        };*/
        mainLayout = (RelativeLayout) findViewById(R.id.time_reaction_main);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        cameraPreview.setLayoutParams(params);
        cameraPreview.getLayoutParams().width = 550;
        cameraPreview.getLayoutParams().height = 350;
        mainLayout.addView(cameraPreview,0);

        detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, cameraPreview);
        detector.setDetectAttention(true);
        detector.setDetectAllEmotions(true);
        /*detector.setDetectEyeClosure(true);
        detector.setDetectMouthOpen(true);*/

        detector.setImageListener(this);
        detector.setOnCameraEventListener(this);
        try {
            detector.start();
        } catch (Exception e) {
            Log.i("Prueba" , "::: Pinta:"+e.getMessage());

        }
        myTask = new Flujo().execute();
    }

    public void circlePressed(View view){

        if((Boolean)view.getTag()){
            userChooseCircle = true;
            correct++;
            points.setText("" + correct);
        }
        else{
            mistake++;
            recuento++;
        }
    }


    public void toHelp(View view){
        Intent intent = new Intent(this,HelpTR.class);
        startActivity(intent);
    }


    /*
     * Método escuchador de eventos. Botón salir.
     *
     * Cancela la hebra en segundo plano y salta a la Actividad ResultsTR,
     * pasando las respuestas correctas e incorrectas hasta el momento.
     */
    public void toResults(View view){
        cancelation = true;
        myTask.cancel(cancelation);
        Log.i("Prueba" , "::: hebra cancelada");
        double sum = 0;
        for (Double i : tReaction){
            sum += i;
        }
        averageTime = sum/tReaction.size();
        omission = recuento - correct - mistake;
        averageTime = (double) Math.round(averageTime*100)/100;
        Log.i("Prueba" , "::: valores calculados: omission " + omission + " y averageTime: " + averageTime);
        Intent intent = new Intent(TimeReaction.this, ResultsTR.class);
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
        // info related to eyes/mouth
        /*intent.putExtra("NumEye", countEye);
        intent.putExtra("NumMouth", countMouth);*/
        startActivity(intent);
    }


    private class Flujo extends AsyncTask<Void, Integer, Integer>{

        ArrayList<Integer> indexCircles;
        double taskTime = 300;


        protected void onPreExecute(){
            indexCircles = new ArrayList<>();
            tReaction = new ArrayList<>();
            indexCircles.add(0);
            indexCircles.add(1);
            indexCircles.add(2);
            indexCircles.add(3);
            indexCircles.add(4);

        }

        @Override
        protected Integer doInBackground(Void... params) {

            Random r = new Random();
            double time4answer = 2;
            int lastNumber = 5;

            try{
                Thread.sleep(2000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            if(timer == 0){
                publishProgress(6);
            }
            while(timer<taskTime){
                Log.i("Prueba" , "::: tiempo timer: "+ timer);
                int indexDinamico = r.nextInt(indexCircles.size());
                publishProgress(indexCircles.get(indexDinamico));
                indexCircles.add(lastNumber);
                lastNumber = indexCircles.get(indexDinamico);
                indexCircles.remove(indexDinamico);
                if (cancelation) break;


                double seconds = 0;
                while(!isCancelled() && !userChooseCircle && seconds<time4answer){
                    try{
                        Thread.sleep(100);
                    } catch (InterruptedException e){
                        cancel(true);
                        e.printStackTrace();
                    }
                    seconds+=0.1;
                }
                timer += seconds;
                recuento++;
                if (userChooseCircle){
                    tReaction.add(seconds);
                }
                userChooseCircle = false;
            }
            Log.i("Prueba" , "::: termina el método doInBackground: ");
            double sum = 0;
            for (Double i : tReaction){
                sum += i;
            }
            averageTime = sum/tReaction.size();
            Log.i("Prueba" , "::: termina el método doInBackground con un avergaTime: " + averageTime);
            return 1;
        }

        protected void onPostExecute(Integer result){

            /*Toast toast = Toast.makeText(getApplicationContext(), "Quémalo todo", Toast.LENGTH_LONG);
            toast.show();*/
            Log.i("Prueba" , "::: respuestas correctas: "+ correct);
            Log.i("Prueba" , "::: fallos: "+ mistake);
            Log.i("Prueba" , "::: total: "+ recuento);
            omission = recuento - correct - mistake;
            Log.i("Prueba" , "::: omisiones: "+ omission);
            averageTime = (double) Math.round(averageTime*100)/100;
            Log.i("Prueba" , "::: tiempo medio de reacción: "+ averageTime);

            final Intent intent = new Intent(TimeReaction.this, ResultsTR.class);
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
            // info related to eyes/mouth
            /*intent.putExtra("NumEye", countEye);
            intent.putExtra("NumMouth", countMouth);*/
            startActivity(intent);

        }

        protected void onProgressUpdate(Integer... view){

            switch (view[0]){
                case 0:
                    backCircle1.setColor(getResources().getColor(R.color.fillCircle));
                    circle1.setTag(true);
                    backCircle2.setColor(Color.TRANSPARENT);
                    circle2.setTag(false);
                    backCircle3.setColor(Color.TRANSPARENT);
                    circle3.setTag(false);
                    backCircle4.setColor(Color.TRANSPARENT);
                    circle4.setTag(false);
                    backCircle5.setColor(Color.TRANSPARENT);
                    circle5.setTag(false);
                    backCircle6.setColor(Color.TRANSPARENT);
                    circle6.setTag(false);
                    break;
                case 1:
                    backCircle1.setColor(Color.TRANSPARENT);
                    circle1.setTag(false);
                    backCircle2.setColor(getResources().getColor(R.color.fillCircle));
                    circle2.setTag(true);
                    backCircle3.setColor(Color.TRANSPARENT);
                    circle3.setTag(false);
                    backCircle4.setColor(Color.TRANSPARENT);
                    circle4.setTag(false);
                    backCircle5.setColor(Color.TRANSPARENT);
                    circle5.setTag(false);
                    backCircle6.setColor(Color.TRANSPARENT);
                    circle6.setTag(false);
                    break;
                case 2:
                    backCircle1.setColor(Color.TRANSPARENT);
                    circle1.setTag(false);
                    backCircle2.setColor(Color.TRANSPARENT);
                    circle2.setTag(false);
                    backCircle3.setColor(getResources().getColor(R.color.fillCircle));
                    circle3.setTag(true);
                    backCircle4.setColor(Color.TRANSPARENT);
                    circle4.setTag(false);
                    backCircle5.setColor(Color.TRANSPARENT);
                    circle5.setTag(false);
                    backCircle6.setColor(Color.TRANSPARENT);
                    circle6.setTag(false);
                    break;
                case 3:
                    backCircle1.setColor(Color.TRANSPARENT);
                    circle1.setTag(false);
                    backCircle2.setColor(Color.TRANSPARENT);
                    circle2.setTag(false);
                    backCircle3.setColor(Color.TRANSPARENT);
                    circle3.setTag(false);
                    backCircle4.setColor(getResources().getColor(R.color.fillCircle));
                    circle4.setTag(true);
                    backCircle5.setColor(Color.TRANSPARENT);
                    circle5.setTag(false);
                    backCircle6.setColor(Color.TRANSPARENT);
                    circle6.setTag(false);
                    break;
                case 4:
                    backCircle1.setColor(Color.TRANSPARENT);
                    circle1.setTag(false);
                    backCircle2.setColor(Color.TRANSPARENT);
                    circle2.setTag(false);
                    backCircle3.setColor(Color.TRANSPARENT);
                    circle3.setTag(false);
                    backCircle4.setColor(Color.TRANSPARENT);
                    circle4.setTag(false);
                    backCircle5.setColor(getResources().getColor(R.color.fillCircle));
                    circle5.setTag(true);
                    backCircle6.setColor(Color.TRANSPARENT);
                    circle6.setTag(false);
                    break;
                case 5:
                    backCircle1.setColor(Color.TRANSPARENT);
                    circle1.setTag(false);
                    backCircle2.setColor(Color.TRANSPARENT);
                    circle2.setTag(false);
                    backCircle3.setColor(Color.TRANSPARENT);
                    circle3.setTag(false);
                    backCircle4.setColor(Color.TRANSPARENT);
                    circle4.setTag(false);
                    backCircle5.setColor(Color.TRANSPARENT);
                    circle5.setTag(false);
                    backCircle6.setColor(getResources().getColor(R.color.fillCircle));
                    circle6.setTag(true);
                    break;
                case 6:
                    circle1.setEnabled(true);
                    circle2.setEnabled(true);
                    circle3.setEnabled(true);
                    circle4.setEnabled(true);
                    circle5.setEnabled(true);
                    circle6.setEnabled(true);
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
}
