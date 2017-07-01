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
import com.soundfound.model.User;

import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        final DBHelper myDb = new DBHelper(this);
        try {
            myDb.createTables();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        (findViewById(R.id.register)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
        (findViewById(R.id.login_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = ((EditText) findViewById(R.id.email)).getText().toString();
                String password = ((EditText) findViewById(R.id.password)).getText().toString();
                User user = myDb.findUser(email, password);
                if(user != null) {
                    Log.i(LoginActivity.class.getName(), "Logging in as " + email);
                    Intent intent = new Intent(getBaseContext(), PlaylistActivity.class);
                    intent.putExtra("logged", "true");
                    intent.putExtra("userId", user.getId());
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "E-mail or password is not correct!", Toast.LENGTH_SHORT).show();
                    ((EditText) findViewById(R.id.email)).setText("");
                    ((EditText) findViewById(R.id.password)).setText("");
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (getIntent().getStringExtra("logged").equals("false")) {
            moveTaskToBack(true);
        }
    }
}
