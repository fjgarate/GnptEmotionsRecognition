package gnpt.app.fcomediavilla.com.gnpt;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by SalmonManin on 03/03/2017.
 */

public class StatementTR extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statement_treaction);
    }

    public void continuar(View view){
        Intent i = new Intent(this, TimeReaction.class);
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
