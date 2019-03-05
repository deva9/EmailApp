package com.mycompany.myemailapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Collection;

public class Login extends AppCompatActivity{
    Profile profile;
    SocialAuthAdapter adapter;
    SQLController dbc;
    String b,unm;
    SQLiteDatabase s;
    static final String KEY_USERNAME = "username";
    static final String KEY_PASSWORD = "password";
    static final String P_NAME = "p_name";
    public static final String MyPREFERENCES = "MyPrefs";
    private CallbackManager callbackManager;
    String pn,t,a="blah";
    //LoginManager loginm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        dbc = new SQLController(this);
        SharedPreferences sp = getApplicationContext().getSharedPreferences(MyPREFERENCES, 0);
        String provname=sp.getString(P_NAME,"Default");
        String puser = sp.getString(KEY_USERNAME, "Default");
        String ppwd = sp.getString(KEY_PASSWORD, "Default");
        //boolean b1 = sp.getBoolean(b, false);
        Intent sa=getIntent();
        a=sa.getStringExtra("SAS");
        if (!puser.equals("Default") && !ppwd.equals("Default")) {
            //if (sp.getString(KEY_USERNAME,"").equals("")){
            Intent in = new Intent(this, DraftActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            in.putExtra("ID", puser);
            in.putExtra("Pname",provname);
            startActivity(in);
        }
        //DBHelper d=new DBHelper(this);
        Button log = (Button) findViewById(R.id.loginbtn);
        log.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dbc.open();
                EditText user = (EditText) findViewById(R.id.textfrom);
                EditText pass = (EditText) findViewById(R.id.textpass);
                String userid = user.getText().toString().trim();
                String pwd = pass.getText().toString().trim();
                SharedPreferences sp = getSharedPreferences(MyPREFERENCES, 0);
                SharedPreferences.Editor editor = sp.edit();

                String stpwd = dbc.getSingleEntry(userid);
                if (pwd.equals(stpwd)) {
                    editor.putString(KEY_USERNAME, userid);
                    editor.putString(KEY_PASSWORD, pwd);
                    editor.putString(P_NAME,"none");
                    editor.putBoolean(b, true);
                    editor.commit();
                    //Toast.makeText(Login.this, "Successfully logged in as: " + userid, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Login.this, DraftActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("ID", userid);
                    intent.putExtra("Pname","none");
                    user.setText("");
                    pass.setText("");
                    startActivity(intent);
                } else {
                    Toast.makeText(Login.this, "Login failed ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button fb = (Button) findViewById(R.id.fb);
        Button google = (Button) findViewById(R.id.google);
        Button linkd = (Button) findViewById(R.id.linkd);
        Button lgt = (Button) findViewById(R.id.lgt);
        adapter = new SocialAuthAdapter(new ResponseListener());
        adapter.addProvider(SocialAuthAdapter.Provider.GOOGLEPLUS, R.drawable.googleplus);
        adapter.addProvider(SocialAuthAdapter.Provider.LINKEDIN, R.drawable.linkedin);
        //adapter.enable();
        Collection<String> permissions = Arrays.asList("public_profile", "user_friends", "email");
        //LoginManager.getInstance().logInWithReadPermissions(Login.this, permissions);
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            public void onSuccess(LoginResult loginResult) {
                com.facebook.Profile pr= com.facebook.Profile.getCurrentProfile();
                System.out.println("Success");
                GraphRequest request=GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject json, GraphResponse response) {
                        if (response.getError() != null) {
                            // handle error
                            System.out.println("ERROR");
                        } else {
                            System.out.println("Success");
                            try {
                                String jsonresult = String.valueOf(json);
                                System.out.println("JSON Result" + jsonresult);
                                String str_email = json.getString("email");
                                //String str_id = json.getString("id");
                                //String str_firstname = json.getString("first_name");
                                //String str_lastname = json.getString("last_name");
                                SharedPreferences sp = getSharedPreferences(MyPREFERENCES, 0);
                                SharedPreferences.Editor editor = sp.edit();
                                Intent link = new Intent(Login.this, DraftActivity.class);
                                link.putExtra("ID", str_email);
                                link.putExtra("Pname","fb");
                                link.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                link.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                editor.putString(KEY_USERNAME, str_email);
                                editor.putString(KEY_PASSWORD, "fb");
                                editor.putString(P_NAME,"fb");
                                editor.putBoolean(b, true);
                                editor.commit();
                                startActivity(link);
                                finish();
                            } catch (JSONException e) {
                                Toast.makeText(Login.this, "An error occured ", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields","id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }
            @Override
            public void onCancel() {
                Log.d("TAG", "On cancel");
                Toast.makeText(Login.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(FacebookException error) {
                Log.d("ERROR", error.toString());
                Toast.makeText(Login.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        fb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("public_profile", "user_friends", "email"));
            }
        });
        google.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pn="GOOGLEPLUS";
                adapter.authorize(Login.this, SocialAuthAdapter.Provider.GOOGLEPLUS);
            }
        });
        linkd.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                pn="LINKEDIN";
                adapter.authorize(Login.this, SocialAuthAdapter.Provider.LINKEDIN);
            }
        });

        /*
        lgt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    //adapter.signOut(Login.this, SocialAuthAdapter.Provider.FACEBOOK.toString());
                    adapter.signOut(getApplicationContext(), SocialAuthAdapter.Provider.GOOGLEPLUS.toString());
                    Toast.makeText(Login.this, "Logged out. ", Toast.LENGTH_SHORT).show();
                }catch (NullPointerException ne){
                    Log.e("Error","Gplus error");}
                try {
                adapter.signOut(getApplicationContext(), SocialAuthAdapter.Provider.LINKEDIN.toString());
                Toast.makeText(Login.this, "Logged out. ", Toast.LENGTH_SHORT).show();}
                catch (NullPointerException ne){
                Log.e("Error","Linkedin error");
            }}
        });
        */

        TextView text = (TextView) findViewById(R.id.text1);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, SignUp.class));
            }
        });
    }

    private final class ResponseListener implements DialogListener{
        private static final long svid=1;

        public void onComplete(Bundle val){
            boolean status;
            unm=val.getString(SocialAuthAdapter.PROVIDER);
            Log.d("PROVIDER:","Provider Name: "+unm);
            //try{
                //t=adapter.getCurrentProvider().getProviderId().toString();
                profile=adapter.getUserProfile();
                status=true;
                if (pn.equalsIgnoreCase("FACEBOOK"))
                    t = profile.getEmail();
                else if (pn.equalsIgnoreCase("LINKEDIN"))
                    t=profile.getEmail();
                else if (pn.equalsIgnoreCase("GOOGLEPLUS"))
                    t=profile.getEmail();
            /*}
            catch (Exception e){
                status=false;
            }*/
            if (status) {
                SharedPreferences sp = getSharedPreferences(MyPREFERENCES, 0);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(KEY_USERNAME, adapter.getUserProfile().getEmail());
                editor.putString(KEY_PASSWORD, "gpln");
                editor.putString(P_NAME,unm);
                editor.putBoolean(b, true);
                editor.commit();

                Intent link=new Intent(Login.this,DraftActivity.class);
                link.putExtra("ID",adapter.getUserProfile().getEmail());
                link.putExtra("Pname",unm);
                //Toast.makeText(Login.this, "Logged in as "+adapter.getUserProfile().getEmail(), Toast.LENGTH_SHORT).show();
                link.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                link.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(link);
            }
            else
                Toast.makeText(Login.this, "NOT Logged in", Toast.LENGTH_SHORT).show();
        }
        public void onError(SocialAuthError error){
            Log.d("Login","Authentication Error");
        }
        public void onCancel(){
            Log.d("Login","Authentication Cancelled");
        }
        @Override
        public void onBack() {
            return;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_draft, menu);
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
    private boolean exit=false;
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, "Press Back again to Exit.",
                    Toast.LENGTH_SHORT).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);
        }
    }
}

