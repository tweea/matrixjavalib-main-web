/*
 * $Id: HttpServlets.java 902 2014-01-23 01:46:10Z tweea@263.net $
 * Copyright(C) 2008 Matrix
 * All right reserved.
 */
package net.matrix.servlet.session;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import net.matrix.text.DateFormatHelper;
import net.matrix.util.IterableEnumeration;

/**
 * 参数和请求工具。
 */
public final class HttpServlets {
	private static final String ERROR_KEY = "error_key";

	private static final String MESSAGE_KEY = "message_key";

	private static final String BACK_URI_KEY = "back_uri";

	private static final String STORE_URI_KEY = "store_uri";

	/**
	 * 阻止实例化。
	 */
	private HttpServlets() {
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// 消息处理方法
	// /////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 附加消息内容。
	 * 
	 * @param request
	 *            请求
	 * @param message
	 *            消息内容
	 */
	public static void addMessage(final HttpServletRequest request, final String message) {
		StringBuilder sb = (StringBuilder) request.getAttribute(MESSAGE_KEY);
		if (sb == null) {
			sb = new StringBuilder();
			request.setAttribute(MESSAGE_KEY, sb);
		}
		sb.append(message);
	}

	/**
	 * 获取消息内容。
	 * 
	 * @param request
	 *            请求
	 * @return 消息内容
	 */
	public static String getMessage(final HttpServletRequest request) {
		StringBuilder sb = (StringBuilder) request.getAttribute(MESSAGE_KEY);
		if (sb == null) {
			return "";
		}
		return sb.toString();
	}

	/**
	 * 附加错误内容。
	 * 
	 * @param request
	 *            请求
	 * @param message
	 *            错误内容
	 */
	public static void addError(final HttpServletRequest request, final String message) {
		StringBuilder sb = (StringBuilder) request.getAttribute(ERROR_KEY);
		if (sb == null) {
			sb = new StringBuilder();
			request.setAttribute(ERROR_KEY, sb);
		}
		sb.append(message);
	}

	/**
	 * 获取消息内容。
	 * 
	 * @param request
	 *            请求
	 * @return 消息内容
	 */
	public static String getError(final HttpServletRequest request) {
		StringBuilder sb = (StringBuilder) request.getAttribute(ERROR_KEY);
		if (sb == null) {
			return "";
		}
		return sb.toString();
	}

	/**
	 * 设置回退 URI。
	 * 
	 * @param request
	 *            请求
	 * @param uri
	 *            回退 URI
	 */
	public static void setBackURI(final HttpServletRequest request, final String uri) {
		request.setAttribute(BACK_URI_KEY, uri);
	}

	/**
	 * 获取回退 URI。
	 * 
	 * @param request
	 *            请求
	 * @return 回退 URI
	 */
	public static String getBackURI(final HttpServletRequest request) {
		return (String) request.getAttribute(BACK_URI_KEY);
	}

	/**
	 * 设置请求 URI。
	 * 
	 * @param request
	 *            请求
	 * @param requestURI
	 *            请求 URI
	 */
	public static void setRequestURI(final HttpServletRequest request, final String requestURI) {
		request.getSession(true).setAttribute(STORE_URI_KEY, requestURI);
	}

	/**
	 * 保存当前请求 URI。
	 * 
	 * @param request
	 *            请求
	 */
	public static void storeRequestURI(final HttpServletRequest request) {
		request.getSession(true).setAttribute(STORE_URI_KEY, request.getRequestURI());
	}

	/**
	 * 获取请求 URI。
	 * 
	 * @param request
	 *            请求
	 * @return 请求 URI
	 */
	public static String getRequestURI(final HttpServletRequest request) {
		return (String) request.getSession(true).getAttribute(STORE_URI_KEY);
	}

	// /////////////////////////////////////////////////////////////////////////////////////
	// 参数获取方法
	// /////////////////////////////////////////////////////////////////////////////////////
	/**
	 * 获取字符串参数。
	 * 
	 * @param request
	 *            请求
	 * @param property
	 *            参数名
	 * @return 参数内容
	 */
	public static String getParameter(final HttpServletRequest request, final String property) {
		String value = request.getParameter(property);
		if (value == null) {
			return "";
		}
		return value;
	}

	/**
	 * 获取整形参数。
	 * 
	 * @param request
	 *            请求
	 * @param property
	 *            参数名
	 * @return 参数内容
	 */
	public static int getIntParameter(final HttpServletRequest request, final String property) {
		String value = request.getParameter(property);
		if (StringUtils.isBlank(value)) {
			return 0;
		}
		return Integer.parseInt(value);
	}

	/**
	 * 获取长整形参数。
	 * 
	 * @param request
	 *            请求
	 * @param property
	 *            参数名
	 * @return 参数内容
	 */
	public static long getLongParameter(final HttpServletRequest request, final String property) {
		String value = request.getParameter(property);
		if (StringUtils.isBlank(value)) {
			return 0L;
		}
		return Long.parseLong(value);
	}

	/**
	 * 获取 BigDecimal 参数。
	 * 
	 * @param request
	 *            请求
	 * @param property
	 *            参数名
	 * @return 参数内容
	 */
	public static BigDecimal getBigDecimalParameter(final HttpServletRequest request, final String property) {
		String value = request.getParameter(property);
		if (StringUtils.isBlank(value)) {
			return BigDecimal.ZERO;
		}
		return new BigDecimal(value);
	}

	/**
	 * 获取日期参数。
	 * 
	 * @param request
	 *            请求
	 * @param property
	 *            参数名
	 * @param format
	 *            日期格式
	 * @return 参数内容
	 */
	public static Calendar getCalendarParameter(final HttpServletRequest request, final String property, final String format) {
		String value = request.getParameter(property);
		if (StringUtils.isBlank(value)) {
			return null;
		}
		return DateFormatHelper.parse(value, format);
	}

	/**
	 * 取得带相同前缀的参数。
	 * 返回的结果的参数名已去除前缀。
	 * 
	 * @param request
	 *            请求
	 * @param prefix
	 *            前缀
	 * @return 参数内容
	 */
	public static Map<String, Object> getParametersStartingWith(final HttpServletRequest request, String prefix) {
		if (prefix == null) {
			prefix = "";
		}
		Enumeration paramNames = request.getParameterNames();
		Map<String, Object> params = new TreeMap<String, Object>();
		for (String paramName : new IterableEnumeration<String>(paramNames)) {
			if ("".equals(prefix) || paramName.startsWith(prefix)) {
				String unprefixed = paramName.substring(prefix.length());
				String[] values = request.getParameterValues(paramName);
				if (values == null || values.length == 0) {
					// Do nothing, no values found at all.
				} else if (values.length > 1) {
					params.put(unprefixed, values);
				} else {
					params.put(unprefixed, values[0]);
				}
			}
		}
		return params;
	}
}
