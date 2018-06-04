package com.example.sunil_karan.breakout;

import android.app.Activity;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RxRulerWheelView view = (RxRulerWheelView) findViewById(R.id.rxr);
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            String num = String.valueOf(2-(i*0.5));
            if (num.equals("0.0")){
                num = "0";
            }
            items.add(num);
            view.setItems(items);
            view.setAdditionCenterMark("");
            view.selectIndex(4);
        }
    }
}
