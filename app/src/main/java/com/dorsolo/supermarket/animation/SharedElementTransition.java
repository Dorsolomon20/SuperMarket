package com.dorsolo.supermarket.animation;

import android.transition.ChangeBounds;
import android.transition.ChangeImageTransform;
import android.transition.ChangeTransform;
import android.transition.TransitionSet;

/**
 * This class provides the logic for the SharedElement transition between the items in the GridViewPagerImages
 * and the ImageDisplayFragment
 */
public class SharedElementTransition extends TransitionSet {

    public SharedElementTransition() {
        setOrdering(ORDERING_TOGETHER);
        addTransition(new ChangeBounds()).addTransition(new ChangeTransform()).addTransition(new ChangeImageTransform());
    }
}
