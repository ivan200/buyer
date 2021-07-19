package app.simple.buyer.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import app.simple.buyer.R

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


fun Menu.tintAllIcons(@ColorInt color: Int) = ColorUtils.tintAllIcons(this, color)

object ColorUtils {
    fun tintDrawable(drawable: Drawable?, @ColorInt color: Int): Drawable? {
        return when {
            drawable == null -> null
            drawable is VectorDrawableCompat -> {
                drawable.apply { setTintList(ColorStateList.valueOf(color)) }
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && drawable is VectorDrawable -> {
                drawable.apply { setTintList(ColorStateList.valueOf(color)) }
            }
            else -> {
                DrawableCompat.wrap(drawable)
                    .also { DrawableCompat.setTint(it, color) }
                    .let { DrawableCompat.unwrap(it) }
            }
        }
    }

    /**
     * Перекрашивание цвета оверскролла на всех RecyclerView на api<21. Достаточно вызвать 1 раз в onCreate приложения
     */
    @SuppressLint("UseCompatLoadingForDrawables")
    fun changeOverScrollGlowColor(res: Resources, colorID: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            fun changeResColor(res: Resources, resId: String, colorID: Int) {
                try {
                    val drawableId = res.getIdentifier(resId, "drawable", "android")
                    val drawable = res.getDrawable(drawableId)
                    drawable.setColorFilter(res.getColor(colorID), PorterDuff.Mode.SRC_ATOP)
                } catch (ignored: Exception) {
                    //ignore exception
                }
            }

            changeResColor(res, "overscroll_glow", colorID)
            changeResColor(res, "overscroll_edge", colorID)
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

    fun tintAllIcons(menu: Menu, @ColorInt color: Int) {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            tintMenuItemIcon(item, color)
            tintShareIconIfPresent(color, item)
            if(item.hasSubMenu()){
                tintAllIcons(item.subMenu, color)
            }
        }
    }

    fun tintMenuItemIcon(item: MenuItem, @ColorInt color: Int) {
        item.icon = tintDrawable(item.icon, color)
    }

    private fun tintShareIconIfPresent(@ColorInt color: Int, item: MenuItem) {
        item.actionView
            ?.findViewById<View>(R.id.expand_activities_button)
            ?.findViewById<ImageView>(R.id.image)
            ?.apply {
                setImageDrawable(tintDrawable(drawable, color))
            }
    }
}