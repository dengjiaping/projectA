package pro.yueyuan.project_t.data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xuq on 2017/4/6.
 */

public class UserOrderBean implements Serializable {
    /**
     * success : true
     * msg :
     * data : {"gender":true,"nickname":"test2","orders":[{"userId":10000000007,"nickname":"test2","point":24287,"ranking":3,"gameId":13,"gameName":"足球","count":1},{"userId":10000000007,"nickname":"test2","point":94556,"ranking":1,"gameId":10,"gameName":"象棋","count":1},{"userId":10000000007,"nickname":"test2","point":382590,"ranking":1,"gameId":28,"gameName":"短途旅行","count":6},{"userId":10000000007,"nickname":"test2","point":74086,"ranking":5,"gameId":6,"gameName":"狼人杀","count":1}],"id":10000000007,"labels":[{"id":3,"name":"90后"}]}
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
         * gender : true
         * nickname : test2
         * orders : [{"userId":10000000007,"nickname":"test2","point":24287,"ranking":3,"gameId":13,"gameName":"足球","count":1},{"userId":10000000007,"nickname":"test2","point":94556,"ranking":1,"gameId":10,"gameName":"象棋","count":1},{"userId":10000000007,"nickname":"test2","point":382590,"ranking":1,"gameId":28,"gameName":"短途旅行","count":6},{"userId":10000000007,"nickname":"test2","point":74086,"ranking":5,"gameId":6,"gameName":"狼人杀","count":1}]
         * id : 10000000007
         * labels : [{"id":3,"name":"90后"}]
         */

        private boolean gender;
        private String nickname;
        private long id;
        private List<OrdersBean> orders;
        private List<LabelsBean> labels;

        public boolean isGender() {
            return gender;
        }

        public void setGender(boolean gender) {
            this.gender = gender;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public List<OrdersBean> getOrders() {
            return orders;
        }

        public void setOrders(List<OrdersBean> orders) {
            this.orders = orders;
        }

        public List<LabelsBean> getLabels() {
            return labels;
        }

        public void setLabels(List<LabelsBean> labels) {
            this.labels = labels;
        }

        public static class OrdersBean {
            /**
             * userId : 10000000007
             * nickname : test2
             * point : 24287
             * ranking : 3
             * gameId : 13
             * gameName : 足球
             * count : 1
             */

            private long userId;
            private String nickname;
            private int point;
            private int ranking;
            private int gameId;
            private String gameName;
            private int count;

            public long getUserId() {
                return userId;
            }

            public void setUserId(long userId) {
                this.userId = userId;
            }

            public String getNickname() {
                return nickname;
            }

            public void setNickname(String nickname) {
                this.nickname = nickname;
            }

            public int getPoint() {
                return point;
            }

            public void setPoint(int point) {
                this.point = point;
            }

            public int getRanking() {
                return ranking;
            }

            public void setRanking(int ranking) {
                this.ranking = ranking;
            }

            public int getGameId() {
                return gameId;
            }

            public void setGameId(int gameId) {
                this.gameId = gameId;
            }

            public String getGameName() {
                return gameName;
            }

            public void setGameName(String gameName) {
                this.gameName = gameName;
            }

            public int getCount() {
                return count;
            }

            public void setCount(int count) {
                this.count = count;
            }
        }

        public static class LabelsBean {
            /**
             * id : 3
             * name : 90后
             */

            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
