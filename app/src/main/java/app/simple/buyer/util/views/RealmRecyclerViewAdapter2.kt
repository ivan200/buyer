package app.simple.buyer.util.views

import androidx.recyclerview.widget.RecyclerView
import io.realm.OrderedCollectionChangeSet
import io.realm.OrderedRealmCollection
import io.realm.OrderedRealmCollectionChangeListener
import io.realm.RealmList
import io.realm.RealmModel
import io.realm.RealmResults

/**
 * The RealmBaseRecyclerAdapter class is an abstract utility class for binding RecyclerView UI elements to Realm data.
 *
 * This adapter will automatically handle any updates to its data and call `notifyDataSetChanged()`,
 * `notifyItemInserted()`, `notifyItemRemoved()` or `notifyItemRangeChanged()` as appropriate.
 *
 * The RealmAdapter will stop receiving updates if the Realm instance providing the [OrderedRealmCollection] is
 * closed.
 *
 * If the adapter contains Realm model classes with a primary key that is either an `int` or a `long`, call
 * `setHasStableIds(true)` in the constructor and override [getItemId] as described by the Javadoc in that method.
 *
 * @param [T] type of [RealmModel] stored in the adapter.
 * @param [S] type of [RecyclerView.ViewHolder] used in the adapter.
 * @see RecyclerView.Adapter.setHasStableIds
 * @see RecyclerView.Adapter.getItemId
 *
 * @param data collection data to be used by this adapter.
 * @param hasAutoUpdates when it is `false`, the adapter won't be automatically updated when collection data changes.
 * @param updateOnModification when it is `true`, this adapter will be updated when deletions, insertions or
 * modifications happen to the collection data. When it is `false`, only
 * deletions and insertions will trigger the updates. This param will be ignored if
 * `autoUpdate` is `false`.
 */

//https://github.com/realm/realm-android-adapters/blob/master/adapters/src/main/java/io/realm/RealmRecyclerViewAdapter.java
//converted to kotlin and add updateDataNoClear method
@Suppress("unused", "MemberVisibilityCanBePrivate")
abstract class RealmRecyclerViewAdapter2<T : RealmModel, S : RecyclerView.ViewHolder>(
    data: OrderedRealmCollection<T>?,
    private val hasAutoUpdates: Boolean,
    private val updateOnModification: Boolean = true
) :
    RecyclerView.Adapter<S>() {

    private val listener: OrderedRealmCollectionChangeListener<OrderedRealmCollection<T>>?

    /**
     * Data associated with this adapter.
     */
    var data: OrderedRealmCollection<T>? = data
        private set

    private val isDataValid: Boolean
        get() = data != null && data!!.isValid


    init {
        check(!(data != null && !data.isManaged)) {
            "Only use this adapter with managed RealmCollection, " +
                    "for un-managed lists you can just use the BaseRecyclerViewAdapter"
        }
        this.listener = if (hasAutoUpdates) createListener() else null
    }

    private fun createListener(): OrderedRealmCollectionChangeListener<OrderedRealmCollection<T>> {
        return OrderedRealmCollectionChangeListener { collection, changeSet ->
            if (changeSet.state == OrderedCollectionChangeSet.State.INITIAL) {
                notifyDataSetChanged()
                return@OrderedRealmCollectionChangeListener
            }
            // For deletions, the adapter has to be notified in reverse order.
            val deletions = changeSet.deletionRanges
            for (i in deletions.indices.reversed()) {
                val range = deletions[i]
                notifyItemRangeRemoved(range.startIndex + dataOffset(), range.length)
            }

            val insertions = changeSet.insertionRanges
            for (range in insertions) {
                notifyItemRangeInserted(range.startIndex + dataOffset(), range.length)
            }

            if (!updateOnModification) {
                return@OrderedRealmCollectionChangeListener
            }

            val modifications = changeSet.changeRanges
            for (range in modifications) {
                notifyItemRangeChanged(range.startIndex + dataOffset(), range.length)
            }
        }
    }

    /**
     * Returns the number of header elements before the Realm collection elements. This is needed so
     * all indexes reported by the [OrderedRealmCollectionChangeListener] can be adjusted
     * correctly. Failing to override can cause the wrong elements to animate and lead to runtime
     * exceptions.
     *
     * @return The number of header elements in the RecyclerView before the collection elements. Default is `0`.
     */
    fun dataOffset(): Int {
        return 0
    }


    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        if (hasAutoUpdates && isDataValid) {
            addListener(data!!)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        if (hasAutoUpdates && isDataValid) {
            removeListener(data!!)
        }
    }

    override fun getItemCount(): Int {
        return if (isDataValid) data!!.size else 0
    }

    /**
     * Returns the item in the underlying data associated with the specified position.
     *
     * This method will return `null` if the Realm instance has been closed or the index
     * is outside the range of valid adapter data (which e.g. can happen if [.getItemCount]
     * is modified to account for header or footer views.
     *
     * Also, this method does not take into account any header views. If these are present, modify
     * the `index` parameter accordingly first.
     *
     * @param index index of the item in the original collection backing this adapter.
     * @return the item at the specified position or `null` if the position does not exists or
     * the adapter data are no longer valid.
     */
    fun getItem(index: Int): T? {
        require(index >= 0) { "Only indexes >= 0 are allowed. Input was: $index" }

        // To avoid exception, return null if there are some extra positions that the
        // child adapter is adding in getItemCount (e.g: to display footer view in recycler view)
        return data?.let {
            if (index < it.size && isDataValid) it[index] else null
        }
    }

    /**
     * Updates the data associated to the Adapter. Useful when the query has been changed.
     * If the query does not change you might consider using the automaticUpdate feature.
     *
     * @param data the new [OrderedRealmCollection] to display.
     */
    fun updateData(data: OrderedRealmCollection<T>?) {
        updateDataNoClear(data)
        notifyDataSetChanged()
    }

    fun updateDataNoClear(data: OrderedRealmCollection<T>?) {
        if (hasAutoUpdates) {
            this.data?.let(this::removeListener)
            data?.let(this::addListener)
        }
        this.data = data
    }

    @Suppress("UNCHECKED_CAST")
    private fun addListener(data: OrderedRealmCollection<T>) = when (data) {
        is RealmResults<T> -> data.addChangeListener(listener as OrderedRealmCollectionChangeListener<RealmResults<T>>)
        is RealmList<T> -> data.addChangeListener(listener as OrderedRealmCollectionChangeListener<RealmList<T>>)
        else -> throw IllegalArgumentException("RealmCollection not supported: " + data.javaClass)
    }

    @Suppress("UNCHECKED_CAST")
    private fun removeListener(data: OrderedRealmCollection<T>) = when (data) {
        is RealmResults<T> -> data.removeChangeListener(listener as OrderedRealmCollectionChangeListener<RealmResults<T>>)
        is RealmList<T> -> data.removeChangeListener(listener as OrderedRealmCollectionChangeListener<RealmList<T>>)
        else -> throw IllegalArgumentException("RealmCollection not supported: " + data.javaClass)
    }
}
