package gnpt.app.fcomediavilla.com.gnpt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Francisco Mediavilla Sala
 *
 * Clase con el logo de carga antes de cada tarea
 */

public class Initial extends AppCompatActivity {

    Handler handler;
    Runnable runnable;
    String previo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial);
        previo = getIntent().getStringExtra("Procedencia");

        try{
            Thread.sleep(2000);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                switch (getIntent().getStringExtra("keyName")){
                    case "Textmemory1":
                        Intent intent1 = new Intent(Initial.this, StatementTM1.class);
                        startActivity(intent1);
                        break;
                    case "TimeReaction":
                        Intent intent2 = new Intent(Initial.this, StatementTR.class);
                        startActivity(intent2);
                        break;
                    case "Photos":
                        Intent intent3 = new Intent(Initial.this, StatementPH.class);
                        startActivity(intent3);
                        break;
                    case "Tap":
                        Intent intent4 = new Intent(Initial.this, StatementTap.class);
                        startActivity(intent4);
                        break;
                    case "Flexibility":
                        Intent intent5 = new Intent(Initial.this, StatementFlex.class);
                        startActivity(intent5);
                        break;
                    case "Textmemory2":
                        Intent intent6 = new Intent(Initial.this, StatementTM2.class);
                        startActivity(intent6);
                        break;
                }
            }
        };

        //Pasado un segundo avanzamos a la siguiente actividad, en este caso el enunciado de Textmemory
        handler.postDelayed(runnable, 1000);
    }

    /*
     * Método sobreescrito para el botón Back.
     *
     * Se cancela la hebra y vuelves al index.
     */
    @Override
    public void onBackPressed() {
        handler.removeCallbacks(runnable);
        if(previo.equals("Login")) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }else if(previo.equals("Register")){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

}
