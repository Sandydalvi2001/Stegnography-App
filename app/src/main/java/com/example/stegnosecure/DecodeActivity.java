package com.example.stegnosecure;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.Callable;

public class DecodeActivity extends AppCompatActivity implements TextDecodingCallback{

    TextView textView;
    ImageView imageView2;
    EditText message,secret_key;
    Button decode_button,choose_image_button;
    String outputString,AES="AES";


    private static final int SELECT_PICTURE = 100;
    private static final String TAG = "Decode Class";
    //Initializing the UI components
    private Uri filepath;
    //Bitmap
    private Bitmap original_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_decode);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        textView=(TextView)findViewById(R.id.whether_decoded);
        imageView2=(ImageView)findViewById(R.id.image_view2);
        message=(EditText)findViewById(R.id.message) ;
        secret_key=(EditText)findViewById(R.id.secret_key) ;
        decode_button=(Button)findViewById(R.id.decode_button);
        choose_image_button=(Button)findViewById(R.id.choose_image_button);


//        decBtn.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onClick(View v) {
//                try {
//                    outputString= decrypt(encText.getText().toString(),decPasw.getText().toString());
//                    outputText.setText(outputString);
//                }
//                catch(Exception e)
//                {
//                    e.printStackTrace();
//                }
//
//            }
//        });

        //Choose Image Button
        choose_image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageChooser();
            }
        });

        //Decode Button
        decode_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filepath != null) {

                    //Making the ImageSteganography object
                    ImageSteganography imageSteganography = new ImageSteganography(secret_key.getText().toString(),
                            original_image);

                    //Making the TextDecoding object
                    TextDecoding textDecoding = new TextDecoding(DecodeActivity.this, DecodeActivity.this);

                    //Execute Task
                    textDecoding.execute(imageSteganography);
                }
            }
        });


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

//                if (android.os.Build.VERSION.SDK_INT >= 29){
//                    // To handle deprication use
//                    original_image=ImageDecoder.decodeBitmap(ImageDecoder.createSource((Callable<AssetFileDescriptor>) filepath));
////                    ImageDecoder.decodeBitmap(Imagedecoder.createSource())
//                } else{
//                    // Use older version
//                    original_image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filepath);
//                }

                original_image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filepath);

                imageView2.setImageBitmap(original_image);
            } catch (IOException e) {
                Log.d(TAG, "Error : " + e);
            }
        }

    }

    @Override
    public void onStartTextEncoding() {
        //Whatever you want to do by the start of textDecoding
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCompleteTextEncoding(ImageSteganography result) {

        //By the end of textDecoding

        if (result != null) {
            if (!result.isDecoded())
                textView.setText("No message found");
            else {
                if (!result.isSecretKeyWrong()) {
                    textView.setText("Decoded");
                    message.setText("" + result.getMessage());
                } else {
                    textView.setText("Wrong secret key");
                }
            }
        } else {
            textView.setText("Select Image First");
        }


    }

//    private String decrypt(String outputString, String password) throws Exception {
//        SecretKeySpec key=generateKey(password);
//        Cipher c=Cipher.getInstance(AES);
//        c.init(Cipher.DECRYPT_MODE,key);
//        byte[] decodedValue=android.util.Base64.decode(outputString,android.util.Base64.DEFAULT);
//        byte[] decValue=c.doFinal(decodedValue);
//        String decryptedValue=new String(decValue);
//        return decryptedValue;
//
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
}