package atoa_electronics.atoaac;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity {

    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        password = (EditText) findViewById(R.id.editText);
    }

    public void login(View view) {
        if (password.getText().toString().equals("admin")) {
            Intent i = new Intent(Login.this, DeviceList.class);
            startActivity(i);
        }
        else {
            Toast.makeText(getApplicationContext(),"رمز وارد شده صحیح نمی باشد.",Toast.LENGTH_LONG).show();
        }
    }
    public void exit(View view){
        finish();
        System.exit(0);
    }

    public void help(View view) {
        Intent i = new Intent(Login.this, Help.class);
        startActivity(i);

    }
}