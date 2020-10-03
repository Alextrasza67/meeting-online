# webrtc 测试

## 简版
双方访问 room.html ，点击 Prepare 按钮后， 点击 Open 即可。 左侧为本地影像，右侧为远端影像




### 操作流程说明
* 主持人创建房间，并进入
* 主持人添加用户并发出邀请
* 用户通过邀请链接进入，辅以认证，暂时为口令密码，进入后通知房间内人员，有用户加入
* 主持人发出视频邀约
* 用户同意，建立RTC链接，双方同步ICE
* 主视频通话两人进行交流，其他路线仅视频流
* 主持人拥有开关，配置各路线语音开关
* 