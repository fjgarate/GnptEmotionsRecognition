package gnpt.app.fcomediavilla.com.gnpt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/*
 * Created by Francisco Mediavilla Sala
 *
 * Clase index para elegir tarea.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*
     * Método escuchador de eventos. Botón Textmemory1.
     *
     * Arranca esta tarea comenzando por la pantalla Initial
     */
    public void toInitial(View v){
        Intent i = new Intent(this, Initial.class);
        switch (v.getId()){
            case R.id.button_textmemory1:
                i.putExtra("keyName", "Textmemory1");
                break;
            case R.id.button_timereaction:
                i.putExtra("keyName", "TimeReaction");
                break;
            case R.id.button_photos:
                i.putExtra("keyName", "Photos");
                break;
            case R.id.button_tap:
                i.putExtra("keyName", "Tap");
                break;
            case R.id.button_flexibility:
                i.putExtra("keyName","Flexibility");
                break;
            case R.id.button_textmemory2:
                i.putExtra("keyName", "Textmemory2");
                break;
        }
        startActivity(i);
    }
}
