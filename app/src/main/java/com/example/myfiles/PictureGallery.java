package com.example.myfiles;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myfiles.model.ImageDetail;
import com.example.myfiles.model.StaticImages;

import java.util.ArrayList;
import java.util.List;

public class PictureGallery extends AppCompatActivity {

    private int PICK_IMAGE_MULTIPLE = 1;

    private List<ImageDetail> mImages;

    private Button btnGallery;

    private Button btnNext;

    private GridView gvGallery;

    private GalleryAdapter galleryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        btnGallery = findViewById(R.id.btn_choose_image);
        btnNext = findViewById(R.id.btn_next);
        gvGallery = findViewById(R.id.gv);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mImages = new ArrayList<>();
        galleryAdapter = new GalleryAdapter(this, mImages);
        gvGallery.setAdapter(galleryAdapter);
        gvGallery.setVerticalSpacing(gvGallery.getHorizontalSpacing());

        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) gvGallery
                .getLayoutParams();
        mlp.setMargins(0, gvGallery.getHorizontalSpacing(), 0, 0);

        btnGallery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                StaticImages.setImageDetails(mImages);
                Intent i = new Intent(PictureGallery.this, PictureChoose.class);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.detail) {
            Toast.makeText(getApplicationContext(), "Detail", Toast.LENGTH_SHORT).show();
        }else if (id == R.id.share){
            Toast.makeText(getApplicationContext(),"Share", Toast.LENGTH_SHORT).show();
        }else if (id == R.id.delete){
            Toast.makeText(getApplicationContext(),"Delete", Toast.LENGTH_SHORT).show();
        }else if (id == R.id.copy){
            Toast.makeText(getApplicationContext(),"Copy", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
                if (data.getData() != null) {
                    saveImageDetail(data.getData());
                } else {
                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            saveImageDetail(item.getUri());
                        }
                    }
                }
                galleryAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void saveImageDetail(Uri uri) {
        Cursor cursor = queryImage(uri);

        if (cursor != null) {
            mImages.add(getImageDetail(cursor, uri));
        }
    }

    private Cursor queryImage(Uri mImageUri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME};
        return getContentResolver().query(mImageUri, filePathColumn, null,
                null, null);
    }

    private ImageDetail getImageDetail(Cursor cursor, Uri uri) {
        cursor.moveToFirst();

        String dataPath = cursor.getString(
                cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        String displayName = cursor.getString(
                cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));

        cursor.close();

        return new ImageDetail(uri, dataPath, displayName);
    }
}