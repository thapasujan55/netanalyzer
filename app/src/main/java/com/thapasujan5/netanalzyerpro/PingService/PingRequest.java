package com.thapasujan5.netanalzyerpro.PingService;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.Tools.Clipboard;
import com.thapasujan5.netanalzyerpro.Tools.ShowToast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Suzan on 12/10/2015.
 */
public class PingRequest extends AsyncTask<Void, Void, Void> {
    String ip, pingResult = null;
    Dialog dialog;
    TextView tvTitle;
    ProgressBar pb;
    Button btnHide, btnCancel;
    Context context;
    String packets;

    public PingRequest(String ip, Context mContext) {
        this.ip = ip;
        this.context = mContext;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        packets = sp.getString(mContext.getString(R.string.key_ping), "4");
    }

    @Override
    protected void onPreExecute() {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_pinging);
        pb = (ProgressBar) dialog.findViewById(R.id.pbPinging);
        pb.setIndeterminate(true);
        tvTitle = (TextView) dialog.findViewById(R.id.titleText);
        tvTitle.setText(Html.fromHtml("<b>Pinging " + ip + "</b>"));
        btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cancel(true);
                dialog.dismiss();

            }
        });
        btnHide = (Button) dialog.findViewById(R.id.btnMinimize);
        btnHide.setVisibility(View.GONE);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                cancel(true);
            }
        });
        dialog.setCancelable(false);
        dialog.show();
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void... params) {

        String str = "";
        Log.i("PingPackets", packets);
        try {
            Process process = Runtime.getRuntime().exec(
                    "/system/bin/ping -c " + packets + " " + ip);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            int j;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while ((j = reader.read(buffer)) > 0) {
                if (isCancelled()) {
                    break;
                }
                output.append(buffer, 0, j);
            }
            reader.close();
            // body.append(output.toString()+"\n");
            str = output.toString();
            pingResult = str;
            process.destroy();

        } catch (IOException e) {

            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        dialog.dismiss();
        if (pingResult.length() > 0) {
            final Dialog d = new Dialog(context);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setContentView(R.layout.dialog_ping_result);
            final TextView tvPingResult = (TextView) d
                    .findViewById(R.id.tvPingResut);
            tvPingResult.setText(pingResult);
            final TextView tvTitleArea = (TextView) d
                    .findViewById(R.id.tvtitleArea);
            tvTitleArea.setText(Html.fromHtml("<b>Ping Statistics for " + ip + "</b>"));
            Button btnShare = (Button) d.findViewById(R.id.btnShare);
            btnShare.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, pingResult);
                    sendIntent.setType("text/plain");
                    context.startActivity(Intent.createChooser(sendIntent,
                            "Share using"));
//                    overridePendingTransition(R.anim.slide_in,
//                            R.anim.slide_out);
                }
            });
            Button btnCopy = (Button) d.findViewById(R.id.btnCopy);
            btnCopy.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // copy ip to clipboard
                    boolean status = Clipboard.copyToClipboard(
                            context, pingResult);
                    if (status) {
                        new ShowToast(context,
                                "Copied to Clipboard", Color.WHITE,
                                R.drawable.action_bar_bg, 0,
                                Toast.LENGTH_LONG, Gravity.BOTTOM);
                    }
                    d.dismiss();
                }

            });
            Button btnClose = (Button) d.findViewById(R.id.btnClose);
            btnClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    d.dismiss();
                }
            });
            try {
                d.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            new ShowToast(context.getApplicationContext(), "Server unreachable.",
                    Color.YELLOW, R.drawable.action_bar_bg, 0, 2000,
                    Gravity.BOTTOM);
        }
        super.onPostExecute(result);
    }
}