package app.simple.buyer.util.views

import android.app.Activity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.core.util.Consumer
import app.simple.buyer.R
import app.simple.buyer.util.database.Prefs
import com.google.android.material.textfield.TextInputEditText
import java.util.logging.Level
import java.util.logging.Logger

/**
 * Created by Zakharovi on 24.01.2018.
 */

class DialogHelper(val activity: Activity) : DialogHelperBase(activity) {

    fun showInputDialog(title: String,
                        hint: String,
                        positiveButton: String,
                        positiveResult: Consumer<String>) {
        val styleId = if(Prefs(activity).darkTheme) R.style.AppThemeDark_Translucent else R.style.AppThemeLight_Translucent
        val ctx = android.view.ContextThemeWrapper(activity, styleId)
        val subView = LayoutInflater.from(ctx).inflate(R.layout.dialog_add_list_content, null)
        val editText = subView.findViewById<TextInputEditText>(R.id.tiet_add_list)

        editText.hint = hint

        val builder = AlertDialog.Builder(ctx)
        builder.setTitle(title)
        builder.setView(subView)
        builder.setPositiveButton(positiveButton, null)
        val dialog = builder.create()


        editText.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                checkInputDialog(dialog, editText, positiveResult)
                return@setOnEditorActionListener true
            }
            false
        }

        dialog.setOnShowListener { dialog1 ->
            val button = (dialog1 as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener { view ->
                checkInputDialog(dialog, editText, positiveResult)
            }
        }
        val window = dialog.window
        if (window != null) {
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        }
        dialog.show()
    }

    private fun checkInputDialog(dialog: AlertDialog, editText: TextInputEditText, positiveResult: Consumer<String>) {
        try {
            positiveResult.accept(editText.text.toString())
            dialog.dismiss()
        } catch (e: Exception) {
            logger.log(Level.FINE, e.message, e)
            editText.error = e.message
        }
    }


    companion object {
        private val logger = Logger.getLogger(DialogHelper::class.java.name)
    }
}