package gnpt.app.fcomediavilla.com.gnpt;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;
import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.Frame.ROTATE;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;
import com.affectiva.android.affdex.sdk.detector.Face.FaceQuality;


import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by fcome on 16/05/2017.
 */


public class
Login extends AppCompatActivity implements CameraDetector.CameraEventListener,Detector.ImageListener {

    private EditText editText;
    ImageButton position;
    ImageButton ilumination;
    TransitionDrawable iluminationDraw;
    TransitionDrawable positionDraw;

    public static String idUser = "";
    public static String userName = "";
    public static int numSesion = 0;
    public static String idSesion = java.util.UUID.randomUUID().toString();

    int previewWidth = 0;
    int previewHeight = 0;
    LinearLayout mainLayout;
    CameraDetector detector;
    SurfaceView cameraPreview;
    private static final int CAMERA_PERMISSIONS_REQUEST = 42;  //value is arbitrary (between 0 and 255)
    private boolean cameraPermissionsAvailable = false;

    // detecting face brightness and orientation
    float bright, yaw, roll, pitch;
    boolean firstPosition = true;
    boolean firstBrightness = true;
    boolean firstShadow = true;
    CharSequence textPosition = "¡Tu posición es perfecta!";
    CharSequence textBrightness = "¡Tu iluminación es perfecta!";
    CharSequence textShadow = "Tu iluminación es mala. Busca una fuente de luz.";
    int duration = Toast.LENGTH_SHORT;

    // detecting eye closure, widen and mouth widen expressions
    int countFramesEye = 0;
    int countFramesMouth = 0;
    int countMouth = 0;
    int countEye = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        checkForCameraPermissions();
        editText = (EditText) findViewById(R.id.login_user);
        position = (ImageButton) findViewById(R.id.position);
        ilumination = (ImageButton) findViewById(R.id.ilumination);
        iluminationDraw = (TransitionDrawable) ilumination.getDrawable();
        positionDraw = (TransitionDrawable) position.getDrawable();
        iluminationDraw.setCrossFadeEnabled(true);
        positionDraw.setCrossFadeEnabled(true);

        // CameraDetector detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT,new SurfaceView(this) );

        mainLayout = (LinearLayout) findViewById(R.id.login_main);
        cameraPreview = new SurfaceView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        cameraPreview.setLayoutParams(params);
        cameraPreview.getLayoutParams().width = 1;
        cameraPreview.getLayoutParams().height = 1;
        mainLayout.addView(cameraPreview, 0);

        detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, cameraPreview);
        detector.setDetectAllEmojis(true);
        detector.setImageListener(this);
        detector.setOnCameraEventListener(this);
        try {
            detector.start();
        } catch (Exception e) {
            Log.i("Prueba", "::: Pinta:" + e.getMessage());
        }
        Log.i("Prueba", "::: Pinta:");
    }


    public void toIndex(View v) {
        Context context = getApplicationContext();
        if(editText.getText().toString().length() == 0){
            CharSequence textEmptyInput = "Debe acceder con un nombre de usuario";
            Toast toast = Toast.makeText(context, textEmptyInput, duration);
            toast.show();
        }else{
            new ProgressTap().execute(editText.getText().toString());
        }
    }

    public void toNewActivity(String result) {
        Log.i("Login", "toNewActivity result: " + result);
        if(result.equals("exist")){
            Log.i("Login", "toNewActivity");
            idUser = editText.getText().toString();
            Log.i("Prueba", "::: idUser: " + idUser);
            String userSesion = idUser + ".sesion";
            Log.i("Prueba", "::: Nombre userSesion: " + userSesion);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            int sesion = prefs.getInt(userSesion, -1);
            Log.i("Prueba", "::: Num userSesion: " + sesion);
            if (sesion == -1) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(userSesion, 1);
                editor.commit();
                numSesion = 1;
                Log.i("Prueba", "::: idSesion actual: " + numSesion);
            } else {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(userSesion, ++sesion);
                editor.commit();
                numSesion = sesion;
                Log.i("Prueba", "::: idSesion actual: " + numSesion);
            }
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra("Procedencia","Login");
            startActivity(i);
        }else if(result.equals("notExist")){
            Toast.makeText(getApplicationContext(), "El nombre de usuario especificado no existe. Registrese pinchando en el botón 'REGISTRO'", duration).show();
        }else if(result.equals("noConnection")){
            // Si el resultado se ha guardado bien, enviamos lo que este guardado en local, si hay algo
            Database mDbHelper = new Database(getApplicationContext());

            // Gets the data repository in write mode
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            if (db != null) {
                try {
                    String queryUser = String.format("SELECT * FROM %s WHERE %s = %s", Contract.UserTable.TABLE_NAME, Contract.UserTable.USER_NAME, editText.getText().toString());
                    Cursor cursorUser = db.rawQuery(queryUser, null);
                    if(cursorUser.getCount()>0){
                        idUser = editText.getText().toString();
                        Log.i("Prueba", "::: idUser: " + idUser);
                        String userSesion = idUser + ".sesion";
                        Log.i("Prueba", "::: Nombre userSesion: " + userSesion);
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                        int sesion = prefs.getInt(userSesion, -1);
                        Log.i("Prueba", "::: Num userSesion: " + sesion);
                        if (sesion == -1) {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt(userSesion, 1);
                            editor.commit();
                            numSesion = 1;
                            Log.i("Prueba", "::: idSesion actual: " + numSesion);
                        } else {
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putInt(userSesion, ++sesion);
                            editor.commit();
                            numSesion = sesion;
                            Log.i("Prueba", "::: idSesion actual: " + numSesion);
                        }
                        Intent i = new Intent(this, MainActivity.class);
                        i.putExtra("Procedencia","Login");
                        startActivity(i);
                    }else{
                        Toast.makeText(getApplicationContext(), "El nombre de usuario especificado no existe. Registrese pinchando en el botón 'REGISTRO'", duration).show();
                    }
                }catch (Exception e) {
                    Log.e("ResultsPhotos ", "Error while saving result: " + e.toString());
                    Log.i("PruebaDatabase ", "Error while upgrading database: " + e.toString());
                    e.printStackTrace();
                }
                db.close();
            }
        }else{
            Toast.makeText(getApplicationContext(), "No podemos verificar su nombre de usuario.", duration).show();
        }
    }

    public void toRegister(View v){
        Intent i = new Intent(this, Register.class);
        startActivity(i);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    public void onCameraSizeSelected(int width, int height, Frame.ROTATE rotate) {
        Log.i("Prueba", "::: onCameraSizeSelected:");
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
            Log.i("Login", ": NO FACE");
            firstPosition = true;
            positionDraw.resetTransition();
            //firstShadow = true;
            //firstPosition = true;
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

            /*Log.i("Login", ": EXPRESSION" + String.format(" 1 - EYE CLOSURE %.2f", face.expressions.getEyeClosure()));
            Log.i("Login", ": EXPRESSION" + String.format(" 2 - EYE WIDEN %.2f", face.expressions.getEyeWiden()));
            Log.i("Login", ": EXPRESSION" + String.format(" 3 - MOUTH OPEN %.2f", face.expressions.getMouthOpen()));
            Log.i("Login", ": EXPRESSION" + String.format(" 4 - BROW FURROW %.2f", face.expressions.getBrowFurrow()));
            Log.i("Login", ": EXPRESSION" + String.format(" 5 - BROW RAISE %.2f", face.expressions.getBrowRaise()));
            Log.i("Login", ": EXPRESSION" + String.format(" 6 - CHEEK RAISE %.2f", face.expressions.getCheekRaise()));
            Log.i("Login", ": EXPRESSION" + String.format(" 7 - CHIN RAISE %.2f", face.expressions.getChinRaise()));
            Log.i("Login", ": EXPRESSION" + String.format(" 8 - DIMPLER %.2f", face.expressions.getDimpler()));
            Log.i("Login", ": EXPRESSION" + String.format(" 9 - INNER BROW RAISER %.2f", face.expressions.getInnerBrowRaise()));
            Log.i("Login", ": EXPRESSION" + String.format(" 10 - JAW DROP %.2f", face.expressions.getJawDrop()));
            Log.i("Login", ": EXPRESSION" + String.format(" 11 - LID TIGHTEN %.2f", face.expressions.getLidTighten()));
            Log.i("Login", ": EXPRESSION" + String.format(" 12 - LIP CORNER DEPRESSOR %.2f", face.expressions.getLipCornerDepressor()));
            Log.i("Login", ": EXPRESSION" + String.format(" 13 - LIP PRESS %.2f", face.expressions.getLipPress()));
            Log.i("Login", ": EXPRESSION" + String.format(" 14 - LIP PUCKER %.2f", face.expressions.getLipPucker()));
            Log.i("Login", ": EXPRESSION" + String.format(" 15 - LIP STRETCH %.2f", face.expressions.getLipStretch()));
            Log.i("Login", ": EXPRESSION" + String.format(" 16 - LIP SUCK %.2f", face.expressions.getLipSuck()));
            Log.i("Login", ": EXPRESSION" + String.format(" 17 - NOSE WRINKLE %.2f", face.expressions.getNoseWrinkle()));
            Log.i("Login", ": EXPRESSION" + String.format(" 18 - SMILE %.2f", face.expressions.getSmile()));
            Log.i("Login", ": EXPRESSION" + String.format(" 19 - SMIRK %.2f", face.expressions.getSmirk()));
            Log.i("Login", ": EXPRESSION" + String.format(" 20 - UPPER LIP RAISE %.2f", face.expressions.getUpperLipRaise()));*/

            /*Log.i("Login", ": EMOJI" + String.format(" 1 - SMIRK %.2f", face.emojis.getSmirk()));
            Log.i("Login", ": EMOJI" + String.format(" 2 - SMIRK %.2f", face.emojis.getDisappointed()));
            Log.i("Login", ": EMOJI" + String.format(" 3 - SMIRK %.2f", face.emojis.getDominant()));
            Log.i("Login", ": EMOJI" + String.format(" 4 - SMIRK %.2f", face.emojis.getFlushed()));
            Log.i("Login", ": EMOJI" + String.format(" 5 - SMIRK %.2f", face.emojis.getKissing()));
            Log.i("Login", ": EMOJI" + String.format(" 6 - SMIRK %.2f", face.emojis.getLaughing()));
            Log.i("Login", ": EMOJI" + String.format(" 7 - SMIRK %.2f", face.emojis.getRage()));
            Log.i("Login", ": EMOJI" + String.format(" 8 - SMIRK %.2f", face.emojis.getRelaxed()));
            Log.i("Login", ": EMOJI" + String.format(" 9 - SMIRK %.2f", face.emojis.getScream()));
            Log.i("Login", ": EMOJI" + String.format(" 10 - SMIRK %.2f", face.emojis.getSmiley()));
            Log.i("Login", ": EMOJI" + String.format(" 11 - SMIRK %.2f", face.emojis.getStuckOutTongue()));
            Log.i("Login", ": EMOJI" + String.format(" 12 - SMIRK %.2f", face.emojis.getStuckOutTongueWinkingEye()));
            Log.i("Login", ": EMOJI" + String.format(" 13 - SMIRK %.2f", face.emojis.getWink()));*/



            /*if (face.expressions.getEyeClosure() > 60) {
                countFramesEye++;
                if (countFramesEye == 5) {
                    CharSequence textEyesClosed = "Hemos detectado un microsueño...¿Te estás quedando dormido?";
                    Toast toast = Toast.makeText(context, textEyesClosed, duration);
                    toast.show();
                    countFramesEye = 0;
                    countEye++;
                }
            }
            if (face.expressions.getMouthOpen() > 60) {
                countFramesMouth++;
                if (countFramesMouth == 5) {
                    CharSequence textMouthOpen = "Hemos detectado un bostezo :O";
                    Toast toast = Toast.makeText(context, textMouthOpen, duration);
                    toast.show();
                    countFramesMouth = 0;
                    countMouth++;
                }
            }*/

            if (Math.abs(yaw) < 40 && Math.abs(roll) < 20 && Math.abs(pitch) < 20) {
                if (firstPosition) {
                    firstPosition = false;
                    //Toast toast = Toast.makeText(context, textPosition, duration);
                    //toast.show();
                    positionDraw.startTransition(5);
                }
            } else {
                firstPosition = true;
                positionDraw.resetTransition();
            }

            if (bright > 50 && firstBrightness) {
                firstShadow = true;
                firstBrightness = false;
                //Toast toast = Toast.makeText(context, textBrightness, duration);
                //toast.show();
                iluminationDraw.startTransition(5);
            } else if (bright < 50 && firstShadow) {
                firstBrightness = true;
                firstShadow = false;
                Toast toast = Toast.makeText(context, textShadow, duration);
                toast.show();
                iluminationDraw.reverseTransition(5);
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
        }

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
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
    }

    private class ProgressTap extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... params) {
            Context context = getApplicationContext();
            ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            Log.i("Login DOINBACKGROUND", "Llega?");

            if (isConnected) {
                User user = new User(params[0]);
                String jsonData = user.toJSON();
                //DEPARTAMENTO
                //String baseUrl = "http://138.4.10.143:8443/userlogin";
                //NECN
                //String baseUrl = "http://192.168.0.189:8443/userlogin";
                //CASA
                //String baseUrl = "http://192.168.0.158:8443/userlogin";
                //MOVIL
                String baseUrl = "http://192.168.43.119:8443/userlogin";
                BufferedReader br = null;
                JSONObject jsonObj = new JSONObject();

                try {
                    Log.i("Login TRY", "Llega?" + jsonData);
                    //Send Http PUT request to: "http://some.url" with request header:

                    URL url = new URL(baseUrl);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setReadTimeout(10000);
                    con.setConnectTimeout(15000);
                    con.setRequestMethod("POST");
                    con.setDoInput(true);
                    con.setDoOutput(true);

                    Uri.Builder builder = new Uri.Builder().appendQueryParameter("user", jsonData);
                    String query = builder.build().getEncodedQuery();

                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
                    wr.write(query);
                    wr.flush();

                    StringBuilder sb = new StringBuilder();
                    int HttpResult = con.getResponseCode();
                    if (HttpResult == HttpURLConnection.HTTP_OK) {
                        br = new BufferedReader(
                                new InputStreamReader(con.getInputStream(), "utf-8"));
                        String line = null;
                        while ((line = br.readLine()) != null) {
                            sb.append(line + "\n");
                        }
                        br.close();
                        //Log.i("Login HTTP_OK - String", "::: " + sb.toString());
                        jsonObj = new JSONObject(sb.toString());
                        Log.i("Login HTTP_OK - JSON", "::: " + jsonObj);
                        System.out.println("" + sb.toString());
                    } else {
                        Log.i("Login HTTP_FAIL", "::: " + con.getResponseMessage());
                        System.out.println(con.getResponseMessage());
                    }
                    return jsonObj.getString("msg");

                } catch (Exception e) {
                    Log.i("Connection error", "::: " + e.getMessage());
                    //return jsonObj;
                    return "Error "+ e.getMessage();
                }
            }else{
                return "noConnection";
            }
        }
        protected void onPostExecute(String result) {
            Log.i("Login","onPostExecute");
            toNewActivity(result);
        }
    }
}

