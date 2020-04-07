package com.smartpack.smartflasher

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.Toast

/**
 * Extension FUNCTION to retrieve current AccentColor
 *
 * @return current accent color as [androidx.annotation.ColorInt]
 *
 * Usage (inside a "Context" context):
 * val accentColor = getThemeAccentColor()
 * paint.setColor(accentColor)
 */
fun Context.getThemeAccentColor(): Int {
    val value = TypedValue()
    theme.resolveAttribute(R.attr.colorAccent, value, true)
    return value.data
}

/**
 * Extension PROPERTY to retrieve current PrimaryColor
 *
 * @return current accent color as [androidx.annotation.ColorInt]
 *
 * Usage (inside a "Context" context):
 * paint.setColor(colorPrimary)
 */
val Context.colorPrimary : Int
    get() {
        val value = TypedValue()
        theme.resolveAttribute(R.attr.colorAccent, value, true)
        return value.data
    }

/**
 * Shows a toast. Defaults its length to [Toast.LENGTH_SHORT]
 *
 * @param messageRes a string resource for the message
 * @param length Toast length
 *
 * Usage (inside a "Context" context):
 * toast(R.string.short_toast)
 * toast(R.string.long_toast, Toast.LENGTH_LONG)
 */
fun Context?.toast(messageRes: Int, length: Int = Toast.LENGTH_SHORT) {
    if (this == null) return
    toast(getString(messageRes), length)
}

/**
 * Shows a toast. Defaults its length to [Toast.LENGTH_SHORT]
 *
 * @param message string for the message
 * @param length Toast length
 *
 * Usage (inside a "Context" context):
 * toast("Short toast")
 * toast("Long toast", Toast.LENGTH_LONG)
 */
fun Context?.toast(message: String?, length: Int = Toast.LENGTH_SHORT) {
    if (message == null || this == null) return

    // TODO: make sure we are in the main thread
    Toast.makeText(this, message, length).show()
}

/**
 * More examples with Visibility
 */
fun View.goAway() { this.visibility = View.GONE }
fun View.hide() { this.visibility = View.INVISIBLE }
fun View.show() { this.visibility = View.VISIBLE }