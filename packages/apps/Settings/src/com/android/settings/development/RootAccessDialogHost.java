package com.android.settings.development;

/**
 * Interface for RootAccessWarningDialogFragment callbacks.
 */
public interface RootAccessDialogHost {

    /**
     * Called when the user presses ok on the warning dialog.
     */
    void onRootAccessDialogConfirmed();

    /**
     * Called when the user dismisses or cancels the warning dialog.
     */
    void onRootAccessDialogDismissed();
}