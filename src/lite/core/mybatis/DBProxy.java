package lite.core.mybatis;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.dbcp2.PoolableConnectionFactory;
import org.apache.commons.pool2.ObjectPool;
import org.springframework.stereotype.Component;

@Component
public class DBProxy {

	@Resource(name = "mysql_db")
	MybatisUtil mysql;

	@Resource(name = "mysql_genericObjectPoolBean")
	ObjectPool<PoolableConnection> genericObjectPool;

	@Resource(name = "mysql_poolableConnectionFactoryBean")
	PoolableConnectionFactory poolableConnectionFactory;

	@PostConstruct
	private void postConstruct() {
		poolableConnectionFactory.setPool(genericObjectPool);
	}

	public MybatisUtil getMysql() {
		return mysql;
	}
}
