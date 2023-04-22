package com.android.settings.deviceinfo;

//import android.app.Fragment;
import android.content.Context;
import android.os.Build;
//import android.preference.Preference;
//import android.preference.PreferenceScreen;
import android.text.TextUtils;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import android.os.SystemProperties;
import com.android.settings.R;
public class FireflyVersionPreferenceController extends AbstractPreferenceController implements
        PreferenceControllerMixin {

    private final static String FIREFLY_VERSION_KEY = "firefly_version";

    private final Fragment mFragment;


    public FireflyVersionPreferenceController(Context context, Fragment fragment) {
        super(context);

        mFragment = fragment;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        final Preference pref = screen.findPreference(getPreferenceKey());
        if (pref != null) {
            pref.setSummary(getFireflyVersion());
        }
    }

    @Override
    public String getPreferenceKey() {
        return FIREFLY_VERSION_KEY;
    }

    @Override
    public boolean handlePreferenceTreeClick(Preference preference) {
        return false;
    }


    ///sys/firmware/devicetree/base/model
    //ro.firefly.build.fingerprint
    private String getFireflyVersion(){
        StringBuilder sb = new StringBuilder();

        String tmp = readFromFile(new File("/sys/firmware/devicetree/base/model"));
        if(!TextUtils.isEmpty(tmp)){
            sb.append(tmp);
        }        
        
        tmp = SystemProperties.get("ro.firefly.build.fingerprint");
        if(!TextUtils.isEmpty(tmp)){
            if(sb.length() > 0){
                sb.append("\n");
            }
            sb.append(tmp);
        }

        tmp = readFromFile(new File("/sys/devices/virtual/misc/firefly_hwversion/hwversion"));
        if(!TextUtils.isEmpty(tmp)){
            if(sb.length() > 0){
                sb.append("\n");
            }
            sb.append("hardware V" + tmp);
        }


        if(TextUtils.isEmpty(sb.toString()))
        {
            return mContext.getResources().getString(R.string.device_info_default);
        }
        
        return sb.toString();
    }

    public static String readFromFile(File file) {
        if ((file != null) && file.exists()) {
            try {
                FileInputStream fin = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(fin));
                String value = reader.readLine();
                fin.close();
                return value;
            } catch (IOException e) {
                return null;
            }
        }
        return null;
    }

}