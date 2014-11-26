package com.example.mcornett.testapp;


        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.lang.reflect.Method;
        import java.util.UUID;

        import com.example.mcornett.testapp.R;

        import android.app.Activity;
        import android.bluetooth.BluetoothAdapter;
        import android.bluetooth.BluetoothDevice;
        import android.bluetooth.BluetoothSocket;
        import android.content.Intent;
        import android.media.MediaPlayer;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Handler;
        import android.util.Log;
        import android.view.View;
        import android.view.View.OnClickListener;
        import android.widget.Button;
        import android.widget.ImageButton;
        import android.widget.TextView;
        import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String TAG = "bluetooth1";

    Button btnOn, btnOff;
    Handler handler;
    final int RECIEVE_MESSAGE = 1;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private InputStream inStream = null;
    private StringBuilder sb = new StringBuilder();
    private ConnectedThread mConnectedThread;
    private static char useSound = 'y';
    private static char useLights = 'y';
    public static int MAX_VOLUME = 50;
    public static int[] MUSIC = new int [] {
            R.raw.dyehl, R.raw.dd, R.raw.wotb
    };
    public static int[] SFX = new int [] {
            R.raw.boing, R.raw.giggles
    };

    // SPP UUID service
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC-address of Bluetooth module (you must edit this line)
    private static String address = "98:D3:31:30:0C:5C";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        btnOn = (Button) findViewById(R.id.btnOn);
        btnOff = (Button) findViewById(R.id.btnOff);
        handler = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:                                                   // if receive massage
                        byte[] readBuf = (byte[]) msg.obj;
                        String strIncom = new String(readBuf, 0, msg.arg1);                 // create string from bytes array
                        sb.append(strIncom);                                                // append string
                        int endOfLineIndex = sb.indexOf("\r\n");                            // determine the end-of-line
                        if (endOfLineIndex > 0) {                                            // if end-of-line,

                            String sbprint = sb.substring(0, endOfLineIndex).toString();
                            String temp = "222".toString();
                            if (sbprint.equals("222")) {
                                MediaPlayer m = MediaPlayer.create(getBaseContext(), R.raw.boing);
                                m.start();
                            }
                            sb.delete(0, sb.length());                                      // and clear
                            btnOff.setEnabled(true);
                            btnOn.setEnabled(true);
                        }
                        //Log.d(TAG, "...String:"+ sb.toString() +  "Byte:" + msg.arg1 + "...");
                        break;
                }
            };
        };
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        checkBTState();

        btnOn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                //btnOn.setEnabled(false);
                mConnectedThread.write("000");
            }
        });

        btnOff.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {

                mConnectedThread.write("000");

            }
        });
        setupSoundOnBtn();
        setupSoundOffBtn();
        setupLightsOnBtn();
        setupLightsOffBtn();
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e1) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e1.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        btAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "...Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
            }
        }

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }

    private void checkBTState() {
        // Check for Bluetooth support and then check to make sure it is turned on
        // Emulator doesn't support Bluetooth and will return null
        if(btAdapter==null) {
            errorExit("Fatal Error", "Bluetooth not support");
        } else {
            if (btAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth ON...");
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private class ConnectedThread extends Thread {


        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            inStream = tmpIn;
            outStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = inStream.read(buffer);        // Get number of bytes and message in "buffer"
                    handler.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();     // Send to message queue Handler
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
               outStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }

    private void setupSoundOnBtn() {
        ImageButton soundOnBtn = (ImageButton) findViewById(R.id.soundOnBtn);
        soundOnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                useSound = 'y';
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
                useSound = 'n';
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
                useLights = 'y';
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
                useLights = 'n';
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
        return (float)(Math.log(MAX_VOLUME-volume)/Math.log(MAX_VOLUME));
    }

    public void playMusic(int track_index, int volume) {
        MediaPlayer mediaPlayer = new MediaPlayer().create(this, MUSIC[track_index]);
        float media_volume= 1- convert_volume(volume);
        mediaPlayer.setVolume(media_volume, media_volume);
        mediaPlayer.start();
    }

    public void playSfx(int track_index, int volume) {
        MediaPlayer mediaPlayer = new MediaPlayer().create(this, SFX[track_index]);
        float media_volume= 1- convert_volume(volume);
        mediaPlayer.setVolume(media_volume, media_volume);
        mediaPlayer.start();
    }
}

