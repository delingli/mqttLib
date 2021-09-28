### RabbitMqLib技术文档
一.接入方式:
  ### 1.  引进 implementation 'com.idc.idcsdk:rabbitmq:1.0.0'


  -----

## 2.  在Application oncreate()中调用
    RabbitService. startRabbitService(Context context, RabbitMqOption option, RabbitMqParamsOption rabbitMqParamsOption)

### 参数说明:
## RabbitMqOption
    这是配置参数 host 用户名密码 port
## RabbitMqParamsOption   这是初始化提交参数，
    框架已经为我们默认传递以下参数；
    type   1
    device_sn  
    resolution,
    memory,device_ip,
    device_model,
    device_version,root  
    storage;

    其中 type默认为1;默认为信发业务；此参数信发可传递null

    其它业务只需要构造业务特异性参数即可;
    例如展厅:
    ArrayMap<String,String> params=new ArrayMap<>();
    params.put("type","5");
    RabbitMqParamsOption rabbitMqParamsOption1=new RabbitMqParamsOption
    .RabbitMqParamsOptionBuilder()
    .params(params).build();
  ----
## 3.自定义广播接收器用于接收数据


      IntentFilter intentFilter = new IntentFilter();
      intentFilter.addAction(RabitMqAction.RECEOIVER_ACTION);
      myReceiver = new MyReceiver();
      mLocalBroadcastManager.registerReceiver(myReceiver, intentFilter);


 注意 :考虑数据安全性 此库应用本地广播 LocalBroadcastManager,业务层注册也应该LocalBroadcastManager注册

  广播注册方式AndroidManifest 高版本无效，业务层应当在全局application中代码注册广播

### 广播接收到的数据
      if (intent != null) {
        String contentMessage = intent.getStringExtra(RabitMqAction.KEY_CONTENT_MESSAGE);//数据


        考虑到数据涉及到业务层，且每个项目的数据格式不同，因此不对数据做封装，这里返回服务器的原生数据；使用时自行解析


### 4.发布消息,  RabbitMqManager


      在初始化结束后，业务可使用RabbitMqManager 进行发布消息；它是一个单例
