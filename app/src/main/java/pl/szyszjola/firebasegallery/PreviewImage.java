package pl.szyszjola.firebasegallery;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class PreviewImage extends AppCompatActivity {

    ImageView imageView;
    private FireBaseStorageConector conector = new FireBaseStorageConector(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_image);
        imageView = findViewById(R.id.fullSizeImage);
        String path = getIntent().getStringExtra(MainRecyclerViewAdapter.PATH_KEY);
        Toast.makeText(this, path, Toast.LENGTH_SHORT).show();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        conector.firebaseDownload(imageView, path,width,height);
    }
}
