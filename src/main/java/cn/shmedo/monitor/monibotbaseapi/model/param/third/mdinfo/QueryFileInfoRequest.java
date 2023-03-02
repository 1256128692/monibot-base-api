package cn.shmedo.monitor.monibotbaseapi.model.param.third.mdinfo;

import jakarta.validation.constraints.NotBlank;

public class QueryFileInfoRequest  {

    @NotBlank
    private String bucketName;
    private Long validityPeriod;
    @NotBlank
    private String filePath;

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public Long getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(Long validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public QueryFileInfoRequest() {
    }

    public QueryFileInfoRequest(String bucketName, String filePath) {
        this.bucketName = bucketName;
        this.filePath = filePath;
    }
}
