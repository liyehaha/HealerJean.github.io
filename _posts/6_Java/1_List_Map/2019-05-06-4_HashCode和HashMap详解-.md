---
title: HashCode和HashMap详解
date: 2019-05-07 03:33:00
tags: 
- SpringBoot
category: 
- SpringBoot
description:  HashCode和HashMap详解
---

<!-- 

https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/blogImages/
　　首行缩进

<font  clalss="healerColor" color="red" size="5" >     </font>

<font  clalss="healerSize"  size="5" >     </font>
-->




## 前言

#### [博主github](https://github.com/HealerJean)
#### [博主个人博客http://blog.healerjean.com](http://HealerJean.github.io)    

[感谢大神HashCode](https://segmentfault.com/a/1190000010799123)

[感谢大神HashMap](https://blog.csdn.net/qq_38182963/article/details/78940047)

### 1、一些常见的HashCode

#### 1.1、Integer

```java

    @Test
    public void Integer_HashCode(){

        Integer one  = new Integer(20);
        System.out.println(one.hashCode()); //20

    }

    /**
     * Integer 的 hashCode 就是它的value
     *
     *     public int hashCode() {
     *         return Integer.hashCode(value);
     *     }
     */
```



#### 1.2、String

```java

@Test
public void String_HashCode(){
    String str1  ="123";
    System.out.println(str1.hashCode()); // 48690

}

/**
 * ASCII http://tool.oschina.net/commons?type=4

* String 类的散列值就是依次遍历其每个字符成员，
* 递归的将当前得到的hashCode乘以31然后加上下一个字符成员的ASCII值 （h = 31 * h + val[i];）
*
*   h 初始为 0
*  '1'  49   h = 31 * 0  + 49 = 49
*  '2'  50   h = 31 * 49 + 50 = 1569
*  '3'  51   h = 31 * 1569 + 51 = 48690   
*
*     public int hashCode() {
*         int h = hash;
*         if (h == 0 && value.length > 0) {
*             char val[] = value;
*
*             for (int i = 0; i < value.length; i++) {
*                 h = 31 * h + val[i];
*             }
*             hash = h;
*         }
*         return h;
*     }
*/

```



### 2、为什么HashCode 会使用31

关于网上的一些解释    

[Why does Java's hashCode() in String use 31 as a multiplier?](https://stackoverflow.com/questions/299304/why-does-javas-hashcode-in-string-use-31-as-a-multiplier)     

排名第一的答案    

```
The value 31 was chosen because it is an odd prime. If it were even and the multiplication overflowed, information would be lost, as multiplication by 2 is equivalent to shifting. The advantage of using a prime is less clear, but it is traditional. A nice property of 31 is that the multiplication can be replaced by a shift and a subtraction for better performance: 31 * i == (i << 5) - i`. Modern VMs do this sort of optimization automatically.


选择数字31是因为它是一个奇质数，，相对来说，如果选择一个偶数会在乘法运算中产生溢出，导致数值信息丢失，因为乘二相当于移位运算。

选择质数的优势并不是特别的明显，但这是一个传统。同时，数字31有一个很好的特性，即乘法运算可以被移位和减法运算取代，来获取更好的性能：31 * i == (i << 5) - i，现代的 Java 虚拟机可以自动的完成这个优化。

（h = 31 * h + val[i];）

```

排名第二的答案     ，后面有可视化验证

```
As Goodrich and Tamassia point out, If you take over 50,000 English words (formed as the union of the word lists provided in two variants of Unix), using the constants 31, 33, 37, 39, and 41 will produce less than 7 collisions in each case. Knowing this, it should come as no surprise that many Java implementations choose one of these constants.

正如 Goodrich 和 Tamassia 指出的那样，如果你对超过 50,000 个英文单词（由两个不同版本的 Unix 字典合并而成）进行 hash code 运算，并使用常数 31, 33, 37, 39 和 41 作为乘子，每个常数算出的哈希值冲突数都小于7个，所以在上面几个常数中，常数 31 被 Java 实现所选用也就不足为奇了。

```



#### 2.1、原因，后面可视化也有更详细的解释

##### 2.1、31是一个不大不小的质数（素数）     

##### 2.2、31可以被 JVM 优化，`31 * i = (i << 5) - i`。现代的 Java 虚拟机可以自动的完成这个优化

 

```

假设 n=3
i=0 -> h = 31 * 0 + val[0]
i=1 -> h = 31 * (31 * 0 + val[0]) + val[1]
i=2 -> h = 31 * (31 * (31 * 0 + val[0]) + val[1]) + val[2]
       h = 31*31*31*0 + 31*31*val[0] + 31*val[1] + val[2]
       h = 31^(n-1)*val[0] + 31^(n-2)*val[1] + val[2]


这里先分析质数2。-------仅计算公式中次数最高的那一项-----------
首先，假设 n = 6，然后把质数2和 n 带入上面的计算公式。结果是2^5 = 32，是不是很小。所以这里可以断定，当字符串长度不是很长时，用质数2做为乘子算出的哈希值，数值不会很大。也就是说，哈希值会分布在一个较小的数值区间内，分布性不佳，最终可能会导致冲突率上升，质数2做为乘子会导致哈希值分布在一个较小区间内

那么如果用一个较大的大质数101会产生什么样的结果呢？根据上面的分析，我想大家应该可以猜出结果了。就是不用再担心哈希值会分布在一个小的区间内了，因为101^5 = 10,510,100,501。但是要注意的是，这个计算结果太大了。如果用 int 类型表示哈希值，结果会溢出，最终导致数值信息丢失。如果不在意质数101容易导致数据信息丢失问题，或许其是一个更好的选择。

尽管数值信息丢失并不一定会导致冲突率上升，但是我们暂且先认为质数101（或者更大的质数）也不是很好的选择。最后，我们再来看看质数31的计算结果： 31^5 = 28629151，结果值相对于32和10,510,100,501来说。是不是很nice，不大不小。


```



#### 2.2、可视化得出结论

计算哈希算法冲突率并不难，比如可以一次性将所有单词的 hash code 算出，并放入 Set 中去除重复值。之后拿单词数减去 set.size() 即可得出冲突数，有了冲突数，冲突率就可以算出来了。当然，如果使用 JDK8 提供的流式计算 API，则可更方便算出，代码片段如下：



```java
public static Integer hashCode(String str, Integer multiplier) {
    int hash = 0;
    for (int i = 0; i < str.length(); i++) {
        hash = multiplier * hash + str.charAt(i);
    }

    return hash;
}
    
/**
 * 计算 hash code 冲突率，顺便分析一下 hash code 最大值和最小值，并输出
 * @param multiplier
 * @param hashs
 */
public static void calculateConflictRate(Integer multiplier, List<Integer> hashs) {
    Comparator<Integer> cp = (x, y) -> x > y ? 1 : (x < y ? -1 : 0);
    int maxHash = hashs.stream().max(cp).get();
    int minHash = hashs.stream().min(cp).get();

    // 计算冲突数及冲突率
    int uniqueHashNum = (int) hashs.stream().distinct().count();
    int conflictNum = hashs.size() - uniqueHashNum;
    double conflictRate = (conflictNum * 1.0) / hashs.size();

    System.out.println(String.format("multiplier=%4d, minHash=%11d, maxHash=%10d, conflictNum=%6d, conflictRate=%.4f%%",
                multiplier, minHash, maxHash, conflictNum, conflictRate * 100));
}
```



![1557195124268](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/blogImages/1557195124268.png)

从上图可以看出， 



简单总结     

1、 质数2、冲突率达到了 55%以上，而且hash值分布不是很广泛，，仅仅分布在了整个哈希空间的正半轴部分，即 0 ~ 2^31-1。而负半轴 -2^31 ~ -1，则无分布。这也证明了我们上面断言，即质数2作为乘子时，对于短字符串，生成的哈希值分布性不佳。

2、奇质数，101、109 表现也不错，冲突率很低，说明了**哈希值溢出不一定导致冲突率比较高**，但是溢出的话，我们不认为是我们的优选乘子    ,如果不在意质数101容易导致数据信息丢失问题，或许其是一个更好的选择。

3、偶数 32、36 这两个并不好，32的冲突率已经超过了50%,尽管36表现好一些，但是和31,37相比，冲突率还是比较高的，但是**偶数也不一定作为乘子冲突率就比较高 **    

4、**奇质数 31、37 、41 表现都不出，冲突数都小于7个，使用较小的质数做为乘子时，冲突率会很高。   选择比较大的质数作为乘子时，冲突率会降低，但是可能会再次哈希值溢出**



#### 2.3、哈希值分布可视化

上面的2.2介绍了不同数字作为乘子的冲突率情况，下面分析一下不同数字作为乘子时，hash值得分布情况

[https://segmentfault.com/a/1190000010799123](https://segmentfault.com/a/1190000010799123)



### 3、HashCode使用

#### 3.1、HashCode定义（确定位置，但不能确定地址）

 

从Object角度看，JVM每new一个Object，它都会将这个Object丢到一个Hash表中去，这样的话，下次做Object的比较或者取这个对象的时候（读取过程），它会根据对象的HashCode再从Hash表中取这个对象。这样做的目的是提高取对象的效率。若HashCode相同再去调用equal。      



（1）HashCode的存在主要是用于查找的快捷性，如Hashtable，HashMap等，HashCode是用来在散列存储结构中确定对象的存储地址的；    

（2）如果两个对象相同， equals方法一定返回true，并且这两个对象的HashCode一定相同；除非重写了方法    

（3）如果对象的equals方法被重写，那么对象的HashCode也尽量重写，并且产生HashCode使用的对象，一定要和equals方法中使用的一致，否则就会违反上面提到的第2点；      

（4）两个对象的HashCode相同，并不一定表示两个对象就相同，也就是equals方法不一定返回true，只能够说明这两个对象在散列存储结构中，如Hashtable，他们存放在同一个篮子里。

 

####  3.2、HashCode作用



Java中的集合（Collection）有两类，一类是List，再有一类是Set。前者集合内的元素是有序的，元素可以重复；后者元素无序，但元素不可重复。 equals方法可用于保证元素不重复，但是，如果每增加一个元素就检查一次，如果集合中现在已经有1000个元素，那么第1001个元素加入集合时，就要调用1000次equals方法。这显然会大大降低效率。      

于是，Java采用了哈希表的原理。    



 哈希算法也称为散列算法，是将数据依特定算法直接指定到一个地址上。这样一来，当集合要添加新的元素时，先调用这个元素的HashCode方法，就一下子能定位到它应该放置的物理位置上。     

（1）如果这个位置上没有元素，它就可以直接存储在这个位置上，不用再进行任何比较了；     

（2）如果这个位置上已经有元素了，就调用它的equals方法与新元素进行比较，相同的话就不存了；     

（3）不相同的话，也就是发生了Hash key相同导致冲突的情况，那么就在这个Hash key的地方产生一个链表，将所有产生相同HashCode的对象放到这个单链表上去，串在一起（很少出现）。这样一来实际调用equals方法的次数就大大降低了，几乎只需要一两次。 （下面1、的实例就为这里的测试实例）

 

####  3.3、HashCode是用于查找使用的，而equals是用于比较两个对象的是否相等的。

（1）例如内存中有这样的位置 ：0  1  2  3  4  5  6  7        

而我有个类，这个类有个字段叫ID，我要把这个类存放在以上8个位置之一，如果不用HashCode而任意存放，那么当查找时就需要到这八个位置里挨个去找，或者用二分法一类的算法。       

但如果用HashCode那就会使效率提高很多。  定义我们的HashCode为ID％8，比如我们的ID为9，9除8的余数为1，那么我们就把该类存在1这个位置，如果ID是13，求得的余数是5，那么我们就把该类放在5这个位置。依此类推。       

（2）但是如果两个类有相同的HashCode，例如9除以8和17除以8的余数都是1，也就是说，我们先通过 HashCode来判断两个类是否存放某个桶里，但这个桶里可能有很多类，比如hashtable，那么我们就需要再通过 equals 在这个桶里找到我们要的类。  



#### 3.4、Set测试

##### 3.4.1、实例重写HashCode方法



```java

public class HashTest {    
    private int i;    
    
    public int getI() {    
        return i;    
    }    
    
    public void setI(int i) {    
        this.i = i;    
}   
 
    @Override
    public int hashCode() {    
        return i % 10;    
}
    
/**
*  对象的内存地址与hashcode有关系，但并不是hashcode值相等，就是代表内存地址相同，这种想法是幼稚的
*  比如hashtable中hashcode值相等，
*  	但是存了很多的对象，这表明对象的== 肯定不相等，Ojbect逆向推理，equals不相等，==肯定不相等 
*  
*/

    public final static void main(String[] args) {    
        HashTest a = new HashTest();    
        HashTest b = new HashTest();  

        System.out.println(a.hashCode() == b.hashCode());  //true 人为制造hashcode值相同  
        System.out.println(a==b);    //false //== 比较对象的相等比较对象引用地址是否相等。还要要比较对象内容是否相等
        System.out.println(a.equals(b));    //false 不同的对象 object中 == 和euqals是一样的

        a.setI(1);    
        b.setI(1);    
        Set<HashTest> set = new HashSet<HashTest>();    
        set.add(a);    
        set.add(b);    
        //没有 equels 重写的情况
        System.out.println(a.hashCode() == b.hashCode());  //true hashcode相同   

        System.out.println(a.equals(b));    //false 不同的对象 ，创建出来的是地址就不同了

        //2 这个时候会发想存入了两个值  set中存放是根据hashcode值存放，如果hashcode值相同，
        //再比较equals值，如果equals值也相同，则产生一个单链表放进去
        System.out.println(set.size());    

    }    

```

![1557200814335](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/blogImages/1557200814335.png)





##### 3.4.2、重写equels方法

```java

public class HashTest {    
    private int i;    
    
    public int getI() {    
        return i;    
    }    
    
    public void setI(int i) {    
        this.i = i;    
    }    
    
    @Override
    public boolean equals(Object object) {    
        if (object == null) {    
            return false;    
        }    
        if (object == this) {    
            return true;    
        }    
        if (!(object instanceof HashTest)) {    
            return false;    
        }    
        HashTest other = (HashTest) object;    
        if (other.getI() == this.getI()) {    
            return true;    
        }    
        return false;    
    }  
    
    @Override
    public int hashCode() {    
        return i % 10;    
    }    
     public final static void main(String[] args) {    
        HashTest a = new HashTest();    
        HashTest b = new HashTest();  
        
        System.out.println(a.hashCode() == b.hashCode());         
        System.out.println(a==b);           
        System.out.println(a.equals(b));   

        a.setI(1);    
        b.setI(1);    
        Set<HashTest> set = new HashSet<HashTest>();    
        set.add(a);    
        set.add(b);    
       
        System.out.println(a.hashCode() == b.hashCode());          
        System.out.println(a.equals(b));   
        
        System.out.println(set.size());    
        
    }    
}    
}

```

![1557200890482](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/blogImages/1557200890482.png)







##### 3.5、map测试

```java
package com.hlj.util.Z009HashCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description
 * @Author HealerJean
 * @Date 2018/4/12
 */
public class D02_Object {

    public static void main(String[] args) {
        Map <Object,Object> map = new HashMap<>();

        Object o = new Object();
        map.put(o,"Stromg");


        Object o2 = new Object();
        map.put(o2,3);


        System.out.println(map.get(o)); //Stromg
        System.out.println(map.get(o2)); //3

        o = 456;
        System.out.println(map.get(o)); //null  因为提供的key的hashcode的地址不是我们存储的地址，所以不能找到，但是之前存储的还在


        Person person = new Person();
        person.setAge(1);
        map.put(person,3);
        System.out.println(map.get(person)); //3

        person.setName("HealerJean");
        System.out.println(map.get(person)); //3
        System.out.println("map的大小"+map.size()); //map的大小3

        person = null ; // person设置为null， map中还是具有该person对象
        System.out.println(map.get(person));  //null
        System.out.println("map的大小"+map.size()); //map的大小3

        System.out.println("打印map的结果集");
        Object[] list = map.keySet().toArray();
        for (Object object :list){
            System.out.println("key="+object.toString()+"||||| value="+map.get(object).toString());
        }


    }


}

class Person {
    int age;
    String name;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "age=" + age +
                ", name='" + name + '\'' +
                '}';
    }
}


打印结果

/**
* Stromg
* 3
* null
* 3
* 3
* map的大小3
* null
* map的大小3
* 打印map的结果集
* key=java.lang.Object@9807454||||| value=3
* key=Person{age=1, name='HealerJean'}||||| value=3
* key=java.lang.Object@4fca772d||||| value=Stromg
*/


```





## 5、HashMap源码分析


https://blog.csdn.net/v123411739/article/details/78996181



### 5.1、类结构

 

![1570783709850](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/blogImages/1570783709850.png)



```java

public class HashMap<K,V> extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable {


```





![1571037240033](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/blogImages/1571037240033.png)





```java

内部类

// 链表节点, 继承自Entry
static class Node<K,V> implements Map.Entry<K,V> {  
    final int hash;
    final K key;
    V value;
    Node<K,V> next;
 
    // ... ...
}

```






![1571037201880](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/blogImages/1571037201880.png)



```java
内部类

// 红黑树节点
static final class TreeNode<K,V> extends LinkedHashMap.Entry<K,V> {
    TreeNode<K,V> parent;  // red-black tree links
    TreeNode<K,V> left;
    TreeNode<K,V> right;
    TreeNode<K,V> prev;    // needed to unlink next upon deletion
    boolean red;
   
    // ...
    
    
    
    
LinkedHashMap.Entry 在LinkedHashMap 类中

  /**
     * HashMap.Node subclass for normal LinkedHashMap entries.
     */
    static class Entry<K,V> extends HashMap.Node<K,V> {
        Entry<K,V> before, after;
        Entry(int hash, K key, V value, Node<K,V> next) {
            super(hash, key, value, next);
        }
    }

 static class Node<K,V> implements Map.Entry<K,V> {

```



#### 5.1.1、类注意点

（1）允许NULL值，NULL键    

（2）扩容的阀值threshold，当键值对的数量超过这个阀值会产生扩容， Hash数组 每次会扩容长度为以前的2倍    

（3）**不要轻易改变负载因子，负载因子过高（不会轻易扩容）会导致链表过长，查找键值对时间复杂度就会增高，负载因子过低（一下子就会扩容了）会导致hash桶的 数量过多，空间复杂度会增高**   

（4）HashMap是多线程不安全    



```

JDK1.7进行多线程put操作，之后遍历，直接死循环，CPU飙到100%，
JDK1.8中进行多线程操作会出现节点和value值丢失    


为什么JDK1.7与JDK1.8多线程操作会出现很大不同，是因为JDK 1.8的作者对resize方法进行了优化不会产生链表闭环。

在JDK1.8前，在多线程的情况下，使用HashMap进行put操作会造成死循环。这是因为多次put操作会引发HashMap的扩容机制，HashMap的扩容机制采用头插法的方式移动元素，这样会造成链表闭环，形成死循环。JDK1.8中HashMap使用高低位来平移元素，这样保证效率的同时避免了多线程情况下扩容造成死循环的问题。

```

（5）尽量设置HashMap的初始容量，尤其在数据量大的时候，防止多次resize





#### 5.1.2、类常亮

```java
//默认hash桶数组初始长度16
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; 

//默认负载因子 0.75
static final float DEFAULT_LOAD_FACTOR = 0.75f;

//hash桶最大容量2的30次幂
static final int MAXIMUM_CAPACITY = 1 << 30;

//只有桶的数量  大于 64 才会发生链表树化。64在承受范围之内，不需要转化，因为转化是会小号时间的
static final int MIN_TREEIFY_CAPACITY = 64;

//链表长度大于 8 时，并且桶的数量大于等于MIN_TREEIFY_CAPACITY 64时，链表树化 
static final int TREEIFY_THRESHOLD = 8;

//在哈希表扩容时,如果发现链表长度小于6,则会由树重新退化为链表
static final int UNTREEIFY_THRESHOLD = 6;

```



#### 5.1.3、实例变量




```java
//hash桶
transient Node<K,V>[] table;                         

//键值对的数量
transient int size;

//负载因子
final float loadFactor;

//HashMap结构修改的次数
transient int modCount;

//扩容的阀值，当键值对的数量超过这个阀值会产生扩容 （构造器的时候，它是桶的数量），Node 数组是在第一次 put 时才会进行初始化，初始化时会将此时的 threshold 值作为新表的 capacity 值，然后用 capacity 和 loadFactor 计算新表的真正 threshold 值
int threshold;

```



#### 5.1.4、4个构造函数



+ 全部使用默认值


```java
public HashMap() {
    this.loadFactor = DEFAULT_LOAD_FACTOR;  
}

```



+ 指定初始化桶数组的大小

```java
public HashMap(int initialCapacity) {
    this(initialCapacity, DEFAULT_LOAD_FACTOR);
}
```



+  指定初始化桶数组的大小 和 负载因子，这个负载因子不好控制，不建议使用


```java

public HashMap(int initialCapacity, float loadFactor) {                                                                   
    if (initialCapacity < 0)
        throw new IllegalArgumentException("Illegal initial capacity: " +
                                           initialCapacity);
    if (initialCapacity > MAXIMUM_CAPACITY)
        initialCapacity = MAXIMUM_CAPACITY;
    if (loadFactor <= 0 || Float.isNaN(loadFactor))
        throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
    this.loadFactor = loadFactor;
    //下面介绍一下这行代码的作用
    this.threshold = tableSizeFor(initialCapacity);
}
```

 

+ 直接放入一个已经存在的map

```java

public HashMap(Map<? extends K, ? extends V> m) {
    this.loadFactor = DEFAULT_LOAD_FACTOR;
    putMapEntries(m, false);
}
```



hash桶没有在构造函数中初始化，`tableSizeFor(initialCapacity)`方法，这个方法的作用是，将你传入的`initialCapacity`做计算，返回一个大于等于`initialCapacity` 最小的2的幂次方。这个暂时作为桶的初始化数量，， 在第一次存储键值对的对桶进行初始化的时候回使用。



##### 1、tableSizeFor   确定桶的数量

但是还没有初始化桶，只有在put存储键值对的时候才回进行桶的初始化



```java
//16  => 16

static final int tableSizeFor(int cap) {                                                                      
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
```



### 5.2、put方法



```java

public V put(K key, V value) {
    return putVal(hash(key), key, value, false, true);
}
 

// Node<K,V> p  确定的索引位置的键值对
// Node<K,V> e  新插入的键值对
// K k          索引位置的键值对的key
// int n        桶的长度 
// int n, i      数组索引的位置
final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
               boolean evict) {
    Node<K,V>[] tab; Node<K,V> p; int n, i;
    // 1.校验table是否为空或者length等于0，如果是则调用resize方法进行初始化
    if ((tab = table) == null || (n = tab.length) == 0)
        n = (tab = resize()).length;
    // 2.通过hash值计算索引位置，将该索引位置的头节点赋值给p，如果p为空则直接在该索引位置新增一个节点即可
    if ((p = tab[i = (n - 1) & hash]) == null)
        tab[i] = newNode(hash, key, value, null);
    else {
        // table表该索引位置不为空，则进行查找
        Node<K,V> e; K k;
        // 3.判断p节点的key和hash值是否跟传入的相等，如果相等, 则p节点即为要查找的目标节点，将p节点赋值给e节点
        if (p.hash == hash && ((k = p.key) == key || (key != null && key.equals(k))))
            e = p;
        // 4.判断p节点是否为TreeNode, 如果是则调用红黑树的putTreeVal方法查找目标节点
        else if (p instanceof TreeNode)
            e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
        else {
            // 5.走到这代表p节点为普通链表节点，则调用普通的链表方法进行查找，使用binCount统计链表的节点数
            for (int binCount = 0; ; ++binCount) {
                // 6.如果p的next节点为空时，则代表找不到目标节点，则新增一个节点并插入链表尾部
                if ((e = p.next) == null) {
                    p.next = newNode(hash, key, value, null);
                    // 7.当链表的长度大于等于树化阀值8，并且hash桶的长度大于等于MIN_TREEIFY_CAPACITY，如果超过则调用treeifyBin方法将链表节点转为红黑树节点，
                    // 减一是因为循环是从p节点的下一个节点开始的
                    if (binCount >= TREEIFY_THRESHOLD - 1)
                        treeifyBin(tab, hash);
                    break;
                }
                // 8.如果e节点存在hash值和key值都与传入的相同，则e节点即为目标节点，跳出循环
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    break;
                p = e;  // 将p指向下一个节点，遍历连表
            }
        }
        // 9.如果e节点不为空，则代表目标节点存在，使用传入的value覆盖该节点的value，并返回oldValue
        if (e != null) {
            V oldValue = e.value;
            if (!onlyIfAbsent || oldValue == null)
                e.value = value;
            afterNodeAccess(e); // 用于LinkedHashMap
            return oldValue;
        }
    }
    ++modCount;
    // 10.如果插入节点后节点数超过阈值，则调用resize方法进行扩容
    if (++size > threshold)
        resize();
    afterNodeInsertion(evict);  // 用于LinkedHashMap
    return null;
}
```









#### 5.2.1、put--已经存返回旧的数据，添加新数据返回NULL



```java
map.put("a","111");
System.out.println(map.put("a" , "aaa"));//111
System.out.println(map.put("b" , "bbb"));//NULL

```



#### 5.2.2、putTreeVal，红黑树放入数据



红黑树put一个新元素时  

1.如果该元素键的hash值小于当前节点的hash值的时候，就会作为当前节点的左节点；    

2、hash值大于当前节点hash值得时候作为当前节点的右节点。   

3、那么hash值相同的时候呢？   

这时还是会先尝试看是否能够通过Comparable进行比较一下，要想看看是否能基于Comparable进行比较的话，首先要看该元素键是否实现了Comparable接口，此时就需要用到comparableClassFor方法来获取该元素键的Class。

```java
@Data
public class People  implements Comparable<People>{
    private String username;
    private String password;
    private Integer id;
    private String name;

    @Override
    public int compareTo(People p) {
        return id - p.getId();
    }
}
```



```java

/**
 * 红黑树的put操作，红黑树插入会同时维护原来的链表属性, 即原来的next属性
 */
final TreeNode<K,V> putTreeVal(HashMap<K,V> map, Node<K,V>[] tab,
                               int h, K k, V v) {
    Class<?> kc = null;
    boolean searched = false;
    
    // 1.查找根节点, 索引位置的头节点并不一定为红黑树的根节点
    TreeNode<K,V> root = (parent != null) ? root() : this;
    
    // 2.将根节点赋值给p节点，开始进行查找
    for (TreeNode<K,V> p = root;;) {
        int dir, ph; K pk;
        
        // 3.如果传入的hash值小于p节点的hash值，将dir赋值为-1，代表向p的左边查找树
        if ((ph = p.hash) > h)
            dir = -1;
        
        // 4.如果传入的hash值大于p节点的hash值， 将dir赋值为1，代表向p的右边查找树
        else if (ph < h)
            dir = 1;
        
        // 5.如果传入的hash值和key值等于p节点的hash值和key值, 则p节点即为目标节点, 返回p节点
        else if ((pk = p.key) == k || (k != null && k.equals(pk)))
            return p;
        
        // 6.如果k所属的类没有实现Comparable接口 或者实现了该接口 k和p节点的key相等
        else if ((kc == null && (kc = comparableClassFor(k)) == null) ||
                 (dir = compareComparables(kc, k, pk)) == 0) {
            
       // 6.1 第一次符合条件, 从p节点的左节点和右节点分别调用find方法进行查找, 如果查找到目标节点则返回，找不到则定义一套规则来比较大小，决定左右子树
            if (!searched) {
                TreeNode<K,V> q, ch;
                searched = true;
                if (((ch = p.left) != null &&
                     (q = ch.find(h, k, kc)) != null) ||
                    ((ch = p.right) != null &&
                     (q = ch.find(h, k, kc)) != null))
                    return q;
            }
            
       // 6.2 否则使用定义的一套规则来比较k和p节点的key的大小, 用来决定向左还是向右放入数据
            dir = tieBreakOrder(k, pk); // dir<0则代表k<pk，则向p左边查找；反之亦然
        }
 
        TreeNode<K,V> xp = p;   // xp赋值为x的父节点,中间变量,用于下面给x的父节点赋值
        
        // 7.dir<=0则向p左边查找,否则向p右边查找,如果为null,则代表该位置即为x的目标位置
        if ((p = (dir <= 0) ? p.left : p.right) == null) {
            // 走进来代表已经找到x的位置，只需将x放到该位置即可
            Node<K,V> xpn = xp.next;    // xp的next节点
            
            // 8.创建新的节点, 其中x的next节点为xpn, 即将x节点插入xp与xpn之间
            TreeNode<K,V> x = map.newTreeNode(h, k, v, xpn);
            
            // 9.调整x、xp、xpn之间的属性关系
            if (dir <= 0)   // 如果时dir <= 0, 则代表x节点为xp的左节点
                xp.left = x;
            else        // 如果时dir> 0, 则代表x节点为xp的右节点
                xp.right = x;
            xp.next = x;    // 将xp的next节点设置为x
            x.parent = x.prev = xp; // 将x的parent和prev节点设置为xp
            // 如果xpn不为空,则将xpn的prev节点设置为x节点,与上文的x节点的next节点对应
            if (xpn != null)
                ((TreeNode<K,V>)xpn).prev = x;
            
            // 10.进行红黑树的插入平衡调整
            moveRootToFront(tab, balanceInsertion(root, x));
            return null;
        }
    }
}

```



#### 5.2.3、comparableClassFor 判断是否实现了Comparable

```java

/**
* 如果对象x的类是C，如果C实现了Comparable<C>接口，那么返回C，否则返回null
*/
static Class<?> comparableClassFor(Object x) {
    if (x instanceof Comparable) {
        Class<?> c; Type[] ts, as; Type t; ParameterizedType p;
        if ((c = x.getClass()) == String.class) // 如果x是个字符串对象
            return c; // 返回String.class
        /*
         * 为什么如果x是个字符串就直接返回c了呢 ? 因为String  实现了 Comparable 接口，可参考如下String类的定义
         * public final class String implements java.io.Serializable, Comparable<String>, CharSequence
         */ 
 
        // 如果 c 不是字符串类，获取c直接实现的接口（如果是泛型接口则附带泛型信息）    
        if ((ts = c.getGenericInterfaces()) != null) {
            for (int i = 0; i < ts.length; ++i) { // 遍历接口数组
                // 如果当前接口t是个泛型接口 
                // 如果该泛型接口t的原始类型p 是 Comparable 接口
                // 如果该Comparable接口p只定义了一个泛型参数
                // 如果这一个泛型参数的类型就是c，那么返回c
                if (((t = ts[i]) instanceof ParameterizedType) &&
                    ((p = (ParameterizedType)t).getRawType() ==
                        Comparable.class) &&
                    (as = p.getActualTypeArguments()) != null &&
                    as.length == 1 && as[0] == c) // type arg is c
                    return c;
            }
            // 上面for循环的目的就是为了看看x的class是否 implements  Comparable<x的class>
        }
    }
    return null; // 如果c并没有实现 Comparable<c> 那么返回空


```



#### 5.2.4、compareComparables 比较是否相等



```java
/**
* 如果x所属的类是kc，返回k.compareTo(x)的比较结果
* 如果x为空，或者其所属的类不是kc，返回0
*/
@SuppressWarnings({"rawtypes","unchecked"}) // for cast to Comparable
static int compareComparables(Class<?> kc, Object k, Object x) {
    return (x == null || x.getClass() != kc ? 0 :
            ((Comparable)k).compareTo(x));
}

```



#### 5.2.5、tieBreakOrder 使用定义的一套规则来比较 k 和 p 节点的 key 的大小，用来决定向左还是向右查找

```java
// 用于不可比较或者hashCode相同时进行比较的方法, 只是一个一致的插入规则，用来维护重定位的等价性。
static int tieBreakOrder(Object a, Object b) {  
    int d;
    if (a == null || b == null ||
        (d = a.getClass().getName().
         compareTo(b.getClass().getName())) == 0)
        d = (System.identityHashCode(a) <= System.identityHashCode(b) ?
             -1 : 1);
    return d;
}

```





#### 5.2.6、treeifyBin 链表转化为红黑树

```java

/**
 * 将链表节点转为红黑树节点
 */
final void treeifyBin(Node<K,V>[] tab, int hash) {
    int n, index; Node<K,V> e;
    // 1.如果table为空或者table的长度小于64, 调用resize方法进行扩容
    if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
        resize();
    // 2.根据hash值计算索引值，将该索引位置的节点赋值给e，从e开始遍历该索引位置的链表
    else if ((e = tab[index = (n - 1) & hash]) != null) {
        TreeNode<K,V> hd = null, tl = null;
        do {
            // 3.将链表节点转红黑树节点
            TreeNode<K,V> p = replacementTreeNode(e, null);
            // 4.如果是第一次遍历，将头节点赋值给hd
            if (tl == null)	// tl为空代表为第一次循环
                hd = p;
            else {
                // 5.如果不是第一次遍历，则处理当前节点的prev属性和上一个节点的next属性
                p.prev = tl;    // 当前节点的prev属性设为上一个节点
                tl.next = p;    // 上一个节点的next属性设置为当前节点
            }
            // 6.将p节点赋值给tl，用于在下一次循环中作为上一个节点进行一些链表的关联操作（p.prev = tl 和 tl.next = p）
            tl = p;
        } while ((e = e.next) != null);
        // 7.将table该索引位置赋值为新转的TreeNode的头节点，如果该节点不为空，则以以头节点(hd)为根节点, 构建红黑树
        if ((tab[index] = hd) != null)
            hd.treeify(tab);
    }
}

```





#### 5.2.7、treeify 构建红黑树

```java
/**
 * 构建红黑树
 */
final void treeify(Node<K,V>[] tab) {
    TreeNode<K,V> root = null;
    // 1.将调用此方法的节点赋值给x，以x作为起点，开始进行遍历
    for (TreeNode<K,V> x = this, next; x != null; x = next) {
        next = (TreeNode<K,V>)x.next;   // next赋值为x的下个节点
        x.left = x.right = null;    // 将x的左右节点设置为空
        // 2.如果还没有根节点, 则将x设置为根节点
        if (root == null) {
            x.parent = null;    // 根节点没有父节点
            x.red = false;  // 根节点必须为黑色
            root = x;   // 将x设置为根节点
        }
        else {
            K k = x.key;	// k赋值为x的key
            int h = x.hash;	// h赋值为x的hash值
            Class<?> kc = null;
            // 3.如果当前节点x不是根节点, 则从根节点开始查找属于该节点的位置
            for (TreeNode<K,V> p = root;;) {
                int dir, ph;
                K pk = p.key;
                // 4.如果x节点的hash值小于p节点的hash值，则将dir赋值为-1, 代表向p的左边查找
                if ((ph = p.hash) > h)
                    dir = -1;
                // 5.如果x节点的hash值大于p节点的hash值，则将dir赋值为1, 代表向p的右边查找
                else if (ph < h)
                    dir = 1;
                // 6.走到这代表x的hash值和p的hash值相等，则比较key值
                else if ((kc == null && // 6.1 如果k没有实现Comparable接口 或者 x节点的key和p节点的key相等
                          (kc = comparableClassFor(k)) == null) ||
                         (dir = compareComparables(kc, k, pk)) == 0)
                    // 6.2 使用定义的一套规则来比较x节点和p节点的大小，用来决定向左还是向右查找
                    dir = tieBreakOrder(k, pk);
 
                TreeNode<K,V> xp = p;   // xp赋值为x的父节点,中间变量用于下面给x的父节点赋值
                // 7.dir<=0则向p左边查找,否则向p右边查找,如果为null,则代表该位置即为x的目标位置
                if ((p = (dir <= 0) ? p.left : p.right) == null) {
                    // 8.x和xp节点的属性设置
                    x.parent = xp;  // x的父节点即为最后一次遍历的p节点
                    if (dir <= 0)   // 如果时dir <= 0, 则代表x节点为父节点的左节点
                        xp.left = x;
                    else    // 如果时dir > 0, 则代表x节点为父节点的右节点
                        xp.right = x;
                    // 9.进行红黑树的插入平衡(通过左旋、右旋和改变节点颜色来保证当前树符合红黑树的要求)
                    root = balanceInsertion(root, x);
                    break;
                }
            }
        }
    }
    // 10.如果root节点不在table索引位置的头节点, 则将其调整为头节点
    moveRootToFront(tab, root);
}

```



#### 5.2.8、**moveRootToFront** 红黑树平衡调整

```java
/**
 * 将root放到头节点的位置
 * 如果当前索引位置的头节点不是root节点, 则将root的上一个节点和下一个节点进行关联,
 * 将root放到头节点的位置, 原头节点放在root的next节点上
 */
static <K,V> void moveRootToFront(Node<K,V>[] tab, TreeNode<K,V> root) {
    int n;
    // 1.校验root是否为空、table是否为空、table的length是否大于0
    if (root != null && tab != null && (n = tab.length) > 0) {
        // 2.计算root节点的索引位置
        int index = (n - 1) & root.hash;
        TreeNode<K,V> first = (TreeNode<K,V>)tab[index];
        // 3.如果该索引位置的头节点不是root节点，则该索引位置的头节点替换为root节点
        if (root != first) {
            Node<K,V> rn;
            // 3.1 将该索引位置的头节点赋值为root节点
            tab[index] = root;
            TreeNode<K,V> rp = root.prev;   // root节点的上一个节点
            // 3.2 和 3.3 两个操作是移除root节点的过程
            // 3.2 如果root节点的next节点不为空，则将root节点的next节点的prev属性设置为root节点的prev节点
            if ((rn = root.next) != null)
                ((TreeNode<K,V>)rn).prev = rp;
            // 3.3 如果root节点的prev节点不为空，则将root节点的prev节点的next属性设置为root节点的next节点
            if (rp != null)
                rp.next = rn;
            // 3.4 和 3.5 两个操作将first节点接到root节点后面
            // 3.4 如果原头节点不为空, 则将原头节点的prev属性设置为root节点
            if (first != null)
                first.prev = root;
            // 3.5 将root节点的next属性设置为原头节点
            root.next = first;
            // 3.6 root此时已经被放到该位置的头节点位置，因此将prev属性设为空
            root.prev = null;
        }
        // 4.检查树是否正常
        assert checkInvariants(root);
    }
}

```



#### 5.2.9、**checkInvariants** 检查树是否正常

```java
/**
 * Recursive invariant check
 */
static <K,V> boolean checkInvariants(TreeNode<K,V> t) { // 一些基本的校验
    TreeNode<K,V> tp = t.parent, tl = t.left, tr = t.right,
        tb = t.prev, tn = (TreeNode<K,V>)t.next;
    if (tb != null && tb.next != t)
        return false;
    if (tn != null && tn.prev != t)
        return false;
    if (tp != null && t != tp.left && t != tp.right)
        return false;
    if (tl != null && (tl.parent != t || tl.hash > t.hash))
        return false;
    if (tr != null && (tr.parent != t || tr.hash < t.hash))
        return false;
    if (t.red && tl != null && tl.red && tr != null && tr.red)  // 如果当前节点为红色, 则该节点的左右节点都不能为红色
        return false;
    if (tl != null && !checkInvariants(tl))
        return false;
    if (tr != null && !checkInvariants(tr))
        return false;
    return true;
}

```









### 5.3、resize方法



```java
final Node<K,V>[] resize() {
    Node<K,V>[] oldTab = table;
    int oldCap = (oldTab == null) ? 0 : oldTab.length;
    int oldThr = threshold;
    int newCap, newThr = 0;
    // 1.如果旧hash桶不为空
    if (oldCap > 0) {
        
// 1.1 判断hash桶是否超过最大容量值：如果超过则将阈值设置为Integer.MAX_VALUE(2的30次幂)，并直接返回老表,
        if (oldCap >= MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return oldTab;
        }
        // 1.2 将新的桶长度赋值为旧桶长度的2倍，
         //这时新的桶长度<最大容量Integer.MAX_VALUE(2的30次幂) ，并且旧桶长度>=初始的桶长度DEFAULT_INITIAL_CAPACITY（16）, 则将新阈值设置为原来的两倍
        else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                 oldCap >= DEFAULT_INITIAL_CAPACITY)
            newThr = oldThr << 1; // double threshold
    }
    // 2.如果旧桶为空, 旧桶的阈值大于0, 是因为HashMap初始构造器的时候放入了阈值，则将新表的容量设置为老表的阈值
    else if (oldThr > 0)
        newCap = oldThr;
    else {
        //3.老表的容量为0, 老表的阈值为0，这种情况是HashMap初始构造器没有传初始容量的new方法创建的空表，将阈值和容量设置为默认值 ，新的阀值  = 容量*负载因子
        newCap = DEFAULT_INITIAL_CAPACITY;
        newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
    }
    
    // 4.如果新表的阈值为0, 则通过新的容量*负载因子获得阈值
    if (newThr == 0) {
        float ft = (float)newCap * loadFactor;
        newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                  (int)ft : Integer.MAX_VALUE);
    }
    
    // 5.将当前阈值设置为刚计算出来的新的阈值，定义新表，容量为刚计算出来的新容量，将table设置为新定义的表。
    threshold = newThr;
    @SuppressWarnings({"rawtypes","unchecked"})
    Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
    table = newTab;
    // 6.如果老表不为空，则需遍历所有节点，将节点赋值给新表
    if (oldTab != null) {
        for (int j = 0; j < oldCap; ++j) {
            Node<K,V> e;
            if ((e = oldTab[j]) != null) {  // 将索引值为j的老表头节点赋值给e
                oldTab[j] = null; // 将老表的节点设置为空, 以便垃圾收集器回收空间
                
      // 7.如果e.next为空, 则代表老表的该位置只有1个节点，计算新表的索引位置, 直接将该节点放在该位置
                if (e.next == null)
                    newTab[e.hash & (newCap - 1)] = e;
                
                // 8.如果是红黑树节点，则进行红黑树的重hash分布(跟链表的hash分布基本相同)
                else if (e instanceof TreeNode)
                    ((TreeNode<K,V>)e).split(this, newTab, j, oldCap)
                    ;
                else { // preserve order
                    // 9.如果是普通的链表节点，则进行普通的重hash分布
                    Node<K,V> loHead = null, loTail = null; // 存储索引位置为:“原索引位置”的节点
                    Node<K,V> hiHead = null, hiTail = null; // 存储索引位置为:“原索引位置+oldCap”的节点
                    Node<K,V> next;
                    do {
                        next = e.next;
                        // 9.1 如果e的hash值与老表的容量进行与运算为0,则扩容后的索引位置跟老表的索引位置一样
                        if ((e.hash & oldCap) == 0) {
                            if (loTail == null) // 如果loTail为空, 代表该节点为第一个节点
                                loHead = e; // 则将loHead赋值为第一个节点
                            else
                                loTail.next = e;    // 否则将节点添加在loTail后面
                            loTail = e; // 并将loTail赋值为新增的节点
                        }
                        // 9.2 如果e的hash值与老表的容量进行与运算为1,则扩容后的索引位置为:老表的索引位置＋oldCap
                        else {
                            if (hiTail == null) // 如果hiTail为空, 代表该节点为第一个节点
                                hiHead = e; // 则将hiHead赋值为第一个节点
                            else
                                hiTail.next = e;    // 否则将节点添加在hiTail后面
                            hiTail = e; // 并将hiTail赋值为新增的节点
                        }
                    } while ((e = next) != null);
                    // 10.如果loTail不为空（说明老表的数据有分布到新表上“原索引位置”的节点），则将最后一个节点
                    // 的next设为空，并将新表上索引位置为“原索引位置”的节点设置为对应的头节点
                    if (loTail != null) {
                        loTail.next = null;
                        newTab[j] = loHead;
                    }
                    // 11.如果hiTail不为空（说明老表的数据有分布到新表上“原索引+oldCap位置”的节点），则将最后
                    // 一个节点的next设为空，并将新表上索引位置为“原索引+oldCap”的节点设置为对应的头节点
                    if (hiTail != null) {
                        hiTail.next = null;
                        newTab[j + oldCap] = hiHead;
                    }
                }
            }
        }
    }
    // 12.返回新表
    return newTab;

```



#### 5.3.1、红黑树操作 split



```java
/**
 * 扩容后，红黑树的hash分布，只可能存在于两个位置：原索引位置、原索引位置+oldCap
 */
final void split(HashMap<K,V> map, Node<K,V>[] tab, int index, int bit) {
    TreeNode<K,V> b = this;	// 拿到调用此方法的节点
    TreeNode<K,V> loHead = null, loTail = null; // 存储索引位置为:“原索引位置”的节点
    TreeNode<K,V> hiHead = null, hiTail = null; // 存储索引位置为:“原索引+oldCap”的节点
    int lc = 0, hc = 0;
    // 1.以调用此方法的节点开始，遍历整个红黑树节点
    for (TreeNode<K,V> e = b, next; e != null; e = next) {	// 从b节点开始遍历
        next = (TreeNode<K,V>)e.next;   // next赋值为e的下个节点
        e.next = null;  // 同时将老表的节点设置为空，以便垃圾收集器回收
        // 2.如果e的hash值与老表的容量进行与运算为0,则扩容后的索引位置跟老表的索引位置一样
        if ((e.hash & bit) == 0) {
            if ((e.prev = loTail) == null)  // 如果loTail为空, 代表该节点为第一个节点
                loHead = e; // 则将loHead赋值为第一个节点
            else
                loTail.next = e;    // 否则将节点添加在loTail后面
            loTail = e; // 并将loTail赋值为新增的节点
            ++lc;   // 统计原索引位置的节点个数
        }
        // 3.如果e的hash值与老表的容量进行与运算为1,则扩容后的索引位置为:老表的索引位置＋oldCap
        else {
            if ((e.prev = hiTail) == null)  // 如果hiHead为空, 代表该节点为第一个节点
                hiHead = e; // 则将hiHead赋值为第一个节点
            else
                hiTail.next = e;    // 否则将节点添加在hiTail后面
            hiTail = e; // 并将hiTail赋值为新增的节点
            ++hc;   // 统计索引位置为原索引+oldCap的节点个数
        }
    }
    // 4.如果原索引位置的节点不为空
    if (loHead != null) {   // 原索引位置的节点不为空
        // 4.1 如果节点个数<=6个则将红黑树转为链表结构
        if (lc <= UNTREEIFY_THRESHOLD)
            tab[index] = loHead.untreeify(map);
        else {
            // 4.2 将原索引位置的节点设置为对应的头节点
            tab[index] = loHead;
            // 4.3 如果hiHead不为空，则代表原来的红黑树(老表的红黑树由于节点被分到两个位置)
            // 已经被改变, 需要重新构建新的红黑树
            if (hiHead != null)
                // 4.4 以loHead为根节点, 构建新的红黑树
                loHead.treeify(tab);
        }
    }
    // 5.如果索引位置为原索引+oldCap的节点不为空
    if (hiHead != null) {   // 索引位置为原索引+oldCap的节点不为空
        // 5.1 如果节点个数<=6个则将红黑树转为链表结构
        if (hc <= UNTREEIFY_THRESHOLD)
            tab[index + bit] = hiHead.untreeify(map);
        else {
            // 5.2 将索引位置为原索引+oldCap的节点设置为对应的头节点
            tab[index + bit] = hiHead;
            // 5.3 loHead不为空则代表原来的红黑树(老表的红黑树由于节点被分到两个位置)
            // 已经被改变, 需要重新构建新的红黑树
            if (loHead != null)
                // 5.4 以hiHead为根节点, 构建新的红黑树
                hiHead.treeify(tab);
        }
    }
}
```



#### 5.3.2、untreeify红黑树转化为链表

```java
/**
 * 将红黑树节点转为链表节点, 当节点<=6个时会被触发
 */
final Node<K,V> untreeify(HashMap<K,V> map) {
    Node<K,V> hd = null, tl = null; // hd指向头节点, tl指向尾节点
    // 1.从调用该方法的节点, 即链表的头节点开始遍历, 将所有节点全转为链表节点
    for (Node<K,V> q = this; q != null; q = q.next) {
        // 2.调用replacementNode方法构建链表节点
        Node<K,V> p = map.replacementNode(q, null);
        // 3.如果tl为null, 则代表当前节点为第一个节点, 将hd赋值为该节点
        if (tl == null)
            hd = p;
        // 4.否则, 将尾节点的next属性设置为当前节点p
        else
            tl.next = p;
        tl = p; // 5.每次都将tl节点指向当前节点, 即尾节点
    }
    // 6.返回转换后的链表的头节点
    return hd;
}
```



#### 5.3.3、扩容问题

+ 扩容后，节点重 hash 为什么只可能分布在 “原索引位置” 与 “原索引 + oldCap 位置” ？  



假设老表的容量为 16，即 oldCap = 16，则新表容量为 16 * 2 = 32  

节点 1 的 hash 值为：0000 0000 0000 0000 0000 1111 0000 1010，   

节点 2 的 hash 值为：0000 0000 0000 0000 0000 1111 0001 1010       



**老表计算：节点 1 和节点 2 在老表的索引位置计算如下图，节点 1 和节点 2 的索引位置只取决于节点 hash 值的最后 4 位。**   



![1571108185741](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/blogImages/1571108185741.png) 



新表计算，可以知道如果两个节点在老表的索引位置相同，则新表的索引位置只取决于节点hash值倒数第5位的值，而此位置的值刚好为老表的容量值 16    

此时节点在新表的索引位置只有两种情况：“原索引位置” 和 “原索引 + oldCap位置”   

在此例中即为 10 和 10 + 16 = 26。   



![1571108175105](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/blogImages/1571108175105.png)



由于结果只取决于节点 hash 值的倒数第 5 位，而此位置的值刚好为老表的容量值 16，因此此时新表的索引位置的计算可以替换为计算3，直接使用节点的 hash 值与老表的容量 16 进行位于运算，如果结果为 0 则该节点在新表的索引位置为原索引位置，否则该节点在新表的索引位置为 “原索引 + oldCap 位置”。



![1571108581916](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/blogImages/1571108581916.png)



```java
bit =  oldCap ；
if ((e.hash & bit) == 0) {
    if ((e.prev = loTail) == null)  // 如果loTail为空, 代表该节点为第一个节点
        loHead = e; // 则将loHead赋值为第一个节点
    else
        loTail.next = e;    // 否则将节点添加在loTail后面
    loTail = e; // 并将loTail赋值为新增的节点
    ++lc;   // 统计原索引位置的节点个数
}
```













### 5.4、get方法



```
1、计算hash值
2、判断hash桶是否为空，以及桶长度是否大于0，并且是否该hash对应的数组索引位置是否有数据first，并且key相同，如果有数据并且key相同的话，直接返回 
3、如果2不成立，并且该节点first有next值，则判断是否是红黑树节点，如果是红黑树节点，则去红黑树下查找，如果不是红黑树节点，则代表是链表，则去链表中查找。
4、如果2 3都不成立，则返回null

```



```java
public V get(Object key) {
    Node<K,V> e;
    return (e = getNode(hash(key), key)) == null ? null : e.value;
}
 

final Node<K,V> getNode(int hash, Object key) {
    Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
    
    // 1.对table进行校验：
    //table不为空 && table长度大于0 && table索引位置(使用table.length - 1和hash值进行位与运算)的节点不为空,说明是有数据的
    if ((tab = table) != null && (n = tab.length) > 0 && (first = tab[(n - 1) & hash]) != null) {
        
        // 2.检查first节点的hash值和key是否和入参的一样，如果一样则first即为目标节点，直接返回first节点
        if (first.hash == hash && ((k = first.key) == key || (key != null && key.equals(k))))
            return first;
        
        
        // 3.如果first不是目标节点，并且first的next节点不为空则继续遍历
        if ((e = first.next) != null) {
            if (first instanceof TreeNode)
                // 4.如果是红黑树节点，则调用红黑树的查找目标节点方法getTreeNode
                return ((TreeNode<K,V>)first).getTreeNode(hash, key);
            do {
                
                // 5.执行链表节点的查找，向下遍历链表, 直至找到节点的key和入参的key相等时,返回该节点
                if (e.hash == hash &&
                    ((k = e.key) == key || (key != null && key.equals(k))))
                    return e;
            } while ((e = e.next) != null);
        }
    }
    // 6.找不到符合的返回空
    return null;
}
```



#### 5.6.1、获取红黑树节点  **getTreeNode**

```java
final TreeNode<K,V> getTreeNode(int h, Object k) {
    // 1.首先找到红黑树的根节点；
    // 2.使用根节点调用find方法
    return ((parent != null) ? root() : this).find(h, k, null);
}
```

 

#### 5.6.1、红黑树 find （不过多讲解，就是一个排序树）

```java
/**
 * 从调用此方法的节点开始查找, 通过hash值和key找到对应的节点
 * 此方法是红黑树节点的查找, 红黑树是特殊的自平衡二叉查找树
 * 平衡二叉查找树的特点：左节点<根节点<右节点
 */
final TreeNode<K,V> find(int h, Object k, Class<?> kc) {
    // 1.将p节点赋值为调用此方法的节点，即为红黑树根节点
    TreeNode<K,V> p = this;
    // 2.从p节点开始向下遍历
    do {
        int ph, dir; K pk;
        TreeNode<K,V> pl = p.left, pr = p.right, q;
        // 3.如果传入的hash值小于p节点的hash值，则往p节点的左边遍历
        if ((ph = p.hash) > h)
            p = pl;
        else if (ph < h) // 4.如果传入的hash值大于p节点的hash值，则往p节点的右边遍历
            p = pr;
        // 5.如果传入的hash值和key值等于p节点的hash值和key值,则p节点为目标节点,返回p节点
        else if ((pk = p.key) == k || (k != null && k.equals(pk)))
            return p;
        else if (pl == null)    // 6.p节点的左节点为空则将向右遍历
            p = pr;
        else if (pr == null)    // 7.p节点的右节点为空则向左遍历
            p = pl;
        
        // 8.将p节点与k进行比较
        else if ((kc != null ||
                  (kc = comparableClassFor(k)) != null) && // 8.1 kc不为空代表k实现了Comparable
                 (dir = compareComparables(kc, k, pk)) != 0)// 8.2 k<pk则dir<0, k>pk则dir>0
            // 8.3 k<pk则向左遍历(p赋值为p的左节点), 否则向右遍历
            p = (dir < 0) ? pl : pr;
        // 9.代码走到此处, 代表key所属类没有实现Comparable, 直接指定向p的右边遍历（这样就不需要判断了自定规则了，没必要啊）
        else if ((q = pr.find(h, k, kc)) != null) 
            return q;
        // 10.代码走到此处代表“pr.find(h, k, kc)”为空, 因此直接向左遍历
        else
            p = pl;
    } while (p != null);
    return null;
}


8.将 p 节点与 k 进行比较。如果传入的 key（即代码中的参数 k）所属的类实现了 Comparable 接口（kc 不为空，comparableClassFor 方法见下面），
则将 k 跟 p 节点的 key 进行比较（kc 实现了 Comparable 接口，因此通过 kc 的比较方法进行比较），并将比较结果赋值给 dir，如果 dir<0 则代表 k<pk，则向 p 节点的左边遍历（pl）；否则，向 p 节点的右边遍历（pr）。



static Class<?> comparableClassFor(Object x) {
    // 1.判断x是否实现了Comparable接口
    if (x instanceof Comparable) {
        Class<?> c; Type[] ts, as; Type t; ParameterizedType p;
        // 2.校验x是否为String类型
        if ((c = x.getClass()) == String.class) // bypass checks
            return c;
        if ((ts = c.getGenericInterfaces()) != null) {
            // 3.遍历x实现的所有接口
            for (int i = 0; i < ts.length; ++i) {
                // 4.如果x实现了Comparable接口，则返回x的Class
                if (((t = ts[i]) instanceof ParameterizedType) &&
                    ((p = (ParameterizedType)t).getRawType() ==
                     Comparable.class) &&
                    (as = p.getActualTypeArguments()) != null &&
                    as.length == 1 && as[0] == c) // type arg is c
                    return c;
            }
        }
    }
    return null;

```





###  5.5、**remove 方法**

```java
/**
 * 移除某个节点
 */
public V remove(Object key) {
    Node<K,V> e;
    return (e = removeNode(hash(key), key, null, false, true)) == null ?
        null : e.value;
}
 
final Node<K,V> removeNode(int hash, Object key, Object value,
                           boolean matchValue, boolean movable) {
    Node<K,V>[] tab; Node<K,V> p; int n, index;
    // 1.如果table不为空并且根据hash值计算出来的索引位置不为空, 将该位置的节点赋值给p
    if ((tab = table) != null && (n = tab.length) > 0 &&
        (p = tab[index = (n - 1) & hash]) != null) {
        Node<K,V> node = null, e; K k; V v;
        // 2.如果p的hash值和key都与入参的相同, 则p即为目标节点, 赋值给node
        if (p.hash == hash &&
            ((k = p.key) == key || (key != null && key.equals(k))))
            node = p;
        else if ((e = p.next) != null) {
            // 3.否则将p.next赋值给e，向下遍历节点
            // 3.1 如果p是TreeNode则调用红黑树的方法查找节点
            if (p instanceof TreeNode)
                node = ((TreeNode<K,V>)p).getTreeNode(hash, key);
            else {
                // 3.2 否则，进行普通链表节点的查找
                do {
                    // 当节点的hash值和key与传入的相同,则该节点即为目标节点
                    if (e.hash == hash &&
                        ((k = e.key) == key ||
                         (key != null && key.equals(k)))) {
                        node = e;	// 赋值给node, 并跳出循环
                        break;
                    }
                    p = e;  // p节点赋值为本次结束的e，在下一次循环中，e为p的next节点
                } while ((e = e.next) != null); // e指向下一个节点
            }
        }
        // 4.如果node不为空(即根据传入key和hash值查找到目标节点)，则进行移除操作
        if (node != null && (!matchValue || (v = node.value) == value ||
                             (value != null && value.equals(v)))) {
            // 4.1 如果是TreeNode则调用红黑树的移除方法
            if (node instanceof TreeNode)
                ((TreeNode<K,V>)node).removeTreeNode(this, tab, movable);
            // 4.2 如果node是该索引位置的头节点则直接将该索引位置的值赋值为node的next节点，
            // “node == p”只会出现在node是头节点的时候，如果node不是头节点，则node为p的next节点
            else if (node == p)
                tab[index] = node.next;
            // 4.3 否则将node的上一个节点的next属性设置为node的next节点,
            // 即将node节点移除, 将node的上下节点进行关联(链表的移除)
            else
                p.next = node.next;
            ++modCount;
            --size;
            afterNodeRemoval(node); // 供LinkedHashMap使用
            // 5.返回被移除的节点
            return node;
        }
    }
    return null;
}

```





#### 5.5.1、红黑树 removeTreeNode

```java
**
 * 红黑树的节点移除
 */
final void removeTreeNode(HashMap<K,V> map, Node<K,V>[] tab,
                          boolean movable) {
    // --- 链表的处理start ---
    int n;
    // 1.table为空或者length为0直接返回
    if (tab == null || (n = tab.length) == 0)
        return;
    // 2.根据hash计算出索引的位置
    int index = (n - 1) & hash;
    // 3.将索引位置的头节点赋值给first和root
    TreeNode<K,V> first = (TreeNode<K,V>)tab[index], root = first, rl;
    // 4.该方法被将要被移除的node(TreeNode)调用, 因此此方法的this为要被移除node节点,
    // 将node的next节点赋值给succ节点，prev节点赋值给pred节点
    TreeNode<K,V> succ = (TreeNode<K,V>)next, pred = prev;
    // 5.如果pred节点为空，则代表要被移除的node节点为头节点，
    // 则将table索引位置的值和first节点的值赋值为succ节点(node的next节点)即可
    if (pred == null)
        tab[index] = first = succ;
    else
        // 6.否则将pred节点的next属性设置为succ节点(node的next节点)
        pred.next = succ;
    // 7.如果succ节点不为空，则将succ的prev节点设置为pred, 与前面对应
    if (succ != null)
        succ.prev = pred;
    // 8.如果进行到此first节点为空，则代表该索引位置已经没有节点则直接返回
    if (first == null)
        return;
    // 9.如果root的父节点不为空, 则将root赋值为根节点
    if (root.parent != null)
        root = root.root();
    // 10.通过root节点来判断此红黑树是否太小, 如果是则调用untreeify方法转为链表节点并返回
    // (转链表后就无需再进行下面的红黑树处理)
    if (root == null || root.right == null ||
        (rl = root.left) == null || rl.left == null) {
        tab[index] = first.untreeify(map);  // too small
        return;
    }
    // --- 链表的处理end ---
 
    // --- 以下代码为红黑树的处理 ---
    // 11.将p赋值为要被移除的node节点，pl赋值为p的左节点，pr赋值为p 的右节点
    TreeNode<K,V> p = this, pl = left, pr = right, replacement;
    // 12.如果p的左节点和右节点都不为空时
    if (pl != null && pr != null) {
        // 12.1 将s节点赋值为p的右节点
        TreeNode<K,V> s = pr, sl;
        // 12.2 向左一直查找，跳出循环时,s为没有左节点的节点
        while ((sl = s.left) != null)
            s = sl;
        // 12.3 交换p节点和s节点的颜色
        boolean c = s.red; s.red = p.red; p.red = c;
        TreeNode<K,V> sr = s.right; // s的右节点
        TreeNode<K,V> pp = p.parent;    // p的父节点
        // --- 第一次调整和第二次调整：将p节点和s节点进行了位置调换 ---
        // 12.4 第一次调整
        // 如果p节点的右节点即为s节点，则将p的父节点赋值为s，将s的右节点赋值为p
        if (s == pr) {
            p.parent = s;
            s.right = p;
        }
        else {
            // 将sp赋值为s的父节点
            TreeNode<K,V> sp = s.parent;
            // 将p的父节点赋值为sp
            if ((p.parent = sp) != null) {
                // 如果s节点为sp的左节点，则将sp的左节点赋值为p节点
                if (s == sp.left)
                    sp.left = p;
                // 否则s节点为sp的右节点，则将sp的右节点赋值为p节点
                else
                    sp.right = p;
            }
            // s的右节点赋值为p节点的右节点
            if ((s.right = pr) != null)
                // 如果pr不为空，则将pr的父节点赋值为s
                pr.parent = s;
        }
        // 12.5 第二次调整
        // 将p的左节点赋值为空，pl已经保存了该节点
        p.left = null;
        // 将p节点的右节点赋值为sr，如果sr不为空，则将sr的父节点赋值为p节点
        if ((p.right = sr) != null)
            sr.parent = p;
        // 将s节点的左节点赋值为pl，如果pl不为空，则将pl的父节点赋值为s节点
        if ((s.left = pl) != null)
            pl.parent = s;
        // 将s的父节点赋值为p的父节点pp
        // 如果pp为空，则p节点为root节点, 交换后s成为新的root节点
        if ((s.parent = pp) == null)
            root = s;
        // 如果p不为root节点, 并且p是pp的左节点，则将pp的左节点赋值为s节点
        else if (p == pp.left)
            pp.left = s;
        // 如果p不为root节点, 并且p是pp的右节点，则将pp的右节点赋值为s节点
        else
            pp.right = s;
        // 12.6 寻找replacement节点，用来替换掉p节点
        // 12.6.1 如果sr不为空，则replacement节点为sr，因为s没有左节点，所以使用s的右节点来替换p的位置
        if (sr != null)
            replacement = sr;
        // 12.6.1 如果sr为空，则s为叶子节点，replacement为p本身，只需要将p节点直接去除即可
        else
            replacement = p;
    }
    // 13.承接12点的判断，如果p的左节点不为空，右节点为空，replacement节点为p的左节点
    else if (pl != null)
        replacement = pl;
    // 14.如果p的右节点不为空,左节点为空，replacement节点为p的右节点
    else if (pr != null)
        replacement = pr;
    // 15.如果p的左右节点都为空, 即p为叶子节点, replacement节点为p节点本身
    else
        replacement = p;
    // 16.第三次调整：使用replacement节点替换掉p节点的位置，将p节点移除
    if (replacement != p) { // 如果p节点不是叶子节点
        // 16.1 将p节点的父节点赋值给replacement节点的父节点, 同时赋值给pp节点
        TreeNode<K,V> pp = replacement.parent = p.parent;
        // 16.2 如果p没有父节点, 即p为root节点，则将root节点赋值为replacement节点即可
        if (pp == null)
            root = replacement;
        // 16.3 如果p不是root节点, 并且p为pp的左节点，则将pp的左节点赋值为替换节点replacement
        else if (p == pp.left)
            pp.left = replacement;
        // 16.4 如果p不是root节点, 并且p为pp的右节点，则将pp的右节点赋值为替换节点replacement
        else
            pp.right = replacement;
        // 16.5 p节点的位置已经被完整的替换为replacement, 将p节点清空, 以便垃圾收集器回收
        p.left = p.right = p.parent = null;
    }
    // 17.如果p节点不为红色则进行红黑树删除平衡调整
    // (如果删除的节点是红色则不会破坏红黑树的平衡无需调整)
    TreeNode<K,V> r = p.red ? root : balanceDeletion(root, replacement);
 
    // 18.如果p节点为叶子节点, 则简单的将p节点去除即可
    if (replacement == p) {
        TreeNode<K,V> pp = p.parent;
        // 18.1 将p的parent属性设置为空
        p.parent = null;
        if (pp != null) {
            // 18.2 如果p节点为父节点的左节点，则将父节点的左节点赋值为空
            if (p == pp.left)
                pp.left = null;
            // 18.3 如果p节点为父节点的右节点， 则将父节点的右节点赋值为空
            else if (p == pp.right)
                pp.right = null;
        }
    }
    if (movable)
        // 19.将root节点移到索引位置的头节点
        moveRootToFront(tab, r);
}

```





### 5、最终总结

#### 1、时间复杂度

Ο(1)＜Ο(log2n)＜Ο(n)＜Ο(nlog2n)＜Ο(n2)<Ο(n3)＜…＜Ο(n^2)＜Ο(n!)



分四步：      

```
1.判断key，根据key算出索引。     
2.根据索引获得索引位置所对应的键值对链表。      
3.遍历键值对链表，根据key找到对应的Entry键值对。    
4.拿到value。    
```

分析：     

以上四步要保证HashMap的时间复杂度O(1)，第三步链表的的遍历对hashmap时间复杂度影响最大，链表查找的时间复杂度为O(n)，与链表长度有关。我们要保证那个链表长度为1，才可以说时间复杂度能满足O(1)。这样只有那个hash算法尽量减少冲突，才能使链表长度尽可能短，理想状态为1。      

因此可以得出结论：HashMap的查找时间复杂度只有在最理想的情况下才会为O(1)，而要保证这个理想状态不是我们开发者控制的，如果最差的情况下，即所有元素的hashCode都一样，那就放在同一个桶里面，这样时间复杂度在链表中 O(n)，红黑树中O（logn），但这种情况概率几乎没有，所以HashMao的时间复杂度是O(1)



#### 2、引入红黑树原因

提高HashMap性能，解决了由于哈希碰撞链表过长导致索引效率慢的问题。利用红黑树增删改查的特点，时间复杂度从O(n)降低为O(logn)



#### 3、HashMap为什么右移16和使用 & 与运算代替模运算



hash值是为了获取数组下标的，很明显就知道该hash值是为了均匀散列表的下标，仔细看看，就知道下面使用了 **hashcode 右移16位再和自己异或得到的hash值**  



##### 3.1、明确：定位哈希桶数组索引位置是使用了桶长度和hash值

```java
// 代码1
static final int hash(Object key) { // 计算key的hash值
    int h;
    // 1.先拿到key的hashCode值; 2.将hashCode的高16位参与运算
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}

// 代码2
int n = tab.length;

// 将(tab.length - 1) 与 hash值进行&运算
int index = (n - 1) & hash;

```

整个过程本质上就是三步：

1. 拿到 key 的 hashCode 值
2. 将 hashCode 的高位参与运算，重新计算 hash 值
3. 将计算出来的 hash 值与 (table.length - 1) 进行 & 运算



```java
static final int hash(Object key) {
       int h;
       return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```



##### 3.2、为什么要右移16位



**当 table 长度为 16 时，table.length - 1 = 15 ，用二进制来看，此时低 4 位全是 1，高 28 位全是 0，与 0 进行 & 运算必然为 0，因此此时 hashCode 与 “table.length - 1” 的 & 运算结果只取决于 hashCode 的低 4 位，在这种情况下，hashCode 的高 28 位就没有任何作用，并且由于 hash 结果只取决于 hashCode 的低 4 位，hash 冲突的概率也会增加。因此，在 JDK 1.8 中，将高位也参与计算，目的是为了降低 hash 冲突的概率。**





**举例说明**  

对象 A 的 hashCode 为  0100 0010 0011 1000 1000 0011 1100 0000     

对象 B 的 hashCode 为  0011 1011 1001 1100 0101 0000 1010 0000     

对象A和对象B的hashcode不相同，   如果不经过hash运算，如果数组长度是16（默认就是16），也就是 15 与运算这两个数    

15 的二进制数                  0000 0000 0000 0000 0000 0000 0000 1111    ，发现结果都是0。这样的话数组小标就都是0了，这样的结果应该不是我们想看到的，只使用了hashcode的低4位，没有充分利用hashcde，如果数据足够多，这种重复的数据还是挺多的          



**解决**        

如果我们将 hashCode 值右移 16 位，也就是取 int 类型的一半，刚好将该二进制数对半切开。并且使用位异或运算（如果两个数对应的位置相反，则结果为1，反之为0），这样的话，就能避免我们上面的情况的发生，即(h = key.hashCode()) ^ (h >>> 16)。       **相当于让自己的前半段16位和后半段16位做一个异或的运算**         

总的来说，使用位移 16 位和 异或 就是防止这种极端情况。但是，该方法在一些极端情况下还是有问题，比如：10000000000000000000000000 和 1000000000100000000000000 这两个数，如果数组长度是16，那么即使右移16位，在异或，hash 值还是会重复。但是为了性能，**对这种极端情况，JDK 的作者选择了性能。毕竟这是少数情况，为了这种情况去增加 hash 时间，性价比不高。**     



当数组吃长度n为 16 的时候数组下标：  1111  & 101010100101001001000（随便写的） 1000 = 8 



![1570788285450](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/blogImages/1570788285450.png)





##### 3.3、HashMap 为什么使用 & 与运算代替模运算

其中 n 是数组的长度。其实该算法的结果和模运算的结果是相同的。但是，对于现代的处理器来说，**除法和求余数（模运算）**是最慢的动作，        



通过上面的3.2末尾，可以看到，可以看到，**当 n 为 2 的幂次方的时候，减一之后就会得到 一堆1111…… 的数字，这个数字正好可以掩码 （都是一堆 1111……），并且得到的结果取决于 hash 值。因为 hash 位值是1，那么最终的结果也是1 ，hash位 值是0，最终的结果也是0。**  



```
tab[   (n - 1) & hash   ]；
```



#### 4、HashMap 的容量为什么建议是 2的幂次方？

接4.2、hash 算法的目的是为了让hash值均匀的分布在桶中（数组），那么，如何做到呢？试想一下，如果不使用 2 的幂次方作为数组的长度会怎么样？        

假设我们的数组长度是10，还是上面的公式：    

1001 & 101010100101001001000 结果：1000 = 8    

1001 & 101000101101001001001 结果：1001 = 9    

1001 & 101010101101101001010 结果： 1000 = 8    

1001 & 101100100111001101100 结果： 1000 = 8    

结论    

所以说，**我们一定要保证 & 中的二进制位全为 1，才能最大限度的利用 hash 值，并更好的散列，只有全是1 ，才能有更多的散列结果**。如果是 1001，有的散列结果是永远都不会出现的，比如 1111，1010，1011,…，只要 & 之前的数有 0， 对应的 1 肯定就不会出现（因为只有都是1才会为1）。大大限制了散列的范围。



#### 5、我们自定义 HashMap 容量最好是多少

那我们如何自定义呢？自从有了阿里的规约插件，每次楼主都要初始化容量，如果我们预计我们的散列表中有2个数据，那么我就初始化容量为2嘛      

绝对不行，如果大家看过源码就会发现，**如果Map中已有数据的容量达到了初始容量的 75%，那么散列表就会扩容，而扩容将会重新将所有的数据重新散列，性能损失严重**，所以，我们可以必须要大于我们预计数据量的 1.34 倍，如果是2个数据的话，就需要初始化 2.68 个容量。当然这是开玩笑的，2.68 不可以，3 可不可以呢？肯定也是不可以的，我前面说了，如果不是2的幂次方，散列结果将会大大下降。导致出现大量链表。那么我可以将初始化容量设置为4。 当然了，如果你预计大概会插入 12 条数据的话，那么初始容量为16简直是完美，一点不浪费，而且也不会扩容。

项目经验总计:     

1、比如链表数很多，肯定是数组初始化长度不对，这样的话会造成拥挤     

2、如果某个map很大，注意，肯定是事先没有定义好初始化长度    

假设，某个Map存储了10000个数据，那么他会扩容到 20000，实际上，根本不用 20000，只需要 10000* 1.34= 13400 个，然后向上找到一个2 的幂次方，也就是 16384 初始容量足够。





#### 6、jdk1.7链表是如何死循环的



##### 6.1、JDK1.7 put源码

+ Put一个Key,Value对到Hash表中：

```java
public V put(K key, V value)
{
    ......
    //算Hash值
    int hash = hash(key.hashCode());
    int i = indexFor(hash, table.length);
    //如果该key已被插入，则替换掉旧的value （链接操作）
    for (Entry<K,V> e = table[i]; e != null; e = e.next) {
        Object k;
        if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {
            V oldValue = e.value;
            e.value = value;
            e.recordAccess(this);
            return oldValue;
        }
    }
    modCount++;
    //该key不存在，需要增加一个结点
    addEntry(hash, key, value, i);
    return null;
}
```



+ 新建一个更大尺寸的hash表，然后把数据从老的Hash表中迁移到新的Hash表中。



```java
void resize(int newCapacity)
{
    Entry[] oldTable = table;
    int oldCapacity = oldTable.length;
    ......
    //创建一个新的Hash Table
    Entry[] newTable = new Entry[newCapacity];
    
    //将Old Hash Table上的数据迁移到New Hash Table上
    transfer(newTable);
    table = newTable;
    threshold = (int)(newCapacity * loadFactor);
}
```



+ 迁移的源代码

```java
void transfer(Entry[] newTable)
{
    Entry[] src = table;
    int newCapacity = newTable.length;
    //下面这段代码的意思是：
    //  从OldTable里摘一个元素出来，然后放到NewTable中
    for (int j = 0; j < src.length; j++) {
        Entry<K,V> e = src[j];
        if (e != null) {
            src[j] = null;
            do {
                Entry<K,V> next = e.next;
                int i = indexFor(e.hash, newCapacity);
                e.next = newTable[i];
                newTable[i] = e;
                e = next;
            } while (e != null);
        }
    }
}
```

##### 6.2、JDK1.7、正常的ReHash的过程



![1571047625203](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/blogImages/1571047625203.png)



##### 6.3、JDK1.7、并发的ReHash的过程



**1）假设我们有两个线程**   



+ 我们再回头看一下我们的 transfer代码中的这个细节：

  ```java
  do {
      Entry<K,V> next = e.next; // <--假设线程一执行到这里就被调度挂起了
      int i = indexFor(e.hash, newCapacity);
      e.next = newTable[i];
      newTable[i] = e;
      e = next;
  } while (e != null);
  ```

![1571047930327](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/blogImages/1571047930327.png)

注意，**因为Thread1的 e 指向了key(3)，而next指向了key(7)，其在线程二rehash后，指向了线程二重组后的链表**。我们可以看到链表的顺序被反转后。  



**2）线程一被调度回来执行。**

  

- **先是执行 newTalbe[i] = e;**
- **然后是e = next，导致了e指向了key(7)，**
- **而下一次循环的next = e.next导致了next指向了key(3)**





![1571048988768](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/blogImages/1571048988768.png)

**3）一切安好。**  



线程一接着工作。**把key(7)摘下来，放到newTable[i]的第一个，然后把e和next往下移**。   



![1571049186173](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/blogImages/1571049186173.png)

**4）环形链接出现。** 



![1571049473246](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/blogImages/1571049473246.png)  







## 6、JDK1.7 HashMap



**HashMap底层就是一个数组结构，数组中的每一项又是一个链表。当新建一个HashMap的时候，就会初始化一个数组。**



![1571104324137](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/blogImages/1571104324137.png)





HashMap的主干数组，可以看到就是一个Entry数组，初始值为空数组{}，



```java
transient Entry<K,V>[] table = (Entry<K,V>[]) EMPTY_TABLE;
```



Entry是HashMap中的一个静态内部类。代码如下



```java
 static class Entry<K,V> implements Map.Entry<K,V> {
        final K key;
        V value;
        Entry<K,V> next;//存储指向下一个Entry的引用，单链表结构
        int hash;//对key的hashcode值进行hash运算后得到的值，存储在Entry，避免重复计算

        /**
         * Creates new entry.
         */
        Entry(int h, K k, V v, Entry<K,V> n) {
            value = v;
            next = n;
            key = k;
            hash = h;
        } 

```



**类变量**

```java
    /** 初始容量，默认16 */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

    /** 最大初始容量，2^30 */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /** 负载因子，默认0.75，负载因子越小，hash冲突机率越低 */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /** 初始化一个Entry的空数组 */
    static final Entry<?,?>[] EMPTY_TABLE = {};

    /** 将初始化好的空数组赋值给table，table数组是HashMap实际存储数据的地方，并不在EMPTY_TABLE数组中 */
    transient Entry<K,V>[] table = (Entry<K,V>[]) EMPTY_TABLE;

    /** HashMap实际存储的元素个数 */
    transient int size;

    /** 临界值（HashMap 实际能存储的大小）,公式为(threshold = capacity * loadFactor) */
    int threshold;

    /** 负载因子 */
    final float loadFactor;

    /** HashMap的结构被修改的次数，用于迭代器 */
    transient int modCount;
```



**hashmap构造器的不会初始化数组，而是在put的时候调用inflateTable初始化的**



```java
public HashMap(int initialCapacity, float loadFactor) {
        // 判断设置的容量和负载因子合不合理
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        // 设置负载因子，临界值此时为容量大小，后面第一次put时由inflateTable(int toSize)方法计算设置
        this.loadFactor = loadFactor;
        threshold = initialCapacity;
        init();
    }

    public HashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public HashMap() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public HashMap(Map<? extends K, ? extends V> m) {
        this(Math.max((int) (m.size() / DEFAULT_LOAD_FACTOR) + 1,
                      DEFAULT_INITIAL_CAPACITY), DEFAULT_LOAD_FACTOR);
        inflateTable(threshold);
        putAllForCreate(m);
    }
```



当我们往HashMap中put元素的时候，先根据key的hashCodet通过hash运算得到hash值，根据hash值得到这个元素在数组中的位置（即下标），   



如果数组该位置上已经存放有其他元素了，那么在这个位置上的元素将以链表的形式存放，新加入的放在链头，最先加入的放在链尾。如果数组该位置上没有元素，就直接将该元素放到此数组中的该位置上。



```java
	public V put(K key, V value) {  
    // 如果table引用指向成员变量EMPTY_TABLE，那么初始化HashMap（设置容量、临界值，新的Entry数组引用）
    if (table == EMPTY_TABLE) {
        inflateTable(threshold);
    }
        
    // 若“key为null”，则将该键值对添加到table[0]处，遍历该链表，如果有key为null，则将value替换。没有就创建新Entry对象放在链表表头
    // 所以table[0]的位置上，永远最多存储1个Entry对象，形成不了链表。key为null的Entry存在这里 
    if (key == null)  
        return putForNullKey(value);  
    // 若“key不为null”，则计算该key的哈希值
    int hash = hash(key);  
    // 搜索指定hash值在对应table中的索引
    int i = indexFor(hash, table.length);  
    // 循环遍历table数组上的Entry对象，判断该位置上key是否已存在
    for (Entry<K,V> e = table[i]; e != null; e = e.next) {  
        Object k;  
        // 哈希值相同并且对象相同
        if (e.hash == hash && ((k = e.key) == key || key.equals(k))) {  
            // 如果这个key对应的键值对已经存在，就用新的value代替老的value，然后退出！
            V oldValue = e.value;  
            e.value = value;  
            e.recordAccess(this);  
            return oldValue;  
        }  
    }  
    // 修改次数+1
    modCount++;
    // table数组中没有key对应的键值对，就将key-value添加到table[i]处 
    addEntry(hash, key, value, i);  
    return null;  
} 

```

  



需要根据key的hash值来求得对应数组中的位置。如何计算这个位置就是hash算法。前面说过HashMap的数据结构是数组和链表的结合，所以我们当然希望这个HashMap里面的元素位置尽量的分布均匀些，尽量使得每个位置上的元素数量只有一个，那么当我们用hash算法求得这个位置的时候，马上就可以知道对应位置的元素就是我们要的，而不用再去遍历链表，这样就大大优化了查询的效率。      



   hash方法：对于任意给定的对象，只要它的 hashCode() 返回值相同，那么程序调用 hash(int h) 方法所计算得到的 hash 码值总是相同的，这个hash算法和jdk1.8是不一样的，但是作用是相同的都是为了充分利用hashcode，降低hash冲突。   



**indexFor：我们首先想到的就是把hash值对数组长度取模运算，这样一来，元素的分布相对来说是比较均匀的。但是，“模”运算的消耗还是比较大的**，在HashMap中是这样做的：调用 indexFor(int h, int length) 方法来计算该对象应该保存在 table 数组的哪个索引处。indexFor(int h, int length) 方法的代码如下：





```java
transient int hashSeed = 0;

 final int hash(Object k) {
        int h = hashSeed;
        if (0 != h && k instanceof String) {
            return sun.misc.Hashing.stringHash32((String) k);
        }

        h ^= k.hashCode();

        // This function ensures that hashCodes that differ only by
        // constant multiples at each bit position have a bounded
        // number of collisions (approximately 8 at default load factor).
        h ^= (h >>> 20) ^ (h >>> 12);
        return h ^ (h >>> 7) ^ (h >>> 4);
    }
```


```java
static int indexFor(int h, int length) {  
    return h & (length-1);  
}  

```



**addEntry(hash, key, value, i)方法根据计算出的index数组位置值，将key-value对放在数组table的i索引处。**



```java

/**
* 将Entry添加到数组bucketIndex位置对应的哈希桶中，并判断数组是否需要扩容
*/
void addEntry(int hash, K key, V value, int bucketIndex) {
    // 如果数组长度大于等于容量×负载因子，并且要添加的位置为null
    if ((size >= threshold) && (null != table[bucketIndex])) {
        // 长度扩大为原数组的两倍，代码分析见下面扩容机制
        resize(2 * table.length);
        hash = (null != key) ? hash(key) : 0;
        bucketIndex = indexFor(hash, table.length);
    }
    createEntry(hash, key, value, bucketIndex);
}


/**
* 在链表中添加一个新的Entry对象在链表的表头
*/
void createEntry(int hash, K key, V value, int bucketIndex) {
    //bucketIndex其实就是下表, 获取指定 bucketIndex 索引处的 Entry   
    // 将新创建的 Entry 放入 bucketIndex 索引处，并让新的 Entry 指向原来的 Entry  
    //添加key到table[bucketIndex]位置，新的元素总是在table[bucketIndex]的第一个元素，原来的元素后移（妈的，这里的下表不是顺序产生的，而是根据hash值产生的）
    Entry<K,V> e = table[bucketIndex];
    table[bucketIndex] = new Entry<>(hash, key, value, e);
    size++;
}
```



hashmap存储元素的本质是一个数组。`inflateTable`可以看到数组的初始化是在第一次put的时候进行的



```java
   private void inflateTable(int toSize) {
        // Find a power of 2 >= toSize
        int capacity = roundUpToPowerOf2(toSize);

        threshold = (int) Math.min(capacity * loadFactor, MAXIMUM_CAPACITY + 1);
        table = new Entry[capacity];
        initHashSeedAsNeeded(capacity);
    }
```



下面有些烧脑，具体看连接
https://segmentfault.com/a/1190000018520768


```java
    final boolean initHashSeedAsNeeded(int capacity) {
    //当我们初始化的时候hashSeed为0,0!=0 这时为false.
        boolean currentAltHashing = hashSeed != 0;
        
        //isBooted()这个方法里面返回了一个boolean值,我们看下面的代码
        boolean useAltHashing = sun.misc.VM.isBooted() &&
                (capacity >= Holder.ALTERNATIVE_HASHING_THRESHOLD);
        boolean switching = currentAltHashing ^ useAltHashing;
        if (switching) {
            hashSeed = useAltHashing
                ? sun.misc.Hashing.randomHashSeed(this)
                : 0;
        }
        return switching;
    }

```



关于数组大小，为2的幂次方，如果初始化的时候不是2的幂次方，会向上取2的幂次方.比如说初始指定大小10，`roundUpToPowerOf2`函数运算会返回2^4=16。  



```java
private static int roundUpToPowerOf2(int number) {
        // assert number >= 0 : "number must be non-negative";
        return number >= MAXIMUM_CAPACITY
                ? MAXIMUM_CAPACITY
                : (number > 1) ? Integer.highestOneBit((number - 1) << 1) : 1;
}
```







get()方法源码如下：

```java
public V get(Object key) {
    // 若key为null，遍历table[0]处的链表（实际上要么没有元素，要么只有一个Entry对象），取出key为null的value
    if (key == null)
        return getForNullKey();
    // 若key不为null，用key获取Entry对象
    Entry<K,V> entry = getEntry(key);
    // 若链表中找到的Entry不为null，返回该Entry中的value
    return null == entry ? null : entry.getValue();
}

final Entry<K,V> getEntry(Object key) {
    if (size == 0) {
        return null;
    }
    // 计算key的hash值
    int hash = (key == null) ? 0 : hash(key);
    // 计算key在数组中对应位置，遍历该位置的链表
    for (Entry<K,V> e = table[indexFor(hash, table.length)];
         e != null;
         e = e.next) {
        Object k;
        // 若key完全相同，返回链表中对应的Entry对象
        if (e.hash == hash &&
            ((k = e.key) == key || (key != null && key.equals(k))))
            return e;
    }
    // 链表中没找到对应的key，返回null
    return null;
}
```

  

扩容机制：  

当HashMapde的存储的数量超出了加载因子与当前容量的乘积（默认16*0.75=12）时，通过调用resize方法重新创建一个原来HashMap大小的两倍的newTable数组，最大扩容到2^30+1，并将原先table的元素全部移到newTable里面，重新计算hash，然后再重新根据hash分配位置。这个过程叫作rehash，因为它调用hash方法找到新的bucket位置。



```java
void resize(int newCapacity) {
        Entry[] oldTable = table;
        int oldCapacity = oldTable.length;
        // 如果之前的HashMap已经扩充打最大了，那么就将临界值threshold设置为最大的int值
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        // 根据新传入的newCapacity创建新Entry数组
        Entry[] newTable = new Entry[newCapacity];
        // 用来将原先table的元素全部移到newTable里面，重新计算hash，然后再重新根据hash分配位置
        transfer(newTable, initHashSeedAsNeeded(newCapacity));
        // 再将newTable赋值给table
        table = newTable;
        // 重新计算临界值，扩容公式在这儿（newCapacity * loadFactor）
        threshold = (int)Math.min(newCapacity * loadFactor, MAXIMUM_CAPACITY + 1);
    }
```





```java
void transfer(Entry[] newTable, boolean rehash) {
        int newCapacity = newTable.length;
        for (Entry<K,V> e : table) {
            while(null != e) {
                Entry<K,V> next = e.next;
                if (rehash) {
                    e.hash = null == e.key ? 0 : hash(e.key);
                }
                int i = indexFor(e.hash, newCapacity);
                e.next = newTable[i];
                newTable[i] = e;
                e = next;
            }
        }
    }
```









<font  color="red" size="5" >     
感兴趣的，欢迎添加博主微信
 </font>

<br/>



哈，博主很乐意和各路好友交流，如果满意，请打赏博主任意金额，感兴趣的在微信转账的时候，备注您的微信或者其他联系方式。添加博主微信哦。    

请下方留言吧。可与博主自由讨论哦

|微信 | 微信公众号|支付宝|
|:-------:|:-------:|:------:|
| ![微信](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/assets/img/tctip/weixin.jpg)|![微信公众号](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/assets/img/my/qrcode_for_gh_a23c07a2da9e_258.jpg)|![支付宝](https://raw.githubusercontent.com/HealerJean/HealerJean.github.io/master/assets/img/tctip/alpay.jpg) |



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
		id: 's7O2fIRBWKgbi8lr',
    });
    gitalk.render('gitalk-container');
</script> 

<!-- Gitalk end -->

