package top.daoha.trigger.http;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.daoha.api.IMarketIndexService;
import top.daoha.api.dto.GoodsMarketRequestDTO;
import top.daoha.api.dto.GoodsMarketResponseDTO;
import top.daoha.api.response.Response;
import top.daoha.domain.activity.model.entity.MarketProductEntity;
import top.daoha.domain.activity.model.entity.TrialBalanceEntity;
import top.daoha.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import top.daoha.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import top.daoha.domain.activity.model.valobj.TeamStatisticVO;
import top.daoha.domain.activity.service.IIndexGroupBuyMarketService;
import top.daoha.types.enums.ResponseCode;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/gbm/index/")
public class MarketIndexController implements IMarketIndexService {

    @Resource
    private IIndexGroupBuyMarketService iIndexGroupBuyMarketService;

    @RequestMapping(value = "query_group_buy_market_config",method = RequestMethod.POST)
    @Override
    public Response<GoodsMarketResponseDTO> queryGroupBuyMarketConfig(@RequestBody GoodsMarketRequestDTO requestDTO) {
        try {
            log.info("查询拼团营销配置开始:{} goodsId:{}", requestDTO.getGoodsId(), requestDTO.getGoodsId());
            if(Objects.isNull(requestDTO.getGoodsId()) || Objects.isNull(requestDTO.getGoodsId())) {
                return Response.<GoodsMarketResponseDTO>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }

            //1.营销优惠试算
            TrialBalanceEntity trialBalanceEntity = iIndexGroupBuyMarketService.indexMarketTrial(MarketProductEntity.builder()
                            .userId(requestDTO.getUserId())
                            .goodsId(requestDTO.getGoodsId())
                            .source(requestDTO.getSource())
                            .channel(requestDTO.getChannel())
                            .build());
            //这里面有优惠后的信息
            GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = trialBalanceEntity.getGroupBuyActivityDiscountVO();
            Long activityId = groupBuyActivityDiscountVO.getActivityId();

            //这个数据展示现有的未拼团成功的团队
            List<UserGroupBuyOrderDetailEntity> userGroupBuyOrderDetailEntity=iIndexGroupBuyMarketService.queryInProgressUserGroupBuyOrderDetailList(activityId,requestDTO.getUserId(),1,2);


            TeamStatisticVO teamStatisticVO =iIndexGroupBuyMarketService.queryTeamStatisticByActivity(activityId);

            GoodsMarketResponseDTO.Goods goods = GoodsMarketResponseDTO.Goods.builder()
                    .goodsName(trialBalanceEntity.getGoodsName())
                    .goodsId(trialBalanceEntity.getGoodsId())
                    .originalPrice(trialBalanceEntity.getOriginalPrice())
                    .deductionPrice(trialBalanceEntity.getDeductionPrice())
                    .payPrice(trialBalanceEntity.getPayPrice())
                    .build();

            List<GoodsMarketResponseDTO.Team> teams = new ArrayList<>();
            if(null != userGroupBuyOrderDetailEntity && !userGroupBuyOrderDetailEntity.isEmpty()) {
                for (UserGroupBuyOrderDetailEntity DetailEntity : userGroupBuyOrderDetailEntity) {
                    GoodsMarketResponseDTO.Team build = GoodsMarketResponseDTO.Team.builder()
                            .userId(DetailEntity.getUserId())
                            .teamId(DetailEntity.getTeamId())
                            .activityId(DetailEntity.getActivityId())
                            .targetCount(DetailEntity.getTargetCount())
                            .completeCount(DetailEntity.getCompleteCount())
                            .lockCount(DetailEntity.getLockCount())
                            .validStartTime(DetailEntity.getValidStartTime())
                            .validEndTime(DetailEntity.getValidEndTime())
                            .validTimeCountdown(GoodsMarketResponseDTO.Team.differenceDateTime2Str(new Date(),DetailEntity.getValidEndTime()))
                            .outTradeNo(DetailEntity.getOutTradeNo())
                                .build();
                    teams.add(build);
                }
            }

            GoodsMarketResponseDTO.TeamStatistic teamStatistic = GoodsMarketResponseDTO.TeamStatistic.builder()
                    .allTeamCount(teamStatisticVO.getAllTeamCount())
                    .allTeamUserCount(teamStatisticVO.getAllTeamUserCount())
                    .allTeamCompleteCount(teamStatisticVO.getAllTeamCompleteCount())
                    .build();

            Response<GoodsMarketResponseDTO> response = Response.<GoodsMarketResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(GoodsMarketResponseDTO.builder()
                            .activityId(activityId)
                            .goods(goods)
                            .teamList(teams)
                            .teamStatistic(teamStatistic)
                            .build())
                    .build();
            log.info("查询拼团营销配置完成：{} goodsId:{} reponse:{}", requestDTO.getGoodsId(), requestDTO.getGoodsId(), JSON.toJSONString(response));
            return response;

        }catch (Exception e){
            log.info("查询拼团营销配置失败：{} goodsId:{} ", requestDTO.getGoodsId(), requestDTO.getGoodsId());
            return Response.<GoodsMarketResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }

    }
}
