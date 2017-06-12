package gnpt.app.fcomediavilla.com.gnpt;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

/**
 * Created by Francisco Mediavilla Sala
 *
 * Clase del reloj previa a las preguntas
 */

public class Clock extends AppCompatActivity {

    AnimationDrawable animation;
    ImageView image;
    Handler handler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clock);

        //Creación de la animación del reloj
        image = (ImageView) findViewById(R.id.clock);
        animation = (AnimationDrawable)image.getDrawable();
        animation.start();

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                final Intent intent = new Intent(Clock.this, Questions.class);
                intent.putExtra("Tarea", "Textmemory");
                startActivity(intent);
            }
        };
        //Espera 4 segundos (tiempo de la animación) antes de dar paso a las preguntas
        handler.postDelayed(runnable, 4000);

    }

    /*
     * Método sobreescrito para el botón Back.
     *
     * Se cancela la hebra y vuelves al index.
     */
    @Override
    public void onBackPressed() {
        handler.removeCallbacks(runnable);
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

}
