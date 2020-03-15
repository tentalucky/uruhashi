package mahoroba.uruhashi.presentation.databinding

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import mahoroba.uruhashi.domain.game.FoulBallDirection
import mahoroba.uruhashi.presentation.customView.FoulBallDirectionSelectingView

object FoulBallDirectionSelectingViewBindingAdapter {

    @BindingAdapter("app:selected_value")
    @JvmStatic
    fun setSelectedValue(view: FoulBallDirectionSelectingView, value: FoulBallDirection?) {
        view.selectedValue = value
    }

    @InverseBindingAdapter(attribute = "app:selected_value")
    @JvmStatic
    fun getSelectedValue(view: FoulBallDirectionSelectingView): FoulBallDirection? {
        return view.selectedValue
    }

    @BindingAdapter("app:selected_valueAttrChanged")
    @JvmStatic
    fun setSelectedValueChangedListener(view: FoulBallDirectionSelectingView, attrChange: InverseBindingListener) {
        view.addOnSelectedValueChangedListener { attrChange.onChange() }
    }

    @BindingAdapter("app:isAtLine")
    @JvmStatic
    fun setIsAtLine(view: FoulBallDirectionSelectingView, value: Boolean) {
        view.isAtLine = value
    }

    @InverseBindingAdapter(attribute = "app:isAtLine")
    @JvmStatic
    fun geIsAtLine(view: FoulBallDirectionSelectingView): Boolean {
        return view.isAtLine
    }

    @BindingAdapter("app:isAtLineAttrChanged")
    @JvmStatic
    fun setIsAtLineChangedListener(view: FoulBallDirectionSelectingView, attrChange: InverseBindingListener) {
        view.addIsAtLineChangedListener { attrChange.onChange() }
    }
}