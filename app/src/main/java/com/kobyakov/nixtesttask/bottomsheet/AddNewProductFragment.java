package com.kobyakov.nixtesttask.bottomsheet;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.kobyakov.nixtesttask.R;
import com.kobyakov.nixtesttask.model.Product;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class AddNewProductFragment extends BottomSheetDialogFragment {

    private final String TAG = getClass().getSimpleName();

    private InsertListener insertListener;

    @BindView(R.id.photo)
    ImageView imageView;
    @BindView(R.id.product_title)
    TextInputEditText editText;

    private Unbinder unbinder;
    private String path;

    private static final int REQUEST_IMAGE_CAPTURE = 100;
    private static final int REQUEST_IMAGE_FROM_GALLERY = 101;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 120;
    private static final String AUTHORITY = "com.kobyakov.nixtesttask.provider";
    private static final String PATH = "path";
    private RequestManager glide;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_FROM_GALLERY && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String imgPath = replaceURIToNeedPath(selectedImage);
            if (imgPath.contains("external")) {
                path = getRealPathFromUri(Objects.requireNonNull(getContext()), selectedImage);
            } else {
                path = imgPath;
            }
        }
        glide.load(path).error(glide.load(R.drawable.help)).into(imageView);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        glide = Glide.with(this);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (path != null) {
            outState.putString(PATH, path);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null && savedInstanceState.getString(PATH) != null) {
            path = savedInstanceState.getString(PATH);
            glide.load(path).into(imageView);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_product, container, false);

        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        try (Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null)) {
            int column_index = Objects.requireNonNull(cursor).getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
    }

    private String replaceURIToNeedPath(Uri uri) {
        String result = "";
        if (uri != null && uri.getPath() != null) {
            result = uri.getPath().replace("/raw//", "");
        }
        return result;
    }

    @OnClick(R.id.btn_send)
    public void send() {
        String title = Objects.requireNonNull(editText.getText()).length() > 0 ? editText.getText().toString() : getString(R.string.undefined);

        Product product = new Product(title, path, getCurrentDateAndTime());
        insertListener.insertToDb(product, getString(R.string.added_new_position));

        path = null;
        dismiss();
    }

    @OnClick(R.id.btn_set_photo_from_gallery)
    public void setPhotoFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Objects.requireNonNull(getContext()).checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        } else {
            openGalleryIntent();
        }
    }

    private String getCurrentDateAndTime() {
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return simpleDateFormat.format(currentTime);
    }

    private void openGalleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, REQUEST_IMAGE_FROM_GALLERY);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_WRITE_EXTERNAL_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGalleryIntent();
                } else {
                    Toast.makeText(getActivity(), R.string.permission_denied, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @OnClick(R.id.btn_set_photo_from_camera)
    public void setPhotoFromCamera() {
        openCameraIntent();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = Objects.requireNonNull(getContext()).getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(Objects.requireNonNull(getContext()).getPackageManager()) != null) {
            //Create a file to store the image
            File photoFile = null;
            try {
                photoFile = createImageFile();
                path = photoFile.getAbsolutePath();
            } catch (IOException ex) {
                Log.d(TAG, ex.getLocalizedMessage());
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(), AUTHORITY, photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(pictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        insertListener = (InsertListener) context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        Log.d(TAG, "onDestroyView");
    }

    public interface InsertListener {
        void insertToDb(Product product, String message);
    }
}