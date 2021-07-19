package app.simple.buyer.entities

/**
 * Created by Zakharovi on 31.01.2018.
 */
interface OrderType {
    companion object {
        /** Ручная сортировка **/
        const val NOT_SET = -1

        /** Ручная сортировка **/
        const val HAND = 0

        /** Сортировка по алфавиту **/
        const val ALPHABET = 1

        /** Сортировка по популярности **/
        const val POPULARITY = 2

        /** Сортировка по размеру **/
        const val SIZE = 3

        /** Сортировка по дате создания **/
        const val CREATED = 4

        /** Сортировка по дате изменения **/
        const val MODIFIED = 5

        /** Сортировка по суммарной цене **/
        const val PRICE = 6

        /** Сортировка по категории **/
        const val CATEGORY = 7
    }
}