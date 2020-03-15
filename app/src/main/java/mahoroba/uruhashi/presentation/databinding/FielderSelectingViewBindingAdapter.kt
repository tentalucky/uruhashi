package mahoroba.uruhashi.presentation.databinding

import android.databinding.BindingAdapter
import android.databinding.BindingMethod
import android.databinding.BindingMethods
import mahoroba.uruhashi.domain.game.Position
import mahoroba.uruhashi.presentation.customView.FielderSelectingView

@BindingMethods(
    BindingMethod(
        type = FielderSelectingView::class,
        attribute = "android:onClick",
        method = "setOnFieldButtonClickedListener"
    )
)
object FielderSelectingViewBindingAdapter {
}