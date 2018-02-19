package pl.szyszjola.firebasegallery;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;
import java.util.ArrayList;

public class MainRecyclerView extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final int PERMISSION_REQUEST_CODE = 1;
    private RecyclerView recyclerView;
    private MainRecyclerViewAdapter viewAdapter;
    private ArrayList<Picture.SinglePicture> pictureList = new ArrayList<>();
    final String  TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recycler_view);

        //getting Permission
        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                Log.w("dd", "dd");
                // firebaseUpload();
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
            } else {
                Log.w("ddll", "dd");
                requestPermission(); // Code for permission
            }
        }
        else
        {

        }

        pictureList.add(new Picture.SinglePicture("Petra Ral", "photo/avatar.jpg", "fff"));
        pictureList.add(new Picture.SinglePicture("Inna Petra", "photo/girl2.png", "dddsds"));
        pictureList.add(new Picture.SinglePicture("Inna Petra", "photo/girl4.png", "dddsds"));
        pictureList.add(new Picture.SinglePicture("Inna Petra", "photo/girl4.png", "dddsds"));
        //Creating recycler view and bind it with Adapter
        recyclerView = findViewById(R.id.recyclew_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        viewAdapter = new MainRecyclerViewAdapter(pictureList, this);
        recyclerView.setAdapter(viewAdapter);

    }


    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }
}
