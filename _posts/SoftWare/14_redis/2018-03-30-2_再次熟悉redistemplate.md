---
title: 再次熟悉RedisTemplate
date: 2018-03-30 23:33:00
tags: 
- Cache
category: 
- Cache
description: 再次熟悉RedisTemplate
---
<!-- image url 
https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/blogImages
　　首行缩进
<font color="red">  </font>
-->

## 前言

其实人造的工具类的出现，很大程度上帮助了开发人员，但是说到成长性，其实对于刚刚接触redis的同学们来说，我觉得这是一件非常不好的事情。下面咱们具体说说吧！！！

### 特别说明：
 1、其实项目中一般用到的也就是StringRedisTemplate 
 2、后面稍微有点 value是set的情况，请仔细观察，会说明使用场景
 3、其实用到的无非也就是key、value 。所以我下面的自定义的时候，没有使用hash



## 1、Reids定义的5中数据结构

|redis操作|子api接口|操作类型|
|:---|:--|:---|
|redisTemplate.opsForValue();|ValueOperations|操作字符串|
|redisTemplate.opsForHash();|HashOperations|操作hash|
|redisTemplate.opsForList();|ListOperations|操作list |
|redisTemplate.opsForSet();|SetOperations|操作set|
|redisTemplate.opsForZSet();|ZSetOperations|操作有序set|
|boundValueOps(K)|BoundValueOperations|以绑定指定key的方式，操作具有简单值的条目|
|boundListOps(K)	|BoundListOperations|以绑定指定key的方式，操作具有list的条目|
|boundSetOps(K)|BoundSetOperations|以绑定指定key的方式，操作具有set的条目|
|boundZSet(K)	|BoundZSetOperations|以绑定指定key的方式，操作具有ZSet（排序的set）的条目|
|boundHashOps(K)	|BoundHashOperations|以绑定指定key的方式，操作具有hash值的条目|


​		





### 1、推荐使用StringRedisTemplate

<br/>
下面毫无父子兄弟关系的两个类，怎么能互相注入呢？这个是Spring的Editor机制，搜索下ValueOperationsEditor这个类就知道了，Spring在注入的时候调用了Editor的setValue方法。
<br/>

<font color="red">  


可以将redisTemplate注入到ValueOperations，避免了ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue(); 这样来获取ValueOperations；如果是这种方式的话，是用来操作Long,Double类型的，如果不使用这种方式，则会报错（我的就报错了，也不知道为啥，加上就好使了）

<br/>
2、ValueOperations RedisOperations
就像他们的名字一样 ，一个是操作数据的值的，一个是操作key的其他（过期，删除等）

 </font>

```java
ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue(); 
```

```
@Component
public class RedisLongData implements InitializingBean {

    @Resource(name="redisTemplate")
    private ValueOperations<String, Long> valueOperations;

    @Resource(name="redisTemplate")
    private RedisTemplate<String,Long> redisTemplate;


    @Override
    public void afterPropertiesSet() throws Exception {
        redisTemplate.setValueSerializer(new GenericToStringSerializer<Long>(Long.class));
        valueOperations = redisTemplate.opsForValue();
    }



```




```java

@Component
public class RedisObjectData {

    @Resource(name="redisTemplate")
    private ValueOperations<String, Object> valueOperations;

    @Resource(name = "redisTemplate")
    private RedisOperations<String,Object> operations;




```

## 1、实际项目中的使用

其实在项目中实际应用的时候，通过序列化key和value为String，已经将redisTemplate变成StringRedisTemplate了，也就是说redistemplate可以直接向StringRedisTemplate医院使用，但还是优先推荐StringRedisTemplate ，但是这里我想说的是，我下面的时候用的是自定义的，比直接String牛逼多了，好不。也可以拿来当StringRedisTemplate

```xml

<bean id="redisTemplate" class="org.springframework.data.redis.core.RedisTemplate" scope="prototype">
    <property name="connectionFactory" ref="jedisFactory"/>
    <property name="keySerializer">
        <bean class="com.hlj.redis.cacheSerializer.CustomStringRedisSerializer"/>
    </property>
    <property name="valueSerializer">
        <bean class="com.hlj.redis.cacheSerializer.CustomJSONStringRedisSerializer"/>
    </property>
</bean>


<bean id="stringRedisTemplate" class="org.springframework.data.redis.core.StringRedisTemplate" scope="prototype">
    <property name="connectionFactory" ref="jedisFactory"/>
</bean>



```

### 2、测试结果一样

```java
@RestController
public class OptsForValuesController {

    @Resource(name="stringRedisTemplate")
    private StringRedisTemplate  stringRedisTemplate;


    @Resource(name = "redisTemplate")
    private  RedisTemplate redisTemplate;

    private  String  KEY = "stringRedisTemplate";

    @GetMapping("set")
    public String set(){
        stringRedisTemplate.opsForValue().set(KEY," stringRedisTemplate.opsForValue().set");
        redisTemplate.opsForValue().set("redisTemplate","redisTemplate.opsForValue().set");
        return   stringRedisTemplate.opsForValue().get(KEY).toString();
    }

```

![WX20180330-182911@2x](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/blogImages/WX20180330-182911@2x.png)


## 2、关于其他的一些基本的方法


```java

package com.duodian.youhui.admin.config.redis;

/**
 * @Description
 * @Author HealerJean
 * @Date 2018/3/29  下午6:48.
 */
public class Readme {


    /*


方法	子API接口	描述
opsForValue()	ValueOperations	     描述具有简单值的条目
opsForList()	ListOperations	     操作具有list值的条目
opsForSet()	    SetOperations	     操作具有set值的条目
opsForZSet()	ZSetOperations	     操作具有ZSet值（排序的set）的条目
opsForHash()	HashOperations	     操作具有hash值的条目
boundValueOps(K)	BoundValueOperations	以绑定指定key的方式，操作具有简单值的条目
boundListOps(K)	BoundListOperations	以绑定指定key的方式，操作具有list的条目
boundSetOps(K)	BoundSetOperations	以绑定指定key的方式，操作具有set的条目
boundZSet(K)	BoundZSetOperations	以绑定指定key的方式，操作具有ZSet（排序的set）的条目
boundHashOps(K)	BoundHashOperations	以绑定指定key的方式，操作具有hash值的条目




## 1、
stringRedisTemplate.opsForValue().set("test", "100",60*10,TimeUnit.SECONDS);//向redis里存入数据和设置缓存时间

stringRedisTemplate.opsForValue().get("test")//根据key获取缓存中的val

stringRedisTemplate.opsForValue().increment(key,1); //增加1

stringRedisTemplate.expire("red_123",1000 , TimeUnit.MILLISECONDS);//设置过期时间

## 2
stringRedisTemplate.delete("test");//根据key删除缓存

stringRedisTemplate.getExpire("test")//根据key获取过期时间,默认是秒

stringRedisTemplate.getExpire("test",TimeUnit.SECONDS)//根据key获取过期时间并换算成指定单位



//以绑定指定key的方式，通过key获取设置属性
stringRedisTemplate.boundValueOps("test").increment(-1);//val做-1操作

stringRedisTemplate.boundValueOps("test").increment(1);//val +1

stringRedisTemplate.boundValueOps(key).expire(seconds, TimeUnit.SECONDS);


set 无序，不重复
stringRedisTemplate.opsForSet().add("red_123", "1","2","3");//向指定key中存放set集合
stringRedisTemplate.opsForSet().members("red_123");//根据key获取set集合
//用户是否在线
stringRedisTemplate.opsForSet().isMember("red_123", "1")//根据key查看集合中是否存在指定数据


stringRedisTemplate.boundSetOps(setKey).remove(param);


stringRedisTemplate.hasKey("546545");//检查key是否存在，返回boolean值






    */
}

```

## 3、通过序列化和反序列进行获取值


```java
package com.hlj.redis.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description
 * @Author HealerJean
 * @Date 2018/4/2  上午11:16.
 */
@RestController
public class SerializerController {

    @Resource(name="stringRedisTemplate")
    private StringRedisTemplate stringRedisTemplate;


    private  String  KEY = "stringRedisTemplate";

    StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();


    @GetMapping("serialize")
    public String serialize(){

        return stringRedisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection redisConnection) throws DataAccessException {
                //选中哪一个库,默认是第一个库
             //   redisConnection.select();
                byte[] keys = stringRedisSerializer.serialize(KEY);
                byte[] values = redisConnection.get(keys);
                return stringRedisSerializer.deserialize(values);
            }
        });
    }


    //自增
    @GetMapping("increase")
    public Long increase(){
        return stringRedisTemplate.execute(new RedisCallback<Long>() {
            @Override
            public Long doInRedis(RedisConnection redisConnection) throws DataAccessException {
                byte[] keyBytes = stringRedisSerializer.serialize("long");
                Long val = redisConnection.incr(keyBytes);
                redisConnection.expire(keyBytes,100L);
                return val;
            }
        });
    }

}

```

## 4、value为Long类型的数据

那么 这个时候就不能使用`StringRedisTemplate` 了，使用它会报错(废话，key当然不能为long类型了，妈的，除了String其他的也不可以)的，也不可以使用使用RedisTemplate，因为它判断不了是Long还是Integer，但是我们之前自己设置过一次Long的处理工具类进行操作


```java
@Resource(name = "redisTemplate")
private  RedisTemplate redisTemplate;

@Resource(name="redisTemplate")
private RedisTemplate<String,Long>  redisTemplateLong;


@GetMapping("LongKey")
public String set(Long id){
    redisTemplate.opsForValue().set(LongKey,id);


    return   stringRedisTemplate.opsForValue().get(KEY).toString();
}



```


## 使用情况


```java
redis 广告的点击次数和流量统计
public class RedisConstants {

    public static final String CACHE_COUNT = "cache.count";
    public static final String CACHE_COUNT_AD = "cache.count.ad";
    public static final String CACHE_COUNT_AD_BLACK = "cache.count.ad.black";
    public static final String CACHE_COUNT_TIME = "cache.count.time";

    public static final String CACHE_AD_GET = "cache.ad.get";
    public static final String CACHE_AD_SHOW = "cache.ad.show";
    public static final String CACHE_AD_CLICK = "cache.ad.click";
    public static final String CACHE_AD_SKIP = "cache.ad.skip";
    public static final String CACHE_AD_JUMP = "cache.ad.jump";
}

```

如果再使用进行查询到json对象的时候需要强制转换，这里提供一个方法


```java
@Component
public class MyObjectMapper extends ObjectMapper {

    private static final long serialVersionUID = 4402127997078513582L;

    public MyObjectMapper() {
        this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}


ZbtGameRoom room = mapper.convertValue(redisWithTemplate.opsForValue().get(CacheKey.USER_ROOM + userId), ZbtGameRoom.class);

```


Encache是后台我们自己用的

​    

如果满意，请打赏博主任意金额，感兴趣的请下方留言吧。可与博主自由讨论哦

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
		id: 'h150mdb20RBuzvTP',
    });
    gitalk.render('gitalk-container');
</script> 

<!-- Gitalk end -->

