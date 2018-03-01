package pl.szyszjola.firebasegallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


class FireBaseStorageConector {

    private StorageReference storage;
    private Context mContext;

    FireBaseStorageConector(Context mContext) {
        storage = FirebaseStorage.getInstance().getReference();
        this.mContext = mContext;
    }

    void firebaseUpload(String myPath) {
        //adding the file to storage
        Uri file = Uri.fromFile(new File(myPath));
        final StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/" + myPath.substring(myPath.lastIndexOf(".") + 1, myPath.length())).build();
        UploadTask uploadTask = storage.child("photo/" + file.getLastPathSegment()).putFile(file, metadata);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                ProgressDialogFragment.setProgress("Załadowano "  + Math.round(progress) + "%");


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
                    ((Activity)mContext).finish();
                   // Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                }
                catch (NullPointerException ex)
                {
                    Toast.makeText(mContext, ex.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

     void firebaseDownload(final ImageView imageView, String path, final Integer reqWidth, final Integer reqHeight) {

        StorageReference storageRef = storage.child(path);
        final long ONE_MEGABYTE = 6000 * 6000;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap =  ImageResize.decodeSampledBitmapFromBytes(bytes, reqWidth,reqHeight);
                imageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageView.setImageResource(R.drawable.image_not_found);
            }
        });

    }

    void firebaseDownloadFullSize(final ImageView imageView, String path) {

        StorageReference storageRef = storage.child(path);
        final long ONE_MEGABYTE = 6000 * 6000;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap =  BitmapFactory.decodeByteArray(bytes,0,bytes.length);

                FileOutputStream outStream = null;
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/ButterflyGallery");
                dir.mkdirs();
                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);
                try {
                    outStream = new FileOutputStream(outFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                try {
                    outStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageView.setImageResource(R.drawable.image_not_found);
            }
        });

    }
}
