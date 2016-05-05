Welcome to use the Beetle J2EE Application Framework(BJAF ver2.2.4) 
* The SDK directory description is as follows: 
	SDK 
	�� �� dist	Build packages directory, including framework and related configuration file .
	�� �� docs	Document directory, including the development of guidelines, and JavaDoc documentation .
	�� �� lib	BJAF reference of third-party libraries .
	�� �� sample	development samples. 
	�� �� src	 BJAF source code .
* build instructions 
	Dist directory including the distribution of 'beetle.jar' Compiled package, you can also compile the source code to generate beetle.jar by yourself. 
	Configured the ant and the JDK's home directory. 
	* * execute: "ant genJar" generation beetle.jar package.
	* * execute: "ant javadoc" generate Java doc framework API documentation, as the utf-8 encoding, the browser should be displayed properly to utf-8 encoding, or gibberish 
	* * execute: "ant genDbCode" depending on the database table structure to automatically generate the persistence layer source code (vo \ dao \ daoImp), 
							Configure the DBConfig dist directory. XML \ genCodeConf. Properties \ genCodeConf XML file and a detailed reference guide for development 
* Samples
	* * WebDemo, Web layer framework function example, execute "ant genWebDemoWar" generated in the output directory WebDemo. war, 
			can be directly deployed to perform the Tomcat container. 
	* * PsDemo, Persistence layer function example, execute: "ant runPsDemo" in local PsDemo example of sample directory. 
	* *AppDemo,  Based on RPC implementation example of the application server. 
	* * DWZ, DWZ is a good web UI framework whick created by chinese, this example demonstrates DWZ combined with bjaf, 
		involving the web layer, business layer and persistence layer, is a comprehensive example; 
		execute: "ant genDwzWar" build sample directory DWZ example, generate DWZ. War, can be published to the tomcat container 
		{note: first configuration database, refer to the WEB web-inf \ config \ the following database configuration, 
		demonstration recommend using h2 database (http://www.h2database.com/) 
		To create a "demodb" database, user name: admin, password: 760224 import demodb_h2. SQL steps can} 
	* * WebConsole, Based on the Bootstrap (http://twitter.github.io/bootstrap/) development example, 
		the function is consistent with the DWZ, configure the database to DWZ example. 
		execute: "ant genWebConsoleWar" generate WebConsole. War, published to the tomcat container 
* technical support: 
	http://www.mc2e.cc/j2ee/index.html 
	https://github.com/jbeetle/BJAF3.x
	yuhaodong@gmail.com 
Enjoy it! 
Yu Haodong 2016-05-05 
