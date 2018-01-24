package app.simple.buyer.util.views

import android.content.Context
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import app.simple.buyer.R
import app.simple.buyer.util.crash.LogModule
import io.reactivex.functions.Consumer

/**
 * Created by Zakharovi on 24.01.2018.
 */

object DialogHelper {
    fun showInputDialog(
            context: Context,
            title: String,
            hint: String,
            positiveButton: String,
            positiveResult: Consumer<String>) {
        val ctx = android.view.ContextThemeWrapper(context, R.style.AppTheme_Dialog)
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

    fun checkInputDialog(dialog:AlertDialog, editText: TextInputEditText, positiveResult: Consumer<String>){
        try {
            positiveResult.accept(editText.text.toString())
            dialog.dismiss()
        } catch (e: Exception) {
            LogModule.printToLog(e)
            editText.error = e.message
        }
    }

}