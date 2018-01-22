package app.simple.buyer.util.views

import android.view.View

import rx.functions.Func1

/**
 * Created by Zakharovi on 12.01.2018.
 */

class MultiCellObject<T>(val obj: T, val onCreateHolder: Func1<View, BindHolder<T>>, val resource: Int)

