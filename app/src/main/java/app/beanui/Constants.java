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

import java.util.ArrayList;
import java.util.List;

public class Constants extends Application {
    public static char use_lights = 'y';
    public static char use_sound = 'y';
    public static boolean is_on = false;
    public static int MAX_VOLUME = 50;
    public static int[] MUSIC = new int [] {
            R.raw.test1, R.raw.test2, R.raw.test3
    };
    public static int[] SFX = new int [] {
            R.raw.click, R.raw.click, R.raw.click
    };
}
