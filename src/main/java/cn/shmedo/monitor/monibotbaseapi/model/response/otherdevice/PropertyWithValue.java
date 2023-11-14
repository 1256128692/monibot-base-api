package cn.shmedo.monitor.monibotbaseapi.model.response.otherdevice;

import cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo.FileInfoResponse;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-10-07 14:10
 **/
@Data
@Builder
public class PropertyWithValue {
    private Integer ID;
    private String name;
    private String unit;
    private String value;
    private String className;
    private Byte type;
    private Boolean required;
    private String enumField;
    private Boolean multiSelect;

    private List<String> ossList;
    private List<FileInfoResponse> fileList;
}
