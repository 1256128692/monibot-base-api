package cn.shmedo.monitor.monibotbaseapi.model.enums;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

/**
 * @program: monibot-base-api
 * @author: gaoxu
 * @create: 2023-04-17 14:46
 **/
public enum ProjectImageType {
    /**
     * 底图
     */
    Img("Image"),
    /**
     * 全景
     */
    OverallView("OverallView"),
    /**
     * 三维
     */
    SpatialView("SpatialView");

    private String type;

    ProjectImageType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static boolean isValidate(String type){
        if (StringUtils.isBlank(type)){
            return false;
        }

        switch (type){
            case "Image":
                return true;
            case "OverallView":
                return true;
            case "SpatialView":
                return true;
            default:
                return false;
        }
    }

}
