package cn.shmedo.monitor.monibotbaseapi.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.injector.methods.AlwaysUpdateSomeColumnById;
import com.baomidou.mybatisplus.extension.injector.methods.InsertBatchSomeColumn;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Author cyf
 * @Date 2023/2/23 11:51
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.config
 * @ClassName: MybatisPlusConfig
 * @Description:
 * @Version 1.0
 */
@Configuration
public class MybatisPlusConfig {
    /**
     * 新增分页拦截器，并设置数据库类型为mysql
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }

    @Bean
    public DefaultSqlInjector easySqlInjector() {
        return new DefaultSqlInjector() {
            @Override
            public List<AbstractMethod> getMethodList(Class<?> mapperClass, TableInfo tableInfo) {
                List<AbstractMethod> methodList = super.getMethodList(mapperClass, tableInfo);
                //自动填充策略为更新填充策略时，不用插入值
                methodList.add(new InsertBatchSomeColumn(i -> i.getFieldFill() != FieldFill.UPDATE));
                //自动填充策略为插入填充策略时，字段不用更新
                methodList.add(new AlwaysUpdateSomeColumnById(i -> i.getFieldFill() != FieldFill.INSERT));
                return methodList;
            }
        };
    }
}

