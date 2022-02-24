package app.simple.buyer.fragments.iteminfo

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.BidiFormatter
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import app.simple.buyer.R
import app.simple.buyer.base.BaseFragment
import app.simple.buyer.base.OnBackPressedListener
import app.simple.buyer.databinding.FragmentItemInfoBinding
import app.simple.buyer.util.Utils
import app.simple.buyer.util.showIf
import app.simple.buyer.util.views.viewBinding
import kotlin.math.max
import kotlin.math.min


class ItemInfoFragment : BaseFragment(R.layout.fragment_item_info), Toolbar.OnMenuItemClickListener, OnBackPressedListener {

    override val title: Int get() = R.string.lists_item_info

    private val binding by viewBinding(FragmentItemInfoBinding::bind)
    private val model: ItemInfoViewModel by viewModels()

    private val navbarBg get() = binding.viewNavbarBg.navBarLayoutBg

    override val toolbar: Toolbar get() = binding.viewToolbar.toolbar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity.supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        setHasOptionsMenu(true)
        toolbar.setOnMenuItemClickListener(this)

        model.getTitle()?.let {
            toolbar.title = it
            mActivity.title = it
        }

        binding.commentEditText.setText(model.comment)
        binding.commentEditText.doAfterTextChanged {
            model.commentChanged(it?.toString().orEmpty())
        }

        configureNumberEditText()

        binding.listsBaseLayout.setOnClickListener {
            Utils.hideKeyboard(mActivity)
            binding.etNumber.clearFocus()
            binding.commentEditText.clearFocus()
        }

        binding.btnNumberPlus.setOnClickListener {
            model.incrementCount()
        }
        binding.btnNumberMinus.setOnClickListener {
            model.decrementCount()
        }

        model.getCreatedDate().let {
            binding.tvDateCreate.showIf { it != null }
            if(it != null) {
                binding.tvDateCreate.text = requireContext().formatString(R.string.list_item_created, it)
            }
        }
        model.getModifiedDate().let {
            binding.tvDateModify.showIf { it != null }
            if(it != null){
                binding.tvDateModify.text = requireContext().formatString(R.string.list_item_modified, it)
            }
        }
        model.getBuyedDate().let {
            binding.tvDateBuy.showIf { it != null }
            if(it != null) {
                binding.tvDateBuy.text = requireContext().formatString(R.string.list_item_buyed, it)
            }
        }

        observeModel()

        binding.fab.setOnClickListener {
            mActivity.onBackPressed()
        }
    }


    private fun configureNumberEditText(){
        binding.etNumber.apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            transformationMethod = simpleTransformationMethod
            setOnEditorActionListener { _, actionId, event ->
                if (event?.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                    Utils.hideKeyboard(mActivity)
                    clearFocus()
                }
                false
            }
            onPreImeKeyListener = { keyCode: Int, event: KeyEvent? ->
                if (keyCode == KeyEvent.KEYCODE_BACK && event?.action == KeyEvent.ACTION_UP) {
                    Utils.hideKeyboard(mActivity)
                    clearFocus()
                }
            }
            setOnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    setText("")
                    setSelection(0)
                    Utils.showKeyboard(mActivity, this)
                } else if (text.isNullOrEmpty()) {
                    setText("${model.count.value!!}")
                }
            }
            addTextChangedListener(getTextWatcher())
        }
    }

    val simpleTransformationMethod = object : PasswordTransformationMethod() {
        override fun getTransformation(source: CharSequence, view: View): CharSequence = source
    }

    private fun observeModel(){
        model.count.observe(viewLifecycleOwner){
            val strValue = "$it"
            if(binding.etNumber.text?.toString().orEmpty() != strValue) {
                binding.etNumber.setText(strValue)
                binding.etNumber.setSelection(strValue.length)
            }
        }
        model.isDecrementButtonEnable.observe(viewLifecycleOwner){
            binding.btnNumberMinus.isEnabled = it
        }
    }

    override fun onResume() {
        super.onResume()
        Utils.showKeyBoard3(binding.commentEditText)
    }

    fun Context.formatString(@StringRes resId: Int, result: String): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            String.format(getString(resId), BidiFormatter.getInstance().unicodeWrap(result))
        } else {
            String.format(getString(resId), result)
        }
    }

    fun getTextWatcher() = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            //not used
        }
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            //not used
        }
        override fun afterTextChanged(s: Editable?) {
            val input = s?.toString().orEmpty()
            if (input.isNotEmpty() && input != "${model.count.value!!}") {
                var longValue = input.filter(Char::isDigit).toLongOrNull() ?: model.count.value!!
                if (longValue < 1) longValue = 1
                val result = "$longValue"
                if (input != result) {
                    val pos: Int = binding.etNumber.selectionStart - (input.length - result.length)
                    binding.etNumber.setText(result)
                    binding.etNumber.setSelection(max(0, min(pos, result.length)))
                } else {
                    model.countOnTextChanged(longValue)
                }
            }
        }
    }

    override fun onPause() {
        model.onPause()
        super.onPause()
    }

    override fun onBackPressed(): Boolean {
        model.onBackPressed()
        Utils.hideKeyboard(mActivity)
        return true
    }

    override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat?): WindowInsetsCompat? {
        setRecyclerPaddings(null, appBarLayout, binding.fab, insets, navBarBg = navbarBg)
        return super.onApplyWindowInsets(v, insets)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (toolbar.menu?.size() == 0) {
            toolbar.inflateMenu(R.menu.item_info)
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_item_edit -> {
                //
            }
        }
        return true
    }

}