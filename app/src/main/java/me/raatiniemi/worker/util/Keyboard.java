package me.raatiniemi.worker.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

public class Keyboard {
    /**
     * Tag for logging.
     */
    private static final String TAG = "Keyboard";

    /**
     * Store the InputMethodManager.
     */
    private static InputMethodManager mInputMethodManager;

    /**
     * Retrieve the InputMethodManager.
     *
     * @param context Context used to retrieve the InputMethodManager.
     * @return InputMethodManager if we're able to retrieve it, otherwise null.
     */
    @Nullable
    private static InputMethodManager getInputMethodManager(@NonNull Context context) {
        // If we don't have the input method manager available,
        // we have to retrieve it from the context.
        if (null == mInputMethodManager) {
            try {
                mInputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            } catch (ClassCastException e) {
                Log.w(
                    TAG,
                    "Unable to cast the Context.INPUT_METHOD_SERVICE to " +
                        "InputMethodManager: " + e.getMessage()
                );
            }
        }

        return mInputMethodManager;
    }

    /**
     * Forcing the keyboard to show.
     *
     * @param context Context used when showing the keyboard.
     */
    public static void show(@NonNull Context context) {
        // Check that we have the input method manager available.
        InputMethodManager manager = getInputMethodManager(context);
        if (null == manager) {
            Log.w(TAG, "Unable to retrieve the InputMethodManager");
            return;
        }

        manager.toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
}
