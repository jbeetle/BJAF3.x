package plugin;

import demo.XXXApp.plugin.IXXXPlugin;

public class AModulePlugin implements IXXXPlugin {

	@Override
	public void deal(String word) {
		System.out.println("AModulePlugin[" + word + "]");
	}

}
