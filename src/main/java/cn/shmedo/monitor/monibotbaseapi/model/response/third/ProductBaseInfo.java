package cn.shmedo.monitor.monibotbaseapi.model.response.third;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-06 14:58
 */
public class ProductBaseInfo {
    private Integer ID;
    private String productName;
    private String productToken;

    public ProductBaseInfo() {
    }

    public ProductBaseInfo(Integer ID, String productName, String productToken) {
        this.ID = ID;
        this.productName = productName;
        this.productToken = productToken;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductToken() {
        return productToken;
    }

    public void setProductToken(String productToken) {
        this.productToken = productToken;
    }
}
