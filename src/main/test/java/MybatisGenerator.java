import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.apache.commons.lang3.StringUtils;

/**
 * <p>一句话描述</p>
 *
 * @author Kern
 */
public class MybatisGenerator {

    public static void main(String[] args) {
        AutoGenerator generator = new AutoGenerator();
        //配置数据库连接参数
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        // 生成JDBC信息
        String jdbcUrl = "jdbc:mysql://192.168.73.128:3306/test_big_table?useUnicode=true&characterEncoding=UTF-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowMultiQueries=true&serverTimezone=Asia/Shanghai";
        String driverClassName = "com.mysql.cj.jdbc.Driver";
        String userName = "root";
        String password = "123456";
        DbType dbType = DbType.MYSQL;

        // 生成的包路径配置
        String parentPackage = "com.itdl";
        // 实体类所在包【根包parentPackage+entityPackage】
        String entityPackage = "entity";
        // mapper接口所在包【根包parentPackage+mapperPackage】
        String mapperPackage = "mapper";
        // mapper接口对应xml所在目录【根包parentPackage+xmlPackage】
        String xmlPackage = "mapper";
        // service接口所在包【根包parentPackage+servicePackage】，不需要生成service填写为空字符串
        String servicePackage = "";
        // service接口实现类所在包【根包parentPackage+serviceImplPackage】，不需要生成service填写为空字符串
        String serviceImplPackage = "";
        // controller类所在包【根包parentPackage+controllerPackage】，不需要生成service填写为空字符串
        String controllerPackage = "";
        // 设置Map普洱接口的自定义父接口，为空字符串表示不设置，默认使用BaseMapper
        String supperMapperClass = "com.itdl.mapper.MyBaseMapper";
        // 要生成entity,mapper,servcie等的表名，多个表名使用逗号分割
        // fr_project_user_permission
        String[] includeTableNames = new String[]{"user_info"};
        String authorName = "itdl";
        // 输出目录，默认当前目录的src/main/java下，也可以指定为磁盘固定路径
        String outPath = System.getProperty("user.dir") + "/src/main/java";

        dataSourceConfig.setDriverName(driverClassName);
        dataSourceConfig.setUrl(jdbcUrl);
        dataSourceConfig.setUsername(userName);
        dataSourceConfig.setPassword(password);
        dataSourceConfig.setDbType(dbType);

        generator.setDataSource(dataSourceConfig);

        //配置文件生成路径参数
        PackageConfig packageConfig = new PackageConfig();
        // 配置模板
        TemplateConfig templateConfig = new TemplateConfig();
        packageConfig.setParent(parentPackage);
        packageConfig.setEntity(entityPackage);
        packageConfig.setMapper(mapperPackage);
        packageConfig.setXml(xmlPackage);
        if (!StringUtils.isBlank(servicePackage)){
            packageConfig.setService(servicePackage);
        }else{
           templateConfig.setService("");
        }
        if (!StringUtils.isBlank(serviceImplPackage)){
            packageConfig.setServiceImpl(serviceImplPackage);
        }else{
            templateConfig.setServiceImpl("");
        }
        if (!StringUtils.isBlank(controllerPackage)){
            packageConfig.setController(controllerPackage);
        }else{
            templateConfig.setController(controllerPackage);
        }
        generator.setPackageInfo(packageConfig);

        //策略配置
        StrategyConfig strategyConfig = new StrategyConfig();
        //pojo类超类
//        strategyConfig.setSuperEntityClass(BaseEntity.class);
        //表名转驼峰
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        //字段驼峰命名
        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);
        //set方法builder模式
        strategyConfig.setChainModel(true);
        //使用lombok注解
        strategyConfig.setEntityLombokModel(true);
        //不生成serial version uuid
        strategyConfig.setEntitySerialVersionUID(true);
        //要生成的表名
        strategyConfig.setInclude(includeTableNames);
        // 设置Mapper普洱父类
        if (StringUtils.isNotEmpty(supperMapperClass)){
            strategyConfig.setSuperMapperClass(supperMapperClass);
        }



        // 配置自定义输出模板
        //指定自定义模板路径，注意不要带上.ftl/.vm, 会根据使用的模板引擎自动识别
        // templateConfig.setEntity("templates/entity2.java");
        // 不生成service和controller
//         templateConfig.setService("");
//         templateConfig.setServiceImpl("");

        generator.setTemplate(templateConfig);

        generator.setStrategy(strategyConfig);

        //全局配置
        GlobalConfig globalConfig = new GlobalConfig();
        //输出目录
        globalConfig.setOutputDir(outPath);
        globalConfig.setAuthor(authorName);
        generator.setGlobalConfig(globalConfig);
        globalConfig.setDateType(DateType.ONLY_DATE);
        generator.execute();
    }


}