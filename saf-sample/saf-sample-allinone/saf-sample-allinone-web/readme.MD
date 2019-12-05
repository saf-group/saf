样例运行程序需要增加启动参数:
使用如下参数启动不需要(1)中的配置，已经配好了。只在dev下可运行，其他环境没有做配置。
-Denv=dev -Dapp.id=saf-web-allinone

如果想要尝试修改配置，建立自己名字的集群，再复制一份相同配置，就可以随意CRUD，不会影响别人。
启动参数改为：
-Denv=dev -Dapp.id=saf-web-allinone -Dapollo.cluster=local-hepengyuan

浏览器访问可以看到：
http://localhost:3333/user/getUserDetail?userId=1
http://localhost:3333/shop/getShopDetail?shopId=1
http://localhost:3333/config/getSMSConfig

backup:
-Dapp.logging.mode=dev -Denv=dev -Dapp.id=saf-web-allinone