package com.example.stegnosecure;

import android.Manifest;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class EncodeActivity extends AppCompatActivity implements TextEncodingCallback{

    TextView whether_encoded;
    ImageView imageView;
    EditText message,secret_key;
    Button btn2,cibtn,encodeBtn,save_image_btn,share_btn;
    String outputString,AES="AES";
    //Objects needed for encoding
    private TextEncoding textEncoding;
    private ImageSteganography imageSteganography;
    private ProgressDialog save;
    private Uri filepath;
    //Bitmaps
    private Bitmap original_image;
    private Bitmap encoded_image;

//    private static final int IMAGE_PICK_CODE = 1000;

    private static final int SELECT_PICTURE = 100;

    private static final String TAG = "EncodeActivity Class";

//    private static final int PERMISSION_REQUEST_CODE=100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encode);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView=(ImageView)findViewById(R.id.imageView);
        message=(EditText)findViewById(R.id.message);
        secret_key=(EditText)findViewById(R.id.secret_key);
        btn2=(Button)findViewById(R.id.btn2);
        cibtn=(Button)findViewById(R.id.cibtn);
        encodeBtn=(Button)findViewById(R.id.encodeBtn);
        whether_encoded=(TextView)findViewById(R.id.whether_encoded);
        save_image_btn=(Button)findViewById(R.id.save_image_btn);
        share_btn=(Button)findViewById(R.id.share_btn);

        //share button
        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                File path=fil
//                Uri uriPath=Uri.fromFile(file1);
//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("image/png");
//                intent.putExtra(Intent.EXTRA_SUBJECT,"send encoded image");
//                intent.putExtra(Intent.EXTRA_STREAM, uriPath);
//
//                if(intent.resolveActivity(getPackageManager())!= null)
//                {
//                    startActivity(Intent.createChooser(intent, "Share"));
//                }


                ////////////
//                final Bitmap share_img = encoded_image;
//                Uri file_path=filepath;
//                shareImage(share_img,file_path);
                ///////////////

//                File imagePath = new File(context.getCacheDir(), "images");
//                File newFile = new File(imagePath, "image.png");
//                Uri contentUri = FileProvider.getUriForFile(context, "com.example.myapp.fileprovider", newFile);

//                if (contentUri != null) {
//                    Intent shareIntent = new Intent();
//                    shareIntent.setAction(Intent.ACTION_SEND);
//                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
//                    shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
//                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
//                    startActivity(Intent.createChooser(shareIntent, "Choose an app"));


//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                intent.setDataAndType(Uri.parse("file://" + "/sdcard/download"), "image/*");
//                startActivityForResult(Intent.createChooser(intent, "Select image to share:"), SELECT_PICTURE);


//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.putExtra(Intent.EXTRA_SUBJECT,"m");
//                intent.setType("text/plain");
////                intent.setData(Uri.parse("mailto:"));
//                if(intent.resolveActivity(getPackageManager())!= null)
//                {
//                    startActivity(intent);
//                }
            }
        });

        checkAndRequestPermissions();

        //Choose image button
        cibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageChooser();
            }
        });

        //Encode Button
        encodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whether_encoded.setText("");
                if (filepath != null) {

                    if (message.getText() != null) {

                        //ImageSteganography Object instantiation
                        imageSteganography = new ImageSteganography(message.getText().toString(),
                                secret_key.getText().toString(),
                                original_image);
                        //TextEncoding object Instantiation
                        textEncoding = new TextEncoding(EncodeActivity.this, EncodeActivity.this);
                        //Executing the encoding
                        textEncoding.execute(imageSteganography);
                    }
                }
            }
        });


//Save image button
        save_image_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Bitmap imgToSave = encoded_image;
                Thread PerformEncoding = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveToInternalStorage(imgToSave);
                    }
                });
                save = new ProgressDialog(EncodeActivity.this);
                save.setMessage("Saving, Please Wait...");
                save.setTitle("Saving Image");
                save.setIndeterminate(false);
                save.setCancelable(false);
                save.show();
                ////////////////////////

//              startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));

                ////////////////////////
                PerformEncoding.start();
            }
        });

    }

//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK) {
//            if (requestCode == SELECT_PICTURE) {
//                Uri selectedImageUri = data.getData();
//                //OI FILE Manager
//                filemanagerstring = selectedImageUri.getPath();
//
//                //MEDIA GALLERY
//                selectedImagePath = getPath(selectedImageUri);
//
//                //NOW WE HAVE OUR WANTED STRING
//                if (selectedImagePath != null)
//                    System.out.println("selectedImagePath is the right one for you!");
//                else
//                    System.out.println("filemanagerstring is the right one for you!");
//
//                Intent shareIntent = new Intent();
//                shareIntent.setAction(Intent.ACTION_SEND);
//                shareIntent.putExtra(Intent.EXTRA_STREAM, selectedImageUri);
//                shareIntent.setType("image/*");
//                startActivity(Intent.createChooser(shareIntent, "Share image via:"));
//            }
//        }
//    }

    private void shareImage(Bitmap share_img, Uri uri) {

//        File f=new File(getExternalCacheDir()+"/"+getResources().getString(R.string.app_name)+".png");
//        Intent shareint;
//
//        try {
//            FileOutputStream outputStream=new FileOutputStream(f);
//            share_img.compress(Bitmap.CompressFormat.PNG,100,outputStream);
//
//            outputStream.flush();
//            outputStream.close();
//            shareint =new Intent(Intent.ACTION_SEND);
//            shareint.setType("image/png");
//            shareint.putExtra(Intent.EXTRA_STREAM,Uri.fromFile(f));
//            shareint.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        }catch (Exception e)
//        {
//            throw new RuntimeException(e);
//        }
//
//        startActivity(Intent.createChooser(shareint,"share image"));


//////////////////////// //////////////////////
//        String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), share_img, "palette", "share palette");
//        Uri bitmapUri = Uri.parse(bitmapPath);
//
//        Intent intent = new Intent(Intent.ACTION_SEND);
//        intent.setType("image/png");
//        intent.putExtra(Intent.EXTRA_SUBJECT,"send encoded image");
//        intent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
//
//        if(intent.resolveActivity(getPackageManager())!= null)
//        {
//            startActivity(Intent.createChooser(intent, "Share"));
//        }
//////////////////////////////////////

        //////////////////////
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setType("image/png");
        startActivity(intent);
        ///////////////////////////

//        try {
//            fOut = new FileOutputStream(file);
//            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fOut); // saving the Bitmap to a file
//            fOut.flush(); // Not really required
//            fOut.close(); // do not forget to close the stream
//            whether_encoded.post(new Runnable() {
//                @Override
//                public void run() {
//                    save.dismiss();
//                }
//            });
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private void ImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Image set to imageView
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filepath = data.getData();
            try {
                original_image = MediaStore.Images.Media.getBitmap(getContentResolver(), filepath);

                imageView.setImageBitmap(original_image);
            } catch (IOException e) {
                Log.d(TAG, "Error : " + e);
            }
        }
    }



    private void saveToInternalStorage(Bitmap bitmapImage) {
        OutputStream fOut;
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), "Encoded" + ".PNG"); // the File to save ,
        try {
            fOut = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fOut); // saving the Bitmap to a file
            fOut.flush(); // Not really required
            fOut.close(); // do not forget to close the stream
            /////////////////////////////////////

//            Uri uri=Uri.parse(file.getPath());

//            Intent intent = new Intent(Intent.ACTION_SEND);
//            intent.setType("image/png");
//            intent.putExtra(Intent.EXTRA_SUBJECT,"send encoded image");
//            intent.putExtra(Intent.EXTRA_STREAM, uri);
//
//            if(intent.resolveActivity(getPackageManager())!= null)
//            {
//                startActivity(Intent.createChooser(intent, "Share"));
//            }


//            Intent intent = new Intent(android.content.Intent.ACTION_SEND);
//            intent.putExtra(Intent.EXTRA_STREAM, uri);
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.setType("image/png");
//            startActivity(intent);

            //////////////////////////////////////
            whether_encoded.post(new Runnable() {
                @Override
                public void run() {
                    save.dismiss();
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkAndRequestPermissions() {

        int permissionWriteStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int ReadPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (ReadPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionWriteStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[0]), 1);
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private String encrypt(String Data, String password) throws Exception{
//        SecretKeySpec key=generateKey(password);
//        Cipher c=Cipher.getInstance(AES);
//        c.init(Cipher.ENCRYPT_MODE,key);
//        byte[] encVal = c.doFinal(Data.getBytes());
//        String Base64encodeString=android.util.Base64.encodeToString(encVal, android.util.Base64.DEFAULT);
////                Base64.Encoder encoder = Base64.getEncoder(); // for the basic encoder, or:
//        return Base64encodeString;
//    }
//
//    private SecretKeySpec generateKey(String password) throws Exception{
//        final MessageDigest digest=MessageDigest.getInstance("SHA-256");
//        byte[] bytes=password.getBytes("UTF-8");
//        digest.update(bytes,0,bytes.length);
//        byte[] key=digest.digest();
//        SecretKeySpec secretKeySpec=new SecretKeySpec(key,"AES");
//        return secretKeySpec;
//    }

    @Override
    public void onStartTextEncoding() {

    }

    @Override
    public void onCompleteTextEncoding(ImageSteganography result) {

        //By the end of textEncoding

        if (result != null && result.isEncoded()) {
            encoded_image = result.getEncoded_image();
            whether_encoded.setText("Encoded");
            imageView.setImageBitmap(encoded_image);

        }
    }
}
//path="Android/data/it.steganography/files/Pictures"