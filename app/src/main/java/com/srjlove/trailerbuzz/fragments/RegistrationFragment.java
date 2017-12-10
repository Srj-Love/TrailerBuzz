package com.srjlove.trailerbuzz.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.srjlove.trailerbuzz.R;
import com.srjlove.trailerbuzz.databinding.FragmentRegistrationBinding;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Suraj on 12/4/2017.
 */

public class RegistrationFragment extends Fragment {


    public static final String CHAT_PREFS = "ChatPrefs";
    public static final String DISPLAY_NAME_KEY = "username";

    private static final String TAG = RegistrationFragment.class.getSimpleName();
    private static String REDUCED_IMAGE_PATH;
    private FragmentActivity mActivity;
    private Context mContext;
    private FragmentRegistrationBinding mBinding;
    private FirebaseAuth mAuth;
    private final int UPLOAD_PIC_REQUEST_CODE = 456;
    private boolean mStoragePermission;
    private int ACCESSING_PERMISSIONS_REQUEST_CODE = 67;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_registration, container, false);

        mActivity = getActivity();
        mContext = getContext();
        mAuth = FirebaseAuth.getInstance();

        setupProfileData();
        mBinding.registerConfirmPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int id, KeyEvent event) {
                if (id == 666 || id == EditorInfo.IME_NULL) {
                    Log.d(TAG, "onEditorAction: attempt to register" + "");
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });
        mAuth.signOut();

        mBinding.registerSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegistration();
            }
        });
        return mBinding.getRoot();
    }

    /**
     * user clciked at IV
     */
    private void setupProfileData() {
        mBinding.ivPrifilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: clciked at profile IV");
                if (!mStoragePermission) {
                    startActivityForResult(new Intent(Intent.ACTION_PICK).setType("image/*"), UPLOAD_PIC_REQUEST_CODE);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        verifyStoragePermission();
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPLOAD_PIC_REQUEST_CODE && resultCode == Activity.RESULT_OK) { // all OK
            Uri imgFile = data.getData();
            String compressImgeOfFile = compressImage(imgFile.toString());

            Log.d(TAG, "onActivityResult: found image" + imgFile.toString());
            Log.d(TAG, "onActivityResult: found compression string image" + compressImgeOfFile);

            UploadNewPhotoTask mTask = new UploadNewPhotoTask();
            mTask.execute(imgFile.toString()); // now all work  done is responsible on AsyncTask
        }
    }


    /**
     * ***************          1   **********
     * referring from :  ->
     * https://stackoverflow.com/questions/37320239/how-to-resize-compress-a-camera-image-or-gallery-image-in-android-before-upload
     *
     * @param imageUri the image Uri
     * @return
     */
    private String compressImage(String imageUri) {

        String filePath = getRealPathFromURI(imageUri);
        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 816.0f;
        float maxWidth = 612.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }


//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        String filename = getFilename();
        try {
            out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filename;

    }

    /*       ********           4      ******     */

    // referring from : https://stackoverflow.com/questions/37320239/how-to-resize-compress-a-camera-image-or-gallery-image-in-android-before-upload
    public String getFilename() {
        File file = new File(Environment.getExternalStorageDirectory().getPath(), "MyFolder/Images");
        if (!file.exists()) {
            file.mkdirs();
        }
        String uriSting = (file.getAbsolutePath() + "/" + System.currentTimeMillis() + ".jpg");
        return uriSting;

    }

    /*           *************   3     ************       */
    // referring from : https://stackoverflow.com/questions/37320239/how-to-resize-compress-a-camera-image-or-gallery-image-in-android-before-upload
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    /*          *************   5   **************      */
    // referring from : https://stackoverflow.com/questions/37320239/how-to-resize-compress-a-camera-image-or-gallery-image-in-android-before-upload
    private String getRealPathFromURI(String contentURI) {
        Uri contentUri = Uri.parse(contentURI);
        Cursor cursor = mActivity.getContentResolver().query(contentUri, null, null, null, null);
        if (cursor == null) {
            return contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(index);
        }
    }


    /**
     * ***************     RUNTIME PERMISSION     ************
     * generating () for asking permission to user to access Internet
     * if the user device is >= JB
     */
    private void verifyStoragePermission() {
        Log.d(TAG, "verifyStoragePermission: asking for permissions");
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE, // read
                Manifest.permission.WRITE_EXTERNAL_STORAGE, // write
                Manifest.permission.CAMERA};  // camera
        if (ContextCompat.checkSelfPermission(this.mActivity.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.mActivity.getApplicationContext(),
                        permissions[1]) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this.mActivity.getApplicationContext(),
                        permissions[2]) == PackageManager.PERMISSION_GRANTED) {
            mStoragePermission = true; // user has given all the permissions let's navigate to Intent
        } else { // if user denied my request, asking him again for Permissions
            ActivityCompat.requestPermissions(mActivity, permissions, ACCESSING_PERMISSIONS_REQUEST_CODE);
        }


    }

    private void attemptRegistration() {

        // Reset errors displayed in the form.
        mBinding.registerEmail.setError(null);
        mBinding.registerPassword.setError(null);

        // Store values at the time of the login attempt.
        String email = mBinding.registerEmail.getText().toString();
        String password = mBinding.registerPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mBinding.registerPassword.setError(getString(R.string.error_invalid_password));
            focusView = mBinding.registerPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mBinding.registerEmail.setError(getString(R.string.error_field_required));
            focusView = mBinding.registerEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mBinding.registerEmail.setError(getString(R.string.error_invalid_email));
            focusView = mBinding.registerEmail;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            createFirebaseUser();
        }

    }

    //          creating FB user and registering to them
    private void createFirebaseUser() {
        Log.d(TAG, "createFirebaseUser: creating user");
        final String email = mBinding.registerEmail.getText().toString();
        String password = mBinding.registerPassword.getText().toString();
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(mActivity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                Log.d(TAG, "createUser onComplete: " + task.isSuccessful());

                if (!task.isSuccessful()) {
                    Log.d(TAG, "user create failed " + task.getException());
                    showErrorDialog("Registration attempt failed.");
                } else {
                    Toast.makeText(mActivity, "User created successfully", Toast.LENGTH_SHORT).show();
                    saveDisplayName(); // using SP to save user's Username

                    Fragment mFragment = new LoginFragment();
                    Bundle mBundle = new Bundle();
                    mBundle.putString(getString(R.string.login_email), email);

                    FirebaseAuth.getInstance().signOut();
//                    mBundle.putString(getString(R.string.image_file_path), REDUCED_IMAGE_PATH);
//                    mBundle.putString(getString(R.string.download_uri), downloadUrl.toString());

                    mFragment.setArguments(mBundle);
                    getFragmentManager().beginTransaction().replace(R.id.main_container, mFragment).commit();

                }

            }
        });
    }

    public void saveDisplayName() {
        String displayName = mBinding.registerUsername.getText().toString();
        SharedPreferences prefs = mActivity.getSharedPreferences(CHAT_PREFS, 0);
        prefs.edit().putString(DISPLAY_NAME_KEY, displayName).apply();
    }

    private boolean isEmailValid(String email) {
        //  logic to check for a valid email
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //  logic to check for a valid password
        String confirmPassword = mBinding.registerConfirmPassword.getText().toString();
        Log.d(TAG, "isPasswordValid: confirmPassword: " + confirmPassword);
        Log.d(TAG, "isPasswordValid: password: " + password);
        return confirmPassword.equals(password) && password.length() > 4;
    }

    // Creating an alert dialog to show in case registration failed
    private void showErrorDialog(String message) {
        new AlertDialog.Builder(mActivity)
                .setTitle("Oops")
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    /*
                All for saving an Image to file  in vary low size and retrieving from a file
     */
    private class UploadNewPhotoTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... strings) {
            REDUCED_IMAGE_PATH = compressImage(strings[0]);
            return compressImage(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            mBinding.ivPrifilePic.setImageURI(Uri.parse(s));
            executeUploadTaskToFirebase(s);
        }
    }

    private void executeUploadTaskToFirebase(String s) {
        //FilePaths mPath = new FilePaths();

        //                      FIREBASE PART TO STORE AT FB STORAGE
        mBinding.ivPrifilePic.buildDrawingCache();
        Bitmap bitmap = mBinding.ivPrifilePic.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] byteData = baos.toByteArray();

        final StorageReference mStorageReference = FirebaseStorage.getInstance().getReference()
                .child("images/users" + "/" + "/profile_image"+ mBinding.registerEmail.getText().toString()); // repplacing image with new images

        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpeg")
                .setContentLanguage("en")
                .setCustomMetadata("srj's first photo ", "uploading data")
                .setCustomMetadata("location", "India")
                .build();

        UploadTask mUploadTask = null;
        mUploadTask = mStorageReference.putBytes(byteData, metadata);
        mUploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                Log.d(TAG, "onSuccess: downloadUr l"+downloadUrl);

                setupDownloadUri(downloadUrl);
//                Toast.makeText(mActivity, "Stored image on Server" + downloadUrl.getPath(), Toast.LENGTH_SHORT).show();
//                FirebaseDatabase.getInstance().getReference().child(getString(R.string.dbnode_users))
//                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
//                        .child(getString(R.string.field_profile_image)).setValue(downloadUrl.toString());
//                Log.d(TAG, "onSuccess: Database uploaded to Firebase");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(mContext, "Could not Uploaded photo, Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDownloadUri(Uri downloadUrl) {
            String displayProfile = downloadUrl.toString();
            SharedPreferences prefs = mActivity.getSharedPreferences(CHAT_PREFS, 0);
            prefs.edit().putString(getString(R.string.download_uri), displayProfile).apply();
    }

}
