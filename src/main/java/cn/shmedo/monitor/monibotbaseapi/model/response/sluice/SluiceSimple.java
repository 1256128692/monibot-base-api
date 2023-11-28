package cn.shmedo.monitor.monibotbaseapi.model.response.sluice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Chengfs on 2023/11/21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SluiceSimple {
    /**
     * 项目ID (水闸ID)
     */
    private Integer projectID;

    /**
     * 项目名称 (水闸名称)
     */
    private String projectName;

    /**
     * 所属渠道
     */
    private String canal;

    /**
     * 水闸类型
     */
    private String sluiceType;

    /**
     * 管理单位
     */
    private String manageUnit;

    /**
     * 闸孔数量
     */
    private Integer sluiceHoleNum;
}