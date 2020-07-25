package com.example.myfiles;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myfiles.model.ImageDetail;

import java.util.ArrayList;
import java.util.List;

public class GalleryAdapter extends BaseAdapter {

    private Context ctx;

    private List<ImageDetail> mArrayUri;

    public ArrayList<Uri> selectedImages;


    public GalleryAdapter(Context ctx, List<ImageDetail> mArrayUri) {
        this.ctx = ctx;
        this.mArrayUri = mArrayUri;
        selectedImages = new ArrayList<>();
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
    public int getItemViewType(int position) {
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

        final CheckBox itemCheckBox = itemView.findViewById(R.id.itemCheckBox);

        final ImageDetail image = mArrayUri.get(position);



        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d("imagepath", image.getDataPath());
                if (itemCheckBox.isChecked()){
                    itemCheckBox.setChecked(false);
                    selectedImages.remove(image.getUri());
                } else {
                    itemCheckBox.setChecked(true);
                    selectedImages.add(image.getUri());
                }
            }
        });

        itemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemCheckBox.isChecked()){
                    selectedImages.add(image.getUri());
                } else {

                    selectedImages.remove(image.getUri());

                }
            }
        });

        return itemView;
    }

}