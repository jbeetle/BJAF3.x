package beetle.generate.genbean;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.beetle.framework.util.structure.DocumentTemplate;

import beetle.generate.genbean.II.CustomComparator;

public class GenDaoConfigFile {
	public final static Map<String, II> IIMAP = new HashMap<String, II>();

	public void gen(String outdir, String homedir) {
		DocumentTemplate dt = new DocumentTemplate(homedir);
		List<II> mylist = new ArrayList<II>();
		Iterator<II> it = IIMAP.values().iterator();
		while (it.hasNext()) {
			II ii = it.next();
			System.out.println(ii);
			if (ii.getImp() != null && ii.getImp().length() > 1) {
				mylist.add(ii);
			}
		}
		// Collections.sort(mylist);
		Collections.sort(mylist, new CustomComparator());
		Map m = new HashMap();
		m.put("iilist", mylist);
		FileWriter fw;
		try {
			fw = new FileWriter(new File(outdir + "DAOConfig.xml"));
			dt.process(m, "DAOConfig.ftl", fw);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			dt.clearCache();
			IIMAP.clear();
		}
	}
}
