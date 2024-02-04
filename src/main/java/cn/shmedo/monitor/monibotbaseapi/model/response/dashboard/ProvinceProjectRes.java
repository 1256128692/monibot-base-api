package cn.shmedo.monitor.monibotbaseapi.model.response.dashboard;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author wuxl
 * @Date 2024/1/23 17:10
 * @PackageName:cn.shmedo.monitor.monibotbaseapi.model.response.dashboard
 * @ClassName: ProvinceProjectRes
 * @Description: TODO
 * @Version 1.0
 */
@Data
@Accessors(chain = true)
public class ProvinceProjectRes {
    private Integer provinceCode;
    private String provinceName;
    private String provincialCapital;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private int projectCount;
    private List<City> cityList;

    @Data
    @Accessors(chain = true)
    public static class City{
        private Integer cityCode;
        private String cityName;
        private int projectCount;
    }
}
