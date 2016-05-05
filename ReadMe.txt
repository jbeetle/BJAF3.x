欢迎使用甲壳虫应用开发框架(ver2.2.4)
*框架开发包的目录说明如下：
	sdk
	├─dist	发布目录，包括编译包和框架相关的配置文件
	├─docs	文档目录，包括开发指南、和JavaDoc文档
	├─lib	框架引用的第3方类库
	├─sample	开发示例
	└─src 框架源代码
*编译说明
	dist目录包括了发布的beetle.jar编译包，您也可以自己编译源代码生成beetle.jar包。
	配置好ant和jdk的home目录。
	**执行："ant genJar" 生成beetle.jar框架包
	**执行："ant javadoc" 生成框架的java doc api文档，为utf-8编码，浏览器要改成utf-8编码才能正常显示，否则乱码
	**执行："ant genDbCode"会根据数据库表结构自动生成持久层的源代码（vo\dao\daoImp），
	   配置dist目录的DBConfig.xml\genCodeConf.properties\genCodeConf.xml文件，详细参考开发指南
*Sample示例	
	**WebDemo，Web层框架功能示例，执行“ant genWebDemoWar”在output目录下生成webdemo.war，可直接部署到Tomcat容器下执行。
	**PsDemo，持久层功能示例，执行："ant runPsDemo"在本地执行sample目录下的PsDemo例子。
	**AppDemo，是基于RPC自己实现应用服务器的例子。
	**dwz，dwz是个不错国产的web ui框架，此例子演示dwz与bjaf结合使用，涉及到web层、业务层和持久层，是个综合例子；
	   执行："ant genDwzWar"构建sample目录下dwz例子，生成dwz.war，可发布到tomcat容器执行
	   {注意先配置数据库，参考WEB-INF\config\下面的数据库配置，演示建议使用h2数据库(http://www.h2database.com/)
	   建立一个"demodb"数据库，用户名：admin 密码：760224 导入demodb_h2.sql脚步即可}
	 **WebConsole，是基于Bootstrap（http://twitter.github.io/bootstrap/）开发的例子，功能与DWZ一致，配置数据库与DWZ例子一样。
		执行：“ant genWebConsoleWar”生成WebConsole.war，发布到tomcat容器执行
*技术支持：
	http://www.beetlesoft.net/j2ee/index.html
	http://code.google.com/p/beetle-j2ee-application-framework/
	yuhaodong@gmail.com
Enjoy it!
余浩东 2013-05-17


	
	
	
	