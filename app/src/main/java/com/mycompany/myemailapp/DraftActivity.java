package com.mycompany.myemailapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class DraftActivity extends AppCompatActivity {
    String s1,s2,s3,s4;
    //private DBHelper databaseHelper;
    SQLController dbc;
    SQLiteDatabase ss;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ss!= null && ss.isOpen()) {
            ss.close();
        }
        setContentView(R.layout.activity_draft);
        dbc=new SQLController(this);
        dbc.open();
        Intent qq=getIntent();
        final String Q=qq.getStringExtra("ID");
        final String r=qq.getStringExtra("Pname");
        Toast.makeText(this,"Logged in as "+Q,Toast.LENGTH_SHORT).show();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView) findViewById(R.id.list_data);
        Button btn = (Button) findViewById(R.id.btn1);
                btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent add_mem = new Intent(DraftActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
                add_mem.putExtra("IDD",Q);
                add_mem.putExtra("Pname",r);
                startActivity(add_mem);
            }
        });

        Cursor c = dbc.getAllData();
        String[] from = new String[]{DBHelper._TO, DBHelper._CC, DBHelper._SUBJ, DBHelper._BODY};
        int[] to = new int[]{R.id.dbto,R.id.dbcc, R.id.dbsub, R.id.dbbody};
        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(DraftActivity.this, R.layout.list_item_data, c, from, to);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                    Log.d("Log: ", "clicked on item: " + position);
                    Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                    Cursor c = ((SimpleCursorAdapter)listView.getAdapter()).getCursor();
                    c.moveToPosition(position);
                    Intent fin=new Intent(DraftActivity.this,MainActivity.class);
                    int idd=Integer.parseInt(c.getString(0));
                    fin.putExtra("IDD",Q);
                    fin.putExtra("Pname",r);
                    fin.putExtra("To", c.getString(1));
                    fin.putExtra("CC", c.getString(2));
                    fin.putExtra("Subject", c.getString(3));
                    fin.putExtra("Body", c.getString(4));

                    //dbc.deleteData(String.valueOf(position));
                    dbc.close();
                    startActivity(fin);
            }
                        }
        );
    }
        // Database query can be a time consuming task ..
        // so its safe to call database query in another thread
        // Handler, will handle this stuff for you <img src="http://s0.wp.com/wp-includes/images/smilies/icon_smile.gif?m=1129645325g" alt=":)" class="wp-smiley">
/*
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                customAdapter = new CustomAdapter(DraftActivity.this, databaseHelper.getAllData());
                listView.setAdapter(customAdapter);
            }
        });
        databaseHelper.getAllData();
    }

    public void onClickEnterData(View btn) {
        startActivityForResult(new Intent(DraftActivity.this,MainActivity.class), ENTER_DATA_REQUEST_CODE);
    }*/

   /* protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ENTER_DATA_REQUEST_CODE && resultCode == RESULT_OK) {
            s1=data.getExtras().getString("Eto");
            s2=data.getExtras().getString("CC");
            s3=data.getExtras().getString("Subject");
            s4=data.getExtras().getString("Body");

            databaseHelper.insertdraft(s1,s2,s3,s4);
            customAdapter.changeCursor(databaseHelper.getAllData());
        }
    }*/

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
