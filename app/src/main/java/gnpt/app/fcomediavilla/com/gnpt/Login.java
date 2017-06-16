package gnpt.app.fcomediavilla.com.gnpt;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.Frame.ROTATE;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;
import com.affectiva.android.affdex.sdk.detector.Face.FaceQuality;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fcome on 16/05/2017.
 */


public class Login extends AppCompatActivity implements CameraDetector.CameraEventListener,Detector.ImageListener{

    private EditText editText;
    public static String idUser = "";
    public static int numSesion = 0;
    public static String idSesion = java.util.UUID.randomUUID().toString();

    int previewWidth = 0;
    int previewHeight = 0;
    LinearLayout mainLayout;
    CameraDetector detector;
    SurfaceView cameraPreview;
    private static final int CAMERA_PERMISSIONS_REQUEST = 42;  //value is arbitrary (between 0 and 255)
    private static final int EXTERNAL_STORAGE_PERMISSIONS_REQUEST = 73;
    private boolean cameraPermissionsAvailable = false;
    private boolean storagePermissionsAvailable = false;

    // detecting face brightness and orientation
    float bright, yaw, roll, pitch;
    boolean firstPosition = true;
    boolean firstBrightness = true;
    boolean firstShadow = true;
    CharSequence textPosition = "¡Tu posición es perfecta!";
    CharSequence textBrightness = "¡Tu iluminación es perfecta!";
    CharSequence textShadow = "Tu iluminación es mala. Busca una fuente de luz.";
    int duration = Toast.LENGTH_SHORT;

    // detecting appearance
    CharSequence age, ethnicity, glasses, gender;
    int countAppearance = 0;

    // detecting eye closure, widen and mouth widen expressions
    int countEye = 0;
    int countMouth = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        checkForCameraPermissions();
        editText = (EditText) findViewById(R.id.login_user);
        //  CameraDetector detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT,new SurfaceView(this) );
        mainLayout = (LinearLayout) findViewById(R.id.login_main);

        cameraPreview = new SurfaceView(this);
      /*  cameraPreview = new SurfaceView(this) {
            @Override
            public void onMeasure(int widthSpec, int heightSpec) {
               // int measureWidth = MeasureSpec.getSize(widthSpec);
               // int measureHeight = MeasureSpec.getSize(heightSpec);
                 int measureWidth = 10;
                 int measureHeight = 10;
                int width;
                int height;
                if (previewHeight == 0 || previewWidth == 0) {
                    width = measureWidth;
                    height = measureHeight;
                } else {
                    float viewAspectRatio = (float)measureWidth/measureHeight;
                    float cameraPreviewAspectRatio = (float) previewWidth/previewHeight;

                    if (cameraPreviewAspectRatio > viewAspectRatio) {
                        width = measureWidth;
                        height =(int) (measureWidth / cameraPreviewAspectRatio);
                    } else {
                        width = (int) (measureHeight * cameraPreviewAspectRatio);
                        height = measureHeight;
                    }
                }
                setMeasuredDimension(width,height);
            }
        };*/
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        cameraPreview.setLayoutParams(params);
        cameraPreview.getLayoutParams().width = 1;
        cameraPreview.getLayoutParams().height = 1;
        mainLayout.addView(cameraPreview,0);

        detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, cameraPreview);
        detector.setDetectAge(true);
        detector.setDetectGender(true);
        detector.setDetectGlasses(true);
        detector.setDetectEthnicity(true);

        detector.setDetectEyeWiden(true);
        detector.setDetectEyeClosure(true);
        detector.setDetectMouthOpen(true);
        detector.setDetectAttention(true);
        detector.setDetectEngagement(true);
        detector.setDetectValence(true);

        detector.setImageListener(this);
        detector.setOnCameraEventListener(this);
        try {
            detector.start();
        } catch (Exception e) {
            Log.i("Prueba" , "::: Pinta:"+e.getMessage());
        }
        Log.i("Prueba" , "::: Pinta:");

/*
        surfaceViewVisibilityButton = (Button) findViewById(R.id.surfaceview_visibility_button);
        surfaceViewVisibilityButton.setText("HIDE SURFACE VIEW");
        surfaceViewVisibilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraPreview.getVisibility() == View.VISIBLE) {
                    cameraPreview.setVisibility(View.INVISIBLE);
                    surfaceViewVisibilityButton.setText("SHOW SURFACE VIEW");
                } else {
                    cameraPreview.setVisibility(View.VISIBLE);
                    surfaceViewVisibilityButton.setText("HIDE SURFACE VIEW");
                }
            }
        });

        detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, cameraPreview);
        detector.setDetectSmile(true);
        detector.setDetectAge(true);
        detector.setDetectEthnicity(true);
        detector.setImageListener(this);
        detector.setOnCameraEventListener(this);
    }*/
    }


    public void toIndex(View v){
        //Primero guardamos el valor de idUser
        idUser = editText.getText().toString();
        Log.i("Prueba" , "::: idUser: " + idUser);
        String userSesion = idUser + ".sesion";
        Log.i("Prueba" , "::: Nombre userSesion: " + userSesion);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int sesion = prefs.getInt(userSesion, -1);
        Log.i("Prueba" , "::: Num userSesion: " + sesion);
        if (sesion == -1){
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(userSesion, 1);
            editor.commit();
            numSesion = 1;
            Log.i("Prueba" , "::: idSesion actual: " + numSesion);
        }
        else{
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(userSesion, ++sesion);
            editor.commit();
            numSesion = sesion;
            Log.i("Prueba" , "::: idSesion actual: " + numSesion);
        }

        //Vamos al index de tareas
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
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
        Context context = getApplicationContext();
        if (list == null)
            return;
        if (list.size() == 0) {
            Log.i("Login" , ": NO FACE");
            firstShadow = true;
            firstPosition = true;

        } else {

            Face face = list.get(0);
            bright = face.qualities.getBrightness();
            yaw = face.measurements.orientation.getYaw();
            roll = face.measurements.orientation.getRoll();
            pitch = face.measurements.orientation.getPitch();

            //Log.i("Login" , ": ORIENTATION "+ String.format("YAW %.2f",face.measurements.orientation.getYaw()));
            //Log.i("Login" , ": ORIENTATION "+ String.format("ROLL %.2f",face.measurements.orientation.getRoll()));
            //Log.i("Login" , ": ORIENTATION "+ String.format("PITCH %.2f",face.measurements.orientation.getPitch()));

            //Log.i("Login" , ": BRIGHTNESS "+ String.format(" %.2f",face.qualities.getBrightness()));

            Log.i("Login" , ": ENGAGEMENT"+ String.format(" %.2f",face.emotions.getEngagement()));
            Log.i("Login" , ": VALENCE"+ String.format(" %.2f",face.emotions.getValence()));
            Log.i("Login" , ": ATTENTION"+ String.format(" %.2f",face.expressions.getAttention()));

            Log.i("Login" , ": EXPRESSION"+ String.format(" EYE CLOSURE %.2f",face.expressions.getEyeClosure()));
            Log.i("Login" , ": EXPRESSION"+ String.format(" EYE WIDEN %.2f",face.expressions.getEyeWiden()));
            Log.i("Login" , ": EXPRESSION"+ String.format(" MOUTH OPEN %.2f",face.expressions.getMouthOpen()));

            if(face.expressions.getEyeClosure()>60){
                countEye++;
                if(countEye==5){
                    CharSequence textEyesClosed = "Hemos detectado un microsueño...¿Te estás quedando dormido?";
                    Toast toast = Toast.makeText(context, textEyesClosed, duration);
                    toast.show();
                    countEye = 0;
                }
            }
            if(face.expressions.getMouthOpen()>60){
                countMouth++;
                if(countMouth==5){
                    CharSequence textMouthOpen = "Hemos detectado un bostezo :O";
                    Toast toast = Toast.makeText(context, textMouthOpen, duration);
                    toast.show();
                    countMouth = 0;
                }
            }

            if(Math.abs(yaw)<30 && Math.abs(roll)<15 && Math.abs(pitch)<15){
                if(firstPosition){
                    firstPosition = false;
                    Toast toast = Toast.makeText(context, textPosition, duration);
                    toast.show();
                }
            }else{
                firstPosition = true;
            }

            if(bright>50 && firstBrightness){
                firstShadow = true;
                firstBrightness = false;
                Toast toast = Toast.makeText(context, textBrightness, duration);
                toast.show();
            }else if(bright<50 && firstShadow){
                firstBrightness = true;
                firstShadow = false;
                Toast toast = Toast.makeText(context, textShadow, duration);
                toast.show();
            }

            if(countAppearance<30){
                switch (face.appearance.getAge()) {
                    case AGE_UNKNOWN:
                        Log.i("Login", ": APPEARANCE AGE unknown");
                        age = "UNKNOWN";
                        break;
                    case AGE_UNDER_18:
                        Log.i("Login", ": APPEARANCE AGE under 18");
                        age = "UNDER_18";
                        break;
                    case AGE_18_24:
                        Log.i("Login", ": APPEARANCE AGE between 18 and 24");
                        age = "18_24";
                        break;
                    case AGE_25_34:
                        Log.i("Login", ": APPEARANCE AGE between 25 and 34");
                        age = "25_34";
                        break;
                    case AGE_35_44:
                        Log.i("Login", ": APPEARANCE AGE between 35 and 44");
                        age = "35_44";
                        break;
                    case AGE_45_54:
                        Log.i("Login", ": APPEARANCE AGE between 45 and 54");
                        age = "45_54";
                        break;
                    case AGE_55_64:
                        Log.i("Login", ": APPEARANCE AGE between 55 and 64");
                        age = "55_64";
                        break;
                    case AGE_65_PLUS:
                        Log.i("Login", ": APPEARANCE AGE over 65");
                        age = "OVER_65";
                        break;
                }

                switch (face.appearance.getEthnicity()) {
                    case UNKNOWN:
                        Log.i("Login", ": APPEARANCE ETHNITICY unknown");
                        ethnicity = "UNKNOWN";
                        break;
                    case CAUCASIAN:
                        Log.i("Login", ": APPEARANCE ETHNITICY caucasian");
                        ethnicity = "CAUCASIAN";
                        break;
                    case BLACK_AFRICAN:
                        Log.i("Login", ": APPEARANCE ETHNITICY black african");
                        ethnicity = "BLACK_AFRICAN";
                        break;
                    case EAST_ASIAN:
                        Log.i("Login", ": APPEARANCE ETHNITICY east asian");
                        ethnicity = "EAST_ASIAN";
                        break;
                    case SOUTH_ASIAN:
                        Log.i("Login", ": APPEARANCE ETHNITICY south asian");
                        ethnicity = "SOUTH_ASIAN";
                        break;
                    case HISPANIC:
                        Log.i("Login", ": APPEARANCE ETHNITICY hispanic");
                        ethnicity = "HISPANIC";
                        break;
                }
                switch (face.appearance.getGlasses()) {
                    case NO:
                        Log.i("Login" , ": APPEARANCE GLASSES no");
                        glasses = "NO";
                        break;
                    case YES:
                        Log.i("Login" , ": APPEARANCE GLASSES yes");
                        glasses = "YES";
                        break;
                }
                switch (face.appearance.getGender()) {
                    case UNKNOWN:
                        Log.i("Login", ": APPEARANCE GENDER unknown");
                        gender = "UNKNOWN";
                        break;
                    case MALE:
                        Log.i("Login", ": APPEARANCE GENDER male");
                        gender = "MALE";
                        break;
                    case FEMALE:
                        Log.i("Login", ": APPEARANCE GENDER female");
                        gender = "FEMALE";
                        break;
                }
            }
            countAppearance++;
            if(countAppearance==30){
                CharSequence textAppearances = "Edad: " + age + "\nSexo: " + gender + "\nEtnia: " + ethnicity + "\nGafas: " + glasses;
                Toast toast = Toast.makeText(context, textAppearances, Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    private void checkForCameraPermissions() {
        cameraPermissionsAvailable =
                ContextCompat.checkSelfPermission(
                        getApplicationContext(),
                        Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;

        if (!cameraPermissionsAvailable) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showPermissionExplanationDialog(CAMERA_PERMISSIONS_REQUEST);
            } else {
                // No explanation needed, we can request the permission.
                requestCameraPermissions();
            }
        }
    }
    private void showPermissionExplanationDialog(int requestCode) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                Login.this);

        // set title
        alertDialogBuilder.setTitle(getResources().getString(R.string.insufficient_permissions));

        // set dialog message
        if (requestCode == CAMERA_PERMISSIONS_REQUEST) {
            alertDialogBuilder
                    .setMessage(getResources().getString(R.string.permissions_camera_needed_explanation))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.understood), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            requestCameraPermissions();
                        }
                    });
        } else if (requestCode == EXTERNAL_STORAGE_PERMISSIONS_REQUEST) {
            alertDialogBuilder
                    .setMessage(getResources().getString(R.string.permissions_storage_needed_explanation))
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.understood), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            requestStoragePermissions();
                        }
                    });
        }

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void requestStoragePermissions() {
        if (!storagePermissionsAvailable) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    EXTERNAL_STORAGE_PERMISSIONS_REQUEST);

            // EXTERNAL_STORAGE_PERMISSIONS_REQUEST is an app-defined int constant that must be between 0 and 255.
            // The callback method gets the result of the request.
        }
    }

    private void requestCameraPermissions() {
        if (!cameraPermissionsAvailable) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSIONS_REQUEST);

            // CAMERA_PERMISSIONS_REQUEST is an app-defined int constant that must be between 0 and 255.
            // The callback method gets the result of the request.
        }
    }
}