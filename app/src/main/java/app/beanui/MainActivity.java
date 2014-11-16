package app.beanui;

import android.app.Activity;
import android.app.Application;
import android.media.MediaPlayer;
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
    }

    private void setupSoundOnBtn() {
        ImageButton soundOnBtn = (ImageButton) findViewById(R.id.soundOnBtn);

        soundOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Constants.use_sound = true;
                Toast.makeText(
                        MainActivity.this,"Yes sound",Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void setupSoundOffBtn() {
        ImageButton soundOffBtn = (ImageButton) findViewById(R.id.soundOffBtn);

        soundOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.use_sound = false;
                Toast.makeText(
                        MainActivity.this,"No sound",Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void setupLightsOnBtn() {
        ImageButton lightsOnBtn = (ImageButton) findViewById(R.id.lightsOnBtn);

        lightsOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.use_lights = true;
                Toast.makeText(
                        MainActivity.this,"Yes lights",Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    private void setupLightsOffBtn() {
        ImageButton lightsOffBtn = (ImageButton) findViewById(R.id.lightsOffBtn);

        lightsOffBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.use_lights = false;
                Toast.makeText(
                        MainActivity.this,"No lights",Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    public void DJ(int sound_type, int track_index, int volume) {
        if (sound_type == 1)
            playMusic(track_index,volume);
            /* TODO: stop current music playing */
        else
            playSfx(track_index, volume);
    }

    public float convert_volume(int volume) {
        return (float)(Math.log(Constants.MAX_VOLUME-volume)/Math.log(Constants.MAX_VOLUME));
    }

    public void playMusic(int track_index, int volume) {
        MediaPlayer mediaPlayer = new MediaPlayer().create(this, Constants.MUSIC[track_index]);
        float media_volume= 1- convert_volume(volume);
        mediaPlayer.setVolume(media_volume, media_volume);
        mediaPlayer.start();
    }

    public void playSfx(int track_index, int volume) {
        MediaPlayer mediaPlayer = new MediaPlayer().create(this, Constants.SFX[track_index]);
        float media_volume= 1- convert_volume(volume);
        mediaPlayer.setVolume(media_volume, media_volume);
        mediaPlayer.start();
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
