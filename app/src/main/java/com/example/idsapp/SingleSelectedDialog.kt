package com.example.idsapp

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.res.Resources
import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import android.os.Bundle
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.*


class SingleSelectedDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        val dialog = super.onCreateDialog(savedInstanceState)
        val dialog = Dialog(requireContext(), R.style.dialog)
        dialog?.window?.setGravity(Gravity.CENTER)
        //设置actionbar的隐藏
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    fun isShow(): Boolean {
        return dialog?.isShowing ?: false
    }

    fun destory() {
        if (dialog != null) {
            dialog!!.dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        resizeDialogFragment()
    }

    private fun resizeDialogFragment() {
        val dialog = dialog
        if (dialog != null) {
            val window = dialog.window
            val lp = window!!.attributes
            lp.width = AdaptScreenUtils.pt2Px(340f)
            lp.height = AdaptScreenUtils.pt2Px(198f)
            if (null != window) {
                window.setLayout(lp.width, lp.height)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(false)
//        dialog?.setOnKeyListener(object : DialogInterface.OnKeyListener {
//            override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    return true
//                }
//                return false
//            }
//        })

        return inflater.inflate(R.layout.single_selected_layout, container, false)
    }

    var mItemBean: ItemBean? = null
    var mSingleSelectedAdapter: SingleSelectedAdapter? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var mRecycler: RecyclerView = view.findViewById<RecyclerView>(R.id.recycler)
        var btnm_sure: Button = view.findViewById<Button>(R.id.btnm_sure)
        var mList = mutableListOf<ItemBean>()
        mList.add(ItemBean("精鑫", false, 1))
        mList.add(ItemBean("深海瑞格", false, 2))
        mList.add(ItemBean("0830B", false, 3))
        mList.add(ItemBean("Android盒子", false, 4))
        var defaultSelectId = SPUtils.getInstance().getInt(KEY_SELECT, -1)
        mList?.forEach {
            if (it.id == defaultSelectId) {
                it.selected = true
            }
        }

        mRecycler.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        mSingleSelectedAdapter = SingleSelectedAdapter(mList, requireContext())
        mRecycler.adapter = mSingleSelectedAdapter
        mItemBean = mSingleSelectedAdapter?.getItemBean()
        mSingleSelectedAdapter?.addOnItemClickListener {
            mItemBean = it
            LogUtils.dTag(TAG, "你选的是:${mItemBean?.name} id: ${mItemBean?.id}")

        }
        btnm_sure.setOnClickListener {
            mItemBean?.run {
                SPUtils.getInstance().put(KEY_SELECT, this.id)
                LogUtils.dTag(TAG, "选中的是:${this.name} id: ${this.id}")
                ToastUtils.showShort("选中保存成功，手动杀进程重启生效")
//                AppUtils.relaunchApp(true)
            }

        }
    }

    interface ItemCLickListener {
        fun onClose();

    }

    private var mItemCLickListener: ItemCLickListener? = null

    fun addOnItemCLickListener(mitemclicklistener: ItemCLickListener?) {
        mItemCLickListener = mitemclicklistener
    }


    companion object {
        val KEY_SELECT: String = "select_item"
        val TAG: String = "SingleSelectedDialog"

    }
}