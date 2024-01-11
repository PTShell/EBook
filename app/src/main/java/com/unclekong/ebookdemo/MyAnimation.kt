package com.unclekong.ebookdemo

import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation

object MyAnimation {
    private const val ANIMATION_DURATION = 400

    //for the previous movement
    @JvmStatic
    fun inFromRightAnimation(): Animation {
        val inFromRight: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        )
        inFromRight.duration = ANIMATION_DURATION.toLong()
        inFromRight.interpolator = AccelerateInterpolator()
        return inFromRight
    }

    @JvmStatic
    fun outToLeftAnimation(): Animation {
        val outtoLeft: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        )
        outtoLeft.duration = ANIMATION_DURATION.toLong()
        outtoLeft.interpolator = AccelerateInterpolator()
        return outtoLeft
    }

    // for the next movement
    @JvmStatic
    fun inFromLeftAnimation(): Animation {
        val inFromLeft: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        )
        inFromLeft.duration = ANIMATION_DURATION.toLong()
        inFromLeft.interpolator = AccelerateInterpolator()
        return inFromLeft
    }

    @JvmStatic
    fun outToRightAnimation(): Animation {
        val outtoRight: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, +1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        )
        outtoRight.duration = ANIMATION_DURATION.toLong()
        outtoRight.interpolator = AccelerateInterpolator()
        return outtoRight
    }

    //for the previous movement
    fun inFromUpAnimation(): Animation {
        val inFromUp: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, +1.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        )
        inFromUp.duration = ANIMATION_DURATION.toLong()
        inFromUp.interpolator = AccelerateInterpolator()
        return inFromUp
    }

    fun outToDownAnimation(): Animation {
        val outtoDown: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f
        )
        outtoDown.duration = ANIMATION_DURATION.toLong()
        outtoDown.interpolator = AccelerateInterpolator()
        return outtoDown
    }

    // for the next movement
    fun inFromDownAnimation(): Animation {
        val inFromDown: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f
        )
        inFromDown.duration = ANIMATION_DURATION.toLong()
        inFromDown.interpolator = AccelerateInterpolator()
        return inFromDown
    }

    fun outToUpAnimation(): Animation {
        val outtoUp: Animation = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, +1.0f
        )
        outtoUp.duration = ANIMATION_DURATION.toLong()
        outtoUp.interpolator = AccelerateInterpolator()
        return outtoUp
    }
}