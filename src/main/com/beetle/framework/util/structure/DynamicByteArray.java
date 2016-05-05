package com.beetle.framework.util.structure;

public class DynamicByteArray {
	private static int INITIAL_CAPACITY = 10;;
	private int totalCapacity;
	private int size;
	private byte[] b;

	public DynamicByteArray(int initialCapacity) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Capacity: "
					+ initialCapacity);
		this.b = new byte[initialCapacity];
		totalCapacity = initialCapacity;
		size = 0;
	}

	public DynamicByteArray() {
		this(INITIAL_CAPACITY);
	}

	public int size() {
		return size;
	}

	public byte[] getBytes() {
		return b;
	}

	public void add(byte[] incrb, int flag) {
		int iAdd = flag + size - totalCapacity;
		if (iAdd > 0) {
			totalCapacity = flag + size;
			byte[] oldByte = this.b;
			this.b = new byte[totalCapacity];
			System.arraycopy(oldByte, 0, this.b, 0, oldByte.length);
		}
		System.arraycopy(incrb, 0, this.b, size, flag);
		size = size + flag;
	}
}
