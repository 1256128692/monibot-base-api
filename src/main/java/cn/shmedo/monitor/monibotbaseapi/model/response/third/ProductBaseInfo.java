package cn.shmedo.monitor.monibotbaseapi.model.response.third;

/**
 * @author: youxian.kong@shmedo.cn
 * @date: 2023-05-06 14:58
 */
public class ProductBaseInfo {
    private Integer productID;
    private String productName;
    private String productToken;

    public ProductBaseInfo() {
    }

    public ProductBaseInfo(Integer productID, String productName, String productToken) {
        this.productID = productID;
        this.productName = productName;
        this.productToken = productToken;
    }

    public Integer getProductID() {
        return productID;
    }

    public void setProductID(Integer productID) {
        this.productID = productID;
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
