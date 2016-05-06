package net.wishwall.domain;

import java.util.List;

/**
 * @author panRongFu on 2016/4/30.
 * @Description
 * @email pan@ipushan.com
 */
public class WishsDTO {


    /**
     * code : 200
     * result : [{"username":"yoyo","userid":"921bb250-0157-11e6-9aef-75c231961465","icon":"http://wishwall.oss-cn-shenzhen.aliyuncs.com/19715b71-7c3f-4ddc-838c-fda993959bd21461838492851.jpg","content":"我想跟你谈谈","time":"2016-04-28T16:00:00.000Z","wishid":"7505e240-0df4-11e6-9cbc-7f96a01c1c25","wish_img":[{"imageid":"http://wishwall.oss-cn-shenzhen.aliyuncs.com/9fe164ca-4ab2-4292-aa0d-50593c231608"},{"imageid":"http://wishwall.oss-cn-shenzhen.aliyuncs.com/a78c63b9-6b89-4ee3-a41e-94b0594beca2"}]}]
     * message : ok
     */

    private int code;
    private String message;
    /**
     * username : yoyo
     * userid : 921bb250-0157-11e6-9aef-75c231961465
     * icon : http://wishwall.oss-cn-shenzhen.aliyuncs.com/19715b71-7c3f-4ddc-838c-fda993959bd21461838492851.jpg
     * content : 我想跟你谈谈
     * time : 2016-04-28T16:00:00.000Z
     * wishid : 7505e240-0df4-11e6-9cbc-7f96a01c1c25
     * wish_img : [{"imageid":"http://wishwall.oss-cn-shenzhen.aliyuncs.com/9fe164ca-4ab2-4292-aa0d-50593c231608"},{"imageid":"http://wishwall.oss-cn-shenzhen.aliyuncs.com/a78c63b9-6b89-4ee3-a41e-94b0594beca2"}]
     */

    private List<ResultBean> result;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        private String username;
        private String userid;
        private String icon;
        private String content;
        private String time;
        private String wishid;
        /**
         * imageid : http://wishwall.oss-cn-shenzhen.aliyuncs.com/9fe164ca-4ab2-4292-aa0d-50593c231608
         */

        private List<WishImgBean> wish_img;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getWishid() {
            return wishid;
        }

        public void setWishid(String wishid) {
            this.wishid = wishid;
        }

        public List<WishImgBean> getWish_img() {
            return wish_img;
        }

        public void setWish_img(List<WishImgBean> wish_img) {
            this.wish_img = wish_img;
        }

        public static class WishImgBean {
            private String imageid;

            public String getImageid() {
                return imageid;
            }

            public void setImageid(String imageid) {
                this.imageid = imageid;
            }
        }
    }
}
