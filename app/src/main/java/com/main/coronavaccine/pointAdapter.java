package com.main.coronavaccine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.main.coronavaccine.MainActivity;
import com.main.coronavaccine.MapActivity;
import com.main.coronavaccine.R;
import com.naver.maps.map.overlay.InfoWindow;
import com.naver.maps.map.overlay.Marker;

import java.util.ArrayList;

public class pointAdapter extends InfoWindow.DefaultViewAdapter
{
    private final Context mContext;
    private final ViewGroup mParent;
    ArrayList<String[]> center = new ArrayList<>();
    Marker[] mmarkers;
    int[] mindex;
    public pointAdapter(@NonNull Context context, ViewGroup parent, Marker[] markers, int[] index)
    {
        super(context);
        mContext = context;
        mParent = parent;
        mmarkers = markers;
        mindex = index;
    }

    @NonNull
    @Override
    protected View getContentView(@NonNull InfoWindow infoWindow)
    {
        Marker marker = infoWindow.getMarker();
        int i =0;
        for(i = 0; i<mmarkers.length; i++){
            if(marker == mmarkers[i])
                break;
        }
        center = MapActivity.st_center;
        View view = (View) LayoutInflater.from(mContext).inflate(R.layout.adapter, mParent, false);

        TextView txtType = (TextView) view.findViewById(R.id.txttype);
        TextView txtApp = (TextView) view.findViewById(R.id.txtapp);
        TextView txtTitle = (TextView) view.findViewById(R.id.txttitle);
        TextView txtAddr = (TextView) view.findViewById(R.id.txtaddr);
        TextView txtTel = (TextView) view.findViewById(R.id.txttel);
        System.out.println(mindex[i]);

        txtTitle.setText(center.get(mindex[i])[2]);
        txtType.setText(center.get(mindex[i])[1]);
        txtApp.setText(center.get(mindex[i])[4]);
        txtAddr.setText(center.get(mindex[i])[6]);
        txtTel.setText(center.get(mindex[i])[7]);

        return view;
    }
}
