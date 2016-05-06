package net.wishwall.domain;

/**
 * @author panRongFu on 2016/4/27.
 * @Description
 * @email pan@ipushan.com
 */
public class CodeByNameDTO {


    /**
     * code : 200
     * result : {"id":31033,"code":4510,"parentId":45,"name":"百色市","level":2}
     * message : ok
     */

    private int code;
    /**
     * id : 31033
     * code : 4510
     * parentId : 45
     * name : 百色市
     * level : 2
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
        private int id;
        private int code;
        private int parentId;
        private String name;
        private int level;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public int getParentId() {
            return parentId;
        }

        public void setParentId(int parentId) {
            this.parentId = parentId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getLevel() {
            return level;
        }

        public void setLevel(int level) {
            this.level = level;
        }
    }
}
