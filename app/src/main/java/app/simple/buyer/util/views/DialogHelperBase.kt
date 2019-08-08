package app.simple.buyer.util.views

import android.content.Context
import android.text.TextUtils
import android.util.TypedValue
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AlertDialog

abstract class DialogHelperBase(private val context: Context) {

    private var title: CharSequence? = null
    private var message: CharSequence? = null
    private var positiveButton: CharSequence? = null
    private var negativeButton: CharSequence? = null
    private var neutralButton: CharSequence? = null

    private var positiveResult: Function0<Unit>? = null
    private var negativeResult: Function0<Unit>? = null
    private var neutralResult: Function0<Unit>? = null
    private var cancelResult: Function0<Unit>? = null
    private var throwable: Throwable? = null
    private var errorIcon: Boolean? = null
    private var cancellable: Boolean? = null

    private var items: Array<String>? = null
    private var checkedItem = -1
    private var itemChosen: Function1<Int, Unit>? = null
    private var checkedItems: BooleanArray? = null

    fun withTitle(title: CharSequence)                      = apply { this.title = title }
    fun withMessage(message: CharSequence)                  = apply { this.message = message;                                     }
    fun withPositiveButton(positiveButton: CharSequence)    = apply { this.positiveButton = positiveButton;                       }
    fun withNegativeButton(negativeButton: CharSequence)    = apply { this.negativeButton = negativeButton;                       }
    fun withNeutralButton(neutralButton: CharSequence)      = apply { this.neutralButton = neutralButton;                         }
    fun withTitle(titleId: Int)                             = apply { this.title = context.getString(titleId);                    }
    fun withMessage(messageId: Int)                         = apply { this.message = context.getString(messageId);                }
    fun withPositiveButton(positiveButtonId: Int)           = apply { this.positiveButton = context.getString(positiveButtonId);  }
    fun withNegativeButton(negativeButtonId: Int)           = apply { this.negativeButton = context.getString(negativeButtonId);  }
    fun withNeutralButton(neutralButtonId: Int)             = apply { this.neutralButton = context.getString(neutralButtonId);    }
    fun withPositiveResult(positiveResult: Function0<Unit>) = apply { this.positiveResult = positiveResult;                       }
    fun withNegativeResult(negativeResult: Function0<Unit>) = apply { this.negativeResult = negativeResult;                       }
    fun withNeutralResult(neutralResult: Function0<Unit>)   = apply { this.neutralResult = neutralResult;                         }
    fun withCancelResult(cancelResult: Function0<Unit>)     = apply { this.cancelResult = cancelResult;                           }
    fun withThrowable(throwable: Throwable)                 = apply { this.throwable = throwable;                                 }
    fun withCancellable(cancellable: Boolean)               = apply { this.cancellable = cancellable;                             }
    fun withErrorIcon()                                     = apply { this.errorIcon = true;                                      }
    fun withoutErrorIcon()                                  = apply { this.errorIcon = false;                                     }
    fun withItems(items: Array<String>)                     = apply { this.items = items;                                         }
    fun withItemsChecked(checkedItems : BooleanArray)       = apply { this.checkedItems = checkedItems;                           }
    fun withItemChecked(checkedItem: Int)                   = apply { this.checkedItem = checkedItem;                             }
    fun withItemResult(itemChosen: Function1<Int, Unit>?)   = apply { this.itemChosen = itemChosen;                               }


    fun show() {
        throwable?.printStackTrace()

        val builder = AlertDialog.Builder(context)

        var curTitle = title
        if (TextUtils.isEmpty(curTitle)) {
            curTitle = context.getString(android.R.string.dialog_alert_title)
        }

        if (!TextUtils.isEmpty(curTitle)) {
            builder.setTitle(curTitle)
        }

        if (errorIcon == null && throwable != null || errorIcon != null && errorIcon!!) {
            var errorIconId = getErrorIconId(context)
            if (errorIconId == 0) {
                errorIconId = android.R.drawable.stat_notify_error
            }
            builder.setIcon(errorIconId)
        }

        val msg = if (message != null) message.toString() else null
        var currMessage: String? = if (throwable != null) getExceptionText(context, throwable, msg) else msg
        currMessage = currMessage?.replace("\\\\n".toRegex(), "\n")
        if (items != null) {
            if (checkedItems != null) {
                builder.setMultiChoiceItems(items, checkedItems) { dialog, which, isChecked -> checkedItems!![which] = isChecked }
            } else {
                builder.setSingleChoiceItems(items, checkedItem) { dialog, which ->
                    itemChosen?.invoke(which)
                    dialog?.dismiss()
                }
            }
        } else {
            if (!TextUtils.isEmpty(currMessage)) {
                builder.setMessage(currMessage)
            }
        }

        val positiveButtonText = if (items == null && TextUtils.isEmpty(positiveButton))
            context.getString(android.R.string.ok)
        else
            positiveButton
        if (!TextUtils.isEmpty(positiveButtonText)) {
            builder.setPositiveButton(positiveButtonText) { dialog, id ->
                positiveResult?.invoke()
                dialog.dismiss()
            }
        }

        if (!TextUtils.isEmpty(negativeButton)) {
            builder.setNegativeButton(negativeButton) { dialog, id ->
                negativeResult?.invoke()
                dialog.dismiss()
            }
        }

        if (!TextUtils.isEmpty(neutralButton)) {
            builder.setNeutralButton(neutralButton) { dialog, id ->
                neutralResult?.invoke()
                dialog.dismiss()
            }
        } else {
            if (needShowReportButton(context, throwable, msg)) {
                builder.setNeutralButton(reportButtonText) { dialog, id ->
                    handleSendReportClick(throwable)
                    dialog.dismiss()
                }
            }
        }

        if (cancellable == null) {
            //по дефолту, если cancelResult не установлен, то при отмене диалога срабатывает negativeResult, если он есть
            if (cancelResult == null && negativeResult != null) {
                cancelResult = negativeResult
            }
        }

        if (cancellable == false) {
            builder.setCancelable(false)
        } else {
            if (cancelResult != null) {
                builder.setOnCancelListener { dialog ->
                    cancelResult?.invoke()
                    dialog.dismiss()
                }
                builder.setCancelable(true)
            }
        }

        val dialog = builder.create()
        if (cancellable == false) {
            dialog.setCancelable(false)
        } else {
            if (cancelResult != null) {
                dialog.setOnCancelListener { d ->
                    cancelResult?.invoke()
                    d.dismiss()
                }
                dialog.setOnKeyListener { arg0, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        cancelResult?.invoke()
                        arg0.dismiss()
                    }
                    true
                }
            }
        }
        dialog.show()

        if (!TextUtils.isEmpty(currMessage) && items == null) {
            fixDialogViewMinHeight(dialog)
        }
    }

    private fun fixDialogViewMinHeight(dialog: AlertDialog) {
        val window = dialog.window ?: return
        val viewById = window.findViewById<View>(android.R.id.message)

        setMinHeightForParents(viewById, 4, 0)
    }

    private fun setMinHeightForParents(v: View?, maxLevel: Int, level: Int) {
        var subLevel = level
        if (v == null) return
        v.minimumHeight = 0
        if (subLevel >= maxLevel) {
            return
        }
        val parent = v.parent
        if (parent is View) {
            setMinHeightForParents(parent as View, maxLevel, ++subLevel)
        }
    }

    protected fun getExceptionText(context: Context, ex: Throwable?, emptyText: String?): String {
        return ex?.message ?: ""
    }

    protected val reportButtonText: String
        get() = "Report"

    protected fun needShowReportButton(context: Context, ex: Throwable?, emptyText: String?): Boolean {
        return false
    }

    protected fun getErrorIconId(context: Context): Int {
        val typedValueAttr = TypedValue()
        context.theme.resolveAttribute(android.R.attr.alertDialogIcon, typedValueAttr, true)
        return typedValueAttr.resourceId
        //        return android.R.drawable.ic_dialog_alert;
    }

    protected fun handleSendReportClick(throwable: Throwable?) {}
}
