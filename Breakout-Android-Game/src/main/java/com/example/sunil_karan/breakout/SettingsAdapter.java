package com.example.sunil_karan.breakout;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * Created by hozo（ ) on 2016-6-24 22:39.
 */
public class SettingsAdapter extends BaseAdapter implements View.OnClickListener{

    private LayoutInflater mLayoutInflater;
    private Context context;
    private ArrayList<HashMap<String, Object>> listDatas;


    public SettingsAdapter(Context context, ArrayList<HashMap<String, Object>> listDatas) {
        this.listDatas = listDatas;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //返回你有多少个不同的布局
    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getCount() {
        return listDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return listDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HashMap<String, Object> listItem = listDatas.get(position);
        ViewHolderThreeType viewHolderThreeType = null;
        viewHolderThreeType = new ViewHolderThreeType();
        convertView = mLayoutInflater.inflate(R.layout.activity_three_type_item, null);
        List<String> items = new ArrayList<>();
        for (int i = 0; i <= 8; i++) {
            String num = String.valueOf(2 - (i * 0.5));
            if (num.equals("0.0")) {
                num = "0";
            }
            items.add(num);
        }
        viewHolderThreeType.mWheelView = (RxRulerWheelView) convertView.findViewById(R.id.wheelview);
        viewHolderThreeType.mWheelView.setItems(items);
        viewHolderThreeType.mWheelView.setAdditionCenterMark("");
        viewHolderThreeType.mWheelView.selectIndex(4);
        viewHolderThreeType.mWheelView.setOnWheelItemSelectedListener((RxRulerWheelView.OnWheelItemSelectedListener) listItem.get("Listner"));
        convertView.setTag(viewHolderThreeType);//如果要复用，不同于一种样式的listView ,这样的写法是错误的,具体原因看下面第一个小哥评论的说法
//                    convertView.setTag(R.id.title_three, viewHolderThreeType);
        return convertView;
    }

    @Override
    public void onClick(View v) {

    }

    class ViewHolderThreeType {
        RxRulerWheelView mWheelView;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

//        RxRulerWheelView mWheelView;
        public MyViewHolder(RxRulerWheelView mWheelView) {

            super(mWheelView);
        }
    }

}

