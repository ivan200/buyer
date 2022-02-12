package app.simple.buyer.fragments.editlists

import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import app.simple.buyer.R
import app.simple.buyer.databinding.DialogAddListContentBinding


/**
 * @author ivan200
 * @since 22.07.2021
 */
class AddListDialogFragment : DialogFragment() {

    private val model: AddListViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title = requireContext().getString(R.string.dialog_add_list_title)
        val hint = requireContext().getString(R.string.dialog_add_list_hint)
        val positiveButton = requireContext().getString(R.string.button_add)
        val negativeButton = requireContext().getString(R.string.button_cancel)
        val errorText = requireContext().getString(R.string.dialog_add_list_error)

        val styleId = if (model.isDarkThemeOn) R.style.AppThemeDark_Dialog else R.style.AppThemeLight_Dialog
        val binding = DialogAddListContentBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireContext(), styleId)
        builder.setTitle(title)
        builder.setView(binding.root)
        builder.setPositiveButton(positiveButton) { dialog, which ->
            model.onAddNewList()
            dismiss()
        }
        builder.setNegativeButton(negativeButton, null)
        val dialog = builder.create()

        val editText = binding.tietAddList
        val inputLayout = binding.tilAddList
        editText.hint = hint
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                //Обработка обрезания первых пробелов в списке
                val text = s.toString()
                val trimmedText = text.trimStart()
                val diff = text.length - trimmedText.length
                if (diff > 0) {
                    var selection = editText.selectionStart
                    selection -= diff
                    if (selection < 0) selection = 0
                    editText.setText(trimmedText)
                    editText.setSelection(selection)
                } else {
                    model.onInputTextChanged(text)
                }
            }
        })
        editText.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                model.onAddNewList()
                dismiss()
                return@setOnEditorActionListener true
            }
            false
        }
        dialog.setOnShowListener { dialog1 ->
            editText.requestFocus()
        }
        val window = dialog.window
        if (window != null) {
            dialog.window!!.setSoftInputMode(SOFT_INPUT_STATE_VISIBLE or SOFT_INPUT_ADJUST_RESIZE)
        }
        model.okEnabled.observe(this) {
            val buttonOk = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            buttonOk?.isEnabled = it
        }
        model.listAlreadyExist.observe(this) {
            inputLayout.error = if (it) errorText else null
        }
        return dialog
    }

    companion object {
        const val TAG = "AddListDialogFragment"
    }
}