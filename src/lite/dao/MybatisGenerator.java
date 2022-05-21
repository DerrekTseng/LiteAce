package lite.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.ColumnOverride;
import org.mybatis.generator.config.CommentGeneratorConfiguration;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.config.JDBCConnectionConfiguration;
import org.mybatis.generator.config.JavaClientGeneratorConfiguration;
import org.mybatis.generator.config.JavaModelGeneratorConfiguration;
import org.mybatis.generator.config.JavaTypeResolverConfiguration;
import org.mybatis.generator.config.ModelType;
import org.mybatis.generator.config.SqlMapGeneratorConfiguration;
import org.mybatis.generator.config.TableConfiguration;
import org.mybatis.generator.internal.DefaultShellCallback;

public class MybatisGenerator {

	public static void main(String[] args) throws Exception {
		startGenerate();
	}

	/******************************************************************************/
	/******************************************************************************/
	/******************************************************************************/
	/******************************************************************************/

	// 使用者工作目錄
	private static final String USER_DIR = System.getProperty("user.dir");

	// java resource 位置
	private static final String TARGET_PROJECT = new File(USER_DIR, "src").getPath();

	// 儲存的 package 名稱
	private static final String targetPackage = "lite.dao";
	private static final String targetPackage_model = "model";
	private static final String targetPackage_mapper = "mapper";
	private static final String targetPackage_xml = "xml";

	// 資料庫連線資訊
	private static final String driverClass = "com.mysql.cj.jdbc.Driver";
	private static final String connectionURL = "jdbc:mysql://localhost:3306/DatabaseName";
	private static final String userId = "username";
	private static final String password = "password";

	// 要產生的 Table
	private static final Set<TableInfo> tableInfos = new HashSet<>();
	static {
		addTable("", "TestTable1");
		addTable("", "TestTable2");
	}

	/******************************************************************************/
	/******************************************************************************/
	/******************************************************************************/
	/******************************************************************************/

	private static void startGenerate() throws Exception {
		List<String> warnings = new ArrayList<>();

		boolean overwrite = true;

		Configuration config = createConfiguration("MyBatis3DynamicSql");

		DefaultShellCallback callback = new DefaultShellCallback(overwrite);

		MyBatisGenerator generator = new MyBatisGenerator(config, callback, warnings);

		generator.generate(null);
	}

	private static Configuration createConfiguration(String targetRuntime) {
		Configuration config = new Configuration();
		config.addContext(createContext(targetRuntime));
		return config;
	}

	private static Context createContext(String targetRuntime) {

		Context context = new Context(ModelType.CONDITIONAL);

		context.setCommentGeneratorConfiguration(createCommentGeneratorConfiguration());

		context.setId(UUID.randomUUID().toString());

		context.setTargetRuntime(targetRuntime);

		context.setJdbcConnectionConfiguration(createJDBCConnectionConfiguration());

		context.setJavaTypeResolverConfiguration(createJavaTypeResolverConfiguration());

		context.setJavaModelGeneratorConfiguration(createJavaModelGeneratorConfiguration());

		context.setSqlMapGeneratorConfiguration(createSqlMapGeneratorConfiguration());

		context.setJavaClientGeneratorConfiguration(createJavaClientGeneratorConfiguration());

		tableInfos.forEach(tableInfo -> {
			context.addTableConfiguration(createTableConfiguration(context, tableInfo.schema, tableInfo.tableName, tableInfo.columnOverrides));
		});

		return context;
	}

	private static JDBCConnectionConfiguration createJDBCConnectionConfiguration() {
		JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
		jdbcConnectionConfiguration.setDriverClass(driverClass);
		jdbcConnectionConfiguration.setConnectionURL(connectionURL);
		jdbcConnectionConfiguration.setUserId(userId);
		jdbcConnectionConfiguration.setPassword(password);
		jdbcConnectionConfiguration.addProperty("nullCatalogMeansCurrent", "true");
		return jdbcConnectionConfiguration;
	}

	private static JavaTypeResolverConfiguration createJavaTypeResolverConfiguration() {
		JavaTypeResolverConfiguration typeResolverConfiguration = new JavaTypeResolverConfiguration();
		typeResolverConfiguration.addProperty("forceBigDecimals", "false");
		return typeResolverConfiguration;
	}

	private static CommentGeneratorConfiguration createCommentGeneratorConfiguration() {
		CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
		commentGeneratorConfiguration.addProperty("suppressAllComments", "true");
		return commentGeneratorConfiguration;
	}

	/**
	 * Model Class
	 * 
	 * @return
	 */
	private static JavaModelGeneratorConfiguration createJavaModelGeneratorConfiguration() {
		JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
		javaModelGeneratorConfiguration.setTargetPackage(targetPackage + "." + targetPackage_model);
		javaModelGeneratorConfiguration.setTargetProject(TARGET_PROJECT);
		javaModelGeneratorConfiguration.addProperty("enableSubPackages", "true");
		javaModelGeneratorConfiguration.addProperty("trimStrings", "true");
		return javaModelGeneratorConfiguration;
	}

	/**
	 * Mapper XML
	 * 
	 * @return
	 */
	private static SqlMapGeneratorConfiguration createSqlMapGeneratorConfiguration() {
		SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
		sqlMapGeneratorConfiguration.setTargetPackage(targetPackage + "." + targetPackage_xml);
		sqlMapGeneratorConfiguration.setTargetProject(TARGET_PROJECT);

		sqlMapGeneratorConfiguration.addProperty("enableSubPackages", "true");
		return sqlMapGeneratorConfiguration;
	}

	/**
	 * Mapper Class
	 * 
	 * @return
	 */
	private static JavaClientGeneratorConfiguration createJavaClientGeneratorConfiguration() {
		JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
		javaClientGeneratorConfiguration.setTargetPackage(targetPackage + "." + targetPackage_mapper);
		javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
		javaClientGeneratorConfiguration.setTargetProject(TARGET_PROJECT);
		javaClientGeneratorConfiguration.addProperty("enableSubPackages", "true");
		return javaClientGeneratorConfiguration;
	}

	/**
	 * table
	 * 
	 * @param context
	 * @param schema
	 * @param tableName
	 * @return
	 */
	private static TableConfiguration createTableConfiguration(Context context, String schema, String tableName, ColumnOverride... columnOverrides) {
		TableConfiguration tableConfiguration = new TableConfiguration(context);
		tableConfiguration.setSchema(schema);
		tableConfiguration.setTableName(tableName);
		tableConfiguration.setDomainObjectName(tableName);
		for (ColumnOverride columnOverride : columnOverrides) {
			tableConfiguration.addColumnOverride(columnOverride);
		}
		tableConfiguration.addProperty("useActualColumnNames", "true");
		return tableConfiguration;
	}

	private static ColumnOverride ColumnOverride(String tableColumnName) {
		ColumnOverride columnOverride = new ColumnOverride(tableColumnName);
		return columnOverride;
	}

	protected static ColumnOverride ColumnOverride(String tableColumnName, String javaProperty) {
		ColumnOverride columnOverride = ColumnOverride(tableColumnName);
		columnOverride.setJavaProperty(javaProperty);
		return columnOverride;
	}

	protected static ColumnOverride ColumnOverride(String tableColumnName, Class<?> jdbcType, String javaProperty, Class<?> javaType) {
		ColumnOverride columnOverride = ColumnOverride(tableColumnName, javaProperty);
		columnOverride.setJavaType(javaType.getName());
		columnOverride.setJavaType(jdbcType.getName());
		return columnOverride;
	}

	protected static ColumnOverride ColumnOverride(String tableColumnName, Class<?> jdbcType, String javaProperty, Class<?> javaType, boolean columnNameDelimited) {
		ColumnOverride columnOverride = ColumnOverride(tableColumnName, jdbcType, javaProperty, javaType);
		columnOverride.setColumnNameDelimited(columnNameDelimited);
		return columnOverride;
	}

	private static void addTable(String schema, String tableName, ColumnOverride... columnOverrides) {
		TableInfo tableInfo = new TableInfo();
		tableInfo.schema = schema;
		tableInfo.tableName = tableName;
		tableInfo.columnOverrides = columnOverrides;
		tableInfos.add(tableInfo);
	}

	private static class TableInfo {
		String schema;
		String tableName;
		ColumnOverride[] columnOverrides;
	}
}
