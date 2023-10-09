package cn.shmedo.monitor.monibotbaseapi.model.param.otherdevice;

import cn.shmedo.monitor.monibotbaseapi.model.param.project.PropertyIdAndValue;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-09-27 17:38
 **/
@Data
public class AddOtherDeviceItem {
    @NotBlank
    private String name;
    @NotBlank
    private String token;
    @NotBlank
    private String model;
    @NotBlank
    private String vendor;
    @NotNull
    private Integer projectID;
    @Pattern(regexp = "^.+$", message = "扩展字段应为JSON格式")
    private String exValue;
    @Valid
    @NotEmpty
    private List<@NotNull PropertyIdAndValue> propertyList;
}
