package com.example.habit.ui.model.Epoxy

import android.util.Log
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.habit.R
import com.example.habit.data.network.model.UiModels.HomePageModels.HomeElements
import com.example.habit.databinding.QuoteScrollerSectionLayoutBinding
import com.example.habit.ui.adapter.QuoteScrollerAdapter
import com.example.habit.ui.util.DpPxUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

class QuoteScrollerEpoxyModel(
    private val quoteSection: HomeElements.QuoteCarousalSection
):ViewBindingKotlinModel<QuoteScrollerSectionLayoutBinding>(R.layout.quote_scroller_section_layout) {
    override fun QuoteScrollerSectionLayoutBinding.bind() {
        Log.e("TAG", "bind: QuoteScrollerEpoxyModel", )
        imageScroller.layoutParams.apply {
            this as ConstraintLayout.LayoutParams
            height=DpPxUtils.dpToPX(quoteSection.imageHeight,imageScroller.context)
            setMargins(
                DpPxUtils.dpToPX(quoteSection.marginLeft,imageScroller.context),
                DpPxUtils.dpToPX(quoteSection.marginTop,imageScroller.context),
                DpPxUtils.dpToPX(quoteSection.marginRight,imageScroller.context),
                DpPxUtils.dpToPX(quoteSection.marginBottom,imageScroller.context),
            )
            imageScroller.setPadding(
                DpPxUtils.dpToPX(quoteSection.paddingLeft,imageScroller.context),
                DpPxUtils.dpToPX(quoteSection.paddingTop,imageScroller.context),
                DpPxUtils.dpToPX(quoteSection.paddingRight,imageScroller.context),
                DpPxUtils.dpToPX(quoteSection.paddingBottom,imageScroller.context),
            )
        }
        val quoteImagesAdapter = QuoteScrollerAdapter(quoteSection.images,quoteSection.marginLeft,quoteSection.imageCornerRadius)
        imageScroller.adapter=quoteImagesAdapter
        imageScroller.clipChildren=false
        imageScroller.clipToPadding=false
        imageScroller.offscreenPageLimit=3
        imageScroller.getChildAt(0).overScrollMode=RecyclerView.OVER_SCROLL_NEVER
        val pageTransformer = CompositePageTransformer()
        pageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY=0.85f+r*0.15f
        }
        imageScroller.setPageTransformer(pageTransformer)

    }
}