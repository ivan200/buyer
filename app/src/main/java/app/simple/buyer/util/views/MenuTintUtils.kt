package app.simple.buyer.util.views

import android.support.v4.graphics.drawable.DrawableCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import app.simple.buyer.R

/**
 * Created by Zakharovi on 30.01.2018.
 */

object MenuTintUtils {
    fun tintAllIcons(menu: Menu, color: Int) {
        for (i in 0 until menu.size()) {
            val item = menu.getItem(i)
            tintMenuItemIcon(color, item)
            tintShareIconIfPresent(color, item)
            if(item.hasSubMenu()){
                tintAllIcons(item.subMenu, color)
            }
        }
    }

    private fun tintMenuItemIcon(color: Int, item: MenuItem) {
        val drawable = item.icon
        if (drawable != null) {
            val wrapped = DrawableCompat.wrap(drawable)
            drawable.mutate()
            DrawableCompat.setTint(wrapped, color)
            item.icon = drawable
        }
    }

    private fun tintShareIconIfPresent(color: Int, item: MenuItem) {
        if (item.actionView != null) {
            val actionView = item.actionView
            val expandActivitiesButton = actionView.findViewById<View>(R.id.expand_activities_button)
            if (expandActivitiesButton != null) {
                val image = expandActivitiesButton.findViewById<ImageView>(R.id.image)
                if (image != null) {
                    val drawable = image.drawable
                    val wrapped = DrawableCompat.wrap(drawable)
                    drawable.mutate()
                    DrawableCompat.setTint(wrapped, color)
                    image.setImageDrawable(drawable)
                }
            }
        }
    }
}
