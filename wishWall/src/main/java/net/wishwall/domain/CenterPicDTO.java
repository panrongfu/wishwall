package net.wishwall.domain;

/**
 * @author panRongFu on 2016/5/13.
 * @Description
 * @email pan@ipushan.com
 */
public class CenterPicDTO {


    /**
     * code : 200
     * result : {"pic_url":"http://wishwall.oss-cn-shenzhen.aliyuncs.com/c97805f8-bdee-4672-8ca2-aed8d9c416d01463129840887.jpg"}
     * message : ok
     */

    private int code;
    /**
     * pic_url : http://wishwall.oss-cn-shenzhen.aliyuncs.com/c97805f8-bdee-4672-8ca2-aed8d9c416d01463129840887.jpg
     */

    private ResultBean result;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

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

    public static class ResultBean {
        private String pic_url;

        public String getPic_url() {
            return pic_url;
        }

        public void setPic_url(String pic_url) {
            this.pic_url = pic_url;
        }
    }
}
