package com.beetle.framework.resource.dic.def;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * 服务方法事务注解
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ServiceTransaction {
	public static enum Manner {
		/**
		 * 需要事务，如果已存在事务，则参与，没有则开一个新的事务
		 */
		REQUIRED(0),
		/**
		 * 无论是否有事务存在，都会开启一个独立的新事务，不会参与原来的事务
		 */
		REQUIRES_NEW(1);

		private final int value;

		private Manner(int value) {
			this.value = value;
		}

		public int value() {
			return this.value;
		}
	}

	public abstract Manner manner() default Manner.REQUIRED;
}
