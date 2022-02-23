package app.simple.buyer.util.views

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.appcompat.widget.AppCompatEditText

//EditText what allow catch show or hide keyboard actions
class PreImeEditText : AppCompatEditText {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle)

    var onPreImeKeyListener: ((keyCode: Int, event: KeyEvent) -> Unit)? = null

    override fun onKeyPreIme(keyCode: Int, event: KeyEvent): Boolean {
        val onKey = super.dispatchKeyEvent(event)
        onPreImeKeyListener?.invoke(keyCode, event)
        return onKey
    }
}