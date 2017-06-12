package gnpt.app.fcomediavilla.com.gnpt;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by SalmonManin on 03/03/2017.
 */

public class HelpTR extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_treaction);
    }

    /*public void continuar(View view){
        Intent i = new Intent(this, TimeReaction.class);
        startActivity(i);
    }

    /*
    * Método sobreescrito para el botón Back.
    *
    * Se cancela la hebra y vuelves al index.
    */
    /*
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,TimeReaction.class);
        startActivity(intent);
    }*/
}
