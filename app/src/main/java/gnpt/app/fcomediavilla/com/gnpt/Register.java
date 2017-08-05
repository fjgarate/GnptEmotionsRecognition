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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.affectiva.android.affdex.sdk.Frame;
import com.affectiva.android.affdex.sdk.detector.CameraDetector;
import com.affectiva.android.affdex.sdk.detector.Detector;
import com.affectiva.android.affdex.sdk.detector.Face;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import static gnpt.app.fcomediavilla.com.gnpt.R.id.editText;
import static gnpt.app.fcomediavilla.com.gnpt.R.id.text;

/**
 * Created by alvaro on 22/06/2017.
 */

public class Register extends AppCompatActivity implements CameraDetector.CameraEventListener,Detector.ImageListener {

    EditText editText;
    Spinner ageText, genderText, ethnicityText, glassesText;
    ArrayAdapter<CharSequence> array;
    String user_name, user_age, user_gender, user_ethnicity, user_glasses;
    int duration = Toast.LENGTH_SHORT;
    public static String idUser = "";
    public static boolean justRegistered = false;
    public static int numSesion = 0;

    // detecting appearance
    String age, ethnicity, glasses, gender;
    //int countAppearance = 0;

    // Affectiva
    int previewWidth = 0;
    int previewHeight = 0;
    LinearLayout mainLayout;
    CameraDetector detector;
    SurfaceView cameraPreview;
    private static final int CAMERA_PERMISSIONS_REQUEST = 42;  //value is arbitrary (between 0 and 255)
    private boolean cameraPermissionsAvailable = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        checkForCameraPermissions();

        editText = (EditText) findViewById(R.id.editText2);
        ageText = (Spinner) findViewById(R.id.register_spinner_age);
        genderText = (Spinner) findViewById(R.id.register_spinner_gender);
        ethnicityText = (Spinner) findViewById(R.id.register_spinner_ethnicity);
        glassesText = (Spinner) findViewById(R.id.register_spinner_glasses);

        array = ArrayAdapter.createFromResource(this, R.array.entries_gender, R.layout.simple_spinner_item);
        array.setDropDownViewResource(R.layout.simple_spinner_item);
        genderText.setAdapter(array);
        array = ArrayAdapter.createFromResource(this, R.array.entries_age, R.layout.simple_spinner_item);
        array.setDropDownViewResource(R.layout.simple_spinner_item);
        ageText.setAdapter(array);
        array = ArrayAdapter.createFromResource(this, R.array.entries_ethnicity, R.layout.simple_spinner_item);
        array.setDropDownViewResource(R.layout.simple_spinner_item);
        ethnicityText.setAdapter(array);
        array = ArrayAdapter.createFromResource(this, R.array.entries_glasses, R.layout.simple_spinner_item);
        array.setDropDownViewResource(R.layout.simple_spinner_item);
        glassesText.setAdapter(array);

        mainLayout = (LinearLayout) findViewById(R.id.register_main);
        cameraPreview = new SurfaceView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        cameraPreview.setLayoutParams(params);
        cameraPreview.getLayoutParams().width = 1;
        cameraPreview.getLayoutParams().height = 1;
        mainLayout.addView(cameraPreview, 0);

        detector = new CameraDetector(this, CameraDetector.CameraType.CAMERA_FRONT, cameraPreview);

        detector.setDetectAllAppearances(true);
        detector.setImageListener(this);
        detector.setOnCameraEventListener(this);
        try {
            detector.start();
        } catch (Exception e) {
            Log.i("Prueba", "::: Pinta:" + e.getMessage());
        }
        Log.i("Prueba", "::: Pinta:");
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
        if (list == null)
            return;
        if (list.size() == 0) {
            Log.i("Login", ": NO FACE");
            //firstShadow = true;
            //firstPosition = true;

        } else {
            Face face = list.get(0);
            //if(countAppearance<30){
            switch (face.appearance.getAge()) {
                case AGE_UNKNOWN:
                    Log.i("Register", ": APPEARANCE AGE unknown");
                    age = "UNKNOWN";
                    break;
                case AGE_UNDER_18:
                    Log.i("Register", ": APPEARANCE AGE under 18");
                    age = "UNDER_18";
                    break;
                case AGE_18_24:
                    Log.i("Register", ": APPEARANCE AGE between 18 and 24");
                    age = "18_24";
                    break;
                case AGE_25_34:
                    Log.i("Register", ": APPEARANCE AGE between 25 and 34");
                    age = "25_34";
                    break;
                case AGE_35_44:
                    Log.i("Register", ": APPEARANCE AGE between 35 and 44");
                    age = "35_44";
                    break;
                case AGE_45_54:
                    Log.i("Register", ": APPEARANCE AGE between 45 and 54");
                    age = "45_54";
                    break;
                case AGE_55_64:
                    Log.i("Register", ": APPEARANCE AGE between 55 and 64");
                    age = "55_64";
                    break;
                case AGE_65_PLUS:
                    Log.i("Register", ": APPEARANCE AGE over 65");
                    age = "OVER_65";
                    break;
            }

            switch (face.appearance.getEthnicity()) {
                case UNKNOWN:
                    Log.i("Register", ": APPEARANCE ETHNITICY unknown");
                    ethnicity = "UNKNOWN";
                    break;
                case CAUCASIAN:
                    Log.i("Register", ": APPEARANCE ETHNITICY caucasian");
                    ethnicity = "CAUCASIAN";
                    break;
                case BLACK_AFRICAN:
                    Log.i("Register", ": APPEARANCE ETHNITICY black african");
                    ethnicity = "BLACK_AFRICAN";
                    break;
                case EAST_ASIAN:
                    Log.i("Register", ": APPEARANCE ETHNITICY east asian");
                    ethnicity = "EAST_ASIAN";
                    break;
                case SOUTH_ASIAN:
                    Log.i("Register", ": APPEARANCE ETHNITICY south asian");
                    ethnicity = "SOUTH_ASIAN";
                    break;
                case HISPANIC:
                    Log.i("Register", ": APPEARANCE ETHNITICY hispanic");
                    ethnicity = "HISPANIC";
                    break;
            }
            switch (face.appearance.getGlasses()) {
                case NO:
                    Log.i("Register", ": APPEARANCE GLASSES no");
                    glasses = "NO";
                    break;
                case YES:
                    Log.i("Register", ": APPEARANCE GLASSES yes");
                    glasses = "YES";
                    break;
            }
            switch (face.appearance.getGender()) {
                case UNKNOWN:
                    Log.i("Register", ": APPEARANCE GENDER unknown");
                    gender = "UNKNOWN";
                    break;
                case MALE:
                    Log.i("Register", ": APPEARANCE GENDER male");
                    gender = "MALE";
                    break;
                case FEMALE:
                    Log.i("Register", ": APPEARANCE GENDER female");
                    gender = "FEMALE";
                    break;
            }
            /*
            countAppearance++;
            if(countAppearance==30){
                CharSequence textAppearances = "Edad: " + age + "\nSexo: " + gender + "\nEtnia: " + ethnicity + "\nGafas: " + glasses;
                Toast toast = Toast.makeText(context, textAppearances, Toast.LENGTH_LONG);
                toast.show();
            }*/
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
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Register.this);

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
        detector.setDetectAllAppearances(false);
    }

    private class ProgressTap extends AsyncTask<String, Integer, String> {
        protected String doInBackground(String... params) {
            Log.i("Register", " Llega a doInBackground");
            User user = new User(params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7], params[8]);
            String jsonData = user.toJSON();
            //DEPARTAMENTO
            //String baseUrl = "http://138.4.10.143:8443/saveuser";
            //NECN
            //String baseUrl = "http://192.168.0.189:8443/saveuser";
            //MOVIL
            String baseUrl = "http://192.168.43.119:8443/saveuser";
            BufferedReader br = null;
            JSONObject jsonObj = new JSONObject();

            try {
                Log.i("Register", " Llega al try");
                //Send Http PUT request to: "http://some.url" with request header:
                URL url = new URL(baseUrl);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(10000);
                con.setConnectTimeout(15000);
                con.setRequestMethod("POST");
                con.setDoInput(true);
                con.setDoOutput(true);

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("user", jsonData);
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
                    Log.i("Register HTTP_OK", "::: " + sb.toString());
                    jsonObj = new JSONObject(sb.toString());
                    Log.i("Register HTTP_OK", "::: " + jsonObj);
                    System.out.println("" + sb.toString());
                } else {
                    Log.i("Register HTTP_FAIL", "::: " + con.getResponseMessage());
                    System.out.println(con.getResponseMessage());
                }
                return jsonObj.getString("msg");

            } catch (Exception e) {
                Log.i("Register ", "::: Connection error = " + e.getMessage());
                //return jsonObj;
                return "Error";
            }
        }
        protected void onPostExecute(String result) {
            Log.i("Login","onPostExecute");
            toNewActivity(result);
        }
    }

    public void save(View v){
        Context context = getApplicationContext();
        user_name = editText.getText().toString();
        user_age = getResources().getStringArray(R.array.entries_age_values)[ageText.getSelectedItemPosition()];
        user_gender = getResources().getStringArray(R.array.entries_gender_values)[genderText.getSelectedItemPosition()];
        user_ethnicity = getResources().getStringArray(R.array.entries_ethnicity_values)[ethnicityText.getSelectedItemPosition()];
        user_glasses = getResources().getStringArray(R.array.entries_glasses_values)[glassesText.getSelectedItemPosition()];
        if(user_name.equals("") || user_age.equals("") || user_gender.equals("") || user_ethnicity.equals("") ||user_glasses.equals("")){
            CharSequence textEmptyField = "Existe algún campo vacío. Rellénelo para proceder a su registro.";
            Toast toast = Toast.makeText(context, textEmptyField, duration);
            toast.show();
        }else{
            justRegistered = true;
            new ProgressTap().execute(user_name, user_age, user_gender, user_ethnicity, user_glasses, age, gender, ethnicity, glasses);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, Login.class);
        intent.putExtra("Procedencia", "Register");
        startActivity(intent);
    }

    public void toNewActivity(String result) {
        Log.i("Register", "toNewActivity result: " + result);
        Context context = getApplicationContext();
        if (result.equals("correcto")) {
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

            /*long resultId;
            // Si el resultado se ha guardado bien, enviamos lo que este guardado en local, si hay algo
            Database mDbHelper = new Database(getApplicationContext());

            // Gets the data repository in write mode
            SQLiteDatabase db = mDbHelper.getReadableDatabase();

            if (db != null) {
                try {
                    // Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put(Contract.ResultsTable.USER_NAME, idUser);

                    // Insert the new row, returning the primary key value of the new row
                    resultId = db.insert(Contract.ResultsTable.TABLE_NAME, null, values);
                    if (resultId != -1) {
                        Log.i("PruebaDatabase ", "New user inserted in results table with id " + resultId);
                    } else {
                        Log.i("PruebaDatabase ", "Error when inserting user in results table");
                    }

                } catch (Exception e) {
                    Log.e("ResultsPhotos ", "Error while saving result: " + e.toString());
                    Log.i("PruebaDatabase ", "Error while upgrading database: " + e.toString());
                    e.printStackTrace();
                }
                db.close();
            }*/
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else if(result.equals("existe")) {
            Toast.makeText(context,"El nombre de usuario especificado ya existe.", duration).show();
        }else{
            CharSequence textError = "Se ha producido un error. Inténtelo de nuevo.";
            Toast toast = Toast.makeText(context, textError, duration);
            toast.show();
        }
    }
}