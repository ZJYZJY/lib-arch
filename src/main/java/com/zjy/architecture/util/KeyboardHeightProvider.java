package com.zjy.architecture.util;

import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager.LayoutParams;
import android.widget.PopupWindow;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

import com.zjy.architecture.R;

/**
 * The keyboard height provider, this class uses a PopupWindow
 * to calculate the window height when the floating keyboard is opened and closed.
 */
public class KeyboardHeightProvider extends PopupWindow implements LifecycleObserver {

    /**
     * The tag for logging purposes
     */
    private final static String TAG = "KeyboardHeightProvider";

    /**
     * The keyboard height observer
     */
    private KeyboardHeightObserver observer;

    /**
     * The view that is used to calculate the keyboard height
     */
    private final View popupView;

    /**
     * The parent view
     */
    private final View parentView;

    /**
     * The root activity that uses this KeyboardHeightProvider
     */
    private final FragmentActivity activity;

    private final OnGlobalLayoutListener onGlobalLayoutListener = new OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            if (popupView != null) {
                handleOnGlobalLayout();
            }
        }
    };

    /**
     * Construct a new KeyboardHeightProvider
     *
     * @param activity The parent activity
     */
    public KeyboardHeightProvider(FragmentActivity activity) {
        super(activity);
        this.activity = activity;
        activity.getLifecycle().addObserver(this);

        this.popupView = LayoutInflater.from(activity).inflate(R.layout.popupwindow, null, false);
        setContentView(popupView);

        setSoftInputMode(LayoutParams.SOFT_INPUT_ADJUST_RESIZE | LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

        parentView = activity.findViewById(android.R.id.content);

        setWidth(0);
        setHeight(LayoutParams.MATCH_PARENT);

    }

    /**
     * Start the KeyboardHeightProvider, this must be called after the onResume of the Activity.
     * PopupWindows are not allowed to be registered before the onResume has finished
     * of the Activity.
     */
    public void start() {
        if (activity.getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)
                && !isShowing()
                && parentView.getWindowToken() != null) {
            setBackgroundDrawable(new ColorDrawable(0));
            showAtLocation(parentView, Gravity.NO_GRAVITY, 0, 0);
        }
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_RESUME)
    public void resume() {
        popupView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_PAUSE)
    public void pause() {
        popupView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    /**
     * Close the keyboard height provider,
     * this provider will not be used anymore.
     */
    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    public void close() {
        dismiss();
        observer = null;
    }

    /**
     * Set the keyboard height observer to this provider. The
     * observer will be notified when the keyboard height has changed.
     * For example when the keyboard is opened or closed.
     *
     * @param observer The observer to be added to this provider.
     */
    public void setKeyboardHeightObserver(KeyboardHeightObserver observer) {
        this.observer = observer;
    }

    /**
     * Popup window itself is as big as the window of the Activity.
     * The keyboard can then be calculated by extracting the popup view bottom
     * from the activity window height.
     */
    private void handleOnGlobalLayout() {

        Point screenSize = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(screenSize);

        Rect rect = new Rect();
        popupView.getWindowVisibleDisplayFrame(rect);

        // REMIND, you may like to change this using the fullscreen size of the phone
        // and also using the status bar and navigation bar heights of the phone to calculate
        // the keyboard height. But this worked fine on a Nexus.
        int orientation = getScreenOrientation();
        int keyboardHeight = screenSize.y - rect.bottom;

        if (keyboardHeight == 0) {
            notifyKeyboardHeightChanged(0, orientation);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            /*
             * The cached portrait height of the keyboard
             */
            notifyKeyboardHeightChanged(keyboardHeight, orientation);
        } else {
            /*
             * The cached landscape height of the keyboard
             */
            notifyKeyboardHeightChanged(keyboardHeight, orientation);
        }
    }

    private int getScreenOrientation() {
        return activity.getResources().getConfiguration().orientation;
    }

    /**
     * 有些手机键盘没弹出时高度是负数，因此键盘高度需要算上负数部分
     */
    private int baseHeight = 0;

    private void notifyKeyboardHeightChanged(int height, int orientation) {
        if (height <= 0) {
            baseHeight = height;
        }
        if (observer != null && height > 0) {
            observer.onKeyboardHeightChanged(height - baseHeight, orientation);
        }
    }
}