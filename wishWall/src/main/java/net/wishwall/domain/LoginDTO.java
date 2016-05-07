package net.wishwall.domain;

/**
 * @author panRongFu on 2016/4/13.
 * @Description
 * @email pan@ipushan.com
 */
public class LoginDTO {
    /**
     * code : 200
     * result : {"userid":"5a5b7960-0065-11e6-9ea7-27ec933c934a","username":"www","password":"www","token":"hEs96o4CBL+BTnSLWDQSG2dPlBdjpXbTcWDFnO1cMBrsUfnfzP89Q/8xj8VA5h0UTSHbDJFMTwOgeO76wmvlLxg43OI4VXROr2XqwuiknJG0e79fVubAr5LpvG8h575ef2w5/oT3UoU=","icon":"","sex":"","phone":"","birth_year":"","birth_month":"","birth_day":"","province":"","city":"","area":"","wToken":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyaWQiOiI1YTViNzk2MC0wMDY1LTExZTYtOWVhNy0yN2VjOTMzYzkzNGEiLCJpYXQiOjE0NjA5NTU5NTIsImV4cCI6MTQ2MDk2MzE1Mn0.gGHqbaobKaGLNDah7_JjOIfjCDtuIsybgb3EmtDjeoE"}
     * message : ok
     */

    private int code;
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
     * wToken : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyaWQiOiI1YTViNzk2MC0wMDY1LTExZTYtOWVhNy0yN2VjOTMzYzkzNGEiLCJpYXQiOjE0NjA5NTU5NTIsImV4cCI6MTQ2MDk2MzE1Mn0.gGHqbaobKaGLNDah7_JjOIfjCDtuIsybgb3EmtDjeoE
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
        private String wToken;

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

        public String getWToken() {
            return wToken;
        }

        public void setWToken(String wToken) {
            this.wToken = wToken;
        }
    }
}
