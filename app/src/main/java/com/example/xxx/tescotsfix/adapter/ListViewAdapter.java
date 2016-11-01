package com.example.xxx.tescotsfix.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.xxx.tescotsfix.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by xxx on 28/10/2016.
 */

public class ListViewAdapter extends BaseAdapter {

    JSONArray jsonArray;

    public ListViewAdapter(JSONArray jsonArray) {
        this.jsonArray = jsonArray;
    }

    @Override
    public int getCount() {
        return jsonArray.length();
    }

    @Override
    public JSONObject getItem(int i) {
        try {
            return jsonArray.getJSONObject(i);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder;
        if (view == null){
            LayoutInflater inflater = (LayoutInflater) viewGroup.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_listview, viewGroup, false);

            holder = new Holder();
            holder.textViewItem = (TextView) view.findViewById(R.id.tv_item);

            view.setTag(holder);
        }else {
            holder = (Holder) view.getTag();
        }

        try {
            holder.textViewItem.setText(getItem(i).getString("nama"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }

    class Holder{
        TextView textViewItem;
    }
}
