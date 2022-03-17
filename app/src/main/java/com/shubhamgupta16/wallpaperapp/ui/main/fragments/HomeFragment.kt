package com.shubhamgupta16.wallpaperapp.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.shubhamgupta16.wallpaperapp.databinding.FragmentMainHomeBinding
import com.shubhamgupta16.wallpaperapp.ui.ListingActivity
import com.shubhamgupta16.wallpaperapp.ui.components.HorizontalCategoriesFragment
import com.shubhamgupta16.wallpaperapp.ui.components.HorizontalColorsFragment
import com.shubhamgupta16.wallpaperapp.ui.components.HorizontalWallpapersFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding:FragmentMainHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    var categoryClickListener:((categoryName:String)->Unit)?=null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newlyAddedHeader.setOnMoreClickListener {
            ListingActivity.open(requireContext(), "Newly Added")
        }

        childFragmentManager.beginTransaction()
            .replace(binding.colorsFragment.id, HorizontalColorsFragment().apply {
                colorClickListener = { colorName, colorValue ->
                    ListingActivity.open(
                        requireContext(),
                        colorName, color = colorName, colorValue = colorValue
                    )
                }
            }).commit()
        (childFragmentManager.findFragmentById(binding.categoriesFragment.id) as HorizontalCategoriesFragment).apply {
            categoryClickListener = { categoryName ->
                this@HomeFragment.categoryClickListener?.let { it(categoryName) }
            }
            fetch()
        }

        (childFragmentManager.findFragmentById(binding.latestWallpaperFragment.id) as HorizontalWallpapersFragment).fetch()
        (childFragmentManager.findFragmentById(binding.popularWallpaperFragment.id) as HorizontalWallpapersFragment).fetch()

    }
}