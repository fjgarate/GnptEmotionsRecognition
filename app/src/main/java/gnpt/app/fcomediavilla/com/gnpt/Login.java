package gnpt.app.fcomediavilla.com.gnpt;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by fcome on 16/05/2017.
 */


public class Login extends AppCompatActivity implements CameraDetector.CameraEventListener  {

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        checkForCameraPermissions();
        editText = (EditText) findViewById(R.id.login_user);
      //  CameraDetector detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT,new SurfaceView(this) );
        mainLayout = (LinearLayout) findViewById(R.id.login_main);


        cameraPreview = new SurfaceView(this) {
            @Override
            public void onMeasure(int widthSpec, int heightSpec) {
               // int measureWidth = MeasureSpec.getSize(widthSpec);
               // int measureHeight = MeasureSpec.getSize(heightSpec);
                 int measureWidth = 220;
                 int measureHeight = 220;
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
        };
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        cameraPreview.setLayoutParams(params);
        mainLayout.addView(cameraPreview,0);
        detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, cameraPreview);
        detector.setDetectSmile(true);
        detector.setDetectAge(true);
        detector.setDetectEthnicity(true);

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
    }
*/ Log.i("Prueba" , "::: Pinta:");
       // detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, cameraPreview);
       // detector.setDetectSmile(true);

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
