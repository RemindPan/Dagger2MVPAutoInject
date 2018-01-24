# Dagger2MVPAutoInject
自定义注解自动生成component, module 等dagger必须的文件， 实现dagger完全解耦

* 现在还有一些阻碍，没有办法很好的解耦掉，必须得手动配置父类的类名

  * 在BaseAutoInjectActivity，根据java的运行原理，当在虚拟机中运行时this会变为具体子类，但是为了避免一些问题，仍会做强转操作。
