package net.wishwall.domain;

import java.util.List;

/**
 * @author panRongFu on 2016/4/18.
 * @Description
 * @email pan@ipushan.com
 */
public class AllChatroomsDTO {
    /**
     * code : 200
     * result : [{"id":"6cd388a0-054a-11e6-bd89-efd462befd9e","userid":"123456","room_name":"你许我愿","create_time":"2016-04-17T16:00:00.000Z","title":null,"describe":null,"icon":null},{"id":"718ddc60-054a-11e6-bd89-efd462befd9e","userid":"123456","room_name":"分享心愿","create_time":"2016-04-17T16:00:00.000Z","title":null,"describe":null,"icon":null},{"id":"8d8dad50-054a-11e6-bd89-efd462befd9e","userid":"123456","room_name":"许愿许愿","create_time":"2016-04-17T16:00:00.000Z","title":null,"describe":null,"icon":null}]
     * message : ok
     */

    private int code;
    private String message;
    /**
     * id : 6cd388a0-054a-11e6-bd89-efd462befd9e
     * userid : 123456
     * room_name : 你许我愿
     * create_time : 2016-04-17T16:00:00.000Z
     * title : null
     * describe : null
     * icon : null
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
        private String id;
        private String userid;
        private String room_name;
        private String create_time;
        private Object title;
        private Object describe;
        private Object icon;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getRoom_name() {
            return room_name;
        }

        public void setRoom_name(String room_name) {
            this.room_name = room_name;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public Object getTitle() {
            return title;
        }

        public void setTitle(Object title) {
            this.title = title;
        }

        public Object getDescribe() {
            return describe;
        }

        public void setDescribe(Object describe) {
            this.describe = describe;
        }

        public Object getIcon() {
            return icon;
        }

        public void setIcon(Object icon) {
            this.icon = icon;
        }
    }
}
