package cn.shmedo.monitor.monibotbaseapi.model.db.third;

public class TbService {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_service.ID
     *
     * @mbg.generated Mon Nov 30 17:22:01 CST 2020
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_service.ServiceName
     *
     * @mbg.generated Mon Nov 30 17:22:01 CST 2020
     */
    private String serviceName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_service.ServiceAlias
     *
     * @mbg.generated Mon Nov 30 17:22:01 CST 2020
     */
    private String serviceAlias;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_service.ServiceDesc
     *
     * @mbg.generated Mon Nov 30 17:22:01 CST 2020
     */
    private String serviceDesc;

    private String serviceLink;

    private String serviceIcon;

    private String serviceClass;

    private Boolean display;

    private Boolean visibleToAll;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tb_service.ID
     *
     * @return the value of tb_service.ID
     *
     * @mbg.generated Mon Nov 30 17:22:01 CST 2020
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tb_service.ID
     *
     * @param id the value for tb_service.ID
     *
     * @mbg.generated Mon Nov 30 17:22:01 CST 2020
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tb_service.ServiceName
     *
     * @return the value of tb_service.ServiceName
     *
     * @mbg.generated Mon Nov 30 17:22:01 CST 2020
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tb_service.ServiceName
     *
     * @param serviceName the value for tb_service.ServiceName
     *
     * @mbg.generated Mon Nov 30 17:22:01 CST 2020
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tb_service.ServiceAlias
     *
     * @return the value of tb_service.ServiceAlias
     *
     * @mbg.generated Mon Nov 30 17:22:01 CST 2020
     */
    public String getServiceAlias() {
        return serviceAlias;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tb_service.ServiceAlias
     *
     * @param serviceAlias the value for tb_service.ServiceAlias
     *
     * @mbg.generated Mon Nov 30 17:22:01 CST 2020
     */
    public void setServiceAlias(String serviceAlias) {
        this.serviceAlias = serviceAlias;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column tb_service.ServiceDesc
     *
     * @return the value of tb_service.ServiceDesc
     *
     * @mbg.generated Mon Nov 30 17:22:01 CST 2020
     */
    public String getServiceDesc() {
        return serviceDesc;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column tb_service.ServiceDesc
     *
     * @param serviceDesc the value for tb_service.ServiceDesc
     *
     * @mbg.generated Mon Nov 30 17:22:01 CST 2020
     */
    public void setServiceDesc(String serviceDesc) {
        this.serviceDesc = serviceDesc;
    }

    public String getServiceLink() {
        return serviceLink;
    }

    public void setServiceLink(String serviceLink) {
        this.serviceLink = serviceLink;
    }

    public String getServiceIcon() {
        return serviceIcon;
    }

    public void setServiceIcon(String serviceIcon) {
        this.serviceIcon = serviceIcon;
    }

    public String getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }

    public Boolean getDisplay() {
        return display;
    }

    public void setDisplay(Boolean display) {
        this.display = display;
    }

    public Boolean getVisibleToAll() {
        return visibleToAll;
    }

    public void setVisibleToAll(Boolean visibleToAll) {
        this.visibleToAll = visibleToAll;
    }
}