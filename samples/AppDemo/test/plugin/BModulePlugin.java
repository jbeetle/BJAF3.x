package plugin;

import demo.XXXApp.plugin.IXXXPlugin;

public class BModulePlugin implements IXXXPlugin {

	@Override
	public void deal(String word) {
		System.out.println("BModulePlugin[" + word + "]");
	}

}
