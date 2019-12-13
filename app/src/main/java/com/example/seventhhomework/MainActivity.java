package com.example.seventhhomework;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    MyRecyclerAdapter myRecyclerAdapter;
    RecyclerView recyclerView;
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> url = new ArrayList<>();
    ArrayList<String> src = new ArrayList<>();
    String path = "http://gank.io/api/data/Android/";
    long pagesNumber = 1;
    long totalNumber = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requireInternetPermission();
        requireCameraPermission();
        getJson(10);
        initView();
    }

    void initView() {
        myRecyclerAdapter = new MyRecyclerAdapter(name, title, url, src, this);
        recyclerView = findViewById(R.id.rc);
        recyclerView.setAdapter(myRecyclerAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, OrientationHelper.VERTICAL));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (ViewCompat.canScrollVertically(recyclerView, -1)) {
                        addData();
                    }
                    else if(ViewCompat.canScrollVertically(recyclerView, 1)){
                        fresh();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int x, int y) {
                super.onScrolled(recyclerView, x, y);
            }
        });
    }

    public void addData() {
        pagesNumber++;
        totalNumber += 10;
        getJson(10);
        myRecyclerAdapter.reSet(name, title, url, src);
        myRecyclerAdapter.reFresh();
    }

    public void fresh() {
        pagesNumber++;
        title.clear();
        url.clear();
        src.clear();
        name.clear();
        getJson(totalNumber);
        myRecyclerAdapter.reSet(name, title, url, src);
        myRecyclerAdapter.reFresh();
    }

    public void requireCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                Toast.makeText(this, "请在应用设置中修改权限", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
                startActivity(intent);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1106);
            }
    }

    public void requireInternetPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED)
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
                Toast.makeText(this, "请在应用设置中修改权限", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
                startActivity(intent);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, 1106);
            }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1106:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "权限被拒，无法打开网页", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    void setList(String result) {
        try {
            JSONArray json = new JSONArray(new JSONObject(result).getJSONArray("results").toString());
            for (int i = 0; i < json.length(); i++) {
                JSONObject jb = json.getJSONObject(i);
                String name = jb.getString("who");
                String desc = jb.getString("desc");
                String url = jb.getString("url");
                String s = null;
                if (jb.has("images")) {
                    s = "";
                    String src = jb.getString("images");
                    int j = 0;
                    while (src.charAt(j++) != '"') ;
                    do {
                        if (src.charAt(j) == '\\') {
                            continue;
                        }
                        s += src.charAt(j);
                    } while (src.charAt(++j) != '"');
                }
                Log.d("name", name);
                Log.d("desc", desc);
                Log.d("url", url);
                this.name.add(name);
                this.title.add(desc);
                this.url.add(url);
                this.src.add(s);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getJson(final long size) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(path + size + "/" + pagesNumber);
                    HttpURLConnection connect = (HttpURLConnection) url.openConnection();
                    InputStream input = connect.getInputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(input));
                    String line = in.readLine();
                    if (line != null)
                        Log.d("line", line);
                    while (line != null) {
                        setList(line);
                        line = in.readLine();
                        if (line != null)
                            Log.d("line", line);
                    }
                    input.close();
                } catch (Exception e) {
                    Log.d("Thread", "error");
                }
            }
        };
        Thread thread = new Thread(runnable, "thread0");
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
