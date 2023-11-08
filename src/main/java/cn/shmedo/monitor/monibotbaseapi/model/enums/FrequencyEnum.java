package cn.shmedo.monitor.monibotbaseapi.model.enums;

public enum FrequencyEnum {
    ONCE(0, "单次"),
    YEARLY(1, "每年");

    private final int value;
    private final String description;

    FrequencyEnum(int value, String description) {
        this.value = value;
        this.description = description;
    }

    public int getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }

    public static boolean isValid(int value) {
        for (FrequencyEnum frequency : values()) {
            if (frequency.value == value) {
                return true;
            }
        }
        return false;
    }

    public static String getDescriptionFromValue(int value) {
        for (FrequencyEnum frequency : values()) {
            if (frequency.value == value) {
                return frequency.description;
            }
        }
        throw new IllegalArgumentException("Invalid FrequencyEnum value: " + value);
    }
}
