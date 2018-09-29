package cn.id0755.sdk.android.entity;

/**
 * 终端设备信息类
 */
public class DeviceInfo {
    /**
     * 设备名称
     */
    private String mDeviceName = "";

    /**
     * 设备类型
     */
    private String mDeviceType = "";

    public DeviceInfo(String deviceName,String deviceType){
        mDeviceName = deviceName;
        mDeviceType = deviceType;
    }

    public String getDeviceName() {
        return mDeviceName;
    }

    public DeviceInfo setDeviceName(String deviceName) {
        this.mDeviceName = deviceName;
        return this;
    }

    public String getDeviceType() {
        return mDeviceType;
    }

    public DeviceInfo setDeviceType(String deviceType) {
        this.mDeviceType = deviceType;
        return this;
    }

}
