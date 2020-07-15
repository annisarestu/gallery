package com.example.myfiles;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myfiles.model.ImageDetail;
import com.example.myfiles.model.StaticImages;

import java.util.ArrayList;
import java.util.List;

public class PictureGallery extends AppCompatActivity {

    private static final String TAG = "PictureGallery" ;
    private static final String LAST_MODIFIED_COLUMN_NAME = "last_modified";

    private int PICK_IMAGE_MULTIPLE = 1;

    private List<ImageDetail> mImages;

    private Button btnGallery;

    private Button btnNext;

    private GridView gvGallery;

    private GalleryAdapter galleryAdapter;

    private ShareActionProvider shareActionProvider;
    private Uri imageUri1, imageUri2;

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
                intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
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
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);

        MenuItem item = menu.findItem(R.id.share);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){

            case R.id.share:
                ArrayList<Uri> imageUris = new ArrayList<Uri>();
                imageUris.add(imageUri1); // Add your image URIs here
                imageUris.add(imageUri2);

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
                shareIntent.setType("image/*");
                startActivity(Intent.createChooser(shareIntent, "Share images to.."));

            case R.id.delete:
               
                break;
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

    private void saveImageDetail(Uri uri) throws Exception {
        Cursor cursor = queryImage(uri);

        if (cursor != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getContentResolver().takePersistableUriPermission(uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
            mImages.add(getImageDetail(cursor, uri));
        }
    }

    private Cursor queryImage(Uri mImageUri) {
        String[] projections = new String[] {
                MediaStore.Images.Media.DATA, MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.MIME_TYPE, LAST_MODIFIED_COLUMN_NAME,
                MediaStore.Images.Media.SIZE
        };
        return getContentResolver().query(mImageUri, projections, null,
                null, null);
    }

    private ImageDetail getImageDetail(Cursor cursor, Uri uri) throws Exception {
        cursor.moveToFirst();

        String dataPath = cursor.getString(
                cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        String displayName = cursor.getString(
                cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
        String mimeType = cursor.getString(
                cursor.getColumnIndex(MediaStore.Images.Media.MIME_TYPE));
        Long dateModified = cursor.getLong(cursor.getColumnIndex(LAST_MODIFIED_COLUMN_NAME));
        Long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.SIZE));
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

        cursor.close();

        return new ImageDetail(bitmap, uri, dataPath, displayName, mimeType, dateModified, size);
    }
}