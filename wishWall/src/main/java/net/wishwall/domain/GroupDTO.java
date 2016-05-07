package net.wishwall.domain;

/**
 * @author panRongFu on 2016/4/20.
 * @Description
 * @email pan@ipushan.com
 */
public class GroupDTO {


    /**
     * code : 200
     * result : {"groupid":"3980e340-062a-11e6-9bc1-85ba3243ce8e","userid":"921bb250-0157-11e6-9aef-75c231961465","name":"都比爬梯","icon":"","username":"","user_icon":"","token":"","introduce":"都比爬梯吗哈哈","number":5,"max_number":500,"create_time":"2016-04-18T16:00:00.000Z","status":0}
     * message : ok
     */

    private int code;
    /**
     * groupid : 3980e340-062a-11e6-9bc1-85ba3243ce8e
     * userid : 921bb250-0157-11e6-9aef-75c231961465
     * name : 都比爬梯
     * icon :
     * username :
     * user_icon :
     * token :
     * introduce : 都比爬梯吗哈哈
     * number : 5
     * max_number : 500
     * create_time : 2016-04-18T16:00:00.000Z
     * status : 0
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
