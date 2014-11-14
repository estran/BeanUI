package app.beanui;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

public class Constants extends Application {
    public static boolean use_lights = true;
    public static boolean use_sound = true;
    public static boolean is_on = false;
    List<String> music_filenames = new ArrayList<String>();
    List<String> sfx_filenames = new ArrayList<String>();
}
