package app.simple.buyer.base

/**
 * Интерфейс для обработки нажатий кнопки назад во фрагментах
 *
 * @author ivan200
 * @since 22.02.2022
 */
interface OnBackPressedListener {
    /**
     * Кнопка назад была нажата
     * @return true если система должна обработать нажатие, false если не нужно обрабатывать
     */
    fun onBackPressed(): Boolean
}