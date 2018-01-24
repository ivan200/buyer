package app.simple.buyer.util.views

import android.view.View
import io.reactivex.functions.Function

/**
 * Created by Zakharovi on 12.01.2018.
 */

class MultiCellObject<T>(val obj: T, val onCreateHolder: Function<View, BindHolder<T>>, val resource: Int)

