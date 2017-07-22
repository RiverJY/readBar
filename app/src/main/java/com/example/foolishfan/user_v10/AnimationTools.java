package com.example.foolishfan.user_v10;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

/**
 * Created by 婧懿 on 2017/7/6.
 */

public class AnimationTools {
    public static void scale(View v) {
        ScaleAnimation anim = new ScaleAnimation(1.0f, 1.3f, 1.0f, 1.3f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        anim.setDuration(300);
        v.startAnimation(anim);

    }
}
