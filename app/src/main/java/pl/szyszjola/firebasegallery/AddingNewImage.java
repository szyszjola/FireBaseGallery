package pl.szyszjola.firebasegallery;

import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.io.File;

public class AddingNewImage extends AppCompatActivity {

    public static final int PICK_IMAGE = 1;
    ImageView imageView;
    Button save;
    String picturePath = "";
    EditText title, description;
    String image = "";
    private DialogFragment mDialog;
    RelativeLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adding_new_image);
        title = findViewById(R.id.editTitle);
        description = findViewById(R.id.editDescription);
        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choseImage();
            }
        });
        layout = findViewById(R.id.adding_layout);
        mDialog = ProgressDialogFragment.newInstance();
        save = findViewById(R.id.btnSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FireBaseStorageConector conector = new FireBaseStorageConector(v.getContext(), mDialog);
                if (image.equals("")) {
                    Snackbar.make(getCurrentFocus(), "Wybierz zdjęcie które chcesz dodać", Snackbar.LENGTH_LONG).show();
                } else {
                    mDialog.show(getFragmentManager(), "Shutdown");
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString(MainRecyclerView.TITLE_KEY, title.getText().toString());
                    bundle.putString(MainRecyclerView.IMAGE_KEY, image);
                    bundle.putString(MainRecyclerView.DESCRIPTION_KEY, description.getText().toString());
                    intent.putExtra(MainRecyclerView.SINGLE_PICTURE_KEY, bundle);
                    setResult(RESULT_OK, intent);
                    // Show new ProgressDialogFragment
                    conector.firebaseUpload(picturePath);
                    save.setVisibility(View.GONE);
                    mDialog.setCancelable(false);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            assert selectedImage != null;
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            assert cursor != null;
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 1024, 1024, false);
            imageView.setImageBitmap(scaledBitmap);
            Uri file = Uri.fromFile(new File(picturePath));
            image = "photo/" + file.getLastPathSegment();
        }
    }

    private void choseImage() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.setType("image/*");
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{pickIntent});
        startActivityForResult(chooserIntent, PICK_IMAGE);
    }
}
