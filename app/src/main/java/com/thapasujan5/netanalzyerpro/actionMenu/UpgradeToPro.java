package com.thapasujan5.netanalzyerpro.actionMenu;


import android.app.Activity;
import android.content.Context;

import com.thapasujan5.netanalzyerpro.tools.OpenMarket;

import com.thapasujan5.netanalyzerpro.R;

public class UpgradeToPro {
    public UpgradeToPro(Context context) {
        context.startActivity(OpenMarket.openURL(context
                .getString(R.string.pro_version_packagename)));
        ((Activity) context).overridePendingTransition(R.anim.slide_in,
                R.anim.slide_out);
    }
}
