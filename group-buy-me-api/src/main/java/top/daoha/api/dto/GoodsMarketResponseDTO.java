package top.daoha.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsMarketResponseDTO {
    //活动id
    private Long activityId;
    // 商品信息
    private Goods goods;
    // 组队信息（1个个人的置顶、2个随机的「获取10个，随机取2个」）
    private List<Team> teamList;
    // 组队统计
    private TeamStatistic teamStatistic;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Goods{
        private String goodsName;
        //用户id
        private String goodsId;
        //原始价格
        private BigDecimal originalPrice;
        //折扣价格
        private BigDecimal deductionPrice;
        //最终价格
        private BigDecimal payPrice;

    }
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Team{
        /**  用户Id */
        private String userId;
        /** 拼团Id */
        private String teamId;
        /** 活动Id */
        private Long activityId;
        /** 目标个数 */
        private Integer targetCount;
        /** 完成个数 */
        private Integer completeCount;
        /** 锁单个数 */
        private Integer lockCount;
        /** 开始时间 */
        private Date validStartTime;
        /** 结束时间 */
        private Date validEndTime;
        /** 倒计时 */
        private String validTimeCountdown;
        /** 外部订单号 */
        private String outTradeNo;


        public static String differenceDateTime2Str(Date validStartTime, Date validEndTime) {
            if(validStartTime == null && validEndTime == null) {
                return "无效时间";
            }
            long diffInMilliseconds = validEndTime.getTime() - validStartTime.getTime();

            if(diffInMilliseconds <= 0) {
                return "活动结束";
            }
            long seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMilliseconds)%60;
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMilliseconds)%60;
            long hours = TimeUnit.MILLISECONDS.toHours(diffInMilliseconds)%24;
            long days = TimeUnit.MILLISECONDS.toDays(diffInMilliseconds);

            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
        }
    }




    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TeamStatistic{
        // 开团队伍数量
        private Integer allTeamCount;
        // 成团队伍数量
        private Integer allTeamCompleteCount;
        // 参团人数总量 - 一个商品的总参团人数
        private Integer allTeamUserCount;
    }
}
