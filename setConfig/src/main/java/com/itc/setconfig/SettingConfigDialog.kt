package com.itc.setconfig

import android.app.Dialog

import android.graphics.Color

import android.os.Bundle
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils

import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.blankj.utilcode.util.*
import com.itc.setconfig.databinding.DialogSettingBinding


class SettingConfigDialog : DialogFragment() {

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
//        resizeDialogFragment()
    }

    private fun resizeDialogFragment() {
        val dialog = dialog
        if (dialog != null) {
            val window = dialog.window
            val lp = window!!.attributes
            lp.width = AdaptScreenUtils.pt2Px(800f)
            lp.height = AdaptScreenUtils.pt2Px(700f)
//            lp.width = AdaptScreenUtils.pt2Px(400f)
//            lp.height = AdaptScreenUtils.pt2Px(300f)
            if (null != window) {
                window.setLayout(lp.width, lp.height)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.setCanceledOnTouchOutside(false)
        /*       dialog?.setOnKeyListener(object : DialogInterface.OnKeyListener {
                   override fun onKey(dialog: DialogInterface?, keyCode: Int, event: KeyEvent?): Boolean {
                       if (keyCode == KeyEvent.KEYCODE_BACK) {
                           return true
                       }
                       return false
                   }
               })*/
        mDialogSettingBinding =
            DialogSettingBinding.inflate(layoutInflater, null, false)

        return mDialogSettingBinding.root
    }

    lateinit var mDialogSettingBinding: DialogSettingBinding



    lateinit var tv_tip_content: TextView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        arguments.getString("")
        var ipp =SPUtils.getInstance().getString(
            SetConfigGlobaConstant.IP_KEY,SetConfigGlobaConstant.IP
        )
        LogUtils.dTag(TAG, "ipp${ipp}")
        mDialogSettingBinding.layoutSeeting.etIp.setText(
           SPUtils.getInstance().getString(
                SetConfigGlobaConstant.IP_KEY, SetConfigGlobaConstant.IP
            )
        )
        mDialogSettingBinding.layoutSeeting.etPort.setText(
           SPUtils.getInstance().getString(
                SetConfigGlobaConstant.MQTT_PORT_KEY, SetConfigGlobaConstant.MQTT_PORT
            )
        )
        mDialogSettingBinding.layoutSeeting.settingPortEt.setText(
           SPUtils.getInstance().getString(
                SetConfigGlobaConstant.NETWORK_PORT_KEY, SetConfigGlobaConstant.HOST_PORT
            )
        )
        mDialogSettingBinding.layoutSeeting.tvUserName.setText(
            SPUtils.getInstance().getString(
                SetConfigGlobaConstant.MQTT_USERNAME_KEY, SetConfigGlobaConstant.MQTT_USERNAME
            )
        )
        mDialogSettingBinding.layoutSeeting.tvUserPassword.setText(
            SPUtils.getInstance().getString(
                SetConfigGlobaConstant.MQTT_PASSWORD_KEY, SetConfigGlobaConstant.MQTT_PASS
            )
        )

        mDialogSettingBinding.layoutSeeting.tvCancel.setOnClickListener {
            dismiss()
        }

        mDialogSettingBinding.layoutSeeting.tvCommit.setOnClickListener {
            if (!TextUtils.isEmpty(mDialogSettingBinding.layoutSeeting.etIp.text) &&
                !TextUtils.isEmpty(mDialogSettingBinding.layoutSeeting.etPort.text) &&
                !TextUtils.isEmpty(mDialogSettingBinding.layoutSeeting.settingPortEt.text) &&
                !TextUtils.isEmpty(mDialogSettingBinding.layoutSeeting.tvUserName.text) &&
                !TextUtils.isEmpty(mDialogSettingBinding.layoutSeeting.tvUserPassword.text)
            ) {

                SPUtils.getInstance().put(
                    SetConfigGlobaConstant.IP_KEY,
                    mDialogSettingBinding.layoutSeeting.etIp.text.toString(),true
                )

                SPUtils.getInstance().put(
                    SetConfigGlobaConstant.MQTT_PORT_KEY,
                    mDialogSettingBinding.layoutSeeting.etPort.text.toString(),true
                )
                SPUtils.getInstance().put(
                    SetConfigGlobaConstant.NETWORK_PORT_KEY,
                    mDialogSettingBinding.layoutSeeting.settingPortEt.text.toString(),true
                )
                SPUtils.getInstance().put(
                    SetConfigGlobaConstant.MQTT_USERNAME_KEY,
                    mDialogSettingBinding.layoutSeeting.tvUserName.text.toString(),true
                )
                SPUtils.getInstance().put(
                    SetConfigGlobaConstant.MQTT_PASSWORD_KEY,
                    mDialogSettingBinding.layoutSeeting.tvUserPassword.text.toString(),true
                )
                var MQTT_PORT_KEY =   SPUtils.getInstance().getString(SetConfigGlobaConstant.MQTT_PORT_KEY,SetConfigGlobaConstant.MQTT_PORT)
                var ip =   SPUtils.getInstance().getString(SetConfigGlobaConstant.IP_KEY, SetConfigGlobaConstant.IP)
                var port =   SPUtils.getInstance().getString(SetConfigGlobaConstant.NETWORK_PORT_KEY,SetConfigGlobaConstant.HOST_PORT)

                LogUtils.dTag(TAG, "ip${ip}" + "port:${port}" + "MQTT_PORT_KEY:${MQTT_PORT_KEY}")

                ToastUtils.showShort("配置成功,请杀掉进程后重启应用")
                mItemCLickListener?.onSure()
//                ToastUtils.showToast("配置成功,请杀掉进程后重启应用")
//                dismiss()
            } else {
                ToastUtils.showShort("配置数据不能为空")
//                mItemCLickListener?.onCancel()
            }


        }
    }




    interface ItemClickListener {
//        fun onCancel()
        fun onSure();

    }

    private var mItemCLickListener: ItemClickListener? = null

    fun addOnItemCLickListener(mitemclicklistener: ItemClickListener?) {
        mItemCLickListener = mitemclicklistener
    }

    companion object {
        const val TAG = "SettingConfigDialog"
        const val key_settingconfigdialog = "KEY_SETTINGCONFIGDIALOG"
    }
}