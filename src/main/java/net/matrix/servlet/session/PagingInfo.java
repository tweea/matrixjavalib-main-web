/*
 * 版权所有 2020 Matrix。
 * 保留所有权利。
 */
package net.matrix.servlet.session;

/**
 * 分页信息。
 */
public class PagingInfo {
    /**
     * 主键。
     */
    private String key;

    /**
     * URL。
     */
    private String url;

    /**
     * 总记录数。
     */
    private long total;

    /**
     * 页号。
     */
    private int pageIndex;

    /**
     * 每页记录数。
     */
    private int pageSize;

    /**
     * 总页数。
     */
    private long totalPage;

    /**
     * 构造空实例。
     */
    public PagingInfo() {
        this("", "", 0, 0, 1);
    }

    /**
     * 根据实际信息构造。
     * 
     * @param key
     *     主键
     * @param url
     *     URL
     * @param total
     *     总记录数
     * @param pageIndex
     *     页号
     * @param pageSize
     *     每页记录数
     */
    public PagingInfo(final String key, final String url, final long total, final int pageIndex, final int pageSize) {
        this.key = key;
        this.url = url;
        this.total = total;
        this.pageIndex = pageIndex;
        if (pageSize <= 0) {
            this.pageSize = 1;
        } else {
            this.pageSize = pageSize;
        }
        computeTotalPage();
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public long getTotal() {
        return total;
    }

    /**
     * 设置总记录数。
     * 
     * @param total
     *     总记录数
     */
    public void setTotal(final long total) {
        this.total = total;
        computeTotalPage();
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(final int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    /**
     * 设置每页记录数。
     * 
     * @param pageSize
     *     每页记录数
     */
    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
        computeTotalPage();
    }

    public long getTotalPage() {
        return totalPage;
    }

    private void computeTotalPage() {
        totalPage = (total + pageSize - 1) / pageSize;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(64);
        sb.append(super.toString());
        sb.append("{key=").append(key).append(",url=").append(url);
        sb.append(",total=").append(total).append(",pageIndex=").append(pageIndex);
        sb.append(",pageSize=").append(pageSize).append('}');
        return sb.toString();
    }
}
