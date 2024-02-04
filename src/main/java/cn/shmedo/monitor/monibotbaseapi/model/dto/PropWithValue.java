package cn.shmedo.monitor.monibotbaseapi.model.dto;

import lombok.Data;

/**
 * tb_project_property 的变体，包含 value 字段
 *
 * @author Chengfs on 2023/11/23
 */
@Data
public class PropWithValue {

    private Integer projectID;
    private String name;
    private Integer groupID;
    private Integer type;
    private String value;
}