package com.beetle.uidemo.web.common;

import java.util.List;

public class DataTableResponse implements java.io.Serializable {

	/**
	 * <pre>
	 * http://www.datatables.net/usage/server-side
	 * int	iTotalRecords	Total records, before filtering (i.e. the total number of records in the database)
	 * int	iTotalDisplayRecords	Total records, after filtering (i.e. the total number of records after filtering has been applied - not just the number of records being returned in this result set)
	 * string	sEcho	An unaltered copy of sEcho sent from the client side. This parameter will change with each draw (it is basically a draw count) - so it is important that this is implemented. Note that it strongly recommended for security reasons that you 'cast' this parameter to an integer in order to prevent Cross Site Scripting (XSS) attacks.
	 * string	sColumns	Optional - this is a string of column names, comma separated (used in combination with sName) which will allow DataTables to reorder data on the client-side if required for display. Note that the number of column names returned must exactly match the number of columns in the table. For a more flexible JSON format, please consider using mDataProp.
	 * array	aaData	The data in a 2D array. Note that you can change the name of this parameter with sAjaxDataProp.
	 * string	DT_RowId	Set the ID property of the TR node for this row
	 * string	DT_RowClass	Add the this class to the TR node for this row
	 * </pre>
	 */
	private static final long serialVersionUID = 1L;
	private int iTotalRecords;
	private int iTotalDisplayRecords;
	private String sEcho;
	private String sColumns;
	private List<?> aaData;

	public DataTableResponse() {
		super();
		this.sColumns = "";
	}

	public int getiTotalRecords() {
		return iTotalRecords;
	}

	public void setiTotalRecords(int iTotalRecords) {
		this.iTotalRecords = iTotalRecords;
	}

	public int getiTotalDisplayRecords() {
		return iTotalDisplayRecords;
	}

	public void setiTotalDisplayRecords(int iTotalDisplayRecords) {
		this.iTotalDisplayRecords = iTotalDisplayRecords;
	}

	public String getsEcho() {
		return sEcho;
	}

	public void setsEcho(String sEcho) {
		this.sEcho = sEcho;
	}

	public String getsColumns() {
		return sColumns;
	}

	public void setsColumns(String sColumns) {
		this.sColumns = sColumns;
	}

	public List<?> getAaData() {
		return aaData;
	}

	public void setAaData(List<?> aaData) {
		this.aaData = aaData;
	}

	@Override
	public String toString() {
		return "DataTableResponse [iTotalRecords=" + iTotalRecords
				+ ", iTotalDisplayRecords=" + iTotalDisplayRecords + ", sEcho="
				+ sEcho + ", sColumns=" + sColumns + ", aaData=" + aaData + "]";
	}

}
