package cn.shmedo.monitor.monibotbaseapi.model.param.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-02-23 13:36
 **/
public class TagKeyAndValue {
    @NotBlank
    @Size(max = 50)
    private String key;
    @Pattern(regexp = "[^\\s]+")
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
