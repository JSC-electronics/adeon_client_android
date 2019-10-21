package cz.jscelectronics.adeon.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.style.ImageSpan
import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.annotation.NonNull
import androidx.appcompat.content.res.AppCompatResources
import java.lang.ref.WeakReference

class CenteredImageSpan : ImageSpan {
    companion object {
        fun getInstance(context: Context, @DrawableRes resourceId: Int): CenteredImageSpan {
            // Add compatibility for Android 4.4
            return if (Build.VERSION.SDK_INT <= 19) {
                val drawable = AppCompatResources.getDrawable(
                    context, resourceId
                )?.apply {
                    setBounds(0, 0, this.intrinsicWidth, this.intrinsicHeight)
                }

                CenteredImageSpan(drawable!!)
            } else {
                CenteredImageSpan(context, resourceId)
            }
        }
    }

    private var mDrawableRef: WeakReference<Drawable>? = null

    // Extra variables used to redefine the Font Metrics when an ImageSpan is added
    private var initialDescent = 0
    private var extraSpace = 0

    private constructor(@NonNull drawable: Drawable, verticalAlignment: Int = ALIGN_BASELINE) :
            super(drawable, verticalAlignment)

    private constructor(
        context: Context, @DrawableRes resourceId: Int,
        verticalAlignment: Int = ALIGN_BASELINE
    ) : super(context, resourceId, verticalAlignment)

    override fun getSize(
        paint: Paint, text: CharSequence,
        @IntRange(from = 0) start: Int, @IntRange(from = 0) end: Int,
        fm: Paint.FontMetricsInt?
    ): Int {
        val d = getCachedDrawable()
        val rect = d?.bounds

        if (fm != null && rect != null) {
            // Centers the text with the ImageSpan
            if (rect.bottom - (fm.descent - fm.ascent) >= 0) {
                // Stores the initial descent and computes the margin available
                initialDescent = fm.descent
                extraSpace = rect.bottom - (fm.descent - fm.ascent)
            }

            fm.descent = extraSpace / 2 + initialDescent
            fm.bottom = fm.descent

            fm.ascent = -rect.bottom + fm.descent
            fm.top = fm.ascent
        }

        return rect?.right ?: 0
    }

    override fun draw(
        canvas: Canvas, text: CharSequence,
        @IntRange(from = 0) start: Int, @IntRange(from = 0) end: Int, x: Float,
        top: Int, y: Int, bottom: Int, paint: Paint
    ) {
        val b = getCachedDrawable()
        canvas.save()

        b?.let {
            var transY = bottom - it.bounds.bottom
            // this is the key
            transY -= paint.fontMetricsInt.descent / 2 - 8

            canvas.translate(x, transY.toFloat())
            it.draw(canvas)
            canvas.restore()
        }
    }

    // Redefined locally because it is a private member from DynamicDrawableSpan
    private fun getCachedDrawable(): Drawable? {
        val wr = mDrawableRef
        var d: Drawable? = null

        if (wr != null)
            d = wr.get()

        if (d == null) {
            d = drawable
            mDrawableRef = WeakReference(d)
        }

        return d
    }
}