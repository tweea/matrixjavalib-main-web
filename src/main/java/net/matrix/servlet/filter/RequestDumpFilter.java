/*
 * 版权所有 2020 Matrix。
 * 保留所有权利。
 */
package net.matrix.servlet.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 保存所有请求内容。
 */
public class RequestDumpFilter
    implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(RequestDumpFilter.class);

    private boolean enabled;

    private boolean hasRequesst = true;

    private boolean hasCookie = true;

    private boolean hasResponse = true;

    private boolean hasSession = true;

    private int maxLength = 100;

    @Override
    public void init(final FilterConfig filterConfigIn)
        throws ServletException {
        this.enabled = "true".equals(filterConfigIn.getInitParameter("enable"));
        if (StringUtils.isNotEmpty(filterConfigIn.getInitParameter("hasRequesst"))) {
            this.hasRequesst = "true".equals(filterConfigIn.getInitParameter("hasRequesst"));
        }
        if (StringUtils.isNotEmpty(filterConfigIn.getInitParameter("hasCookie"))) {
            this.hasCookie = "true".equals(filterConfigIn.getInitParameter("hasCookie"));
        }
        if (StringUtils.isNotEmpty(filterConfigIn.getInitParameter("hasResponse"))) {
            this.hasResponse = "true".equals(filterConfigIn.getInitParameter("hasResponse"));
        }
        if (StringUtils.isNotEmpty(filterConfigIn.getInitParameter("hasSession"))) {
            this.hasSession = "true".equals(filterConfigIn.getInitParameter("hasSession"));
        }
        if (StringUtils.isNotEmpty(filterConfigIn.getInitParameter("maxLength"))) {
            this.maxLength = Integer.parseInt(filterConfigIn.getInitParameter("maxLength"));
        }
    }

    @Override
    public void destroy() {
        this.enabled = false;
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {
        if (enabled) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            HttpSession httpSession = httpRequest.getSession(false);
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            pw.println("============================== 请求内容开始 ======================================");

            if (hasRequesst) {
                dumpRequest(httpRequest, pw);
            }

            if (hasCookie) {
                Cookie[] cookies = httpRequest.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        dumpCookie(cookie, pw);
                    }
                }
            }

            if (hasSession) {
                dumpSession(httpSession, pw);
            }

            if (hasResponse) {
                dumpResponse(httpResponse, pw);
            }

            pw.println("============================== 请求内容结束 ======================================");
            LOG.info(sw.toString());
        }
        // Pass control on to the next filter
        chain.doFilter(request, response);
    }

    private void dumpRequest(final HttpServletRequest request, final PrintWriter writer) {
        // Object Properties
        Map<String, String> requestProperties = new LinkedHashMap<>();
        requestProperties.put("Local", request.getLocalAddr() + ':' + request.getLocalPort());
        requestProperties.put("Remote", request.getRemoteAddr() + ':' + request.getRemotePort());
        requestProperties.put("RequestURI", request.getRequestURI());
        requestProperties.put("PathInfo", request.getPathInfo());
        requestProperties.put("QueryString", request.getQueryString());
        requestProperties.put("Method", request.getMethod());
        requestProperties.put("ContentType", request.getContentType());
        requestProperties.put("ContentLength", Integer.toString(request.getContentLength()));
        requestProperties.put("CharacterEncoding", request.getCharacterEncoding());
        requestProperties.put("Locale", request.getLocale().toString());
        requestProperties.put("Locales", Collections.list(request.getLocales()).toString());
        dumpStringMap(writer, "Request: " + request, requestProperties);

        // request headers
        Map<String, String> requestHeaders = new LinkedHashMap<>();
        List<String> names = Collections.list(request.getHeaderNames());
        Collections.sort(names);
        for (String name : names) {
            String value = request.getHeader(name);
            requestHeaders.put(name, value);
        }
        dumpStringMap(writer, "Request Headers", requestHeaders);

        // request parameters
        Map<String, String> requestParameters = new LinkedHashMap<>();
        names = Collections.list(request.getParameterNames());
        Collections.sort(names);
        for (String name : names) {
            String[] values = request.getParameterValues(name);
            if (values.length == 1) {
                requestParameters.put(name, values[0]);
            } else {
                requestParameters.put(name, Arrays.toString(values));
            }
        }
        dumpStringMap(writer, "Request Parameters", requestParameters);

        // request attributes
        Map<String, Object> requestAttributes = new LinkedHashMap<>();
        names = Collections.list(request.getAttributeNames());
        Collections.sort(names);
        for (String name : names) {
            Object obj = request.getAttribute(name);
            requestAttributes.put(name, obj);
        }
        dumpObjectMap(writer, "Request Attributes", requestAttributes);
    }

    private void dumpCookie(final Cookie cookie, final PrintWriter writer) {
        Map<String, String> cookieProperties = new LinkedHashMap<>();
        cookieProperties.put("Name", cookie.getName());
        cookieProperties.put("Value", cookie.getValue());
        cookieProperties.put("Domain", cookie.getDomain());
        cookieProperties.put("Path", cookie.getPath());
        cookieProperties.put("MaxAge", Integer.toString(cookie.getMaxAge()));
        cookieProperties.put("Secure", Boolean.toString(cookie.getSecure()));
        cookieProperties.put("Version", Integer.toString(cookie.getVersion()));
        cookieProperties.put("Comment", cookie.getComment());
        dumpStringMap(writer, "Cookie: " + cookie, cookieProperties);
    }

    private void dumpSession(final HttpSession session, final PrintWriter writer) {
        writer.print("session: ");
        if (session == null) {
            writer.println("未创建");
        } else {
            writer.println(session);

            // session attributes
            Map<String, Object> sessionAttributes = new LinkedHashMap<>();
            List<String> names = Collections.list(session.getAttributeNames());
            Collections.sort(names);
            for (String name : names) {
                Object obj = session.getAttribute(name);
                sessionAttributes.put(name, obj);
            }
            dumpObjectMap(writer, "Session Attributes", sessionAttributes);
        }
    }

    private void dumpResponse(final HttpServletResponse response, final PrintWriter writer) {
        writer.print("response: ");
        writer.println(response);
    }

    private void dumpStringMap(final PrintWriter writer, final String title, final Map<String, String> map) {
        int maxNameLen = 0;
        int maxValueLen = 0;
        int totalLen = title.length();
        if (!map.isEmpty()) {
            for (Map.Entry<String, String> item : map.entrySet()) {
                if (item.getValue() == null) {
                    item.setValue("(null)");
                }
                if (item.getKey().length() > maxNameLen) {
                    maxNameLen = item.getKey().length();
                }
                if (item.getValue().length() <= maxLength && item.getValue().length() > maxValueLen) {
                    maxValueLen = item.getValue().length();
                } else if (item.getValue().length() > maxLength) {
                    maxValueLen = maxLength;
                }
            }
            if (maxNameLen + maxValueLen + 1 > totalLen) {
                totalLen = maxNameLen + maxValueLen + 1;
            } else {
                maxValueLen = totalLen - (maxNameLen + 1);
            }
        }
        writer.print('+');
        printChar(writer, '-', totalLen);
        writer.println('+');
        writer.print('|');
        writer.print(title);
        printChar(writer, ' ', totalLen - title.length());
        writer.println('|');
        if (map.isEmpty()) {
            writer.print('+');
            printChar(writer, '-', totalLen);
            writer.println('+');
        } else {
            writer.print('+');
            printChar(writer, '-', maxNameLen);
            writer.print('+');
            printChar(writer, '-', maxValueLen);
            writer.println('+');
            for (Map.Entry<String, String> item : map.entrySet()) {
                writer.print('|');
                writer.print(item.getKey());
                printChar(writer, ' ', maxNameLen - item.getKey().length());
                writer.print('|');
                int linNum = item.getValue().length() / maxLength;
                if (item.getValue().length() % maxLength != 0) {
                    linNum++;
                }
                if (linNum == 0) {
                    printChar(writer, ' ', maxValueLen);
                    writer.println('|');
                }
                for (int i = 0; i < linNum; i++) {
                    if (i < linNum - 1) {
                        writer.append(item.getValue(), i * maxLength, (i + 1) * maxLength);
                        writer.println('|');
                        writer.print('|');
                        printChar(writer, ' ', maxNameLen);
                        writer.print('|');
                    } else if (linNum > 1) {
                        writer.append(item.getValue(), i * maxLength, item.getValue().length());
                        printChar(writer, ' ', (i + 1) * maxLength - item.getValue().length());
                        writer.println('|');
                    } else {
                        writer.append(item.getValue());
                        printChar(writer, ' ', maxValueLen - item.getValue().length());
                        writer.println('|');
                    }
                }
            }
            writer.print('+');
            printChar(writer, '-', maxNameLen);
            writer.print('+');
            printChar(writer, '-', maxValueLen);
            writer.println('+');
        }
    }

    private void dumpObjectMap(final PrintWriter writer, final String title, final Map<String, Object> objMap) {
        Map<String, ClassAndToString> map = new LinkedHashMap<>();
        for (Map.Entry<String, Object> item : objMap.entrySet()) {
            map.put(item.getKey(), new ClassAndToString(item.getValue()));
        }
        int maxNameLen = 0;
        int maxClassLen = 0;
        int maxValueLen = title.length();
        if (!map.isEmpty()) {
            for (Map.Entry<String, ClassAndToString> item : map.entrySet()) {
                if (item.getKey().length() > maxNameLen) {
                    maxNameLen = item.getKey().length();
                }
                if (item.getValue().clazz.length() > maxClassLen) {
                    maxClassLen = item.getValue().clazz.length();
                }
                if (item.getValue().toString.length() <= maxLength && item.getValue().toString.length() > maxValueLen) {
                    maxValueLen = item.getValue().toString.length();
                } else if (item.getValue().toString.length() > maxLength) {
                    maxValueLen = maxLength;
                }
            }
            if (maxNameLen + maxClassLen + 1 > maxValueLen) {
                maxValueLen = maxNameLen + maxClassLen + 1;
            } else {
                maxClassLen = maxValueLen - (maxNameLen + 1);
            }
        }
        writer.print('+');
        printChar(writer, '-', maxValueLen);
        writer.println('+');
        writer.print('|');
        writer.print(title);
        printChar(writer, ' ', maxValueLen - title.length());
        writer.println('|');
        if (map.isEmpty()) {
            writer.print('+');
            printChar(writer, '-', maxValueLen);
            writer.println('+');
        } else {
            writer.print('+');
            printChar(writer, '-', maxNameLen);
            writer.print('+');
            printChar(writer, '-', maxClassLen);
            writer.println('+');
            for (Map.Entry<String, ClassAndToString> item : map.entrySet()) {
                writer.print('|');
                writer.print(item.getKey());
                printChar(writer, ' ', maxNameLen - item.getKey().length());
                writer.print('|');
                writer.print(item.getValue().clazz);
                printChar(writer, ' ', maxClassLen - item.getValue().clazz.length());
                writer.println('|');
                int linNum = item.getValue().toString.length() / maxLength;
                if (item.getValue().toString.length() % maxLength != 0) {
                    linNum++;
                }
                if (linNum == 0) {
                    printChar(writer, ' ', maxValueLen);
                    writer.println('|');
                }
                for (int i = 0; i < linNum; i++) {
                    writer.print('|');
                    if (i < linNum - 1) {
                        writer.append(item.getValue().toString, i * maxLength, (i + 1) * maxLength);
                        writer.println('|');
                    } else if (linNum > 1) {
                        writer.append(item.getValue().toString, i * maxLength, item.getValue().toString.length());
                        printChar(writer, ' ', (i + 1) * maxLength - item.getValue().toString.length());
                        writer.println('|');
                    } else {
                        writer.append(item.getValue().toString);
                        printChar(writer, ' ', maxValueLen - item.getValue().toString.length());
                        writer.println('|');
                    }
                }
            }
            writer.print('+');
            printChar(writer, '-', maxValueLen);
            writer.println('+');
        }
    }

    private static final class ClassAndToString {
        final String clazz;

        final String toString;

        /**
         * 根据对象信息构造。
         * 
         * @param obj
         *     对象
         */
        ClassAndToString(final Object obj) {
            if (obj == null) {
                this.clazz = "(n/a)";
                this.toString = "(null)";
            } else {
                this.clazz = obj.getClass().toString();
                if (obj.getClass().isArray()) {
                    this.toString = Arrays.toString((Object[]) obj);
                } else {
                    this.toString = obj.toString();
                }
            }
        }
    }

    private static void printChar(final PrintWriter writer, final char ch, final int repeat) {
        for (int i = 0; i < repeat; i++) {
            writer.print(ch);
        }
    }
}
