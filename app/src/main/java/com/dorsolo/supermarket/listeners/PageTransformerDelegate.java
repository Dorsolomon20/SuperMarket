package com.dorsolo.supermarket.listeners;

/**
 * Delegate the animation process from the PageTransformer to the FirstTimerSliderFragment in order to prevent
 * the findViewById() method each time we want to perform an animation, This approach will improve performance
 * tremendously
 */
public interface PageTransformerDelegate {

    /**
     * Called from the ParallaxPageTransformer to perform UI animations on swipe
     *
     * @param position Position of the page relative to the current front and center position of the pager
     */
    void transformPage(float position);
}