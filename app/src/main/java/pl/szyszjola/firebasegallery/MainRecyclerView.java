package pl.szyszjola.firebasegallery;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainRecyclerView extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private RecyclerView recyclerView;
    private MainRecyclerViewAdapter viewAdapter;
    private List<Picture.SinglePicture> pictureList = new ArrayList<>();
    final String TAG = "TAG";
    FloatingActionButton fab;
    private static final int NEW_ITEM_CODE = 15;
    FirebaseDatabase database;
    DatabaseReference myRef;
    public final static String SINGLE_PICTURE_KEY = "SINGLE_PICTURE";
    public final static String TITLE_KEY = "TITLE";
    public final static String IMAGE_KEY = "IMAGE";
    public final static String DESCRIPTION_KEY = "DESCRIPTION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recycler_view);

        recyclerView = findViewById(R.id.recyclew_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        viewAdapter = new MainRecyclerViewAdapter(pictureList, this);

        //dodawanie nowego obrazka
        fab = findViewById(R.id.fab_dodaj);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fab.show();
                }
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 || dy < 0 && fab.isShown())
                    fab.hide();
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddingNewImage.class);
                startActivityForResult(intent, NEW_ITEM_CODE);
            }
        });

        //pobieranie danych z bazy danych
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pictureList.clear();
                HashMap<String, Picture.SinglePicture> td = (HashMap<String, Picture.SinglePicture>) dataSnapshot.getValue();
                assert td != null : "Baza danych jest pusta";
                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                    Map<String, String> map = (Map<String, String>) childDataSnapshot.getValue();
                    assert map != null;
                    try {
                        pictureList.add(new Picture.SinglePicture(map.get("title"), map.get("image"), map.get("description")));
                    } catch (NullPointerException ex) {
                        Log.w(TAG, ex.getMessage());
                    }
                }
                recyclerView.setAdapter(viewAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "Failed to read value ", databaseError.toException());
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_ITEM_CODE && resultCode == RESULT_OK && data != null) {
            Bundle bundle = data.getBundleExtra(SINGLE_PICTURE_KEY);
            myRef.push().setValue(new Picture.SinglePicture(bundle.getString(TITLE_KEY), bundle.getString(IMAGE_KEY), bundle.getString(DESCRIPTION_KEY)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.new_menu,menu);
       return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.menu_logout:
                SharedPreferences loginPreferences = getSharedPreferences(LogIn.LOGIN, MODE_PRIVATE);
                SharedPreferences.Editor editor = loginPreferences.edit();
                editor.putBoolean(LogIn.KEY_ZAPAMIETANY,false);
                editor.putBoolean(LogIn.KEY_ZALOGOWANY,false);
                editor.commit();
                finish();
                return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        //close the application
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
