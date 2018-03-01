package pl.szyszjola.firebasegallery;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Message;
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
import java.io.FileNotFoundException;


class FireBaseStorageConector {

    private StorageReference storage;
    private Context mContext;
    private DialogFragment mDialog;

    FireBaseStorageConector(Context mContext) {
        storage = FirebaseStorage.getInstance().getReference();
        this.mContext = mContext;
    }

    FireBaseStorageConector(Context mContext, DialogFragment mDialog) {
        storage = FirebaseStorage.getInstance().getReference();
        this.mContext = mContext;
        this.mDialog = mDialog;
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

     void firebaseDownload(final ImageView imageView, String path) {

        StorageReference storageRef = storage.child(path);
        final long ONE_MEGABYTE = 6000 * 6000;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
              Bitmap bitmap =  decodeSampledBitmapFromResource(bytes, 128,96);
                        imageView.setImageBitmap(bitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                imageView.setImageResource(R.drawable.image_not_found);
            }
        });

    }

//region Skalowanie
    private int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    private Bitmap decodeSampledBitmapFromResource(byte[] bytes,
                                                   int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length,options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, 0,bytes.length, options);
    }
//endregion
}
