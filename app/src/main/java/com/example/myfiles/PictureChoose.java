package com.example.myfiles;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.myfiles.model.ImageDetail;
import com.example.myfiles.model.StaticImages;
import com.example.myfiles.processor.ClusterProcessor;
import com.example.myfiles.processor.strategy.ClusterStrategy;
import com.example.myfiles.processor.strategy.ColorClusterStrategy;
import com.example.myfiles.processor.strategy.DateClusterStrategy;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PictureChoose extends AppCompatActivity implements SingleChoiceDialogFragment.SingleChoiceListener{

    private static final String LAST_MODIFIED_COLUMN_NAME = "last_modified";

    private TextView tvCluster;
    private Button btnImg;
    private ImageView imageview;
    private GridView gridView;
    private static final String IMAGE_DIRECTORY = "/cluster";
    private int GALLERY = 1, CAMERA = 2;
    private GalleryAdapter galleryAdapter;
    private ImageDetail imageDetail;
    private List<ImageDetail> similarImages;
    private int selectedAlgorithm = 0;

    private ClusterProcessor clusterProcessor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_cluster);

        requestMultiplePermissions();

        tvCluster = findViewById(R.id.tvCluster);
        btnImg = findViewById(R.id.im_cluster);
        imageview = findViewById(R.id.iv);
        gridView = findViewById(R.id.gv);

        similarImages = new ArrayList<>();
        clusterProcessor = new ClusterProcessor();

        galleryAdapter = new GalleryAdapter(this, similarImages);
        gridView.setAdapter(galleryAdapter);
        gridView.setVerticalSpacing(gridView.getHorizontalSpacing());

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPictureDialog();
            }
        });

        btnImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment singleChoiceDialog = new SingleChoiceDialogFragment(selectedAlgorithm);
                singleChoiceDialog.setCancelable(false);
                singleChoiceDialog.show(getSupportFragmentManager(),"Single Choice Dialog");
            }
        });

    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent intent = new Intent();

        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }

        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    imageDetail = getImageDetail(queryImage(contentURI), contentURI);
                    Toast.makeText(PictureChoose.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    imageview.setImageBitmap(imageDetail.getBitmap());
                } catch (Exception e) {
                    Toast.makeText(PictureChoose.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            try {
                Uri contentUri = saveImage(bitmap);
                imageDetail = getImageDetail(queryImage(contentUri), contentUri);
                imageview.setImageBitmap(bitmap);
                Toast.makeText(PictureChoose.this, "Image Saved!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(PictureChoose.this, "Failed!", Toast.LENGTH_SHORT).show();
            }
        }

        similarImages.clear();
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

    private void searchSimilarImages(ClusterStrategy clusterStrategy) {
        if (imageDetail != null) {
            List<ImageDetail> imageDetails = new ArrayList<>(StaticImages.getImageDetails());
            imageDetails.add(0, imageDetail);
            similarImages.clear();
            similarImages.addAll(clusterProcessor.processCluster(imageDetails, clusterStrategy));
            similarImages.remove(0);
            galleryAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "You haven't selected any image...", Toast.LENGTH_LONG).show();
        }
    }

    public Uri saveImage(Bitmap myBitmap) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);

        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        File f = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".jpg");
        f.createNewFile();
        FileOutputStream fo = new FileOutputStream(f);
        fo.write(bytes.toByteArray());
        MediaScannerConnection.scanFile(this,
                new String[]{f.getPath()},
                new String[]{"image/jpeg"}, null);
        fo.close();
        Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

        return Uri.fromFile(f);
    }

    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            Toast.makeText(getApplicationContext(), "All permissions are granted by user!", Toast.LENGTH_SHORT).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            //openSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), "Some Error! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    @Override
    public void onPositiveButtonClicked(String[] list, int position) {
        String selected = "Selected Item : " + list[position];
        tvCluster.setText(selected);
        selectedAlgorithm = position;

        switch (position) {
            case 0:
                searchSimilarImages(ColorClusterStrategy.getInstance());
                break;
            case 1:
                searchSimilarImages(DateClusterStrategy.getInstance());
                break;
            default:
                Toast.makeText(this, "Selected algorithm not found...", Toast.LENGTH_LONG).show();
                similarImages.clear();
        }
    }

    @Override
    public void onNegativeButtonClicked() {
        tvCluster.setText("Cancel");
    }
}
