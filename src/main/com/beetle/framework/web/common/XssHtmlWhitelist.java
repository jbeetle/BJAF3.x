package com.beetle.framework.web.common;

public enum XssHtmlWhitelist {
	/**
	 * 只保留了文本
	 */
	none,
	/**
	 * 简单的文本属性b, em, i, strong, u
	 */
	simpleText,
	/**
	 * a, b, blockquote, br, cite, code, dd, dl, dt, em, i, li, ol, p, pre,
	 * q,small,strike, strong, sub, sup, u, ul
	 */
	basic,
	/**
	 * a, b, blockquote, br, cite, code, dd, dl,dt, em, i, li, ol, p, pre, q,
	 * small, strike, strong, sub, sup, u, ul、img、src
	 */
	basicWithImages,
	/**
	 * a, b, blockquote,br, caption, cite, code, col, colgroup, dd, dl, dt, em,
	 * h1, h2, h3, h4, h5, h6,i, img, li, ol, p, pre, q, small, strike, strong,
	 * sub, sup, table, tbody, td,tfoot, th, thead, tr, u, ul
	 */
	relaxed, 
	/**
	 * 不做过滤处理
	 */
	noDeal
}
