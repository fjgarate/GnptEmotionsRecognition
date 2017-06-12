package gnpt.app.fcomediavilla.com.gnpt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by fcome on 07/04/2017.
 */

public class StatementTap extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statement_tap);
    }

    public void continuar(View view){
        Intent i = new Intent(this, Tap.class);
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
