package com.edomar.battleship.activities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.edomar.battleship.R;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**Questa classe permette di avere una gestione personalizzata dll'elenco di nomi mostrato nella MatchMakingActivity**/

class CustomAdapter extends ArrayAdapter<String> {

    private static final String TAG = "CustomAdapter";

    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    static class ViewHolder{
        TextView textView;
    }

    public CustomAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    public CustomAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.mContext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {



        //create the view result for showing the animation
        final View result;

        //ViewHolder object
        ViewHolder holder;

        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);
        holder= new ViewHolder();
        holder.textView = (TextView) convertView.findViewById(R.id.player_name_txtView);

        result = convertView;

        convertView.setTag(holder);


        Log.d(TAG, "getView: holder is null? = "+ (holder==null));
        holder.textView.setText(getItem(position));

        return convertView;
    }


}
