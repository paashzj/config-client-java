# config-client
提供对配置的增删改查，并可将配置的变化实时通知到各个进程。

## 重要功能
- 强Schema
- 并发保证
- 变更通知
- 应用程序高性能读

## 设计思路
- 通过id和version解决并发问题
- 通知的时候仅通知id，不通知内容

## 实现
### 通用实现要点
- 配置的增删改查
- 捕获数据的变更
### 基于Mysql的实现
- 一个配置对应一张数据表，转化为对数据表的增删改查
- 通过扫描表，来捕获数据的变更
