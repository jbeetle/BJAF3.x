# BJAF3.x
Beetle J2EE Application Framework Version 3，Powerful, simple and easy to use

## 主要特征
* 依赖注入容器（DIContainer）
	* 组件装配及引用
	* 支付AOP编程
	* 注解编程（InjectField/Aop/ServiceTransaction） 
* 持久层
	* 多种数据源（包括XA数据源）及连接池，灵活配置，支持数据库密码加密处理
	* 数据存取操作器，增删查改无需编写代码，简单易用（TableOperator/QueryOperator/UpdateOperator） 
	* 支持各种数据库的分页功能（包括：Mysql/Oracle/SqlServer/Sysbase/PostgreSql/FireBird/H2等）
	* 支持数据库序列功能
	* 组合查询功能，无需动态拼接查询条件，提高开发效率
	* 透明防止SQL注入
	* NOSQL支持Redis的常用封装，支持String\Map\List\自定义DTO对象简易操作及本地内存优化及连接池优化
* Service业务层
	* Service RPC
		* 同步、异步调用，支持单连接双向通信
		* 长连接、短连接管理
		* 并发连接池，网络通信有效性保护
		* 负载均衡及失效转移 
		* 应用服务器框架（请求管理，内存监控，插件机制，控制台命令等）
	* Command过程框架
		* 本地、RPC、EJB透明结合
		* 支持同步及异步编程
	* Service事务透明
		* 支持JDBC及JTA跨数据源事务
		* 声明式事务支持（REQUIRED/REQUIRES_NEW）  
	* Job计划调度
		* 支持Cron时间表达式
		* 热部署，Job生命周期管理
* Web表示层
	* 标准MVC框架，简单控制器及视图编程，提供各类型控制器，并特供如：防止重复提交、响应结果缓存、get请求方法禁止等功能
	* 支持WebService开发，请求结果动态转换Java对象为JSON或XML格式
	* 支持JWT的OpenAPI的Web服务代理
	* 请求动态缓存，可根据每个请求控制器的特性合理配置相应的缓存机制
	* 自带http客户端，方便调用WebService
	* 支持上传、转PDF文档、验证码、JChat等常见功能
	* 透明防止XSS攻击
	* 请求URL重写（rewirite）
* 工具类组件
	* 系统日志
	* 线程并发处理框架
	* 常见设计模式
	* 队列/缓存/结构等模式化组件
	* 加密工具  
	* 其它常见工具类
	
##扩展组件
* 安全框架
	* 集成Shiro框架
	* 支持用户、角色、权限经典模式（参考sql文件查看表结构）
	* 支持用户登录验证，登录次数限制、锁用户、密码加盐加密等 
* 会记账本（金融类平台核心组件）
	* 账户、科目、流水
	* 借贷复式记账
	* 试算平衡
			   