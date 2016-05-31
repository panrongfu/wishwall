package net.wishwall.domain;

/**
 * @author panRongFu on 2016/5/23.
 * @Description
 * @email pan@ipushan.com
 */
public class VersionDTO {

    private int code;
    private ResultBean result;
    private String message;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


    public class ResultBean{
        String versionName;
        int versionCode;
        String updateInfo;

        public String getUpdateInfo() {
            return updateInfo;
        }

        public void setUpdateInfo(String updateInfo) {
            this.updateInfo = updateInfo;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }
    }
}
