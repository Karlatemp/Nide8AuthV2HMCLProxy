# Nide8.com Auth v2 HTML Proxy System

---
这个项目是为了支持(绕过)来自屑统一的单方面的登录阻拦.
各位用HMCL的伙伴们估计早就发现了这个东西,
但是被迫换其他网站支持/换其他启动器的应该有不少吧.

## 开始
你可以去dist下载我们的最新构建. 或者你可以选择下载整个仓库然后自己构建.

请注意, 这不是一个gui软件, 而是一个命令行软件. 如果你直接双击打开的话,
你将无法直接退出此代理.

~~~shell script
java -jar "Nide8.com Auth v2 HMCL Proxy.jar";
~~~
这是最快的启动方法. 默认的端口是 `4443` (`4444` 在编写的时候已经被占用了),
如果有修改端口的需求. 请改用一下命令

~~~shell script
java -Dport=520 -jar "Nide8.com Auth v2 HMCL Proxy.jar";
~~~
如果你不想每次打开启动器都要执行一次命令的话,
你可以把它写在一个批处理文件里然后把他的快捷方式放在HMCL旁边.
(Linux: \*\*\*\*\*)

或者你可以把此命令注册到开机自运行里去. 但是关闭的时候会有些麻烦.

##配置HMCL
是的, 代理服务器开了, 但是如果不进行配置的话这并没有任何用处.

首先关闭你的HMCL. 编辑的时候如果他处于开启状态的话那么我们做出的修改将不会生效.

打开HMCL所在的文件夹. 你应该能看到一个名为 `hmcl.json` 的文件, 打开她.

把这个文件里面的 `https://auth2.nide8.com:233` 全部替换成 `http://localhost:4443`

## 在HMCL添加统一登录
如果您操作过HMCL的添加外置登录的话, 那么这个应该不难操作, 如果没有,
请 [Baidu](https://www.baidu.com)

```text
http://localhost:4443/{你的统一服务器ID}/
http://localhost:4443/1234567890abcdef1234567890abcdef/
```
## 没人看的后记
本来写这个东西只是为了自己和群友使用, 啊哈, 这也是我第一次在项目里面写后记.

先来说说统一和HMCL的外置登录, Yggdrasil API 的区别把
- [统一 Document](https://login2.nide8.com:233/index/doc)
- [Yggdrasil API](https://github.com/yushijinhun/authlib-injector/wiki/Yggdrasil%20%E6%9C%8D%E5%8A%A1%E7%AB%AF%E6%8A%80%E6%9C%AF%E8%A7%84%E8%8C%83)

是的, 基本没有区别, 而且 Yggdrasil API的前身 Yggdrasil Agent 的发布时间是比统一要早的.
基本能确定统一用的Yggdrasil API. 因为网络问题我目前还没有拉取authlib-injector的仓库
(包括wiki仓库)查看第一条commit的时间.

而且不论这个, HMCL是为数不多的全版本支持的启动器, 支持外置登录的Linux/MacOS系统的启动器少之又少,
单方面拦掉HMCL无异是对这部分用户的极大打击. 是的, 单方面拦截是很不让人喜欢的,
而且在拦截以前, 还没有提供HMCL的连接方法, 这是歧视吗? 我希望我不会再次提交commit到这个仓库

> Karlatemp
>
> ------ 2020/02/22 20-56
