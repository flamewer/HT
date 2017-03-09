package com.flame.mongodb;
public class Paginate {
	
	/**
	 * 分页中，当前页面的索引值，第1页的值为0.
	 */
	private Integer pageIndex;
	
	/**
	 * 分页中，每个页面显示的条目数.
	 */
	private Integer pageSize;

	/**
	 * 构造函数，初始化查询的页码与数据条数. <br>
	 * @param pageIndex 当前页面的索引值，第1页的值为1.
	 * @param pageSize 每个页面显示的条目数.
	 */
	public Paginate(Integer pageIndex, Integer pageSize) {
		this.pageIndex = pageIndex - 1;
		this.pageSize = pageSize;
	}
	
	/**
	 * 获取pageIndex.
	 * @return the pageIndex
	 */
	public final Integer getPageIndex() {
		return pageIndex;
	}

	/**
	 * 设置 pageIndex.
	 * @param index the pageIndex to set
	 */
	public final void setPageIndex(final Integer index) {
		this.pageIndex = index;
	}

	/**
	 * 获取pageSize.
	 * @return the pageSize
	 */
	public final Integer getPageSize() {
		return pageSize;
	}

	/**
	 * 设置 pageSize.
	 * @param size the pageSize to set
	 */
	public final void setPageSize(final Integer size) {
		this.pageSize = size;
	}
}