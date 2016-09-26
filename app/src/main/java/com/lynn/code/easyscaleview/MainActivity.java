package com.lynn.code.easyscaleview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.lynn.code.easyscaleselectorview.EasyBaseScaleView;
import com.lynn.code.easyscaleselectorview.HorizontalScaleView;
import com.lynn.code.easyscaleselectorview.SemiCircleScaleView;
import com.lynn.code.easyscaleselectorview.VerticalScaleView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private HorizontalScaleView mHorizontal;
    private VerticalScaleView mVertical;
    private SemiCircleScaleView mSemiCircle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHorizontal = (HorizontalScaleView) findViewById(R.id.horizontal_scale);
        mVertical = (VerticalScaleView) findViewById(R.id.vertical_scale);
        mSemiCircle = (SemiCircleScaleView) findViewById(R.id.semi_circle_scale);

        List<String> values = new ArrayList<>();
        for (int i = 0; i <= 200; i++) {
            values.add(i + "");
        }

        mHorizontal.initValues(values, new EasyBaseScaleView.OnValueSelectedCallback() {
            @Override
            public void onValueSelected(String value) {
                Log.d("horizontal value: ", value);
            }
        });

        mVertical.initValues(values, new EasyBaseScaleView.OnValueSelectedCallback() {
            @Override
            public void onValueSelected(String value) {
                Log.d("vertical value: ", value);
            }
        });

        mSemiCircle.initValues(values, new EasyBaseScaleView.OnValueSelectedCallback() {
            @Override
            public void onValueSelected(String value) {
                Log.d("semicircle value: ", value);
            }
        });
    }
}
