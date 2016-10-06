package raj.workalley.util;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import raj.workalley.Constants;
import raj.workalley.R;


/**
 * Created by sagar.chauhan on 3/14/16.
 */
public class Helper {

    private static long mLastClickTime = 0;
    private static ProgressDialog progressDialog;

    public static boolean isConnected(Context c) {
        ConnectivityManager conMgr = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (conMgr != null) {
            NetworkInfo resultTypeMobile = conMgr.getNetworkInfo(0);
            NetworkInfo resultTypeWifi = conMgr.getNetworkInfo(1);
            if (((resultTypeMobile != null && resultTypeMobile.isConnectedOrConnecting())) || (resultTypeWifi != null && resultTypeWifi.isConnectedOrConnecting())) {
                return true;
            } else
                return false;
        } else {
            return false;
        }
    }

    public static void showProgressDialogSpinner(Context mActivity, String title, String strMessage, boolean isCancellable) {
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        } else {
            progressDialog = new ProgressDialog(mActivity);
        }
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle(title);
        progressDialog.setMessage((strMessage.equals(null) ? "Loading..." : strMessage));
        progressDialog.setCancelable(isCancellable);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public static String getFormattedDate(String lastUpdated) {
        String formattedDate = null;

        if (lastUpdated == null)
            return "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date date = format.parse(lastUpdated);
            CharSequence date1 = android.text.format.DateFormat.format("dd MMM yy hh:mm:ss", date);
            if (date1 != null)
                formattedDate = date1.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (formattedDate == null)
            return lastUpdated;
        else
            return formattedDate;
    }

    public static void dismissProgressDialog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        }
        progressDialog = null;
    }

    public static void showProgressDialogHorizontal(Context mActivity, String strMessage, boolean isCancellable, int max) {
        if (progressDialog != null) {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        } else {
            progressDialog = new ProgressDialog(mActivity);
        }
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgressPercentFormat(null);
        progressDialog.setTitle("Uploading Images!");
        progressDialog.setMessage((strMessage.equals(null) ? "Loading..." : strMessage));
        progressDialog.setMax(max);
        progressDialog.setCancelable(isCancellable);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public static void updateProgressDialogHorizontal(int progress, String message) {
        if (progressDialog != null) {
            progressDialog.incrementProgressBy(progress);
            if (message != null)
                progressDialog.setMessage(message);
        }
    }

    public static boolean isValidClick() {

        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        return true;
    }


    public static String getAndroidID(Context context) {

        if (context == null)
            return "";
        String device_id = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return device_id;
    }

    public static String getAppVersion(Context mContext) {

        try {
            PackageInfo pInfo = mContext.getApplicationContext().getPackageManager().getPackageInfo(mContext.getApplicationContext().getPackageName(), 0);
            String currentVersion = pInfo.versionName;
            return currentVersion;
        } catch (Exception e) {
//Start the next activity
            return "";
        }

    }

    public static void hideKeyboardIfShown(Activity ctx) {
        InputMethodManager inputMethodManager = (InputMethodManager) ctx.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = ctx.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(ctx);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboardIfShown(Activity act, View view) {

        InputMethodManager inputMethodManager = (InputMethodManager) act.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showToastMessage(Activity mActivity, String strMessage, boolean warningMessage) {
        Toast toast = new Toast(mActivity.getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 0, 100);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    public static class DialogAndButton {
        public Dialog dialog;
        public Button okBtn;
    }

    public static DialogAndButton showPopUpDialog(Context context, String[] buttonText, String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.cc_warning_dialog, null);
        alertDialog.setView(dialogView);
        alertDialog.setCancelable(false);
        final AlertDialog dialog = alertDialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);

        TextView titleTv = (TextView) dialogView.findViewById(R.id.title);
        TextView messageTv = (TextView) dialogView.findViewById(R.id.message);
        ImageView cancel = (ImageView) dialogView.findViewById(R.id.cancel);
        Button singleOk = (Button) dialogView.findViewById(R.id.single_ok_btn);
        Button doubleOk = (Button) dialogView.findViewById(R.id.double_ok_btn);
        Button cancelBtn = (Button) dialogView.findViewById(R.id.cancel_btn);


        titleTv.setText(title);
        messageTv.setText(message);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        boolean isTwoButtonDialog = buttonText.length == 2 ? true : false;
        DialogAndButton dialogAndButton = new DialogAndButton();
        dialogAndButton.dialog = dialog;

        if (isTwoButtonDialog) {
            dialogView.findViewById(R.id.single_ok_btn).setVisibility(View.GONE);
            dialogView.findViewById(R.id.double_btn).setVisibility(View.VISIBLE);
            doubleOk.setText(buttonText[1]);
            cancelBtn.setText(buttonText[0]);
            dialogAndButton.okBtn = doubleOk;
        } else {
            dialogView.findViewById(R.id.single_ok_btn).setVisibility(View.VISIBLE);
            dialogView.findViewById(R.id.double_btn).setVisibility(View.GONE);
            singleOk.setText(buttonText[0]);
            singleOk.setTag(Constants.POPUP_DIALOG_OK_BUTTON_TAG);
            dialogAndButton.okBtn = singleOk;
        }
        //get R.id.single_ok_btn reference for ok button when single button dialog
        // and R.id.double_ok_btn reference for ok button for two button dialog
        dialog.show();

        return dialogAndButton;
    }

    public static void showKeyboard(Context act, View view) {
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /*private Location getLastKnownLocation() {

        LocationManager mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        Boolean gps_enabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        Boolean network_enabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d("sagar", "GPS: " + gps_enabled);
        Log.d("sagar", "Netwrok: " + network_enabled);
        for (String provider : providers) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                mLocationManager.requestLocationUpdates(provider, 10000, 10, this);
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                } else {
                    Log.d("sagar", "last known location, provider: " + provider + " Lattitude : " + l.getLatitude() + " Longitude : " + l.getLongitude());

                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            }
            if (bestLocation == null) {
                return null;
            } else {

            }

        }
        if (bestLocation != null) {
            Log.d("Sagar", "found best last known location: Lattitude : " + bestLocation.getLatitude() + " Longitude : " + bestLocation.getLongitude());
        }
        return bestLocation;
    }*/

}
