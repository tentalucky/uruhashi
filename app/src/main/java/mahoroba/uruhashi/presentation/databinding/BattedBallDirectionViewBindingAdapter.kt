package mahoroba.uruhashi.presentation.databinding

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import mahoroba.uruhashi.domain.game.BattedBallDirection
import mahoroba.uruhashi.presentation.customView.BattedBallDirectionView

object BattedBallDirectionViewBindingAdapter {

    @BindingAdapter("app:direction")
    @JvmStatic
    fun setDirection(view: BattedBallDirectionView, direction: BattedBallDirection?) {
        view.direction = direction
    }

    @InverseBindingAdapter(attribute = "app:direction")
    @JvmStatic
    fun getDirection(view: BattedBallDirectionView): BattedBallDirection? {
        return view.direction
    }

    @BindingAdapter("app:directionAttrChanged")
    @JvmStatic
    fun setDirectionChangedListener(view: BattedBallDirectionView, attrChange: InverseBindingListener) {
        view.addOnDirectionChangedListener { attrChange.onChange() }
    }
}