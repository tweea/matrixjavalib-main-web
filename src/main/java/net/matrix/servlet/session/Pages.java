/*
 * $Id: Pages.java 902 2014-01-23 01:46:10Z tweea@263.net $
 * Copyright(C) 2008 Matrix
 * All right reserved.
 */
package net.matrix.servlet.session;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import net.matrix.lang.Objects2;

/**
 * 分页工具。
 */
public final class Pages {
	public static final String KEY_KEY = "page_key";

	public static final String URL_KEY = "page_url";

	public static final String TATAL_KEY = "page_total";

	public static final String INDEX_KEY = "page_index";

	public static final String SIZE_KEY = "page_size";

	/**
	 * 阻止实例化。
	 */
	private Pages() {
	}

	/**
	 * 保存分页信息。
	 * 
	 * @param request
	 *            请求
	 * @param info
	 *            分页信息
	 */
	public static void setPageInfos(final HttpServletRequest request, final PagingInfo info) {
		request.setAttribute(KEY_KEY, info.getKey());
		if (StringUtils.isEmpty(info.getUrl())) {
			request.setAttribute(URL_KEY, request.getServletPath());
		} else {
			request.setAttribute(URL_KEY, info.getUrl());
		}
		request.setAttribute(TATAL_KEY, info.getTotal());
		request.setAttribute(INDEX_KEY, info.getPageIndex());
		request.setAttribute(SIZE_KEY, info.getPageSize());
	}

	/**
	 * 提取分页信息，默认为每页一条记录。
	 * 
	 * @param request
	 *            请求
	 * @return 分页信息
	 */
	public static PagingInfo getPageInfos(final HttpServletRequest request) {
		return getPageInfos(request, 1);
	}

	/**
	 * 提取分页信息。
	 * 
	 * @param request
	 *            请求
	 * @param defaultPageSize
	 *            默认每页记录数
	 * @return 分页信息
	 */
	public static PagingInfo getPageInfos(final HttpServletRequest request, final int defaultPageSize) {
		PagingInfo info = new PagingInfo();
		info.setKey(getPageKey(request));
		info.setUrl(getUrl(request));
		info.setTotal(getTotal(request));
		info.setPageIndex(getPageIndex(request));
		info.setPageSize(getPageSize(request, defaultPageSize));
		return info;
	}

	private static String getPageKey(final HttpServletRequest request) {
		String key = request.getParameter(KEY_KEY);
		if (StringUtils.isEmpty(key)) {
			key = (String) request.getAttribute(KEY_KEY);
		}
		if (StringUtils.isEmpty(key)) {
			key = UUID.randomUUID().toString();
		}
		request.setAttribute(KEY_KEY, key);
		return key;
	}

	private static String getUrl(final HttpServletRequest request) {
		String url = request.getParameter(URL_KEY);
		if (StringUtils.isEmpty(url)) {
			url = (String) request.getAttribute(URL_KEY);
		}
		if (StringUtils.isEmpty(url)) {
			url = request.getServletPath();
		}
		request.setAttribute(URL_KEY, url);
		return url;
	}

	private static long getTotal(final HttpServletRequest request) {
		long total;
		String page = request.getParameter(TATAL_KEY);
		if (StringUtils.isEmpty(page)) {
			total = Objects2.isNull((Long) request.getAttribute(TATAL_KEY), 0L);
		} else {
			try {
				total = Long.parseLong(page);
			} catch (NumberFormatException e) {
				total = 0L;
			}
		}
		request.setAttribute(TATAL_KEY, total);
		return total;
	}

	private static int getPageIndex(final HttpServletRequest request) {
		int pageIndex;
		String page = request.getParameter(INDEX_KEY);
		if (StringUtils.isEmpty(page)) {
			pageIndex = Objects2.isNull((Integer) request.getAttribute(INDEX_KEY), 0);
		} else {
			try {
				pageIndex = Integer.parseInt(page);
			} catch (NumberFormatException e) {
				pageIndex = 0;
			}
		}
		request.setAttribute(INDEX_KEY, pageIndex);
		return pageIndex;
	}

	private static int getPageSize(final HttpServletRequest request, final int defaultValue) {
		if (defaultValue <= 0) {
			throw new IllegalArgumentException("每页显示数目必须大于等于1");
		}
		int pageSize;
		String page = request.getParameter(SIZE_KEY);
		if (StringUtils.isEmpty(page)) {
			pageSize = Objects2.isNull((Integer) request.getAttribute(SIZE_KEY), defaultValue);
		} else {
			try {
				pageSize = Integer.parseInt(page);
			} catch (NumberFormatException e) {
				pageSize = 0;
			}
		}
		request.setAttribute(SIZE_KEY, pageSize);
		return pageSize;
	}
}
