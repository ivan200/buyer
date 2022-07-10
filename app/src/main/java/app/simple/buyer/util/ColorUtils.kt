package app.simple.buyer.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.util.TypedValue
import android.widget.ImageView
import androidx.annotation.AnyRes
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.shape.MaterialShapeDrawable

@AnyRes
fun Context.getResCompat(@AttrRes id: Int) = ColorUtils.getAttrResCompat(this, id)
fun Context.getDrawableCompat(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)

fun ImageView.setImageResourceCompat(@AttrRes id: Int) {
    this.setImageResource(ColorUtils.getAttrResCompat(this.context, id))
}

@ColorInt
fun Context.getColorCompat(@ColorRes id: Int) = ContextCompat.getColor(this, id)

@ColorInt
fun Context.getColorResCompat(@AttrRes id: Int) = ColorUtils.getColorIdFromAttribute(this, id)

/**
 * Получение ресурса drawable, привязаного к теме через аттрибуты, например android.R.attr.selectableItemBackground
 */
fun Context.getDrawableResCompat(@AttrRes id: Int): Drawable? {
    return TypedValue()
        .also { this.theme.resolveAttribute(id, it, true) }
        .let { ContextCompat.getDrawable(this, if (it.resourceId != 0) it.resourceId else it.data) }
}


//fun Menu.tintAllIcons(@ColorInt color: Int) = ColorUtils.tintAllIcons(this, color)

object ColorUtils {
    fun Drawable.tinted(@ColorInt color: Int): Drawable = when {
        this is VectorDrawableCompat -> {
            this.apply { setTintList(ColorStateList.valueOf(color)) }
        }
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && this is VectorDrawable -> {
            this.apply { setTintList(ColorStateList.valueOf(color)) }
        }
        else -> {
            DrawableCompat.wrap(this)
                .also { DrawableCompat.setTint(it, color) }
                .let { DrawableCompat.unwrap(it) }
        }
    }

    fun getThemeAccentColor(context: Context): Int {
        val colorAttr: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            android.R.attr.colorAccent
        } else {
            //Get colorAccent defined for AppCompat
            context.resources.getIdentifier("colorAccent", "attr", context.packageName)
        }
        val outValue = TypedValue()
        context.theme.resolveAttribute(colorAttr, outValue, true)
        return outValue.data
    }

    /**
     * Перекрашивание цвета оверскролла на всех RecyclerView на api<21. Достаточно вызвать 1 раз в onCreate приложения
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    fun changeOverScrollGlowColor(res: Resources, @ColorInt color: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            @Suppress("DEPRECATION", "kotlin:S1874")

            fun changeResColor(res: Resources, resId: String, @ColorInt color: Int) {
                try {
                    val drawableId = res.getIdentifier(resId, "drawable", "android")
                    val drawable = res.getDrawable(drawableId)
                    drawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                } catch (ignored: Exception) {
                    //ignore exception
                }
            }
            changeResColor(res, "overscroll_glow", color)
            changeResColor(res, "overscroll_edge", color)
        }
    }

    /** Получение id ресурса, привязаного к теме через аттрибуты, например android.R.attr.selectableItemBackground */
    @AnyRes
    fun getAttrResCompat(context: Context, @AttrRes id: Int): Int {
        return TypedValue()
            .also { context.theme.resolveAttribute(id, it, true) }
            .let { if (it.resourceId != 0) it.resourceId else it.data }
    }

    /**
     * Перекрашивание цвета тени fab на api < 21
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    fun changeFabShadowColor(fab: FloatingActionButton, @ColorInt color: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fab.contentBackground
                .let { it as? LayerDrawable }
                ?.let { it.getDrawable(0) as? MaterialShapeDrawable }
                ?.setShadowColor(color)
        }
    }


    /**
     * Получение ресурса цвета, привязаного к теме через аттрибуты, например `android.R.attr.textColorPrimary`
     */
    @SuppressLint("ResourceAsColor")
    @ColorInt
    fun getColorIdFromAttribute(context: Context, @AttrRes colorAttr: Int): Int {
        val resolvedAttr = TypedValue()
        context.theme.resolveAttribute(colorAttr, resolvedAttr, true)
        val colorRes = resolvedAttr.run { if (resourceId != 0) resourceId else data }
        return ContextCompat.getColor(context, colorRes)
    }

//    fun tintAllIcons(menu: Menu, @ColorInt color: Int) {
//        for (i in 0 until menu.size()) {
//            val item = menu.getItem(i)
//            tintMenuItemIcon(item, color)
//            tintShareIconIfPresent(color, item)
//            if(item.hasSubMenu()){
//                tintAllIcons(item.subMenu, color)
//            }
//        }
//    }
//
//    fun tintMenuItemIcon(item: MenuItem, @ColorInt color: Int) {
//        item.icon = item.icon.tinted(color)
//    }
//
//    private fun tintShareIconIfPresent(@ColorInt color: Int, item: MenuItem) {
//        item.actionView
//            ?.findViewById<View>(R.id.expand_activities_button)
//            ?.findViewById<ImageView>(R.id.image)
//            ?.apply {
//                setImageDrawable(drawable.tinted(color))
//            }
//    }
}