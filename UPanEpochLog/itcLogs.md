## 日志收集方案第一版接入文档
  ## 第一步：


              implementation   'com.idc.idcsdk:itclogs:1.0.8'



## 第二步:



     全局初始化:
     LogParamsOption logParamsOption = new LogParamsOption.LogParamsOptionBuilder("http://10.10.20.200")//地址ip
           .port(9039)  //端口
           .dealyTime(5)  //运行最长时间，
           .device_sn("abcdefg")//设备id 注意要跟业务中mqtt或者rabbitmq获取的保持一致，服务器做了数据库查询比对;
           .build();
          LogUtilManager.getInstance().initLog(this, logParamsOption); //初始化




##  第三步:业务代码基础功能接口



  *  LogUtilManager.setAppLogSwitch(boolean switchLog)//开关  , 根据业务层通信机制进行调用

  *  LogUtilManager.submit();//打包上传，并关闭服务， 根据业务层通信机制进行调用

  *  LogUtilManager. getLogSpace();获取目前本地收集的日志文件大小，调用时会关闭日志收集，上传成功，继续收集


  *  LogUtilManager. configLogFileConfig(int fileCount,int fileSize) ;配置文件系统日志命令的文件个数和文件大小，文件大小
  默认单位是Mb;可以不传不调;根据服务器配置传递;


##  详细细节说明:
    *    submit上传成功组件会主动关闭服务;服务器也会默认更改状态

    *   支持失败上传，无网络或者其它原因倒是操作上传失败，组件不做任何操作，等待客户第二次操作上传，上传上次的zip包;

    *  杀掉服务，会调用接口主动通知服务器更改状态

    *   dealyTime() 非必须配置，不做配置调用，默认120分钟；即日志收集120分钟后，视为客户忘记关闭或者点击获取日志按钮；
   会主动触发上传关闭自;

    *    组件收集运行时日志，系统崩溃日志，系统日志，

    *    日志文件命名以 2021-11-9-18:04_logs.zip命名，

    *   crash,Applog，systemLog三个文件夹包含3种日志收集方案分别是崩溃，运行时日志，系统命令收集的log

    *   crash 内日志根据时间戳命名文件夹一条崩溃生成一个，Applog根据applog1.txt，applog2.txt由程序自增控制;程序设置9个文件极限，每个文件大小5Mb;

    *   systemLog 下遵循系统日志自动命名回滚方式,
    *   组件中包含3个接口，上传，通知目前文件大小，通知更新状态;
