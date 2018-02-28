package pl.szyszjola.firebasegallery;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

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
import java.io.InputStream;


class FireBaseStorageConector {

    private StorageReference storage;

    FireBaseStorageConector() {
        storage = FirebaseStorage.getInstance().getReference();
    }

    String firebaseUpload(String myPath) {
        //adding the file to storage

        Uri file = Uri.fromFile(new File(myPath));
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/" + myPath.substring(myPath.lastIndexOf(".") + 1, myPath.length())).build();
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
                try {
                    Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                }
                catch (NullPointerException ex)
                {
                }
            }
        });

        return "photo/" + file.getLastPathSegment();
    }

    void firebaseDownload(final ImageView imageView, String path) {

        StorageReference storageRef = storage.child(path);
        final long ONE_MEGABYTE = 5000 * 5000;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
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
