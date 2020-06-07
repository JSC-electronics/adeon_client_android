package cz.jscelectronics.adeon.adapters

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.signature.ObjectKey
import cz.jscelectronics.adeon.utilities.ADEON_ICON_VERSION_CODE

@BindingAdapter("isGone")
fun bindIsGone(view: View, isGone: Boolean) {
    view.visibility = if (isGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}

@BindingAdapter("imageUrl", "fallbackDrawable")
fun bindImageFromUrl(view: ImageView, imageUrl: Uri?, fallbackDrawable: Drawable?) {
    Glide.with(view.context)
        .load(imageUrl)
        .signature(ObjectKey(ADEON_ICON_VERSION_CODE))
        .fallback(fallbackDrawable)
        .dontAnimate()
        .into(view)
}
