package app.simple.buyer.fragments.additem

import app.simple.buyer.fragments.main.DrawerState

/**
 * Интерфейс для обработки изменения позиции правого дравера
 *
 * @author ivan200
 * @since 23.07.2021
 */
interface DrawerStateConsumer {
    fun onDrawerPositionChanged(pos: DrawerState)
}