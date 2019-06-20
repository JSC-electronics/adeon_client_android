package cz.jscelectronics.adeon.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.style.ImageSpan;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

public class CenteredImageSpan extends ImageSpan {
    private WeakReference<Drawable> mDrawableRef;

    // Extra variables used to redefine the Font Metrics when an ImageSpan is added
    private int initialDescent = 0;
    private int extraSpace = 0;

    public CenteredImageSpan(Context context, final int drawableRes) {
        super(context, drawableRes);
    }

    public CenteredImageSpan(Drawable drawableRes, int verticalAlignment) {
        super(drawableRes, verticalAlignment);
    }

    public CenteredImageSpan(@NonNull Context context, @DrawableRes int resourceId,
                     int verticalAlignment) {
        super(context, resourceId, verticalAlignment);
    }

    @Override
    public int getSize(@NonNull Paint paint, CharSequence text,
                       int start, int end,
                       Paint.FontMetricsInt fm) {
        Drawable d = getCachedDrawable();
        Rect rect = d.getBounds();

//        if (fm != null) {
//            Paint.FontMetricsInt pfm = paint.getFontMetricsInt();
//            // keep it the same as paint's fm
//            fm.ascent = pfm.ascent;
//            fm.descent = pfm.descent;
//            fm.top = pfm.top;
//            fm.bottom = pfm.bottom;
//        }

        if (fm != null) {
            // Centers the text with the ImageSpan
            if (rect.bottom - (fm.descent - fm.ascent) >= 0) {
                // Stores the initial descent and computes the margin available
                initialDescent = fm.descent;
                extraSpace = rect.bottom - (fm.descent - fm.ascent);
            }

            fm.descent = extraSpace / 2 + initialDescent;
            fm.bottom = fm.descent;

            fm.ascent = -rect.bottom + fm.descent;
            fm.top = fm.ascent;
        }

        return rect.right;
    }

    @Override
    public void draw(@NonNull Canvas canvas, CharSequence text,
                     int start, int end, float x,
                     int top, int y, int bottom, @NonNull Paint paint) {
        Drawable b = getCachedDrawable();
        canvas.save();

//        int drawableHeight = b.getIntrinsicHeight();
//        int fontAscent = paint.getFontMetricsInt().ascent;
//        int fontDescent = paint.getFontMetricsInt().descent;
//        int transY = bottom - b.getBounds().bottom +  // align bottom to bottom
//                (drawableHeight - fontDescent + fontAscent) / 2;  // align center to center

        int transY = bottom - b.getBounds().bottom;
        // this is the key
        transY -= (paint.getFontMetricsInt().descent / 2 - 8);

//        int bCenter = b.getIntrinsicHeight() / 2;
//        int fontTop = paint.getFontMetricsInt().top;
//        int fontBottom = paint.getFontMetricsInt().bottom;
//        int transY = (bottom - b.getBounds().bottom) -
//                (((fontBottom - fontTop) / 2) - bCenter);


        canvas.translate(x, transY);
        b.draw(canvas);
        canvas.restore();
    }


    // Redefined locally because it is a private member from DynamicDrawableSpan
    private Drawable getCachedDrawable() {
        WeakReference<Drawable> wr = mDrawableRef;
        Drawable d = null;

        if (wr != null)
            d = wr.get();

        if (d == null) {
            d = getDrawable();
            mDrawableRef = new WeakReference<>(d);
        }

        return d;
    }
}
