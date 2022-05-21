package lite.core.mybatis;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

public class MybatisUtil extends SqlSessionDaoSupport {

	/** 直接下 SQL select 資料 */
	public <E> List<E> doSqlSelect(String sql) {
		return getSqlSession().selectList("UTIL.executeSelectSql", sql);
	}

	/** 直接下 sql */
	public int doSqlUpdate(String sql) {
		return getSqlSession().update("UTIL.execute", sql);
	}

	/**
	 * PageHelper 分頁
	 * 
	 * @param <T>       ResultType 回傳的型別
	 * @param id        XML SQL 的 ID
	 * @param pageNum   頁次
	 * @param pageSize  一頁的大小
	 * @param parameter 查詢參數
	 * @return
	 */
	public <T> PageInfo<T> pageQuery(String id, int pageNum, int pageSize, Object parameter) {
		PageHelper.startPage(pageNum, pageSize);
		List<T> result = getSqlSession().selectList(id, parameter);
		return new PageInfo<T>(result);
	}

	/**
	 * PageHelper 分頁
	 * 
	 * @param <T>       ResultType 回傳的型別
	 * @param id        XML SQL 的 ID
	 * @param pageNum   頁次
	 * @param pageSize  一頁的大小
	 * @param orderby   字串 EX: "rowid desc" 或 "rowid asc"
	 * @param parameter 查詢參數
	 * @return
	 */
	public <T> PageInfo<T> pageQuery(String id, int pageNum, int pageSize, String orderby, Object parameter) {
		PageHelper.startPage(pageNum, pageSize, orderby);
		List<T> result = getSqlSession().selectList(id, parameter);
		return new PageInfo<T>(result);
	}

	/**
	 * 前端 FetchTable 專用分頁
	 * 
	 * @param <P>               ParameterType 參數的型別
	 * @param <R>               ResultType 回傳的型別
	 * @param id                XML SQL 的 ID
	 * @param fetchTableRequest 前端 FetchTable 請求
	 * @return FetchTableResponse 前端 FetchTable 回傳
	 */
	public <P, R> FetchTableResponse<R> doFetchTableQuery(String id, FetchTableRequest<P> fetchTableRequest) {

		PageInfo<R> pageInfo;

		if (StringUtils.isBlank(fetchTableRequest.getOrderby())) {
			pageInfo = pageQuery(id, fetchTableRequest.getPageNum(), fetchTableRequest.getPageSize(), fetchTableRequest.getRequestData());
		} else {
			pageInfo = pageQuery(id, fetchTableRequest.getPageNum(), fetchTableRequest.getPageSize(), fetchTableRequest.getOrderby(), fetchTableRequest.getRequestData());
		}

		FetchTableResponse<R> fetchRowsResponse = new FetchTableResponse<>();
		fetchRowsResponse.setPageNum(pageInfo.getPageNum());
		fetchRowsResponse.setPageSize(pageInfo.getPageSize());
		fetchRowsResponse.setTotalCount(pageInfo.getTotal());
		fetchRowsResponse.setPageCount(pageInfo.getPages());
		fetchRowsResponse.setResponseList(pageInfo.getList());
		return fetchRowsResponse;
	}

	public void clearCache() {
		getSqlSession().clearCache();
	}

	public void close() {
		getSqlSession().close();
	}

	public void commit() {
		getSqlSession().commit();
	}

	public void commit(boolean force) {
		getSqlSession().commit(force);
	}

	public int delete(String statement, Object parameter) {
		return getSqlSession().delete(statement, parameter);
	}

	public int delete(String statement) {
		HashMap<String, String> parameter = new HashMap<>();
		return getSqlSession().delete(statement, parameter);
	}

	public List<BatchResult> flushStatements() {
		return getSqlSession().flushStatements();
	}

	public Configuration getConfiguration() {
		return getSqlSession().getConfiguration();
	}

	public Connection getConnection() {
		return getSqlSession().getConnection();
	}

	public <T> T getMapper(Class<T> type) {
		return getSqlSession().getMapper(type);
	}

	public int insert(String statement, Object parameter) {
		return getSqlSession().insert(statement, parameter);
	}

	public int insert(String statement) {
		HashMap<String, String> parameter = new HashMap<>();
		return getSqlSession().insert(statement, parameter);
	}

	public void rollback() {
		getSqlSession().rollback();
	}

	public void rollback(boolean force) {
		getSqlSession().rollback(force);
	}

	public void select(String statement, Object parameter, ResultHandler<?> handler) {
		getSqlSession().select(statement, parameter, handler);
	}

	public void select(String statement, Object parameter, RowBounds rowBounds, ResultHandler<?> handler) {
		getSqlSession().select(statement, parameter, rowBounds, handler);
	}

	public void select(String statement, ResultHandler<?> handler) {
		getSqlSession().select(statement, handler);
	}

	public <E> List<E> selectList(String statement, Object parameter) {
		return getSqlSession().selectList(statement, parameter);
	}

	public <E> List<E> selectList(String statement) {
		HashMap<String, String> parameter = new HashMap<>();
		return getSqlSession().selectList(statement, parameter);
	}

	public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds) {
		return getSqlSession().selectMap(statement, parameter, mapKey, rowBounds);
	}

	public <K, V> Map<K, V> selectMap(String statement, Object parameter, String mapKey) {
		return getSqlSession().selectMap(statement, parameter, mapKey);
	}

	public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
		return getSqlSession().selectMap(statement, mapKey);
	}

	public <T> T selectOne(String statement, Object parameter) {
		return getSqlSession().selectOne(statement, parameter);
	}

	public <T> T selectOne(String statement) {
		HashMap<String, String> parameter = new HashMap<>();
		return getSqlSession().selectOne(statement, parameter);
	}

	public int update(String statement, Object parameter) {
		return getSqlSession().update(statement, parameter);
	}

	public int update(String statement) {
		return getSqlSession().update(statement);
	}
}
