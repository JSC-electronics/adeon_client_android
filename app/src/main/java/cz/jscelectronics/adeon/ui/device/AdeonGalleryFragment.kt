package cz.jscelectronics.adeon.ui.device

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import cz.jscelectronics.adeon.R
import cz.jscelectronics.adeon.adapters.GalleryAdapter
import cz.jscelectronics.adeon.databinding.FragmentAdeonGalleryBinding


class AdeonGalleryFragment : Fragment() {
    private lateinit var layout: CoordinatorLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentAdeonGalleryBinding.inflate(inflater, container, false).apply {
            val adapter = GalleryAdapter(getDeviceIcons())
            this.imageList.adapter = adapter
        }

        return binding.root
    }

    private fun getDeviceIcons(): List<Int> {
        val icons = ArrayList<Int>()

        val list = resources.obtainTypedArray(R.array.device_icons)
        for (i in 0 until list.length()) {
            icons.add(list.getResourceId(i, -1))
        }
        list.recycle()

        return icons
    }
}
