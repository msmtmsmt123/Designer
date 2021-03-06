/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.lulebe.designer

import android.graphics.Rect
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.Toast

/**
 * Helper class for showing cheat sheets (tooltips) for icon-only UI elements on long-press. This is
 * already default platform behavior for icon-only [android.app.ActionBar] items and tabs.
 * This class provides this behavior for any other such UI element.

 *
 * Based on the original action bar implementation in [
   * ActionMenuItemView.java](https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/core/java/com/android/internal/view/menu/ActionMenuItemView.java).
 */
object CheatSheet {
    /**
     * The estimated height of a toast, in dips (density-independent pixels). This is used to
     * determine whether or not the toast should appear above or below the UI element.
     */
    private val ESTIMATED_TOAST_HEIGHT_DIPS = 48

    /**
     * Sets up a cheat sheet (tooltip) for the given view by setting its [ ]. When the view is long-pressed, a [Toast] with
     * the view's [content description][android.view.View.getContentDescription] will be
     * shown either above (default) or below the view (if there isn't room above it).

     * @param view The view to add a cheat sheet for.
     */
    fun setup(view: View) {
        view.setOnLongClickListener { view -> showCheatSheet(view, view.contentDescription) }
    }

    /**
     * Sets up a cheat sheet (tooltip) for the given view by setting its [ ]. When the view is long-pressed, a [Toast] with
     * the given text will be shown either above (default) or below the view (if there isn't room
     * above it).

     * @param view      The view to add a cheat sheet for.
     * *
     * @param textResId The string resource containing the text to show on long-press.
     */
    fun setup(view: View, textResId: Int) {
        view.setOnLongClickListener { view -> showCheatSheet(view, view.context.getString(textResId)) }
    }

    /**
     * Sets up a cheat sheet (tooltip) for the given view by setting its [ ]. When the view is long-pressed, a [Toast] with
     * the given text will be shown either above (default) or below the view (if there isn't room
     * above it).

     * @param view The view to add a cheat sheet for.
     * *
     * @param text The text to show on long-press.
     */
    fun setup(view: View, text: CharSequence) {
        view.setOnLongClickListener { view -> showCheatSheet(view, text) }
    }

    /**
     * Removes the cheat sheet for the given view by removing the view's [ ].

     * @param view The view whose cheat sheet should be removed.
     */
    fun remove(view: View) {
        view.setOnLongClickListener(null)
    }

    /**
     * Internal helper method to show the cheat sheet toast.
     */
    private fun showCheatSheet(view: View, text: CharSequence): Boolean {
        if (TextUtils.isEmpty(text)) {
            return false
        }

        val screenPos = IntArray(2) // origin is device display
        val displayFrame = Rect() // includes decorations (e.g. status bar)
        view.getLocationOnScreen(screenPos)
        view.getWindowVisibleDisplayFrame(displayFrame)

        val context = view.context
        val viewWidth = view.width
        val viewHeight = view.height
        val viewCenterX = screenPos[0] + viewWidth / 2
        val screenWidth = context.resources.displayMetrics.widthPixels
        val estimatedToastHeight = (ESTIMATED_TOAST_HEIGHT_DIPS * context.resources.displayMetrics.density).toInt()

        val cheatSheet = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        val showBelow = screenPos[1] < estimatedToastHeight
        if (showBelow) {
            // Show below
            // Offsets are after decorations (e.g. status bar) are factored in
            cheatSheet.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL,
                    viewCenterX - screenWidth / 2,
                    screenPos[1] - displayFrame.top + viewHeight)
        } else {
            // Show above
            // Offsets are after decorations (e.g. status bar) are factored in
            // NOTE: We can't use Gravity.BOTTOM because when the keyboard is up
            // its height isn't factored in.
            cheatSheet.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL,
                    viewCenterX - screenWidth / 2,
                    screenPos[1] - displayFrame.top - estimatedToastHeight)
        }

        cheatSheet.show()
        return true
    }
}