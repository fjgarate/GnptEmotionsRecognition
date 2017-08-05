package gnpt.app.fcomediavilla.com.gnpt;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.ImageView;
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
 * Clase del reloj previa a las preguntas
 */

public class Clock extends AppCompatActivity implements CameraDetector.CameraEventListener,Detector.ImageListener{

    AnimationDrawable animation;
    ImageView image;
    Handler handler;
    Runnable runnable;

    /// /----- Affectiva
    int previewWidth = 0;
    int previewHeight = 0;
    ConstraintLayout mainLayout;
    CameraDetector detector;
    SurfaceView cameraPreview;

    // detecting attention and all emotions
    int contDetected = 0;
    int contNoFace = 0;
    ArrayList<EmotionDetected> emotionAllFrames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clock);
        Log.i("Pruebas2" , "::: onCreateClock ");

        emotionAllFrames = (ArrayList<EmotionDetected>) getIntent().getSerializableExtra("EmotionResult");
        contDetected = getIntent().getIntExtra("FramesDetected", 0);
        contNoFace = getIntent().getIntExtra("FramesNoFace", 0);

        // Affectiva
        cameraPreview = new SurfaceView(this);

        mainLayout = (ConstraintLayout) findViewById(R.id.clock_layout);
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
            Log.i("Pruebas2" , "::: detector start");
        } catch (Exception e) {
            Log.i("Prueba" , "::: Pinta:"+e.getMessage());

        }

        //Creación de la animación del reloj
        image = (ImageView) findViewById(R.id.clock);
        animation = (AnimationDrawable)image.getDrawable();
        animation.start();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                final Intent intent = new Intent(Clock.this, Questions.class);
                // info frames in which face was detected/not detected
                intent.putExtra("Tarea", "TextMemory1");
                intent.putExtra("FramesDetected", contDetected);
                intent.putExtra("FramesNoFace", contNoFace);
                // info related to emotions
                intent.putExtra("EmotionResult", emotionAllFrames);
                startActivity(intent);
            }
        };
        //Espera 4 segundos (tiempo de la animación) antes de dar paso a las preguntas
        handler.postDelayed(runnable, 4000);
        Log.i("Pruebas2" , "::: clock ok ");

    }

    /*
     * Método sobreescrito para el botón Back.
     *
     * Se cancela la hebra y vuelves al index.
     */
    @Override
    public void onBackPressed() {
        handler.removeCallbacks(runnable);
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
            emotion.view = 2;

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
}
