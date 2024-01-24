package cn.shmedo.monitor.monibotbaseapi.model.response.dashboard;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author wuxl
 * @Date 2024/1/22 16:44
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.response.dashboard
 * @ClassName: QueryProductServicesRes
 * @Description: TODO
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
public class QueryProductServicesRes {
    private Integer monitorType;
    private String monitorTypeName;
    private int deviceCount;
    private List<Product> productList;

    @Data
    @Accessors(chain = true)
    public static class Product{
        private Integer productID;
        private String productName;
        private int deviceCount;
    }
}
