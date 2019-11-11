package edu.wmdd.sqlite_example;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getName();
    private RentalDBHelper helper = null;
    private SQLiteDatabase db = null;

    RecyclerView recyclerView;
    RecyclerView.Adapter mAdaptor;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize the database, potentially creating it
        helper = new RentalDBHelper(this);
        db = helper.getReadableDatabase();

        // set the recyclerview

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        // Only populate the db if it is empty
        Cursor c = db.rawQuery("SELECT count(*) FROM issues", null);
        c.moveToFirst();
        if (c.getInt(0) == 0) {
            Thread t = new Thread() {
                @Override
                public void run() {
                    // We have to init the data in a separate thread because of networking
                    helper.initData();

                    // We are now ready to initialize the view on the UI thread
                    runOnUiThread(() -> {
                        initView();
                    });
                }
            };
            t.start();
        } else {
            // We are already inside the UI thread
            initView();
        }
        c.close();
    }

    private void initView() {
        Spinner tv = findViewById(R.id.spinnerTextView);
        ArrayList<String> areas = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT DISTINCT(area) FROM issues", null);
        while (cursor.moveToNext()) {
            String area = cursor.getString(0);
            areas.add(area);
        }
        cursor.close();
        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, areas);

        tv.setAdapter(areaAdapter);
        tv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedArea = ((TextView) view).getText().toString();
                Cursor cursor1 = db.rawQuery("SELECT businessURL, operator FROM issues WHERE area = ?", new String[]{selectedArea});
                ArrayList<String> operators = new ArrayList<>();
                ArrayList<String> url_list = new ArrayList<>();

                while (cursor1.moveToNext()) {
                    String operator = cursor1.getString(1);
                    String urls = cursor1.getString(0);
                    operators.add(operator);
                    url_list.add(urls);

                    Log.d("url",urls);
                }

                mAdaptor = new MyAdaptor(getApplicationContext(),operators, url_list);

                recyclerView.setAdapter(mAdaptor);

                LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mMessageReceiver, new IntentFilter("message"));


            }


            public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    String urldata = intent.getStringExtra("url").toString();

                    intent = new Intent(getApplicationContext(), DetailedView.class);
                    intent.putExtra("url",urldata);

                    startActivity(intent);
                    Log.d("urldataa",urldata);




                }
            };





            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
