package com.soundfound.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.soundfound.db.DBHelper;
import com.soundfound.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final DBHelper mydb = new DBHelper(this);
        findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = ((EditText)findViewById(R.id.email_register_text)).getText().toString();
                final String password = ((EditText)findViewById(R.id.register_password)).getText().toString();
                final String repeatPassword = ((EditText)findViewById(R.id.repeat_password)).getText().toString();
                if(!mydb.existsUser(email) && mydb.isValidEmail(email) && password.equals(repeatPassword)) {
                    try {
                        mydb.insertUsers(email, password);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.i(RegisterActivity.class.getName(), "User " + email + " registered");
                    Toast.makeText(getApplicationContext(), "Successful registration", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                }
                else {
                    if(mydb.existsUser(email)) {
                        Toast.makeText(getApplicationContext(), "User with this e-mail already exists", Toast.LENGTH_SHORT).show();
                    }
                    else if(!mydb.isValidEmail(email)) {
                        Toast.makeText(getApplicationContext(), "Invalid email", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                    ((EditText) findViewById(R.id.email_register_text)).setText("");
                    ((EditText) findViewById(R.id.register_password)).setText("");
                    ((EditText) findViewById(R.id.repeat_password)).setText("");
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        intent.putExtra("logged", "false");
        startActivity(intent);
    }
}
