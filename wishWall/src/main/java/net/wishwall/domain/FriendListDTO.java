package net.wishwall.domain;

import java.util.List;

/**
 * @author panRongFu on 2016/4/14.
 * @Description
 * @email pan@ipushan.com
 */
public class FriendListDTO {

    /**
     * code : 200
     * result : [{"userid":"5a5b7960-0065-11e6-9ea7-27ec933c934a","username":"www","password":"www","token":"hEs96o4CBL+BTnSLWDQSG2dPlBdjpXbTcWDFnO1cMBrsUfnfzP89Q/8xj8VA5h0UTSHbDJFMTwOgeO76wmvlLxg43OI4VXROr2XqwuiknJG0e79fVubAr5LpvG8h575ef2w5/oT3UoU=","icon":"","sex":"","phone":"","birth_year":"","birth_month":"","birth_day":"","province":"","city":"","area":"","status":1},{"userid":"b0dbd030-0161-11e6-9cff-8ff3a5dafd85","username":"rrr","password":"rrr","token":"lbsXYLy8usuGB3YKxGKcbz5yPFRpSQQIKv43k2DYNTa6s542cx2A6Vmg1Cs5c7Q+aUPfrx3meC2kd8WZCXKjINVzYKs59c1Xf40ofag86fjHAyF5nRZK3o6ZsraotLOUajWtd5QNdKE=","icon":"","sex":"","phone":"","birth_year":"","birth_month":"","birth_day":"","province":"","city":"","area":"","status":1},{"userid":"cd02bfe0-015b-11e6-a5da-0d59dff86731","username":"eee","password":"eee","token":"z7vO/leXdiwDQosM9OIOf2dPlBdjpXbTcWDFnO1cMBrsUfnfzP89QxcqaVbbDKRQQgITQzw2HNIXWJzPGOKDich+h/vhj6A25qyuC0JqIVlAhL/D0w+z783bQ7gV+Q/SQmvI6yeD8QQ=","icon":"","sex":"","phone":"","birth_year":"","birth_month":"","birth_day":"","province":"","city":"","area":"","status":1}]
     * message : ok
     */

    private int code;
    private String message;
    /**
     * userid : 5a5b7960-0065-11e6-9ea7-27ec933c934a
     * username : www
     * password : www
     * token : hEs96o4CBL+BTnSLWDQSG2dPlBdjpXbTcWDFnO1cMBrsUfnfzP89Q/8xj8VA5h0UTSHbDJFMTwOgeO76wmvlLxg43OI4VXROr2XqwuiknJG0e79fVubAr5LpvG8h575ef2w5/oT3UoU=
     * icon :
     * sex :
     * phone :
     * birth_year :
     * birth_month :
     * birth_day :
     * province :
     * city :
     * area :
     * status : 1
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
        private String userid;
        private String username;
        private String password;
        private String token;
        private String icon;
        private String sex;
        private String phone;
        private String birth_year;
        private String birth_month;
        private String birth_day;
        private String province;
        private String city;
        private String area;
        private int status;
        private String weixin;
        private String qq;
        private String nickname;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }


        public String getQq() {
            return qq;
        }

        public void setQq(String qq) {
            this.qq = qq;
        }

        public String getWeixin() {
            return weixin;
        }

        public void setWeixin(String weixin) {
            this.weixin = weixin;
        }

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

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getIcon() {
            return icon;
        }

        public void setIcon(String icon) {
            this.icon = icon;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getBirth_year() {
            return birth_year;
        }

        public void setBirth_year(String birth_year) {
            this.birth_year = birth_year;
        }

        public String getBirth_month() {
            return birth_month;
        }

        public void setBirth_month(String birth_month) {
            this.birth_month = birth_month;
        }

        public String getBirth_day() {
            return birth_day;
        }

        public void setBirth_day(String birth_day) {
            this.birth_day = birth_day;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }
}
