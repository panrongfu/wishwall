package net.wishwall.domain;

import java.util.List;

/**
 * @author panRongFu on 2016/4/21.
 * @Description
 * @email pan@ipushan.com
 */
public class ApplyAddFriendListDTO {


    /**
     * code : 200
     * result : [{"userid":"cd02bfe0-015b-11e6-a5da-0d59dff86731","username":"eee","password":"eee","token":"z7vO/leXdiwDQosM9OIOf2dPlBdjpXbTcWDFnO1cMBrsUfnfzP89QxcqaVbbDKRQQgITQzw2HNIXWJzPGOKDich+h/vhj6A25qyuC0JqIVlAhL/D0w+z783bQ7gV+Q/SQmvI6yeD8QQ=","icon":"","sex":"","phone":"","birth_year":"","birth_month":"","birth_day":"","province":"","city":"","area":"","status":3},{"userid":"862bc4a0-06c8-11e6-8c12-5bc1965907a5","username":"ggg","password":"ggg","token":"7beXOhmKJxlEWQlFhiP+vj5yPFRpSQQIKv43k2DYNTa6s542cx2A6RwQS5lndS0+yWPs3Eqw8iR4nBMEvjn7X5+Rd+eAJDZJV8I2yge3rcRV+luqNW01FIKQVPmwdJQJHauQT5Eicoo=","icon":"","sex":"","phone":"","birth_year":"","birth_month":"","birth_day":"","province":"","city":"","area":"","status":3},{"userid":"f9c5d6c0-0525-11e6-90eb-6188a9f987d0","username":"ooo","password":"ooo","token":"qIw8mREOdprXfU5XiA0Wiz5yPFRpSQQIKv43k2DYNTa6s542cx2A6aII7/AyiQ8wEe3ln2WDu3FfBERj3DswKhLpXz+fGKtkHxQzI3T2qdAEh2elVjFUIeNueCO5mb9R8S4uvQSdxE4=","icon":"","sex":"","phone":"","birth_year":"","birth_month":"","birth_day":"","province":"","city":"","area":"","status":null}]
     * message : ok
     */

    private int code;
    private String message;
    /**
     * userid : cd02bfe0-015b-11e6-a5da-0d59dff86731
     * username : eee
     * password : eee
     * token : z7vO/leXdiwDQosM9OIOf2dPlBdjpXbTcWDFnO1cMBrsUfnfzP89QxcqaVbbDKRQQgITQzw2HNIXWJzPGOKDich+h/vhj6A25qyuC0JqIVlAhL/D0w+z783bQ7gV+Q/SQmvI6yeD8QQ=
     * icon :
     * sex :
     * phone :
     * birth_year :
     * birth_month :
     * birth_day :
     * province :
     * city :
     * area :
     * status : 3
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
