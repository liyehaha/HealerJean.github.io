---
title: springBoot上传限制
date: 2018-07-06 03:33:00
tags: 
- SpringBoot
category: 
- SpringBoot
description: springBoot上传限制
---
<!-- image url 
https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/blogImages
　　首行缩进
<font color="red">  </font>
-->

## 前言

## 1、springboogt项目控制的上传限制


maxFileSize 是单个文件大小
maxRequestSize是设置总上传的数据大小

+ Spring Boot 1.3 或之前的版本，配置:

```yml

multipart.maxFileSize = 100Mb
multipart.maxRequestSize=150Mb
```

+ Spring Boot 1.4 版本后配置更改为:


```yml
spring.http.multipart.maxFileSize = 100Mb
spring.http.multipart.maxRequestSize = 150Mb
```

+ Spring Boot 2.0 之后的版本配置修改为: 单位Mb改为MB了


```yml
spring.servlet.multipart.max-file-size = 100MB
spring.servlet.multipart.max-request-size = 150MB
```
## 2、前端也可以自己控制


## 3、Nginx控制的上传大小大小

上传文件的大小超出了 Nginx 允许的最大值，如果没有配置的话，默认是1M；


```conf
   client_max_body_size 100m; 上传限制为 100m

http {  
    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    types_hash_max_size 2048;

    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    client_max_body_size 100m; 上传限制为 100m
```



<br/><br/><br/>
如果满意，请打赏博主任意金额，感兴趣的在微信转账的时候，添加博主微信哦， 请下方留言吧。可与博主自由讨论哦

|支付包 | 微信|微信公众号|
|:-------:|:-------:|:------:|
|![支付宝](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/assets/img/tctip/alpay.jpg) | ![微信](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/assets/img/tctip/weixin.jpg)|![微信公众号](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/assets/img/my/qrcode_for_gh_a23c07a2da9e_258.jpg)|




<!-- Gitalk 评论 start  -->

<link rel="stylesheet" href="https://unpkg.com/gitalk/dist/gitalk.css">
<script src="https://unpkg.com/gitalk@latest/dist/gitalk.min.js"></script> 
<div id="gitalk-container"></div>    
 <script type="text/javascript">
    var gitalk = new Gitalk({
		clientID: `1d164cd85549874d0e3a`,
		clientSecret: `527c3d223d1e6608953e835b547061037d140355`,
		repo: `HealerJean.github.io`,
		owner: 'HealerJean',
		admin: ['HealerJean'],
		id: '37aeF5HRRQ4gxehl',
    });
    gitalk.render('gitalk-container');
</script> 

<!-- Gitalk end -->

