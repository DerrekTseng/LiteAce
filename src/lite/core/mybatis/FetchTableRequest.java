package lite.core.mybatis;

/**
 * 前端 FetchTable 請求
 * 
 * @author DerrekTseng
 *
 * @param <P> 參數的型別
 */
public class FetchTableRequest<P> {

	P requestData;

	Integer pageSize;

	Integer pageNum;

	String orderby;

	public P getRequestData() {
		return requestData;
	}

	public void setRequestData(P requestData) {
		this.requestData = requestData;
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

	public String getOrderby() {
		return orderby;
	}

	public void setOrderby(String orderby) {
		this.orderby = orderby;
	}

}
