package com.mycompany.myemailapp;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;

import java.util.ArrayList;

public class MainActivity extends Activity {
    String ss,s1,s2,s3,s4,s5,s6;
    EditText e,e0,e1,e2,e3,e4;
    public static final String MyPREFERENCES = "MyPrefs";
    SocialAuthAdapter adapter;
    SQLController dbc;
    Profile profile;
    SQLiteDatabase s;
    private static final String[] PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Email.CONTACT_ID,
            ContactsContract.CommonDataKinds.Email.DATA
    };
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // FacebookSdk.sdkInitialize(getApplicationContext());
        dbc = new SQLController(this);
        dbc.open();
       adapter=new SocialAuthAdapter(new ResponseListener());
        adapter.addProvider(SocialAuthAdapter.Provider.GOOGLEPLUS,R.drawable.googleplus);
        adapter.addProvider(SocialAuthAdapter.Provider.LINKEDIN, R.drawable.linkedin);
        setContentView(R.layout.activity_main);
        e = (EditText) findViewById(R.id.textfrom);
        e0 = (EditText) findViewById(R.id.textpass);
        e1 = (EditText) findViewById(R.id.textto);
        e1.setSelection(0);
        e2 = (EditText) findViewById(R.id.textcc);
        e3 = (EditText) findViewById(R.id.textsub);
        e4 = (EditText) findViewById(R.id.textmsg);
        e.setText(ss);
        showContacts();
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            getContactNames();
        }
        if (s != null && s.isOpen())
            s.close();
        Intent inten = getIntent();
        s5 = inten.getStringExtra("IDD");
        s6 = inten.getStringExtra("Pname");
        s1 = inten.getStringExtra("To");
        s2 = inten.getStringExtra("CC");
        s3 = inten.getStringExtra("Subject");
        s4 = inten.getStringExtra("Body");
        e.setText(s5);
        e1.setText(s1);
        e2.setText(s2);
        e3.setText(s3);
        e4.setText(s4);
        final Button send = (Button) this.findViewById(R.id.button);
        final Button save = (Button) this.findViewById(R.id.button1);
        final Button logout = (Button) this.findViewById(R.id.logout);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("SendMailActivity", "Send Button Clicked.");
                final String fromEmail = ((EditText) findViewById(R.id.textfrom)).getText().toString();
                final String fromPassword = ((EditText) findViewById(R.id.textpass)).getText().toString();
                AutoCompleteTextView act = (AutoCompleteTextView) findViewById(R.id.textto);
                String toEmails = act.getText().toString();
                String ccEmails = ((EditText) findViewById(R.id.textcc)).getText().toString();
                Log.i("SendMailActivity", "To List: " + toEmails);
                String emailSubject = ((TextView) findViewById(R.id.textsub)).getText().toString();
                String emailBody = ((TextView) findViewById(R.id.textmsg)).getText().toString();
                if (!ccEmails.contains("@"))
                    ccEmails = "";
                if (fromEmail.isEmpty() || fromPassword.isEmpty())
                    Toast.makeText(MainActivity.this, "Enter userID and password both ", Toast.LENGTH_SHORT).show();
                if (!toEmails.contains("@"))
                    Toast.makeText(MainActivity.this, "Enter a valid recipient ", Toast.LENGTH_SHORT).show();
                if (!toEmails.isEmpty() && !fromEmail.isEmpty() && !fromPassword.isEmpty()) {
                    new SendMailTask(MainActivity.this).execute(fromEmail, fromPassword, toEmails, ccEmails, emailSubject, emailBody);
                }
                e.setText("");
                e0.setText("");
                e1.setText("");
                e2.setText("");
                e3.setText("");
                e4.setText("");
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //id++;String Id = Integer.toString(id);
                String toEmail = e1.getText().toString();
                String ccEmail = e2.getText().toString();
                String emailSubj = e3.getText().toString();
                String emailBod = e4.getText().toString();
                if (toEmail.length() != 0 && emailSubj.length() != 0) {
                    dbc.insertdraft(toEmail, ccEmail, emailSubj, emailBod);
                    Intent main = new Intent(MainActivity.this, DraftActivity.class)
                            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(main);
                }
                else
                {
                    Toast.makeText(MainActivity.this, "Enter required columns", Toast.LENGTH_SHORT).show();
                }
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sp = getSharedPreferences(Login.MyPREFERENCES,0);
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();
                LoginManager.getInstance().logOut();
                //adapter.getCurrentProvider().logout();

                if (s6.equalsIgnoreCase("linkedin")) {
                    try {
                    /*adapter.authorize(MainActivity.this, SocialAuthAdapter.Provider.GOOGLEPLUS);
                    adapter.signOut(getApplicationContext(), SocialAuthAdapter.Provider.GOOGLEPLUS.toString());*/
                        adapter.authorize(MainActivity.this, SocialAuthAdapter.Provider.LINKEDIN);
                        adapter.signOut(getApplicationContext(), SocialAuthAdapter.Provider.LINKEDIN.toString());
                    } catch (NullPointerException ee) {
                        Log.e("Error", "Null #1");
                    }
                }
                    if (s6.equalsIgnoreCase("googleplus")){
                        try {
                    adapter.authorize(MainActivity.this, SocialAuthAdapter.Provider.GOOGLEPLUS);
                    adapter.signOut(getApplicationContext(), SocialAuthAdapter.Provider.GOOGLEPLUS.toString());
                            /*adapter.authorize(MainActivity.this, SocialAuthAdapter.Provider.LINKEDIN);
                            adapter.signOut(getApplicationContext(), SocialAuthAdapter.Provider.LINKEDIN.toString());*/
                        } catch (NullPointerException ee) {
                            Log.e("Error", "Null #2");
                        }
                    }
               /* try {

                }catch (NullPointerException ee){
                    Log.e("Error","Null #2");
                }*/
                Intent fintent=new Intent(MainActivity.this,Login.class);
                Toast.makeText(MainActivity.this,"Logged out",Toast.LENGTH_SHORT).show();
                fintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                fintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                fintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                fintent.putExtra("SAS", "sauth");
                startActivity(fintent);
            }
        });
    }
    private final class ResponseListener implements DialogListener {
        private static final long svid=1;
        public void onComplete(Bundle val){
            boolean status=true;
            String unm=val.getString(SocialAuthAdapter.PROVIDER);
            Log.d("PROVIDER:", "Provider Name: " + unm);
            try {
                //t=adapter.getCurrentProvider().getProviderId().toString();
                //profile=adapter.getUserProfile();
                adapter.signOut(getApplicationContext(), adapter.getCurrentProvider().toString());
            }catch (Exception e){
                Log.d("Error","No sign out");
                status=false;
            }

        }
        public void onError(SocialAuthError error){
            Log.d("Login", "Authentication Error");
        }
        public void onCancel(){
            Log.d("Login","Authentication Cancelled");
        }
        @Override
        public void onBack() {
            return;
        }
    }

    private void showContacts() {
                // Check the SDK version and whether the permission is already granted or not.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                } else {
                    //List<String> contacts = getContactNames();
                    //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, contacts);
                    // lstNames.setAdapter(adapter);
                }
            }

            @Override
            public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                                   int[] grantResults) {
                if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission is granted
                        getContactNames();
                    } else {
                        Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            private void getContactNames() {
                ArrayList<String> names = new ArrayList<String>();
                Toast.makeText(this, "Fetching contact data..", Toast.LENGTH_SHORT).show();

                ContentResolver cr = getContentResolver();
                Cursor cur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, PROJECTION, null, null, null);
                if (cur.getCount() > 0) {
                    try {
                        final int contactIdIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.CONTACT_ID);
                        final int emailIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA);
                        long contid;
                        String address;
                        while (cur.moveToNext()) {
                            contid = cur.getLong(contactIdIndex);
                            address = cur.getString(emailIndex);

/*                while (cur1.moveToNext()) {
                    //to get the contact names
                    //String name = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    //Log.e("Name :", name);
                    String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    Log.e("Email", email);*/
                            if (address != null) {
                                names.add(address);
                            }
                        }
                    } finally {
                        cur.close();
                    }
                }
                final String[] emailz = names.toArray(new String[names.size()]);
                final ArrayAdapter<String> e = new ArrayAdapter<String>(this, R.layout.select_item, emailz);
                final MultiAutoCompleteTextView act = (MultiAutoCompleteTextView) findViewById(R.id.textto);
                act.setThreshold(1);
                act.setAdapter(e);
                act.setTokenizer(new MultiAutoCompleteTextView.Tokenizer() {

                    @Override
                    public CharSequence terminateToken(CharSequence arg0) {
                        // TODO Auto-generated method stub
                        return "";
                    }

                    @Override
                    public int findTokenStart(CharSequence arg0, int arg1) {
                        // TODO Auto-generated method stub
                        return 0;
                    }

                    @Override
                    public int findTokenEnd(CharSequence arg0, int arg1) {
                        // TODO Auto-generated method stub
                        return 0;
                    }
                });

                //Setting Sms edittext onclick listener class
                act.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                        String name = (String) arg0.getItemAtPosition(arg2);
                        if (act.getText().toString().length() == 0)
                            act.setText(name);
                        else
                            act.setText(act.getText().toString() + "," + name);
                    }
                });
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
            public void onBackPressed() {
                finish();
            }
        }

       /* ChipBubbleText cp = new ChipBubbleText(MainActivity.this, act,names,1);
        cp.setChipColor("#9999FF");

        cp.setChipTextSize(20);

        cp.initialize();*/
       /* e.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // When clicked, show a toast with the TextView text
                Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
            }
        });*/