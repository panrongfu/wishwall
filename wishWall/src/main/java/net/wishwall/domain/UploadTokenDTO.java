package net.wishwall.domain;

/**
 * @author panRongFu on 2016/4/25.
 * @Description
 * @email pan@ipushan.com
 */
public class UploadTokenDTO {


    /**
     * code : 200
     * result : {"AccessKeyId":"STS.BgKb478f4KEBf2a1j7pCZfYos","AccessKeySecret":"3r8qGeF9M7M8S7DfejGPoe3NBi2r6HPv82J7DUwZs5d1","SecurityToken":"CAESjgMIARKAAZwdoYpxhGp5NCZy+h1175eiSFqEB93IhhasyFDWawNH67L6lU7br3Lxz+hMnb4NXmGlVnTC76V3mWGjOXx4wEpE02yftk2Qxg3lueH204Wlosm4bI1+8XCAsETIDnKfhjJEkFTAD8FnKxzUYu/0Q+qOBBKcVxOCxCCYdu60gw04Gh1TVFMuQmdLYjQ3OGY0S0VCZjJhMWo3cENaZllvcyISMzM1NDU2MjYxNTAyOTE3NDE5KgNhcHAwr8Sl28QqOgZSc2FNRDVCWgoBMRpVCgVBbGxvdxIfCgxBY3Rpb25FcXVhbHMSBkFjdGlvbhoHCgVvc3M6KhIrCg5SZXNvdXJjZUVxdWFscxIIUmVzb3VyY2UaDwoNYWNzOm9zczoqOio6KkoQMTg5NzE0NjExMjIyMjk3N1IFMjY4NDJaD0Fzc3VtZWRSb2xlVXNlcmAAahIzMzU0NTYyNjE1MDI5MTc0MTlyG2FsaXl1bm9zc3Rva2VuZ2VuZXJhdG9ycm9sZXiBzuyAnK6vAw==","Expiration":"2016-04-25T03:14:38Z"}
     * message : ok
     */

    private int code;
    /**
     * AccessKeyId : STS.BgKb478f4KEBf2a1j7pCZfYos
     * AccessKeySecret : 3r8qGeF9M7M8S7DfejGPoe3NBi2r6HPv82J7DUwZs5d1
     * SecurityToken : CAESjgMIARKAAZwdoYpxhGp5NCZy+h1175eiSFqEB93IhhasyFDWawNH67L6lU7br3Lxz+hMnb4NXmGlVnTC76V3mWGjOXx4wEpE02yftk2Qxg3lueH204Wlosm4bI1+8XCAsETIDnKfhjJEkFTAD8FnKxzUYu/0Q+qOBBKcVxOCxCCYdu60gw04Gh1TVFMuQmdLYjQ3OGY0S0VCZjJhMWo3cENaZllvcyISMzM1NDU2MjYxNTAyOTE3NDE5KgNhcHAwr8Sl28QqOgZSc2FNRDVCWgoBMRpVCgVBbGxvdxIfCgxBY3Rpb25FcXVhbHMSBkFjdGlvbhoHCgVvc3M6KhIrCg5SZXNvdXJjZUVxdWFscxIIUmVzb3VyY2UaDwoNYWNzOm9zczoqOio6KkoQMTg5NzE0NjExMjIyMjk3N1IFMjY4NDJaD0Fzc3VtZWRSb2xlVXNlcmAAahIzMzU0NTYyNjE1MDI5MTc0MTlyG2FsaXl1bm9zc3Rva2VuZ2VuZXJhdG9ycm9sZXiBzuyAnK6vAw==
     * Expiration : 2016-04-25T03:14:38Z
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
        private String AccessKeyId;
        private String AccessKeySecret;
        private String SecurityToken;
        private String Expiration;

        public String getAccessKeyId() {
            return AccessKeyId;
        }

        public void setAccessKeyId(String AccessKeyId) {
            this.AccessKeyId = AccessKeyId;
        }

        public String getAccessKeySecret() {
            return AccessKeySecret;
        }

        public void setAccessKeySecret(String AccessKeySecret) {
            this.AccessKeySecret = AccessKeySecret;
        }

        public String getSecurityToken() {
            return SecurityToken;
        }

        public void setSecurityToken(String SecurityToken) {
            this.SecurityToken = SecurityToken;
        }

        public String getExpiration() {
            return Expiration;
        }

        public void setExpiration(String Expiration) {
            this.Expiration = Expiration;
        }
    }
}
