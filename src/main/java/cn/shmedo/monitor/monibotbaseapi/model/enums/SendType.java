package cn.shmedo.monitor.monibotbaseapi.model.enums;

public enum SendType {
    /**
     * HTTP
     */
    HTTP(0),
    /**
     * AMQP
     */
    AMQP(1),
    /**
     * MDNET
     */
    MDNET(2),
    /**
     * MDCS
     */
    MDCS(3),
    /**
     * HTTP_QUERY
     */
    HTTP_QUERY(4),
    /**
     * MDMBASE
     */
    MDMBASE(5);

    private int sendType;

    SendType(int sType) {
        this.sendType = sType;
    }

    public int toInt() {
        return sendType;
    }

    public static SendType valueOf(int sendType) {
        switch (sendType) {
            case 0:
                return HTTP;
            case 1:
                return AMQP;
            case 2:
                return MDNET;
            case 3:
                return MDCS;
            case 4:
                return HTTP_QUERY;
            case 5:
                return MDMBASE;
            default:
                return null;
        }
    }
}
