package com.mycompany.myemailapp;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUp extends AppCompatActivity {
    SQLController dbc;
    EditText user1,pwd1,cpwd1;

    SQLiteDatabase s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbc = new SQLController(this);
        dbc.open();

        Button sign = (Button) findViewById(R.id.loginbtn1);
        sign.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EditText user1 = (EditText) findViewById(R.id.newemail);
                EditText pass1 = (EditText) findViewById(R.id.newpass);
                EditText cpwd1 = (EditText) findViewById(R.id.confirmpass);
                String userName = user1.getText().toString();
                String password = pass1.getText().toString();
                String confirmPassword = cpwd1.getText().toString();
                if (userName.equals("") || password.equals("")
                        || confirmPassword.equals("")) {

                    Toast.makeText(getApplicationContext(), "Fields cannot be vacant",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(userName).matches()){
                    Toast.makeText(SignUp.this, "Enter valid email", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(getApplicationContext(),
                            "Password does not match", Toast.LENGTH_LONG)
                            .show();
                    return;
                } else {
                    dbc.insertemail(userName, password);
                    Toast.makeText(getApplicationContext(),"Account Successfully Created ", Toast.LENGTH_LONG).show();
                    Intent i = new Intent(SignUp.this,Login.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    //finish();
                }
            }
        });

    }
    public void onDestroy(){
        super.onDestroy();
        dbc.close();
    }
}
