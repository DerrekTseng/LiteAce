package lite.core.mybatis;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

@Component
public class DBProxy {

	@Resource(name = "mysql_db")
	MybatisUtil mysql;

	public MybatisUtil getMysql() {
		return mysql;
	}

}
