package app.simple.buyer.util.views;

import android.view.View;

import rx.functions.Func1;

/**
 * Created by Zakharovi on 12.01.2018.
 */

public class MultiCellObject <T>{
    private T object;
    private int resourceId;
    private Func1<View, BindHolder<T>> onCreateHolder;

    public MultiCellObject(T object, Func1<View, BindHolder<T>> onCreateHolder, int resourceId) {
        this.object = object;
        this.resourceId = resourceId;
        this.onCreateHolder = onCreateHolder;
    }

    public T getObject() {
        return object;
    }

    public int getResource() {
        return resourceId;
    }

    public Func1<View, BindHolder<T>> getOnCreateHolder() {
        return onCreateHolder;
    }
}

