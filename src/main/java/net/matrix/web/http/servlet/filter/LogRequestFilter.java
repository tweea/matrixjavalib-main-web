/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.web.http.servlet.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import javax.servlet.http.Part;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import net.matrix.text.ResourceBundleMessageFormatter;

/**
 * 输出请求内容到日志。
 */
public class LogRequestFilter
    implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(LogRequestFilter.class);

    /**
     * 区域相关资源。
     */
    private static final ResourceBundleMessageFormatter RBMF = new ResourceBundleMessageFormatter(LogRequestFilter.class).useCurrentLocale();

    private boolean enabled;

    private boolean hasRequest = true;

    private boolean hasResponse = true;

    private boolean hasSession = true;

    private int maxLength = 100;

    @Override
    public void init(FilterConfig filterConfig)
        throws ServletException {
        this.enabled = "true".equals(filterConfig.getInitParameter("enable"));
        if (StringUtils.isNotEmpty(filterConfig.getInitParameter("hasRequest"))) {
            this.hasRequest = "true".equals(filterConfig.getInitParameter("hasRequest"));
        }
        if (StringUtils.isNotEmpty(filterConfig.getInitParameter("hasResponse"))) {
            this.hasResponse = "true".equals(filterConfig.getInitParameter("hasResponse"));
        }
        if (StringUtils.isNotEmpty(filterConfig.getInitParameter("hasSession"))) {
            this.hasSession = "true".equals(filterConfig.getInitParameter("hasSession"));
        }
        if (StringUtils.isNotEmpty(filterConfig.getInitParameter("maxLength"))) {
            this.maxLength = Integer.parseInt(filterConfig.getInitParameter("maxLength"));
        }
    }

    @Override
    public void destroy() {
        this.enabled = false;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        if (enabled) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            HttpSession httpSession = httpRequest.getSession(false);

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            pw.println();
            pw.println(RBMF.get("============================== 请求内容开始 ======================================"));
            if (hasRequest) {
                dumpRequest(httpRequest, pw);
            }
            if (hasResponse) {
                dumpResponse(httpResponse, pw);
            }
            if (hasSession) {
                dumpSession(httpSession, pw);
            }
            pw.print(RBMF.get("============================== 请求内容结束 ======================================"));
            LOG.info(sw.toString());
        }

        // Pass control on to the next filter
        chain.doFilter(request, response);
    }

    private void dumpRequest(HttpServletRequest request, PrintWriter writer)
        throws IOException, ServletException {
        // request properties
        Map<String, String> properties = Maps.newLinkedHashMapWithExpectedSize(23);
        properties.put("CharacterEncoding", request.getCharacterEncoding());
        properties.put("ContentLength", Long.toString(request.getContentLengthLong()));
        properties.put("ContentType", request.getContentType());
        properties.put("Protocol", request.getProtocol());
        properties.put("Scheme", request.getScheme());
        properties.put("ServerName", request.getServerName());
        properties.put("ServerPort", Integer.toString(request.getServerPort()));
        properties.put("RemoteAddr", request.getRemoteAddr());
        properties.put("RemoteHost", request.getRemoteHost());
        properties.put("Locale", request.getLocale().toString());
        properties.put("Locales", Collections.list(request.getLocales()).toString());
        properties.put("Secure", Boolean.toString(request.isSecure()));
        properties.put("RemotePort", Integer.toString(request.getRemotePort()));
        properties.put("LocalName", request.getLocalName());
        properties.put("LocalAddr", request.getLocalAddr());
        properties.put("LocalPort", Integer.toString(request.getLocalPort()));
        properties.put("Method", request.getMethod());
        properties.put("PathInfo", request.getPathInfo());
        properties.put("ContextPath", request.getContextPath());
        properties.put("QueryString", request.getQueryString());
        properties.put("RequestedSessionId", request.getRequestedSessionId());
        properties.put("RequestURI", request.getRequestURI());
        properties.put("ServletPath", request.getServletPath());
        dumpStringMap(writer, "Request: " + request, properties);

        // request headers
        List<String> names = Collections.list(ObjectUtils.defaultIfNull(request.getHeaderNames(), Collections.emptyEnumeration()));
        Collections.sort(names);
        Map<String, String> headers = Maps.newLinkedHashMapWithExpectedSize(names.size());
        for (String name : names) {
            List<String> values = Collections.list(request.getHeaders(name));
            if (values.size() == 1) {
                headers.put(name, values.get(0));
            } else {
                headers.put(name, values.toString());
            }
        }
        dumpStringMap(writer, "Request Headers", headers);

        // request parameters
        names = Collections.list(request.getParameterNames());
        Collections.sort(names);
        Map<String, String> parameters = Maps.newLinkedHashMapWithExpectedSize(names.size());
        for (String name : names) {
            String[] values = request.getParameterValues(name);
            if (values.length == 1) {
                parameters.put(name, values[0]);
            } else {
                parameters.put(name, Arrays.toString(values));
            }
        }
        dumpStringMap(writer, "Request Parameters", parameters);

        // request cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                dumpCookie(cookie, writer);
            }
        }

        // request parts
        Collection<Part> parts = request.getParts();
        for (Part part : parts) {
            dumpPart(part, writer);
        }

        // request attributes
        names = Collections.list(request.getAttributeNames());
        Collections.sort(names);
        Map<String, Object> attributes = Maps.newLinkedHashMapWithExpectedSize(names.size());
        for (String name : names) {
            Object value = request.getAttribute(name);
            attributes.put(name, value);
        }
        dumpObjectMap(writer, "Request Attributes", attributes);
    }

    private void dumpCookie(Cookie cookie, PrintWriter writer) {
        Map<String, String> properties = new LinkedHashMap<>();
        properties.put("Comment", cookie.getComment());
        properties.put("Domain", cookie.getDomain());
        properties.put("MaxAge", Integer.toString(cookie.getMaxAge()));
        properties.put("Path", cookie.getPath());
        properties.put("Secure", Boolean.toString(cookie.getSecure()));
        properties.put("Name", cookie.getName());
        properties.put("Value", cookie.getValue());
        properties.put("Version", Integer.toString(cookie.getVersion()));
        properties.put("HttpOnly", Boolean.toString(cookie.isHttpOnly()));
        dumpStringMap(writer, "Cookie: " + cookie, properties);
    }

    private void dumpPart(Part part, PrintWriter writer) {
        Map<String, String> properties = new LinkedHashMap<>();
        properties.put("ContentType", part.getContentType());
        properties.put("Name", part.getName());
        properties.put("SubmittedFileName", part.getSubmittedFileName());
        properties.put("Size", Long.toString(part.getSize()));
        dumpStringMap(writer, "Part: " + part, properties);

        // part headers
        List<String> names = new ArrayList(ObjectUtils.defaultIfNull(part.getHeaderNames(), Collections.emptyList()));
        Collections.sort(names);
        Map<String, String> headers = Maps.newLinkedHashMapWithExpectedSize(names.size());
        for (String name : names) {
            Collection<String> values = part.getHeaders(name);
            if (values.size() == 1) {
                headers.put(name, IterableUtils.first(values));
            } else {
                headers.put(name, values.toString());
            }
        }
        dumpStringMap(writer, "Part Headers", headers);
    }

    private void dumpResponse(HttpServletResponse response, PrintWriter writer) {
        // response properties
        Map<String, String> properties = new LinkedHashMap<>();
        properties.put("CharacterEncoding", response.getCharacterEncoding());
        properties.put("ContentType", response.getContentType());
        properties.put("Locale", response.getLocale().toString());
        properties.put("Status", Integer.toString(response.getStatus()));
        dumpStringMap(writer, "Response: " + response, properties);

        // response headers
        List<String> names = new ArrayList(ObjectUtils.defaultIfNull(response.getHeaderNames(), Collections.emptyList()));
        Collections.sort(names);
        Map<String, String> headers = Maps.newLinkedHashMapWithExpectedSize(names.size());
        for (String name : names) {
            Collection<String> values = response.getHeaders(name);
            if (values.size() == 1) {
                headers.put(name, IterableUtils.first(values));
            } else {
                headers.put(name, values.toString());
            }
        }
        dumpStringMap(writer, "Response Headers", headers);
    }

    private void dumpSession(HttpSession session, PrintWriter writer) {
        if (session == null) {
            writer.print("Session: ");
            writer.println(RBMF.get("未创建"));
            return;
        }

        // session properties
        Map<String, String> properties = new LinkedHashMap<>();
        properties.put("CreationTime", Long.toString(session.getCreationTime()));
        properties.put("Id", session.getId());
        properties.put("LastAccessedTime", Long.toString(session.getLastAccessedTime()));
        properties.put("New", Boolean.toString(session.isNew()));
        dumpStringMap(writer, "Session: " + session, properties);

        // session attributes
        List<String> names = Collections.list(session.getAttributeNames());
        Collections.sort(names);
        Map<String, Object> attributes = Maps.newLinkedHashMapWithExpectedSize(names.size());
        for (String name : names) {
            Object value = session.getAttribute(name);
            attributes.put(name, value);
        }
        dumpObjectMap(writer, "Session Attributes", attributes);
    }

    private void dumpStringMap(PrintWriter writer, String title, Map<String, String> map) {
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
                    ++linNum;
                }
                if (linNum == 0) {
                    printChar(writer, ' ', maxValueLen);
                    writer.println('|');
                }
                for (int i = 0; i < linNum; ++i) {
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

    private void dumpObjectMap(PrintWriter writer, String title, Map<String, Object> objMap) {
        Map<String, ClassAndToString> map = Maps.newLinkedHashMapWithExpectedSize(objMap.size());
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
                if (item.getValue().className.length() > maxClassLen) {
                    maxClassLen = item.getValue().className.length();
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
                writer.print(item.getValue().className);
                printChar(writer, ' ', maxClassLen - item.getValue().className.length());
                writer.println('|');
                int linNum = item.getValue().toString.length() / maxLength;
                if (item.getValue().toString.length() % maxLength != 0) {
                    ++linNum;
                }
                if (linNum == 0) {
                    printChar(writer, ' ', maxValueLen);
                    writer.println('|');
                }
                for (int i = 0; i < linNum; ++i) {
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
        final String className;

        final String toString;

        /**
         * 根据对象信息构造。
         *
         * @param obj
         *     对象
         */
        ClassAndToString(Object obj) {
            if (obj == null) {
                this.className = "(n/a)";
                this.toString = "(null)";
            } else {
                this.className = obj.getClass().toString();
                if (obj.getClass().isArray()) {
                    this.toString = Arrays.toString((Object[]) obj);
                } else {
                    this.toString = obj.toString();
                }
            }
        }
    }

    private static void printChar(PrintWriter writer, char ch, int repeat) {
        for (int i = 0; i < repeat; ++i) {
            writer.print(ch);
        }
    }
}
