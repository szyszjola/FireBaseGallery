package pl.szyszjola.firebasegallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

/**
 * Created by jolanta.szyszkiewicz on 2018.02.16.
 */

public class FireBaseConector {

    private StorageReference storage;

    public FireBaseConector() {
        storage = FirebaseStorage.getInstance().getReference();
    }

    public void firebaseUpload() {
        ///adding the file to storage
        String path = Environment.getExternalStorageDirectory().getPath();
        String myJpgPath = path + "/Download/girl2.png";
        Uri file = Uri.fromFile(new File(myJpgPath));
        //Boolean d = file.exists();
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/png").build();
        UploadTask uploadTask = storage.child("photo/" + file.getLastPathSegment()).putFile(file, metadata);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                System.out.println("Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                System.out.println("Upload is paused");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // Handle successful uploads on complete
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
            }
        });

        Log.w("dd", "dd");
    }

    public void firebaseDownload(final ImageView imageView, String path) {

        StorageReference avatarRef = storage.child(path);
        final long ONE_MEGABYTE = 1024 * 1024;
        avatarRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageView.setImageResource(R.drawable.image_not_found);
                Log.w("TAG", e.getMessage());
            }
        });
    }

}
