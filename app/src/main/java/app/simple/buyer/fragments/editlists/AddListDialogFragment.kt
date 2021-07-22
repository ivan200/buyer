package app.simple.buyer.fragments.editlists

import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import app.simple.buyer.R
import app.simple.buyer.util.Utils
import com.google.android.material.textfield.TextInputEditText


/**
 * @author ivan200
 * @since 22.07.2021
 */
class AddListDialogFragment : DialogFragment() {

    private val model: AddListViewModel by viewModels()

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
////        val style = if (model.isDarkThemeOn) R.style.AppThemeDark_Translucent else R.style.AppThemeLight_Translucent
////        setStyle(STYLE_NORMAL, style)
//    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = requireContext().getString(R.string.dialog_add_list_title)
        val hint = requireContext().getString(R.string.dialog_add_list_hint)
        val positiveButton = requireContext().getString(R.string.button_add)

        val styleId = if (model.isDarkThemeOn) R.style.AppThemeDark_Dialog else R.style.AppThemeLight_Dialog
        val subView = layoutInflater.inflate(R.layout.dialog_add_list_content, null)

        val builder = AlertDialog.Builder(requireContext(), styleId)
        builder.setTitle(title)
        builder.setView(subView)
        builder.setPositiveButton(positiveButton, null)
        val dialog = builder.create()

        val editText = subView.findViewById<TextInputEditText>(R.id.tiet_add_list)
        editText.hint = hint
        editText.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
//                checkInputDialog(dialog, editText, positiveResult)
                return@setOnEditorActionListener true
            }
            false
        }

        dialog.setOnShowListener { dialog1 ->
            editText.requestFocus()

//            Utils.showKeyBoard2(editText)
//            val button = (dialog1 as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
//            button.setOnClickListener { view ->
////                checkInputDialog(dialog, editText, positiveResult)
//            }
        }
        val window = dialog.window
        if (window != null) {
            dialog.window!!.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        }
        return dialog
    }

    //    private fun checkInputDialog(dialog: AlertDialog, editText: TextInputEditText, positiveResult: Consumer<String>) {
//        try {
//            positiveResult.accept(editText.text.toString())
//            dialog.dismiss()
//        } catch (e: Exception) {
//            logger.log(Level.FINE, e.message, e)
//            editText.error = e.message
//        }
//    }
    companion object {
        const val TAG = "AddListDialogFragment"
    }

}