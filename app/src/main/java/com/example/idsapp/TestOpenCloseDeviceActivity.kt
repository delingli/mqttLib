package com.example.idsapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bigkoo.pickerview.builder.TimePickerBuilder
import com.bigkoo.pickerview.listener.OnTimeSelectListener
import com.bigkoo.pickerview.view.TimePickerView
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.SPUtils
import com.blankj.utilcode.util.ToastUtils
import com.example.idsapp.databinding.ActivityTestOpenCloseDeviceBinding
//import com.itc.switchdevicecomponent.IDeviceOptManager
import com.itc.switchdevicecomponent.annation.DeviceType
import com.itc.switchdevicecomponent.impl.SwitchDeviceOption
import com.itc.switchdevicecomponent.work.DeviceOptManager
import java.text.SimpleDateFormat
import java.util.*


class TestOpenCloseDeviceActivity : AppCompatActivity() {
    var mCloseDeviceTime: TimePickerView? = null
    var mOpenDeviceTime: TimePickerView? = null
    var mRestartDevice: TimePickerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mBinding = ActivityTestOpenCloseDeviceBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        val sdfs = SimpleDateFormat("yyyy-MM-dd HH:mm")
        val booleanS = booleanArrayOf(true, true, true, true, true, true)
        var mSwitchDeviceOption =
            SwitchDeviceOption.SwitchDeviceOptionBuilder(DeviceType.MODULE_HK8305)
                .device_sn("2AF01F1E243D13314B2268ADBD03F83BD")
                .url("http://10.10.20.91/api/device/device_execute_time")
                .build()
        DeviceOptManager.toInit(this, mSwitchDeviceOption)
//        DeviceOptManager.flushDeviceTime()DeviceOptManager
//        DeviceOptManager.flushDeviceTime()
        mBinding.tvCloseDevice.setOnClickListener {
            if (mCloseDeviceTime == null) {
                mCloseDeviceTime = TimePickerBuilder(this, object : OnTimeSelectListener {
                    override fun onTimeSelect(date: Date?, v: View?) {
                        mBinding.tvCloseDevice.setText(sdfs.format(date))
                    }

                }).setType(booleanS).build()
            }

            mCloseDeviceTime?.let {
                if (it.isShowing) {
                    it.dismiss()
                } else {
                    it.show()
                }
            }
        }
        mBinding.tvOpenDevice.setOnClickListener {
            if (mOpenDeviceTime == null) {
                mOpenDeviceTime = TimePickerBuilder(this, object : OnTimeSelectListener {
                    override fun onTimeSelect(date: Date?, v: View?) {
                        mBinding.tvOpenDevice.setText(sdfs.format(date))
                    }

                }).setType(booleanS).build()

            }

            mOpenDeviceTime?.let {
                if (it.isShowing) {
                    it.dismiss()
                } else {
                    it.show()
                }
            }

        }
        mBinding.tvRestartDevice.setOnClickListener {

            if (mRestartDevice == null) {
                mRestartDevice = TimePickerBuilder(this, object : OnTimeSelectListener {
                    override fun onTimeSelect(date: Date?, v: View?) {
                        mBinding.tvRestartDevice.setText(sdfs.format(date))
                    }


                }).setType(booleanS).build()
            }

            mRestartDevice?.let {
                if (it.isShowing) {
                    it.dismiss()
                } else {
                    it.show()
                }
            }
        }
        var mSingleSelectedDialog = SingleSelectedDialog()
        mBinding.btnSelectDevice.setOnClickListener {
//            yyyy-MM-dd HH:mm

            DeviceOptManager.getHK8305DeviceOpt()?.closeDevice("2022-06-30 14:05")
            DeviceOptManager.getHK8305DeviceOpt()?.startDevice("2022-06-30 14:10")
//            DeviceOptManager.getSkDeviceOpt()?.cancelStartCloseDevice("2022-06-28 14:55","2022-06-28 14:50")
//            DeviceOptManager.getSkDeviceOpt()?.systemReset()
//            DeviceOptManager.getAndroidHeziDeviceOptImpl()?.closeDevice("2022-06-25 10:05")
//            DeviceOptManager.flushDeviceTime()
            /*         if (mSingleSelectedDialog == null) {
                         mSingleSelectedDialog = SingleSelectedDialog()
                     }
                     mSingleSelectedDialog?.let {
                         if (it.isShow()) {
                             it.destory()
                         } else {
                             it.show(supportFragmentManager, "mSingleSelectedDialog")
                         }

                               }*/

        }
        mBinding.btnCancel.setOnClickListener {
            var deviceTypes: String? = getDeviceType()
            DeviceOptManager.getHK8305DeviceOpt()?.cancelStartCloseDevice()

//            IDeviceOptManager.cancelOpenCloseTime(
//                DeviceType.MODULE_SHRG
//            )
        }
        mBinding.btnConfig.setOnClickListener {
            if (mBinding.tvCloseDevice.text.isNullOrEmpty()) {
                ToastUtils.showShort("??????????????????????????????")
                return@setOnClickListener
            }
            if (mBinding.tvOpenDevice.text.isNullOrEmpty()) {
                ToastUtils.showShort("??????????????????????????????")
                return@setOnClickListener
            }
            var openDeviceCala: Calendar = Calendar.getInstance()
            var closeDeviceCala: Calendar = Calendar.getInstance()
            openDeviceCala.time = sdfs.parse(mBinding.tvOpenDevice.text.toString())
            closeDeviceCala.time = sdfs.parse(mBinding.tvCloseDevice.text.toString())

            if (!closeDeviceCala.before(openDeviceCala)) {
                ToastUtils.showShort("?????????????????????????????????????????????")
                return@setOnClickListener
            }
            if (!mBinding.tvRestartDevice.text.isNullOrEmpty()) {
                var restartDeviceCala: Calendar = Calendar.getInstance()
                restartDeviceCala.time = sdfs.parse(mBinding.tvRestartDevice.text.toString())
                if (!restartDeviceCala.before(closeDeviceCala)) {
                    ToastUtils.showShort("???????????????????????????????????????????????????????????????????????????????????????????????????")
                    return@setOnClickListener
                }

                var deviceTypes: String? = getDeviceType()
//                IDeviceOptManager.addRestartDevice(
//                    mBinding.tvRestartDevice.text.toString(),
//                    deviceTypes
//                )
            }
            var deviceTypes: String? = getDeviceType()
//            IDeviceOptManager.addOpenCloseTime(
//                mBinding.tvOpenDevice.text.toString(),
//                mBinding.tvCloseDevice.text.toString(),
//                DeviceType.MODULE_SHRG
//            )

        }
        var defaultSelectId = SPUtils.getInstance().getInt(SingleSelectedDialog.KEY_SELECT, -1)
        LogUtils.dTag(SingleSelectedDialog.TAG, "?????????????????????id${defaultSelectId}")

        if (defaultSelectId == -1) {
            //????????????1
            SPUtils.getInstance().put(SingleSelectedDialog.KEY_SELECT, 1)
            LogUtils.dTag(SingleSelectedDialog.TAG, "?????????????????????id 1")

        }
        var newSelectId = SPUtils.getInstance().getInt(SingleSelectedDialog.KEY_SELECT, -1)
        LogUtils.dTag(SingleSelectedDialog.TAG, "???????????????id${newSelectId}")
        if (newSelectId == 1) {
            mBinding.btnSelectDevice.setText("??????")
        } else if (newSelectId == 2) {
            mBinding.btnSelectDevice.setText("????????????")

        } else if (newSelectId == 3) {
            mBinding.btnSelectDevice.setText("0830B")

        } else if (newSelectId == 4) {
            mBinding.btnSelectDevice.setText("Android??????")

        }

    }

    private fun getDeviceType(): String? {
        var newSelectId = SPUtils.getInstance().getInt(SingleSelectedDialog.KEY_SELECT, -1)
        var deviceTypes: String? = null
        if (newSelectId == 1) {
            deviceTypes = DeviceType.MODULE_JINGXIN
        } else if (newSelectId == 2) {
            deviceTypes = DeviceType.MODULE_SHRG
        } else if (newSelectId == 3) {
            deviceTypes = DeviceType.MODULE_HEZI0830B
        } else if (newSelectId == 4) {
            deviceTypes = DeviceType.MODULE_ANDROID_HE_ZI
        }
        return deviceTypes
    }
}