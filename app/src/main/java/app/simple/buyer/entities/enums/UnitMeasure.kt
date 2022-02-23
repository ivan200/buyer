package app.simple.buyer.entities.enums

import androidx.annotation.StringRes
import app.simple.buyer.R

/**
 * Единица измерения продукта
 *
 * @param value Номер для сохранения в бд
 * @param stringRes Строковый ресурс для получиения текста
 *
 * @author ivan200
 * @since 22.02.2022
 */
enum class UnitMeasure(val value: Int, @StringRes val stringRes: Int) {
    /**
     * Штуки
     */
    PIECE(0, R.string.measure_piece),
    /**
     * Килограмы
     */
    KILOGRAM(1, R.string.measure_kilogram),
    /**
     * Граммы
     */
    GRAM(2, R.string.measure_gram),
    /**
     * Литры
     */
    LITER(3, R.string.measure_liter);

    companion object {
        fun getByValue(value: Int): UnitMeasure {
            return UnitMeasure.values().first { it.value == value }
        }
    }
}