package com.beetle.framework.util.file;

import java.io.*;

/**
 * File utilities.
 */
public final class FileUtil {

	// ---------------------------------------------------------------- file
	// copy

	/**
	 * Buffer size (32KB) for file manipulation methods.
	 */
	public final static int FILE_BUFFER_SIZE = 32 * 1024;

	/**
	 * Copies one file to another. Existing file will be overwritten,
	 * 
	 * @param fileIn
	 *            input file
	 * @param fileOut
	 *            output file
	 * @throws IOException
	 */
	public static void copy(String fileIn, String fileOut) throws IOException {
		copy(new File(fileIn), new File(fileOut), FILE_BUFFER_SIZE);
	}

	/**
	 * Copies one file to another with specified buffer size. Existing file will
	 * be overwritten,
	 * 
	 * @param fileIn
	 *            input file
	 * @param fileOut
	 *            output file
	 * @param bufsize
	 *            size of the buffer used for copying
	 * @throws IOException
	 */
	public static void copy(String fileIn, String fileOut, int bufsize)
			throws IOException {
		copy(new File(fileIn), new File(fileOut), bufsize);
	}

	/**
	 * Copies one file to another. Existing file will be overwritten,
	 * 
	 * @param fileIn
	 *            input file
	 * @param fileOut
	 *            output file
	 * @throws IOException
	 */
	public static void copy(File fileIn, File fileOut) throws IOException {
		copy(fileIn, fileOut, FILE_BUFFER_SIZE);
	}

	/**
	 * Copies one file to another with specified buffer size. Existing file will
	 * be overwritten,
	 * 
	 * @param fileIn
	 *            input file
	 * @param fileOut
	 *            output file
	 * @param bufsize
	 *            size of the buffer used for copying
	 * @throws IOException
	 */
	public static void copy(File fileIn, File fileOut, int bufsize)
			throws IOException {
		FileInputStream in = null;
		FileOutputStream out = null;
		try {
			in = new FileInputStream(fileIn);
			out = new FileOutputStream(fileOut);
			byte[] buf = new byte[bufsize];
			int read;
			while ((read = in.read(buf, 0, bufsize)) != -1) {
				out.write(buf, 0, read);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
		}
	}

	// ---------------------------------------------------------------- string
	// utilities

	/**
	 * Buffer size (32KB) for file string methods.
	 */
	public final static int STRING_BUFFER_SIZE = 32 * 1024;

	/**
	 * Reads file's content into a String. Implicitly assumes that the file is
	 * in the default encoding.
	 * 
	 * @param fileName
	 *            name of the file to read from
	 * @return string with file content or null
	 * @throws IOException
	 */
	public static String readString(String fileName) throws IOException {
		return readString(new File(fileName), STRING_BUFFER_SIZE);
	}

	/**
	 * Reads file's content into a String. Implicitly assumes that the file is
	 * in the default encoding.
	 * 
	 * @param fileName
	 *            name of the file to read from
	 * @param bufferSize
	 *            buffer size
	 * @return string with file content or null
	 * @throws IOException
	 */
	public static String readString(String fileName, int bufferSize)
			throws IOException {
		return readString(new File(fileName), bufferSize);
	}

	/**
	 * Reads file's content into a String. Implicitly assumes that the file is
	 * in the default encoding.
	 * 
	 * @param file
	 *            file to read
	 * @return string with file content or null
	 * @throws IOException
	 */
	public static String readString(File file) throws IOException {
		return readString(file, STRING_BUFFER_SIZE);
	}

	/**
	 * Reads file's content into a String. Implicitly assumes that the file is
	 * in the default encoding.
	 * 
	 * @param file
	 *            file to read
	 * @param bufferSize
	 *            buffer size
	 * @return string with file content or null
	 * @throws IOException
	 */
	public static String readString(File file, int bufferSize)
			throws IOException {
		long fileLen = file.length();
		if (fileLen <= 0L) {
			if (file.exists() == true) {
				return ""; // empty file
			}
			return null; // all other file len problems
		}
		if (fileLen > Integer.MAX_VALUE) { // max String size
			throw new IOException("File too big for loading into a String!");
		}

		FileReader fr = null;
		BufferedReader brin = null;
		char[] buf = null;
		try {
			fr = new FileReader(file);
			brin = new BufferedReader(fr, bufferSize);
			int length = (int) fileLen;
			buf = new char[length];
			brin.read(buf, 0, length);
		} finally {
			if (brin != null) {
				brin.close();
				fr = null;
			}
			if (fr != null) {
				fr.close();
			}
		}
		return new String(buf);
	}

	/**
	 * Writes string to a file. Implicitly assumes that the file will be written
	 * the default encoding.
	 * 
	 * @param fileName
	 *            name of the destination file
	 * @param s
	 *            source string
	 * @throws IOException
	 */
	public static void writeString(String fileName, String s)
			throws IOException {
		writeString(new File(fileName), s, STRING_BUFFER_SIZE);
	}

	/**
	 * Writes string to a file. Implicitly assumes that the file will be written
	 * the default encoding.
	 * 
	 * @param fileName
	 *            name of the destination file
	 * @param s
	 *            source string
	 * @param bufferSize
	 *            buffer size
	 * @throws IOException
	 */
	public static void writeString(String fileName, String s, int bufferSize)
			throws IOException {
		writeString(new File(fileName), s, bufferSize);
	}

	/**
	 * Writes string to a file. Implicitly assumes that the file will be written
	 * the default encoding.
	 * 
	 * @param file
	 *            destination file
	 * @param s
	 *            source string
	 * @throws IOException
	 */
	public static void writeString(File file, String s) throws IOException {
		writeString(file, s, STRING_BUFFER_SIZE);
	}

	/**
	 * Writes string to a file. Implicitly assumes that the file will be written
	 * the default encoding.
	 * 
	 * @param file
	 *            destination file
	 * @param s
	 *            source string
	 * @param bufferSize
	 *            buffer size
	 * @throws IOException
	 */
	public static void writeString(File file, String s, int bufferSize)
			throws IOException {
		FileWriter fw = null;
		BufferedWriter out = null;
		if (s == null) {
			return;
		}
		try {
			fw = new FileWriter(file);
			out = new BufferedWriter(fw, bufferSize);
			out.write(s);
		} finally {
			if (out != null) {
				out.close();
				fw = null;
			}
			if (fw != null) {
				fw.close();
			}
		}
	}

	// ---------------------------------------------------------------- unicode
	// string utilities

	/**
	 * Reads file's content into a String.
	 * 
	 * @param fileName
	 *            source file name
	 * @param encoding
	 *            java encoding string
	 * @return string with file content or null
	 * @throws IOException
	 */
	public static String readString(String fileName, String encoding)
			throws IOException {
		return readString(new File(fileName), STRING_BUFFER_SIZE, encoding);
	}

	/**
	 * Reads file's content into a String.
	 * 
	 * @param fileName
	 *            source file name
	 * @param bufferSize
	 *            buffer size
	 * @param encoding
	 *            java encoding string
	 * @return string with file content or null
	 * @throws IOException
	 */
	public static String readString(String fileName, int bufferSize,
			String encoding) throws IOException {
		return readString(new File(fileName), bufferSize, encoding);
	}

	/**
	 * Reads file's content into a String.
	 * 
	 * @param file
	 *            source file
	 * @param encoding
	 *            java encoding string
	 * @return string with file content or null
	 * @throws IOException
	 */
	public static String readString(File file, String encoding)
			throws IOException {
		return readString(file, STRING_BUFFER_SIZE, encoding);
	}

	/**
	 * Reads file's content into a String. This is a bit different
	 * implementation than other readString() method, since the number of
	 * characters in the file is not known. This currently only affest the value
	 * of the maximum file size.
	 * 
	 * @param file
	 *            source file
	 * @param bufferSize
	 *            buffer size
	 * @param encoding
	 *            java encoding string
	 * @return string with file content or null
	 * @throws IOException
	 */
	public static String readString(File file, int bufferSize, String encoding)
			throws IOException {
		long fileLen = file.length();
		if (fileLen <= 0L) {
			if (file.exists() == true) {
				return ""; // empty file
			}
			return null; // all other file len problems
		}
		if (fileLen > Integer.MAX_VALUE) { // max String size
			throw new IOException("File too big for loading into a String!");
		}

		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader brin = null;

		int length = (int) fileLen;
		char[] buf = null;
		int realSize = 0;
		try {
			fis = new FileInputStream(file);
			isr = new InputStreamReader(fis, encoding);
			brin = new BufferedReader(isr, bufferSize);
			buf = new char[length]; // this is the weakest point, since real
			// file size is not determined
			int c; // anyhow, this is the fastest way doing this
			while ((c = brin.read()) != -1) {
				buf[realSize] = (char) c;
				realSize++;
			}
		} finally {
			if (brin != null) {
				brin.close();
				isr = null;
				fis = null;
			}
			if (isr != null) {
				isr.close();
				fis = null;
			}
			if (fis != null) {
				fis.close();
			}
		}
		return new String(buf, 0, realSize);
	}

	/**
	 * Writes string to a file.
	 * 
	 * @param fileName
	 *            destination file name
	 * @param s
	 *            source string
	 * @param encoding
	 *            java encoding string
	 * @throws IOException
	 */
	public static void writeString(String fileName, String s, String encoding)
			throws IOException {
		writeString(new File(fileName), s, STRING_BUFFER_SIZE, encoding);
	}

	/**
	 * Writes string to a file.
	 * 
	 * @param fileName
	 *            destination file name
	 * @param s
	 *            source string
	 * @param bufferSize
	 *            buffer size
	 * @param encoding
	 *            java encoding string
	 * @throws IOException
	 */
	public static void writeString(String fileName, String s, int bufferSize,
			String encoding) throws IOException {
		writeString(new File(fileName), s, bufferSize, encoding);
	}

	/**
	 * Writes string to a file.
	 * 
	 * @param file
	 *            destination file
	 * @param s
	 *            source string
	 * @param encoding
	 *            java encoding string
	 * @throws IOException
	 */
	public static void writeString(File file, String s, String encoding)
			throws IOException {
		writeString(file, s, STRING_BUFFER_SIZE, encoding);
	}

	/**
	 * Writes string to a file.
	 * 
	 * @param file
	 *            destination file
	 * @param s
	 *            source string
	 * @param bufferSize
	 *            buffer size
	 * @param encoding
	 *            java encoding string
	 * @throws IOException
	 */
	public static void writeString(File file, String s, int bufferSize,
			String encoding) throws IOException {
		if (s == null) {
			return;
		}
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter out = null;
		try {
			fos = new FileOutputStream(file);
			osw = new OutputStreamWriter(fos, encoding);
			out = new BufferedWriter(osw, bufferSize);
			out.write(s);
		} finally {
			if (out != null) {
				out.close();
				osw = null;
				fos = null;
			}
			if (osw != null) {
				osw.close();
				fos = null;
			}
			if (fos != null) {
				fos.close();
			}
		}
	}

	// ---------------------------------------------------------------- object
	// serialization

	/**
	 * Buffer size (32KB) for object serialization methods.
	 */
	public final static int OBJECT_BUFFER_SIZE = 32 * 1024;

	/**
	 * Writes serializable object to a file. Existing file will be overwritten.
	 * 
	 * @param f
	 *            name of the destination file
	 * @param o
	 *            object to write
	 * @throws IOException
	 */
	public static void writeObject(String f, Object o) throws IOException {
		writeObject(f, o, OBJECT_BUFFER_SIZE);
	}

	/**
	 * Writes serializable object to a file with specified buffer size. Existing
	 * file will be overwritten.
	 * 
	 * @param f
	 *            name of the destination file
	 * @param o
	 *            object to write
	 * @param bufferSize
	 *            buffer size used for writing
	 * @throws IOException
	 */
	public static void writeObject(String f, Object o, int bufferSize)
			throws IOException {
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(f);
			bos = new BufferedOutputStream(fos, bufferSize);
			oos = new ObjectOutputStream(bos);
			oos.writeObject(o);
		} finally {
			if (oos != null) {
				oos.close();
				bos = null;
				fos = null;
			}
			if (bos != null) {
				bos.close();
				fos = null;
			}
			if (fos != null) {
				fos.close();
			}
		}
	}

	/**
	 * Reads seralized object from the file.
	 * 
	 * @param f
	 *            name of the source file
	 * @return serialized object from the file.
	 * @throws IOException
	 */
	public static Object readObject(String f) throws IOException,
			ClassNotFoundException, FileNotFoundException {
		return readObject(f, OBJECT_BUFFER_SIZE);
	}

	/**
	 * Reads seralized object from the file with specified buffer size
	 * 
	 * @param f
	 *            name of the source file
	 * @param bufferSize
	 *            size of buffer used for reading
	 * @return serialized object from the file.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws FileNotFoundException
	 */
	public static Object readObject(String f, int bufferSize)
			throws IOException, ClassNotFoundException, FileNotFoundException {
		Object result = null;
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(f);
			bis = new BufferedInputStream(fis, bufferSize);
			ois = new ObjectInputStream(bis);
			result = ois.readObject();
		} finally {
			if (ois != null) {
				ois.close();
				bis = null;
				fis = null;
			}
			if (bis != null) {
				bis.close();
				fis = null;
			}
			if (fis != null) {
				fis.close();
			}
		}
		return result;
	}

	// ---------------------------------------------------------------- byte
	// array

	/**
	 * Reads file content as byte array.
	 * 
	 * @param s
	 *            file name
	 * @return file content
	 * @throws IOException
	 */
	public static final byte[] readBytes(String s) throws IOException {
		return readBytes(new File(s));
	}

	/**
	 * Reads file content as byte array.
	 * 
	 * @param file
	 *            file to read
	 * @return file content
	 * @throws IOException
	 */
	public static final byte[] readBytes(File file) throws IOException {
		FileInputStream fileinputstream = new FileInputStream(file);
		try {
			long l = file.length();
			if (l > Integer.MAX_VALUE) {
				throw new IOException(
						"File too big for loading into a byte array!");
			}
			byte byteArray[] = new byte[(int) l];
			int i = 0;
			for (int j; (i < byteArray.length)
					&& (j = fileinputstream.read(byteArray, i, byteArray.length
							- i)) >= 0; i += j) {
				;
			}
			if (i < byteArray.length) {
				throw new IOException("Could not completely read the file "
						+ file.getName());
			}
			return byteArray;
		} finally {
			fileinputstream.close();
		}

	}

	public static void writeBytes(String filename, byte[] source)
			throws IOException {
		if (source == null) {
			return;
		}
		writeBytes(new File(filename), source, 0, source.length);
	}

	public static void writeBytes(File file, byte[] source) throws IOException {
		if (source == null) {
			return;
		}
		writeBytes(file, source, 0, source.length);
	}

	public static void writeBytes(String filename, byte[] source, int offset,
			int len) throws IOException {
		writeBytes(new File(filename), source, offset, len);
	}

	public static void writeBytes(File file, byte[] source, int offset, int len)
			throws IOException {
		if (len < 0) {
			throw new IOException("File size is negative!");
		}
		if (offset + len > source.length) {
			len = source.length - offset;
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			fos.write(source, offset, len);
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}

}
