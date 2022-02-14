# 中医药知识科普平台

前端项目: https://github.com/C0HERENCE/tcm-client

基于SpringBoot+vue, 整合mybatis, mysql, elasticsearch, redis, springsecurity, jwt

## Structure

``` shell
tcm
├── tcm-common
│   ├── api # http 通用响应, HTTP状态码
│   └── exception # AOP全局异常处理
├── tcm-generated # mybatisplus generator生成
│   ├── domain
│   ├── mapper
│   └── service
│       └── impl
├── tcm-search
│   └── service # Elastic Search搜索服务, 在线翻译API
├── tcm-security
│   ├── component # jwt filter
│   ├── config # spring security 配置, jwt配置
│   └── util # Jwt工具类
└── tcm-web # spring web MVC
    ├── aop # 管理员切面
    ├── bo
    ├── config # redis, spring scurity配置
    ├── controller
    ├── dto
    ├── service
    └── TcmWebApplication.java # 主程序
```
