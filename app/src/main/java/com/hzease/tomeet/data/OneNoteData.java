package com.hzease.tomeet.data;

/**
 * Created by xuq on 2017/9/19.
 */

public class OneNoteData {

    /**
     * success : true
     * msg :
     * data : {"avatarSignature":"1502434206820","senderId":10000000002,"createTime":1508144497000,"nickname":"xml","id":585,"state":0,"content":"cgg"}
     */

    private boolean success;
    private String msg;
    private DataBean data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * avatarSignature : 1502434206820
         * senderId : 10000000002
         * createTime : 1508144497000
         * nickname : xml
         * id : 585
         * state : 0
         * content : cgg
         */

        private String avatarSignature;
        private long senderId;
        private long createTime;
        private String nickname;
        private int id;
        private int state;
        private String content;

        public String getAvatarSignature() {
            return avatarSignature;
        }

        public void setAvatarSignature(String avatarSignature) {
            this.avatarSignature = avatarSignature;
        }

        public long getSenderId() {
            return senderId;
        }

        public void setSenderId(long senderId) {
            this.senderId = senderId;
        }

        public long getCreateTime() {
            return createTime;
        }

        public void setCreateTime(long createTime) {
            this.createTime = createTime;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
