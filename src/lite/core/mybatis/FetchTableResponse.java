package lite.core.mybatis;

import java.util.List;

/**
 * 前端 FetchTable 回傳
 * 
 * @author DerrekTseng
 *
 * @param <R> 回傳的型別
 */
public class FetchTableResponse<R> {

	List<R> responseList;

	Integer pageSize;

	Integer pageNum;

	Integer pageCount;

	Long totalCount;

	public List<R> getResponseList() {
		return responseList;
	}

	public void setResponseList(List<R> responseList) {
		this.responseList = responseList;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageNum() {
		return pageNum;
	}

	public void setPageNum(Integer pageNum) {
		this.pageNum = pageNum;
	}

	public Integer getPageCount() {
		return pageCount;
	}

	public void setPageCount(Integer pageCount) {
		this.pageCount = pageCount;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

}
