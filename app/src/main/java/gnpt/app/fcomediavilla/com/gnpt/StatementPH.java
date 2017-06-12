package gnpt.app.fcomediavilla.com.gnpt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by SalmonManin on 09/03/2017.
 */

public class StatementPH extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statement_photos);
    }

    /*
     * Método escuchador de eventos. Botón continuar blanco.
     *
     * Inicia la actividad de Photos para dar comienzo a la tarea.
     */
    public void continuar(View view){
        Intent i = new Intent(this, Photos.class);
        startActivity(i);
    }

    /*
     * Método sobreescrito para el botón Back.
     *
     * Vuelta al index.
     */
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}
