package net.wishwall.domain;

import java.util.List;

/**
 * @author panRongFu on 2016/4/13.
 * @Description
 * @email pan@ipushan.com
 */
public class FriendIdsDTO {

    /**
     * code : 200
     * result : [{"friendid":"5a5b7960-0065-11e6-9ea7-27ec933c934a"},{"friendid":"b0dbd030-0161-11e6-9cff-8ff3a5dafd85"}]
     * message : ok
     */

    private int code;
    private String message;
    /**
     * friendid : 5a5b7960-0065-11e6-9ea7-27ec933c934a
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
        private String friendid;

        public String getFriendid() {
            return friendid;
        }

        public void setFriendid(String friendid) {
            this.friendid = friendid;
        }
    }
}
