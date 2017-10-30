# WxShare
微信公众平台，自定义分享链接中的文字与图片。

1.启动项目即可自动开启定时获取Ticket线程，并通过ServletContext全局缓存。

2.在实际操作中碰到的情况：在安卓上成功，在苹果失败。具体原因：wx.onMenuShareAppMessage函数中的link链接不能带端口号。
  
3.获取签名：WxUtil.getSign(url);

4.还未入门的小白谢谢大佬们不吝赐教。
