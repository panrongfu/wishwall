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
     * result : [{"username":"yoyo","userid":"921bb250-0157-11e6-9aef-75c231961465","icon":"http://wishwall.oss-cn-shenzhen.aliyuncs.com/19715b71-7c3f-4ddc-838c-fda993959bd21461838492851.jpg","content":"世界这么大我想去走走","time":"2016-05-01T22:32:50.000Z","wishid":"382eded0-1051-11e6-986f-fd048ab96630","wish_img":[{"imageid":"http://wishwall.oss-cn-shenzhen.aliyuncs.com/0c65486c-e93c-462d-a0f4-61c39fecb37d"},{"imageid":"http://wishwall.oss-cn-shenzhen.aliyuncs.com/058d6424-cc1c-4dcb-b0f9-2869bff58f70"}],"wish_like":[{"userid":"921bb250-0157-11e6-9aef-75c231961465","username":"yoyo"}],"wish_comm":[{"userid":"aca2cff0-0f8f-11e6-893b-4b7c1eda8e5b","username":"习大大","icon":"http://wishwall.oss-cn-shenzhen.aliyuncs.com/1ab2182a-d485-4de8-af7c-de38e96fd65b1462102401096.jpg","content":"dfdfdf"}]},{"username":"yoyo","userid":"921bb250-0157-11e6-9aef-75c231961465","icon":"http://wishwall.oss-cn-shenzhen.aliyuncs.com/19715b71-7c3f-4ddc-838c-fda993959bd21461838492851.jpg","content":"今天下午是钓鱼吧","time":"2016-05-13T15:02:59.000Z","wishid":"3c2e1010-0f79-11e6-a437-b52acefa3b5b","wish_img":[{"imageid":"http://wishwall.oss-cn-shenzhen.aliyuncs.com/4a71defc-772b-4206-9472-6bbb2a14472e"},{"imageid":"http://wishwall.oss-cn-shenzhen.aliyuncs.com/95f52b53-c0d4-44e7-a69f-f721a661b6fe"},{"imageid":"http://wishwall.oss-cn-shenzhen.aliyuncs.com/b202f445-1de3-46af-9d05-f479b600b0c3"},{"imageid":"http://wishwall.oss-cn-shenzhen.aliyuncs.com/58a45dda-0418-4de2-b2e8-7fd8ed21e075"},{"imageid":"http://wishwall.oss-cn-shenzhen.aliyuncs.com/9fbaf662-97b2-466c-9e15-d0909b161d11"}],"wish_like":[{"userid":"921bb250-0157-11e6-9aef-75c231961465","username":"yoyo"}],"wish_comm":[{"userid":"7BA13D632CEBA652E68B7270E7DEDF78","username":"留得枯荷聽雨聲","icon":"http://q.qlogo.cn/qqapp/1105300299/7BA13D632CEBA652E68B7270E7DEDF78/40","content":"ddd"},{"userid":"0f3ae300-0f95-11e6-893b-4b7c1eda8e5b","username":"德玛西亚","icon":"http://wishwall.oss-cn-shenzhen.aliyuncs.com/143391e8-375c-4d86-8004-9b5d4bb5538a1462104418862.jpg","content":"订单"}]}]
     * message : ok
     */

    private int code;
    private String message;
    /**
     * username : yoyo
     * userid : 921bb250-0157-11e6-9aef-75c231961465
     * icon : http://wishwall.oss-cn-shenzhen.aliyuncs.com/19715b71-7c3f-4ddc-838c-fda993959bd21461838492851.jpg
     * content : 世界这么大我想去走走
     * time : 2016-05-01T22:32:50.000Z
     * wishid : 382eded0-1051-11e6-986f-fd048ab96630
     * wish_img : [{"imageid":"http://wishwall.oss-cn-shenzhen.aliyuncs.com/0c65486c-e93c-462d-a0f4-61c39fecb37d"},{"imageid":"http://wishwall.oss-cn-shenzhen.aliyuncs.com/058d6424-cc1c-4dcb-b0f9-2869bff58f70"}]
     * wish_like : [{"userid":"921bb250-0157-11e6-9aef-75c231961465","username":"yoyo"}]
     * wish_comm : [{"userid":"aca2cff0-0f8f-11e6-893b-4b7c1eda8e5b","username":"习大大","icon":"http://wishwall.oss-cn-shenzhen.aliyuncs.com/1ab2182a-d485-4de8-af7c-de38e96fd65b1462102401096.jpg","content":"dfdfdf"}]
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
         * imageid : http://wishwall.oss-cn-shenzhen.aliyuncs.com/0c65486c-e93c-462d-a0f4-61c39fecb37d
         */

        private List<WishImgBean> wish_img;
        /**
         * userid : 921bb250-0157-11e6-9aef-75c231961465
         * username : yoyo
         */

        private List<WishLikeBean> wish_like;
        /**
         * userid : aca2cff0-0f8f-11e6-893b-4b7c1eda8e5b
         * username : 习大大
         * icon : http://wishwall.oss-cn-shenzhen.aliyuncs.com/1ab2182a-d485-4de8-af7c-de38e96fd65b1462102401096.jpg
         * content : dfdfdf
         */

        private List<WishCommBean> wish_comm;

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

        public List<WishLikeBean> getWish_like() {
            return wish_like;
        }

        public void setWish_like(List<WishLikeBean> wish_like) {
            this.wish_like = wish_like;
        }

        public List<WishCommBean> getWish_comm() {
            return wish_comm;
        }

        public void setWish_comm(List<WishCommBean> wish_comm) {
            this.wish_comm = wish_comm;
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

        public static class WishLikeBean {
            private String userid;
            private String username;

            public String getUserid() {
                return userid;
            }

            public void setUserid(String userid) {
                this.userid = userid;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
            }
        }

        public static class WishCommBean {
            private String userid;
            private String username;
            private String icon;
            private String content;

            public String getUserid() {
                return userid;
            }

            public void setUserid(String userid) {
                this.userid = userid;
            }

            public String getUsername() {
                return username;
            }

            public void setUsername(String username) {
                this.username = username;
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
        }
    }
}
