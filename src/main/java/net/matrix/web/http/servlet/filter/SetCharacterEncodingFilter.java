/*
 * 版权所有 2024 Matrix。
 * 保留所有权利。
 */
package net.matrix.web.http.servlet.filter;

import java.io.IOException;

import javax.annotation.Nullable;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import org.apache.commons.lang3.StringUtils;

/**
 * <p>
 * Example filter that sets the character encoding to be used in parsing the incoming request,
 * either unconditionally or only if the client did not specify a character encoding. Configuration
 * of this filter is based on the following initialization parameters:
 * </p>
 * <ul>
 * <li><strong>encoding</strong> - The character encoding to be configured for this request, either
 * conditionally or unconditionally based on the <code>ignore</code> initialization parameter. This
 * parameter is required, so there is no default.</li>
 * <li><strong>ignore</strong> - If set to "true", any character encoding specified by the client is
 * ignored, and the value returned by the <code>selectEncoding()</code> method is set. If set to
 * "false, <code>selectEncoding()</code> is called <strong>only</strong> if the client has not
 * already specified an encoding. By default, this parameter is set to "true".</li>
 * </ul>
 * <p>
 * Although this filter can be used unchanged, it is also easy to subclass it and make the
 * <code>selectEncoding()</code> method more intelligent about what encoding to choose, based on
 * characteristics of the incoming request (such as the values of the <code>Accept-Language</code>
 * and <code>User-Agent</code> headers, or a value stashed in the current user's session.
 * </p>
 *
 * @author Craig McClanahan
 */
public class SetCharacterEncodingFilter
    implements Filter {
    /**
     * The default character encoding to set for requests that pass through this
     * filter.
     */
    @Nullable
    protected String encoding;

    /**
     * Should a character encoding specified by the client be ignored?
     */
    protected boolean ignore = true;

    /**
     * Place this filter into service.
     *
     * @param filterConfig
     *     The filter configuration object.
     */
    @Override
    public void init(FilterConfig filterConfig)
        throws ServletException {
        this.encoding = filterConfig.getInitParameter("encoding");
        this.ignore = StringUtils.equalsAnyIgnoreCase(filterConfig.getInitParameter("ignore"), null, "true", "yes");
    }

    /**
     * Take this filter out of service.
     */
    @Override
    public void destroy() {
        this.encoding = null;
    }

    /**
     * Select and set (if specified) the character encoding to be used to
     * interpret request parameters for this request.
     *
     * @param request
     *     The servlet request we are processing.
     * @param response
     *     The servlet response we are creating.
     * @param chain
     *     The filter chain we are processing.
     * @exception IOException
     *     if an input/output error occurs.
     * @exception ServletException
     *     if a servlet error occurs.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        // Conditionally select and set the character encoding to be used
        if (ignore || request.getCharacterEncoding() == null) {
            String selectedEncoding = selectEncoding(request);
            if (selectedEncoding != null) {
                request.setCharacterEncoding(selectedEncoding);
            }
        }

        // Pass control on to the next filter
        chain.doFilter(request, response);
    }

    /**
     * Select an appropriate character encoding to be used, based on the
     * characteristics of the current request and/or filter initialization
     * parameters. If no character encoding should be set, return <code>null</code>.<br>
     * The default implementation unconditionally returns the value configured by the
     * <strong>encoding</strong> initialization parameter for this filter.
     *
     * @param request
     *     The servlet request we are processing.
     */
    protected String selectEncoding(ServletRequest request) {
        return this.encoding;
    }
}
