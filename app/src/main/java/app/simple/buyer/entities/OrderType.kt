package app.simple.buyer.entities

/**
 * Created by Zakharovi on 31.01.2018.
 */
interface OrderType {
    companion object {
        const val HAND = 0
        const val ALPHABET = 1
        const val POPULARITY = 2
        const val SIZE = 3
        const val CREATED = 4
        const val MODIFIED = 5
        const val PRICE = 6
        const val CATEGORY = 7
    }
}