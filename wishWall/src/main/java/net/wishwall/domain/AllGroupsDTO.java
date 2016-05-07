package net.wishwall.domain;

import java.util.List;

/**
 * @author panRongFu on 2016/4/14.
 * @Description
 * @email pan@ipushan.com
 */
public class AllGroupsDTO {
    /**
     * code : 200
     * result : [{"groupid":"583aef20-022d-11e6-8fa0-0f6703b7789c","userid":"921bb250-0157-11e6-9aef-75c231961465","name":"许愿树","icon":"","username":"","user_icon":"","token":"","introduce":"许愿树","number":0,"max_number":500,"create_time":"2016-04-13T16:00:00.000Z","status":0},{"groupid":"6ca89430-022d-11e6-8fa0-0f6703b7789c","userid":"921bb250-0157-11e6-9aef-75c231961465","name":"许愿墙","icon":"","username":"","user_icon":"","token":"","introduce":"许愿墙","number":2,"max_number":500,"create_time":"2016-04-13T16:00:00.000Z","status":0},{"groupid":"72a5a120-022d-11e6-8fa0-0f6703b7789c","userid":"921bb250-0157-11e6-9aef-75c231961465","name":"许愿家族","icon":"","username":"","user_icon":"","token":"","introduce":"许愿家族","number":2,"max_number":500,"create_time":"2016-04-13T16:00:00.000Z","status":0}]
     * message : ok
     */

    private int code;
    private String message;
    /**
     * groupid : 583aef20-022d-11e6-8fa0-0f6703b7789c
     * userid : 921bb250-0157-11e6-9aef-75c231961465
     * name : 许愿树
     * icon :
     * username :
     * user_icon :
     * token :
     * introduce : 许愿树
     * number : 0
     * max_number : 500
     * create_time : 2016-04-13T16:00:00.000Z
     * status : 0
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
        private String groupid;
        private String userid;
        private String name;
        private String icon;
        private String username;
        private String user_icon;
        private String token;
        private String introduce;
        private int number;
        private int max_number;
        private String create_time;
        private int status;

        public String getGroupid() {
            return groupid;
        }

        public void setGroupid(String groupid) {
            this.groupid = groupid;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getUser_icon() {
            return user_icon;
        }

        public void setUser_icon(String user_icon) {
            this.user_icon = user_icon;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getIntroduce() {
            return introduce;
        }

        public void setIntroduce(String introduce) {
            this.introduce = introduce;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        public int getMax_number() {
            return max_number;
        }

        public void setMax_number(int max_number) {
            this.max_number = max_number;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
