### MqttLib技术文档
一.接入方式:



  ### 1.  引进 implementation 'com.idc.idcsdk:mqttLib:1.0.4'


  -----



###  2.  在Application oncreate()中调用
           String host = "tcp://10.10.20.200:1883";

           String topic = "device/" + DeviceIdUtil.getDeviceId(this);

           MqttOption option = new MqttOption.MqttOptionBuilder(host)//例如 "tcp://10.10.20.200:1883";

              .publish_topid(topic)  //订阅主题  拼接  "device/"   例如 "device/" + DeviceIdUtil.getDeviceId(this);

              .response_topid(topic)//接收主题  拼接   "device/"

              .username("itc")  //用户名

              .clientId(deviceId)  //设备唯一id

              .password("itc.pass")  //密码

              .build();


 MqttManager.getInstance().init(this, option, null);
      说明: OnlineInforOption类
      此类用于设置设备上线的传输数据，
      基础数据sdk已经封装好，预留以应对日后服务器可能的变化，例如设备上线 type数字可能会变化，
      目前 设备上线的发布type默认传1;
      如若自定义设备上线的参数，可以构造 OnlineInforOption;没有则传null




  ----
  ###  3.自定义广播接收器用于接收数据


      IntentFilter intentFilter = new IntentFilter();
      intentFilter.addAction(MqttAction.RECEOIVER_ACTION);
      myReceiver = new MyReceiver();
      mLocalBroadcastManager.registerReceiver(myReceiver, intentFilter);


 注意 :考虑数据安全性 sdk应用本地广播 LocalBroadcastManager,业务层注册也应该本地注册

  广播注册方式AndroidManifest 高版本无效，业务层应当在全局application中代码注册广播

### 广播接收到的数据
      if (intent != null) {
        String contentMessage = intent.getStringExtra(MqttAction.KEY_CONTENT_MESSAGE);//数据
        String topId = intent.getStringExtra(MqttAction.KEY_TOPID_MESSAGE);  //订阅的主题


        考虑到数据涉及到业务层，且每个项目的数据格式不同，因此不对数据做封装，这里返回服务器的原生数据；使用时候自行解析


### 4.发布消息,订阅消息  MqttManager


      在初始化结束后，业务可使用MqttManager 进行发布和订阅消息主题；这是一个单例
