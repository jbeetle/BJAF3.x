package com.beetle.framework.util.file;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipUtil {
	/**
	 * compressStr
	 * 
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public static String compressStr(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(out);
		gzip.write(str.getBytes());
		gzip.close();
		return out.toString("ISO-8859-1");
	}

	/**
	 * uncompressStr
	 * 
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public static String uncompressStr(String str) throws IOException {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ByteArrayInputStream in = new ByteArrayInputStream(
				str.getBytes("ISO-8859-1"));
		GZIPInputStream gunzip = new GZIPInputStream(in);
		byte[] buffer = new byte[256];
		int n;
		while ((n = gunzip.read(buffer)) >= 0) {
			out.write(buffer, 0, n);
		}
		// toString()使用平台默认编码，也可以显式的指定如toString("GBK")
		return out.toString();
	}

	/**
	 * Unpacks a zip file to the target directory.
	 * 
	 * @param zipFile
	 *            zip file
	 * @param destDir
	 *            destination directory
	 * @throws IOException
	 */
	public static void unzip(File zipFile, File destDir) throws IOException {
		ZipFile zip = new ZipFile(zipFile);
		try {
			Enumeration<?> en = zip.entries();
			int bufSize = 8196;
			while (en.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) en.nextElement();
				File file = (destDir != null) ? new File(destDir,
						entry.getName()) : new File(entry.getName());
				if (entry.isDirectory()) {
					if (!file.mkdirs()) {
						if (file.isDirectory() == false) {
							throw new IOException("Error creating directory: "
									+ file);
						}
					}
				} else {
					unzi(zip, bufSize, entry, file);
				}
			}
		} finally {
			zip.close();
		}
	}

	private static void unzi(ZipFile zip, int bufSize, ZipEntry entry, File file)
			throws IOException, FileNotFoundException {
		File parent = file.getParentFile();
		if (parent != null && !parent.exists()) {
			if (!parent.mkdirs()) {
				if (file.isDirectory() == false) {
					throw new IOException("Error creating directory: " + parent);
				}
			}
		}

		InputStream in = zip.getInputStream(entry);
		try {
			OutputStream out = new BufferedOutputStream(new FileOutputStream(
					file), bufSize);
			try {
				copyPipe(in, out, bufSize);
			} finally {
				out.close();
			}
		} finally {
			in.close();
		}
	}

	private static void copyPipe(InputStream in, OutputStream out,
			int bufSizeHint) throws IOException {
		int read;
		byte[] buf = new byte[bufSizeHint];
		while ((read = in.read(buf, 0, bufSizeHint)) >= 0) {
			out.write(buf, 0, read);
		}
		out.flush();
	}

}
