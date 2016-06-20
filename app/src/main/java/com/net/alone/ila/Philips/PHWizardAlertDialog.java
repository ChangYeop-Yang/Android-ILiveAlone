package com.net.alone.ila.Philips;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.WindowManager;

import com.net.alone.ila.R;

/**
 * Created by Mari on 2015-11-29.
 */
public final class PHWizardAlertDialog
{
    /* ProgressDialog */
    private ProgressDialog mProgressDialog = null;
    private static PHWizardAlertDialog mPHWizardAlertDialog = null;

    /* Singleton */
    private PHWizardAlertDialog() {};
    public static synchronized PHWizardAlertDialog getInstance()
    {
        if(mPHWizardAlertDialog == null) { mPHWizardAlertDialog = new PHWizardAlertDialog(); }
        return mPHWizardAlertDialog;
    }

    public static void showErrorDialog(Context activityContext, String msg, int btnNameResId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
        builder.setTitle(R.string.Connection).setMessage(msg).setPositiveButton(btnNameResId, null);
        AlertDialog alert = builder.create();
        alert.getWindow().setSoftInputMode( WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        if (! ((Activity) activityContext).isFinishing()) {
            alert.show();
        }

    }

    public void closeProgressDialog() { if (mProgressDialog != null) { mProgressDialog.dismiss(); mProgressDialog = null; } }

    public void showProgressDialog(int resID, Context ctx)
    {
        String message = ctx.getString(resID);
        mProgressDialog = ProgressDialog.show(ctx, null, message, true, true);
        mProgressDialog.setCancelable(false);
    }

    public static void showAuthenticationErrorDialog(final Activity activityContext, String msg, int btnNameResId)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);
        builder.setTitle(R.string.Connection).setMessage(msg)
                .setPositiveButton(btnNameResId, new DialogInterface.OnClickListener()
                { @Override public void onClick(DialogInterface dialog, int which) { activityContext.finish(); } });
        AlertDialog alert = builder.create();
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alert.show();
    }


}
