package app.beanui;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupSoundOnBtn();
        setupSoundOffBtn();
        setupLightsOnBtn();
        setupLightsOffBtn();
        setupStartBtn();
    }

    private void setupSoundOnBtn() {
        ImageButton soundOnBtn = (ImageButton) findViewById(R.id.soundOnBtn);

        soundOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Constants.use_sound = true;
            }
        });
    }

    private void setupSoundOffBtn() {
        ImageButton soundOffBtn = (ImageButton) findViewById(R.id.soundOffBtn);

        soundOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.use_sound = false;
            }
        });
    }

    private void setupLightsOnBtn() {
        ImageButton lightsOnBtn = (ImageButton) findViewById(R.id.lightsOnBtn);

        lightsOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.use_lights = true;
            }
        });
    }

    private void setupLightsOffBtn() {
        ImageButton lightsOffBtn = (ImageButton) findViewById(R.id.lightsOffBtn);

        lightsOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.use_lights = false;
            }
        });
    }

    private void setupStartBtn() {
        ImageButton startBtn = (ImageButton) findViewById(R.id.startButton);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.is_on = true;
                Toast.makeText(
                        MainActivity.this,"Starting",Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
