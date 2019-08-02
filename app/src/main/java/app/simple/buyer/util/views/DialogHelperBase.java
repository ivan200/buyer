package app.simple.buyer.util.views;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

@SuppressWarnings("unused")
public abstract class DialogHelperBase {
    public interface ChooseItemEvent {
        void onChoose(int index);
    }

    public interface ResultEvent {
        void call();
    }

    private CharSequence title;
    private CharSequence message;
    private CharSequence positiveButton;
    private CharSequence negativeButton;
    private CharSequence neutralButton;

    private ResultEvent positiveResult;
    private ResultEvent negativeResult;
    private ResultEvent neutralResult;
    private ResultEvent cancelResult;
    private Throwable throwable;
    private Boolean errorIcon = null;
    private boolean cancellable = true;

    private String[] items;
    private int checkedItem = -1;
    private ChooseItemEvent itemChosen;

    public DialogHelperBase(@NonNull Context context) {
        this.context = context;
    }
    public DialogHelperBase withTitle(CharSequence title)                   { this.title = title;                                           return this; }
    public DialogHelperBase withMessage(CharSequence message)               { this.message = message;                                       return this; }
    public DialogHelperBase withPositiveButton(CharSequence positiveButton) { this.positiveButton = positiveButton;                         return this; }
    public DialogHelperBase withNegativeButton(CharSequence negativeButton) { this.negativeButton = negativeButton;                         return this; }
    public DialogHelperBase withNeutralButton(CharSequence neutralButton)   { this.neutralButton = neutralButton;                           return this; }
    public DialogHelperBase withTitle(Integer titleId)                      { this.title = context.getString(titleId);                      return this; }
    public DialogHelperBase withMessage(Integer messageId)                  { this.message = context.getString(messageId);                  return this; }
    public DialogHelperBase withPositiveButton(Integer positiveButtonId)    { this.positiveButton = context.getString(positiveButtonId);    return this; }
    public DialogHelperBase withNegativeButton(Integer negativeButtonId)    { this.negativeButton = context.getString(negativeButtonId);    return this; }
    public DialogHelperBase withNeutralButton(Integer neutralButtonId)      { this.neutralButton = context.getString(neutralButtonId);      return this; }
    public DialogHelperBase withPositiveResult(ResultEvent positiveResult)  { this.positiveResult = positiveResult;                         return this; }
    public DialogHelperBase withNegativeResult(ResultEvent negativeResult)  { this.negativeResult = negativeResult;                         return this; }
    public DialogHelperBase withNeutralResult(ResultEvent neutralResult)    { this.neutralResult = neutralResult;                           return this; }
    public DialogHelperBase withCancelResult(ResultEvent cancelResult)      { this.cancelResult = cancelResult;                             return this; }
    public DialogHelperBase withThrowable(Throwable throwable)              { this.throwable = throwable;                                   return this; }
    public DialogHelperBase withCancellable(boolean cancellable)            { this.cancellable = cancellable;                               return this; }
    public DialogHelperBase withErrorIcon()                                 { this.errorIcon = true;                                        return this; }
    public DialogHelperBase withoutErrorIcon()                              { this.errorIcon = false;                                       return this; }
    public DialogHelperBase withItems(String[] items)                       { this.items = items;                                           return this; }
    public DialogHelperBase withItemChecked(int checkedItem)                { this.checkedItem = checkedItem;                               return this; }

    public DialogHelperBase withItemResult(ChooseItemEvent itemChosen)      { this.itemChosen = itemChosen;                                 return this; }

    private Context context;
    public DialogHelperBase withYesNoButtons()      {
        return this
                .withPositiveButton(android.R.string.yes)
                .withNegativeButton(android.R.string.no);
    }

    public DialogHelperBase withCancelButton()      {
        return this
                .withNegativeButton(android.R.string.cancel);
    }


    public void show(){
        if(throwable!= null) {
            throwable.printStackTrace();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        CharSequence curTitle = title;
        if(TextUtils.isEmpty(curTitle)){
            curTitle = context.getString(android.R.string.dialog_alert_title);
        }

        if (!TextUtils.isEmpty(curTitle)) {
            builder.setTitle(curTitle);
        }

        if ((errorIcon == null && throwable != null) || (errorIcon != null && errorIcon)) {
            int errorIconId = getErrorIconId(builder.getContext());
            if (errorIconId == 0) {
                errorIconId = android.R.drawable.stat_notify_error;
            }
            builder.setIcon(errorIconId);
        }

        String msg = message != null ? message.toString() : null;
        String currMessage = throwable != null ? getExceptionText(context, throwable, msg) : msg;
        currMessage = currMessage != null ? currMessage.replaceAll("\\\\n", "\n") : null;
        if (items != null){
            builder.setSingleChoiceItems(items, checkedItem, (dialog, which) -> {
                if (itemChosen != null) {
                    itemChosen.onChoose(which);
                }
                if(dialog!= null){
                    dialog.dismiss();
                }
            });
        } else {
            if (!TextUtils.isEmpty(currMessage)) {
                builder.setMessage(currMessage);
            }

            CharSequence positiveButtonText = TextUtils.isEmpty(positiveButton) ? context.getString(android.R.string.ok) : positiveButton;
            if (!TextUtils.isEmpty(positiveButtonText)) {
                builder.setPositiveButton(positiveButtonText, (dialog, id) -> {
                    if (positiveResult != null) {
                        positiveResult.call();
                        dialog.dismiss();
                    }
                });
            }

            if (!TextUtils.isEmpty(negativeButton)) {
                builder.setNegativeButton(negativeButton, (dialog, id) -> {
                    if (negativeResult != null) {
                        negativeResult.call();
                    }
                    dialog.dismiss();
                });
            }

            if (!TextUtils.isEmpty(neutralButton)) {
                builder.setNeutralButton(neutralButton, (dialog, id) -> {
                    if (neutralResult != null) {
                        neutralResult.call();
                    }
                    dialog.dismiss();
                });
            } else {
                if (needShowReportButton(context, throwable, msg)) {
                    builder.setNeutralButton(getReportButtonText(), (dialog, id) -> {
                        handleSendReportClick(throwable);
                        dialog.dismiss();
                    });
                }
            }
        }
        if (cancellable) {
            //по дефолту, если cancelResult не установлен, то при отмене диалога срабатывает negativeResult, если он есть
            if (cancelResult == null && negativeResult != null) {
                cancelResult = negativeResult;
            }
            if (cancelResult != null) {
                builder.setOnCancelListener(dialog -> {
                    cancelResult.call();
                    dialog.dismiss();
                });
                builder.setCancelable(true);
            }
        } else {
            builder.setCancelable(false);
        }
        AlertDialog dialog = builder.create();

        if(cancellable) {
            if (cancelResult != null) {
                dialog.setOnCancelListener(d -> {
                    cancelResult.call();
                    d.dismiss();
                });
            }
            dialog.setOnKeyListener((arg0, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (cancelResult != null) {
                        cancelResult.call();
                    }
                    arg0.dismiss();
                }
                return true;
            });
        } else {
            dialog.setCancelable(false);
        }
        dialog.show();

        if(!TextUtils.isEmpty(currMessage) && items == null) {
            fixDialogViewMinHeight(dialog);
        }
    }

    private static void fixDialogViewMinHeight(AlertDialog dialog){
        Window window = dialog.getWindow();
        if(window == null) return;
        View viewById = window.findViewById(android.R.id.message);

        setMinHeightForParents(viewById, 4,0);
    }

    private static void setMinHeightForParents(View v, int maxLevel, int level){
        if(v == null) return;
        v.setMinimumHeight(0);
        if(level >= maxLevel){
            return;
        }
        ViewParent parent = v.getParent();
        if (parent instanceof View) {
            setMinHeightForParents((View) parent, maxLevel, ++level);
        }
    }



    protected String getReportButtonText(){
        return "Report";
    }

    protected String getExceptionText(@NonNull Context context, Throwable ex, String emptyText) {
        return ex != null ? ex.getMessage() : "";
    }

    protected boolean needShowReportButton(@NonNull Context context, Throwable ex, String emptyText) {
        return false;
    }

    protected int getErrorIconId(Context context){
        TypedValue typedValueAttr = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.alertDialogIcon, typedValueAttr, true);
        return typedValueAttr.resourceId;
//        return android.R.drawable.ic_dialog_alert;
    }

    protected void handleSendReportClick(Throwable throwable){
    }
}
