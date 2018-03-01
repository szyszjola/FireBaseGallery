package pl.szyszjola.firebasegallery;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

//android arsenal

public class ProgressDialogFragment extends DialogFragment {

   static ProgressDialog dialog;

    public static ProgressDialogFragment newInstance() {
        return new ProgressDialogFragment();
    }

    // Build ProgressDialog
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //Create new ProgressDialog
        dialog = new ProgressDialog(getActivity(),ProgressDialog.STYLE_HORIZONTAL);
        dialog.setIndeterminate(true);
        return dialog;
    }

   public static void setProgress(String message)
   {
       dialog.setMessage(message);
   }
}

