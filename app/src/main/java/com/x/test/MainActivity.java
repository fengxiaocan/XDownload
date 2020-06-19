package com.x.test;


import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity{
    private TextView tv1;
    private TextView tv2;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1=findViewById(R.id.tv1);
        tv2=findViewById(R.id.tv2);
        DisplayMetrics metrics=getResources().getDisplayMetrics();
        int widthPixels=metrics.widthPixels;
        float scaledDensity=metrics.scaledDensity;
        float densityDpi=metrics.densityDpi;
        float xdpi=metrics.xdpi;
        float density=metrics.density;
        Log.e("noah","widthPixels="+widthPixels);
        Log.e("noah","densityDpi="+densityDpi);
        Log.e("noah","xdpi="+xdpi);
        Log.e("noah","density="+density);
        Log.e("noah","scaledDensity="+scaledDensity);
        Log.e("noah","applyDimension 12="+TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,12f,metrics));
        Log.e("noah","(12*scaledDensity * (widthPixels/720f))="+(12*scaledDensity * (widthPixels/720f)));
        Log.e("noah","(12sp*scaledDensity * (xdpi/360f))="+(12*scaledDensity * (xdpi/360f)));
        Log.e("noah","(12sp*scaledDensity * (410f/360f))="+(12*scaledDensity * (410f/360f)));
        tv1.setTextSize(TypedValue.COMPLEX_UNIT_PX,12*scaledDensity * (410f/360f));
        tv2.setTextSize(TypedValue.COMPLEX_UNIT_PX,15*scaledDensity * (410f/360f));
    }

}
