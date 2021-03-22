package com.example.stegnosecure;

public interface TextDecodingCallback {

    void onStartTextEncoding();

    void onCompleteTextEncoding(ImageSteganography result);
}
