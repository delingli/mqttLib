  #### deviceOptComponent技术文档

## 一. 前言:
>  此组件是统一处理现有交付业务 屏的开关机操作，红外操作，亮屏息屏操作，定时重启的功能抽离;组件内不做业务调度处理只提供向外的接口数据;过滤运行时日志Tag为IDeviceOpt
## 二.目录


### 1.  引进方式
  > implementation 'com.idc.idcsdk:deviceOptComponent:1.2.3'


### 2.  初始化调用

>   DeviceOptManager.toInit(context: Context?, mSwitchDeviceOption: SwitchDeviceOption?)



### 3.  注意事项
> 目前适配: 0830B，精鑫，深海瑞格，智微Android盒子，视拓，华科8305，华科广谱设备注意华科8305，深海瑞格需要系统签名包



### 4.接口说明
>  DeviceOptManager

 * hasInited()//是否初始化
 * flushDeviceTime()//触发请求获取最新开关机和重启时间
 * cancelRestartDevice(@DeviceType.DeviceType deviceType: String?)//取消重启
 * unInit()//反注册
 * cancelOpenCloseTime( @DeviceType.DeviceType deviceType: String?)//取消开关机任务




公共接口:
