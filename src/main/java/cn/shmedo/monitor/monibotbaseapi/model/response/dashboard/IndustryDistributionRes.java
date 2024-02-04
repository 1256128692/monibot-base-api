package cn.shmedo.monitor.monibotbaseapi.model.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Author wuxl
 * @Date 2024/1/18 14:43
 * @PackageName:com.xxl.job.executor.data.response.mdmbase.dashboard
 * @ClassName: IndustryDistributionRes
 * @Description: 行业分布接口返回实体
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class IndustryDistributionRes implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 项目一级分类
     */
    private int projectMainType;
    /**
     * 项目一级分类名称
     */
    private String projectMainTypeName;
    /**
     * 项目现有量
     */
    private int projectCount;
    /**
     * 项目近一年增长量
     */
    private long growthInThePastYear;
}
