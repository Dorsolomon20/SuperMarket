package com.dorsolo.supermarket.listeners;

/**
 * Listener for the ImageSourceDialog class
 */
public interface ImageSourceListener {

    /**
     * Called from the ImageSourceDialog class once one of the image sources as selected, pass the selected
     * code to the launching class
     *
     * @param code Code of the selected image source, can be either Camera (Full size image) Or Gallery
     */
    void imageSource(int code);
}