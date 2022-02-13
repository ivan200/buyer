package app.simple.buyer.fragments.mainlist

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Fade
import androidx.transition.Transition
import androidx.transition.TransitionManager
import app.simple.buyer.databinding.CellMainListBinding
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.util.hide
import app.simple.buyer.util.show
import app.simple.buyer.util.showIf
import app.simple.buyer.util.views.RealmRecyclerViewAdapter2
import io.realm.OrderedRealmCollection

class MainListAdapter(
    data: OrderedRealmCollection<BuyListItem>,
    var showCheckedItems: Boolean,
    val onItemSelected: Function1<Long, Unit>
) : RealmRecyclerViewAdapter2<BuyListItem, MainListAdapter.MainListHolder>(data, true) {
    init {
        setHasStableIds(true)
    }

    override fun getItemId(index: Int): Long {
        return getItem(index)!!.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListHolder {
        val binding = CellMainListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainListHolder(binding)
    }

    override fun onBindViewHolder(holder: MainListHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class MainListHolder(val binding: CellMainListBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private var itemListId: Long = 0

        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(data: BuyListItem) {
            itemListId = data.id

            binding.apply {
                tvTitle.text = data.buyItem?.name

                if (data.count <= 1L) {
                    tvCount.hide()
                } else {
                    tvCount.show()
                    tvCount.text = data.count.toString()
                }

                checkbox.isChecked = data.isBuyed

//                val w = (checkboxContainer.width + checkboxContainer.marginLeft + checkboxContainer.marginRight).toFloat()
//                if (checkboxContainer.visibility == View.VISIBLE && !showCheckedItems) {
//                    tvTitleContainer.clearAnimation()
//                    checkboxContainer.clearAnimation()
////                    val x = tvTitleContainer.x
////                    tvTitleContainer.x = tvTitleContainer.resources.getDimension(R.dimen.size_icon_bounding)
//                    checkboxContainer.show()
//                    checkboxContainer
//                        .animate()
//                        .alpha(0.0f)
//                        .onAnimationEnd {
//                            tvTitleContainer.clearAnimation()
//                            tvTitleContainer
//                                .animate()
//                                .translationX(-1* w)
//                                .onAnimationEnd {
//                                    tvTitleContainer.x = w
//                                    checkboxContainer.clearAnimation()
//                                    checkboxContainer.hide()
//                                }
//                        }
//                } else if (checkboxContainer.visibility != View.VISIBLE && showCheckedItems) {
//                    checkboxContainer.clearAnimation()
//                    tvTitleContainer.clearAnimation()
//                    tvTitleContainer
//                        .animate()
//                        .translationX(w)
//                        .onAnimationEnd {
//                            tvTitleContainer.clearAnimation()
//                            tvTitleContainer.x = 0f
//                            checkboxContainer.clearAnimation()
//                            checkboxContainer.show()
//                            checkboxContainer
//                                .animate()
//                                .alpha(1.0f)
//                                .onAnimationEnd {
//                                    checkboxContainer.clearAnimation()
//                                    checkboxContainer.show()
//                                }
//                        }
//                } else {
//                    tvTitleContainer.x = w
//                    checkboxContainer.showIf { showCheckedItems }
//                }






//                animationShow?.cancel()
//                animationHide?.cancel()
//
//                val w = (checkboxContainer.width + checkboxContainer.marginLeft + checkboxContainer.marginRight).toFloat()
//                if (!isNew && checkboxContainer.visibility == View.VISIBLE && !showCheckedItems) {
//                    checkboxContainer.visibility = View.VISIBLE
//
//                    val translationAnimator = ObjectAnimator
//                        .ofFloat(tvTitleContainer, View.TRANSLATION_X, 0f, -1 * w)
//                        .setDuration(300)
//
//                    val alphaAnimator = ObjectAnimator
//                        .ofFloat(checkboxContainer, View.ALPHA, checkboxContainer.alpha, 0f)
//                        .setDuration(300)
//
//                    animationHide = AnimatorSet()
//                    animationHide?.playSequentially(
//                        alphaAnimator,
//                        translationAnimator
//                    )
//                    animationHide?.addListener(onEnd = {
//                        tvTitleContainer.x = w
//                        checkboxContainer.visibility = View.GONE
//                    }, onCancel = {
//                        tvTitleContainer.x = w
//                        checkboxContainer.visibility = View.GONE
//                    })
//                    animationHide?.start()
//                } else if (!isNew && checkboxContainer.visibility != View.VISIBLE && showCheckedItems) {
//
//                    val x = tvTitleContainer.x
//                    checkboxContainer.visibility = View.VISIBLE
//
//                    val alphaAnimator = ObjectAnimator
//                        .ofFloat(checkboxContainer, View.ALPHA, checkboxContainer.alpha, 1f)
//                        .setDuration(300)
//
//                    val translationAnimator = ObjectAnimator
//                        .ofFloat(tvTitleContainer, View.TRANSLATION_X, -1*w, 0f)
//                        .setDuration(300)
//
//                    animationShow = AnimatorSet();
//                    animationShow?.playSequentially(
//                        translationAnimator,
//                        alphaAnimator
//                    )
//                    animationHide?.addListener(onEnd = {
//                        checkboxContainer.visibility = View.VISIBLE
//                        tvTitleContainer.x = w
//                    }, onCancel = {
//                        checkboxContainer.visibility = View.VISIBLE
//                        tvTitleContainer.x = w
//                    })
//                    animationShow?.start()
//
//                } else {
//                    if(!isNew){
//                        tvTitleContainer.x = w
//                    }
//                    checkboxContainer.showIf { showCheckedItems }
//                }




                checkboxContainer.showIf { showCheckedItems }

            }
        }

        var animationShow: AnimatorSet? = null
        var animationHide: AnimatorSet? = null


        fun View.fadeVisibility(visibility: Int, duration: Long = 400) {
            val transition: Transition = Fade()
            transition.duration = duration
            transition.addTarget(this)
            TransitionManager.beginDelayedTransition(this.parent as ViewGroup, transition)
            this.visibility = visibility
        }

        fun <T : ViewPropertyAnimator> T.onAnimationEnd(function: () -> Unit): T {
            this.setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    function.invoke()
                }
            })
            return this
        }

        fun <T : ViewPropertyAnimator> T.onAnimationStart(function: () -> Unit): T {
            this.setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationEnd(animation)
                    function.invoke()
                }
            })
            return this
        }


        override fun onClick(v: View?) {
            onItemSelected.invoke(itemListId)
        }
    }
}