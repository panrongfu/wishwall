package net.wishwall.domain;

import java.util.List;

/**
 * @author panRongFu on 2016/4/26.
 * @Description
 * @email pan@ipushan.com
 */
public class ProvCityAreaDTO {

    /**
     * code : 200
     * result : [{"id":1,"code":11,"parentId":0,"name":"北京市","level":1},{"id":338,"code":12,"parentId":0,"name":"天津市","level":1},{"id":636,"code":13,"parentId":0,"name":"河北省","level":1},{"id":3102,"code":14,"parentId":0,"name":"山西","level":1},{"id":4670,"code":15,"parentId":0,"name":"内蒙古自治区","level":1},{"id":5827,"code":21,"parentId":0,"name":"辽宁省","level":1},{"id":7531,"code":22,"parentId":0,"name":"吉林省","level":1},{"id":8558,"code":23,"parentId":0,"name":"黑龙江省","level":1},{"id":10543,"code":31,"parentId":0,"name":"上海市","level":1},{"id":10808,"code":32,"parentId":0,"name":"江苏省","level":1},{"id":12596,"code":33,"parentId":0,"name":"浙江省","level":1},{"id":14234,"code":34,"parentId":0,"name":"安徽省","level":1},{"id":16068,"code":35,"parentId":0,"name":"福建省","level":1},{"id":17359,"code":36,"parentId":0,"name":"江西省","level":1},{"id":19280,"code":37,"parentId":0,"name":"山东省","level":1},{"id":21387,"code":41,"parentId":0,"name":"河南省","level":1},{"id":24022,"code":42,"parentId":0,"name":"湖北省","level":1},{"id":25579,"code":43,"parentId":0,"name":"湖南省","level":1},{"id":28240,"code":44,"parentId":0,"name":"广东省","level":1},{"id":30164,"code":45,"parentId":0,"name":"广西壮族自治区","level":1},{"id":31563,"code":46,"parentId":0,"name":"海南省","level":1},{"id":31929,"code":50,"parentId":0,"name":"重庆市","level":1},{"id":33007,"code":51,"parentId":0,"name":"四川省","level":1},{"id":37906,"code":52,"parentId":0,"name":"贵州省","level":1},{"id":39556,"code":53,"parentId":0,"name":"云南省","level":1},{"id":41103,"code":54,"parentId":0,"name":"西藏自治区","level":1},{"id":41877,"code":61,"parentId":0,"name":"陕西省","level":1},{"id":43776,"code":62,"parentId":0,"name":"甘肃省","level":1},{"id":45286,"code":63,"parentId":0,"name":"青海省","level":1},{"id":45753,"code":64,"parentId":0,"name":"宁夏回族自治区","level":1},{"id":46047,"code":65,"parentId":0,"name":"新疆维吾尔自治区","level":1},{"id":47493,"code":71,"parentId":0,"name":"台湾省","level":1},{"id":47494,"code":81,"parentId":0,"name":"香港特别行政区","level":1},{"id":47495,"code":82,"parentId":0,"name":"澳门特别行政区","level":1}]
     * message : ok
     */

    private int code;
    private String message;
    /**
     * id : 1
     * code : 11
     * parentId : 0
     * name : 北京市
     * level : 1
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
