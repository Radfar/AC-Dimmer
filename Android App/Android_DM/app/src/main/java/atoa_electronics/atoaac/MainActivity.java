package atoa_electronics.atoaac;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import static atoa_electronics.atoaac.R.drawable.background_selector;
import static atoa_electronics.atoaac.R.drawable.background_selector_2;


public class MainActivity extends Activity {

    ImageButton btnUp, btnDown, btnPower;Button btnDis;
    TextView blink;
    String address = null;
    private ProgressDialog progress;
    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Intent newint = getIntent();
        address = newint.getStringExtra(DeviceList.EXTRA_ADDRESS); //receive the address of the bluetooth device

        //view of the ledControl
        setContentView(R.layout.activity_main);

        //call the widgtes
        btnPower = (ImageButton) findViewById(R.id.buttonPower);
        btnUp = (ImageButton)findViewById(R.id.buttonUp);
        btnDown = (ImageButton)findViewById(R.id.buttonDown);
        btnDis = (Button)findViewById(R.id.buttonDisconnect);
        blink = (TextView) findViewById(R.id.LED);

        new ConnectBT().execute(); //Call the class to connect

        btnPower.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                power();      //method to turn on or off
            }
        });

        btnUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                up();      //method to power Up
            }
        });

        btnDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                down();   //method to power Down
            }
        });

        btnDis.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Disconnect(); //close connection
            }
        });
    }

    private void Disconnect()
    {
        if (btSocket!=null) //If the btSocket is busy
        {
            try
            {
                btSocket.close(); //close connection
            }
            catch (IOException e)
            { msg("خطا!");}
        }
        finish(); //return to the first layout

    }

    private void power()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("O".getBytes());
                blink.setBackgroundDrawable(getResources().getDrawable(background_selector_2));

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        blink.setBackgroundDrawable(getResources().getDrawable(background_selector));
                    }
                }, 200);
            }
            catch (IOException e)
            {
                msg("خطا!");
            }
        }
    }

    private void up()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("U".getBytes());
                blink.setBackgroundDrawable(getResources().getDrawable(background_selector_2));

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        blink.setBackgroundDrawable(getResources().getDrawable(background_selector));
                    }
                }, 200);
            }
            catch (IOException e)
            {
                msg("خطا!");
            }
        }
    }

    private void down()
    {
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("D".getBytes());
                blink.setBackgroundDrawable(getResources().getDrawable(background_selector_2));

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        blink.setBackgroundDrawable(getResources().getDrawable(background_selector));
                    }
                }, 200);
            }
            catch (IOException e)
            {
                msg("خطا!");
            }
        }
    }

    // fast way to call Toast
    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
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

    private class ConnectBT extends AsyncTask<Void, Void, Void>  // UI thread
    {
        private boolean ConnectSuccess = true; //if it's here, it's almost connected

        @Override
        protected void onPreExecute()
        {
            progress = ProgressDialog.show(MainActivity.this, "در حال اتصال ...", "لطفا صبر کنید!");  //show a progress dialog
        }

        @Override
        protected Void doInBackground(Void... devices) //while the progress dialog is shown, the connection is done in background
        {
            try
            {
                if (btSocket == null || !isBtConnected)
                {
                    myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                    BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                    btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    btSocket.connect();//start connection
                }
            }
            catch (IOException e)
            {
                ConnectSuccess = false;//if the try failed, you can check the exception here
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) //after the doInBackground, it checks if everything went fine
        {
            super.onPostExecute(result);

            if (!ConnectSuccess)
            {
                msg("اتصال ناموفق. آیا دستگاه درستی را انتخاب کرده اید؟ دوباره تلاش کنید.");
                finish();
            }
            else
            {
                msg("متصل شد.");
                isBtConnected = true;
            }
            progress.dismiss();
        }
    }
}
