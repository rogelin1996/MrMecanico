package mx.avidos.mrmecanicoch;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

import mx.avidos.mrmecanicoch.Bases.BaseActivity;

public class SplashActivity extends BaseActivity {
    private final static int WAIT_MILI = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                comprobarSesion();
            }
        };
        Timer timer = new Timer();
        timer.schedule(timerTask, WAIT_MILI);

    }
}
