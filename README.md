# BJAF3.x
Beetle J2EE Application Framework Version 3，Powerful, simple and easy to use

## 主要特征
* 依赖注入容器（DIContainer）
	* 组件装配及引用
	* 支付AOP编程
	* 注解编程（InjectField/Aop/ServiceTransaction） 
* JDBC持久层
	* 多种数据源（包括XA数据源）及连接池，灵活配置，支持数据库密码加密处理
	* 数据存取操作器，增删查改无需编写代码，简单易用（TableOperator/QueryOperator/UpdateOperator） 
	* 支持各种数据库的分页功能（包括：Mysql/Oracle/SqlServer/Sysbase/PostgreSql/FireBird/H2等）
	* 支持数据库序列功能
	* 组合查询功能，无需动态拼接查询条件，提高开发效率
	* 透明防止SQL注入
* Service业务层
	* Service RPC
		* 同步、异步调用，支持单连接双向通信
		* 长连接、短连接管理
		* 并发连接池，网络通信有效性保护
		* 负载均衡及失效转移 
	* Command过程框架
		* 本地、RPC、EJB透明结合
		* 支持同步及异步编程
	* Service事务透明
		* 支持JDBC及JTA跨数据源事务
		* 声明式事务支持（REQUIRED/REQUIRES_NEW）  