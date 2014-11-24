package app.beanui;

import android.app.Activity;

import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import android.os.Bundle;
import android.os.ParcelUuid;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import android.content.Intent;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 1;
    private String btMacAddress = "98:D3:31:30:0C:5C";
    private static final String TAG = "Tag";
    private boolean canRead = true;
    private Handler handler;
    private OutputStream oStream;
    private InputStream iStream;
    private BluetoothSocket btSocket = null;
    private BluetoothAdapter btAdapter;
    byte delimiter = 12;
    int readBufferPosition = 0;
    byte[] readBuffer = new byte[1024];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up all Bluetooth stuff first
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null) {
            Toast.makeText(getApplicationContext(),
                    "This device does not support Bluetooth and will play " +
                            "in Lights-only mode.",
                    Toast.LENGTH_LONG).show();
        }
        setupSoundOnBtn();
        setupSoundOffBtn();
        setupLightsOnBtn();
        setupLightsOffBtn();
        setupOnBtn();
    }

    private void setupOnBtn() {
        ImageButton onBtn = (ImageButton) findViewById(R.id.onBtn);
        onBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnBTOn();
                connectToBT();
                sendSettings(Constants.use_lights, Constants.use_sound);
                Toast.makeText(
                        MainActivity.this,"Connecting",Toast.LENGTH_LONG)
                        .show();
            }
        });
    }

    public void connectToBT() {
        Log.d(TAG, btMacAddress);
        BluetoothDevice device = btAdapter.getRemoteDevice(btMacAddress);
        ParcelUuid[] uuids;
        uuids = device.getUuids();
        Log.d(TAG, "Connecting...");
        btAdapter.cancelDiscovery();
        try {
            btSocket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());
            Log.d(TAG, "got UUid");
            btSocket.connect();
            Log.d(TAG, "Connected!");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e1) {
                Log.d(TAG, "Cannot close connection");
                e1.printStackTrace();
            }
            Log.d(TAG, "Cannot create socket");
            e.printStackTrace();
        }
        listenForData();
    }

    private void sendSettings(char firstSetting, char secondSetting) {
        try {
            oStream = btSocket.getOutputStream();
        } catch (IOException e) {
            Log.d(TAG, "Error before sending settings", e);
            e.printStackTrace();
        }
        String message = Character.toString(firstSetting) + Character.toString(secondSetting);
        byte[] msgBuffer = message.getBytes();

        try {
            oStream.write(msgBuffer);
        } catch (IOException e) {
            Log.d(TAG, "Error while sending settings", e);
            e.printStackTrace();
        }
    }

    public void listenForData()   {
        try {
            iStream = btSocket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Thread readThread = new Thread(new Runnable() {
            public void run()   {
                while(!Thread.currentThread().isInterrupted() && canRead)   {
                    try {
                        int bytesAvailable = iStream.available();
                        if(bytesAvailable > 0)  {
                            byte[] packetBytes = new byte[bytesAvailable];
                            iStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)   {
                                byte b = packetBytes[i];
                                if(b == delimiter)  {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable() {
                                        public void run()   {
                                            Toast.makeText(getApplicationContext(),
                                                    data,
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                else {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException e)  {
                        canRead = false;
                        e.printStackTrace();
                    }
                }
            }
        });
        readThread.start();
    }

    public void turnBTOn(){
        if (!btAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
            Toast.makeText(getApplicationContext(),"Bluetooth turned on" ,
                    Toast.LENGTH_LONG).show();
        }
        else    {
            Toast.makeText(getApplicationContext(),"Bluetooth is already on",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            btSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupSoundOnBtn() {
        ImageButton soundOnBtn = (ImageButton) findViewById(R.id.soundOnBtn);
        soundOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constants.use_sound = 'y';
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
                Constants.use_sound = 'n';
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
                Constants.use_lights = 'y';
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
                Constants.use_lights = 'n';
                DJ(0,0,50);
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

}