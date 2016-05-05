package beetle.generate.genbean;

import java.util.Comparator;

public class II {
	public static class CustomComparator implements Comparator<II> {

		@Override
		public int compare(II o1, II o2) {
			return o1.face.compareTo(o2.face);
		}

	}

	@Override
	public String toString() {
		return "II [face=" + face + ", imp=" + imp + "]";
	}

	private String face;
	private String imp;

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}

	public String getImp() {
		return imp;
	}

	public void setImp(String imp) {
		this.imp = imp;
	}

	public II() {
		super();
	}

}
