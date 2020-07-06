package com.example.myfiles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myfiles.model.ImageDetail;

import java.util.List;

public class GalleryAdapter extends BaseAdapter {

    private Context ctx;

    private List<ImageDetail> mArrayUri;

    public GalleryAdapter(Context ctx, List<ImageDetail> mArrayUri) {
        this.ctx = ctx;
        this.mArrayUri = mArrayUri;
    }

    @Override
    public int getCount() {
        return mArrayUri.size();
    }

    @Override
    public Object getItem(int position) {
        return mArrayUri.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = LayoutInflater.from(ctx)
                .inflate(R.layout.gv_item, parent, false);
        ImageDetail imageDetail = mArrayUri.get(position);

        ImageView ivGallery = (ImageView) itemView.findViewById(R.id.ivGallery);
        ivGallery.setImageURI(imageDetail.getUri());

        TextView tvName = (TextView) itemView.findViewById(R.id.tvName);
        tvName.setText(imageDetail.getDisplayName());

        return itemView;
    }


}
