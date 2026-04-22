package top.daoha.trigger.http;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.daoha.api.IMarketTradeService;
import top.daoha.api.dto.LockMarketPayOrderRequestDTO;
import top.daoha.api.dto.LockMarketPayOrderResponseDTO;
import top.daoha.api.dto.SettlementMarketPayOrderRequestDTO;
import top.daoha.api.dto.SettlementMarketPayOrderResponseDTO;
import top.daoha.api.response.Response;
import top.daoha.domain.activity.model.entity.MarketProductEntity;
import top.daoha.domain.activity.model.entity.TrialBalanceEntity;
import top.daoha.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import top.daoha.domain.activity.service.IIndexGroupBuyMarketService;
import top.daoha.domain.trade.model.entity.*;
import top.daoha.domain.trade.model.valobj.GroupBuyProgressVO;
import top.daoha.domain.trade.service.ITradeLockOrderService;
import top.daoha.domain.trade.service.settlement.TreadeSettlementOrderService;
import top.daoha.types.enums.ResponseCode;
import top.daoha.types.exception.AppException;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/gbm/trade/")
public class MarketTradeController implements IMarketTradeService {

    @Resource
    private IIndexGroupBuyMarketService indexGroupBuyMarketService;

    @Resource
    private ITradeLockOrderService tradeOrderService;

    @Resource
    private TreadeSettlementOrderService treadeSettlementOrderService;


    @RequestMapping(value = "settlement_market_pay_order", method = RequestMethod.POST)
    @Override
    public Response<SettlementMarketPayOrderResponseDTO> settlementMarketPayOrder(@RequestBody SettlementMarketPayOrderRequestDTO requestDTO) {
        try{
            String userId = requestDTO.getUserId();
            String source = requestDTO.getSource();
            String channel = requestDTO.getChannel();
            String outTradeNo = requestDTO.getOutTradeNo();
            Date outTradeTime = requestDTO.getOutTradeTime();

            log.info("营销交易组队结算开始:{} outTradeNo:{}",requestDTO.getUserId(),requestDTO.getOutTradeNo());
            if (StringUtils.isBlank(userId) || StringUtils.isBlank(source) ||
                    StringUtils.isBlank(channel)||StringUtils.isBlank(outTradeNo)) {
                return Response.<SettlementMarketPayOrderResponseDTO>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }

                TradePaySettlementEntity tradePaySettlementEntity = treadeSettlementOrderService.settlementOrder(
                        TradePaySuccessEntity.builder()
                                .source(requestDTO.getSource())
                                .channel(requestDTO.getChannel())
                                .userId(requestDTO.getUserId())
                                .outTradeNo(requestDTO.getOutTradeNo())
                                .outTradeTime(requestDTO.getOutTradeTime())
                                .build());

                SettlementMarketPayOrderResponseDTO responseDTO = SettlementMarketPayOrderResponseDTO.builder()
                        .userId(tradePaySettlementEntity.getUserId())
                        .teamId(tradePaySettlementEntity.getTeamId())
                        .activityId(tradePaySettlementEntity.getActivityId())
                        .outTradeNo(tradePaySettlementEntity.getOutTradeNo())
                        .build();

                // 返回结果
                Response<SettlementMarketPayOrderResponseDTO> response = Response.<SettlementMarketPayOrderResponseDTO>builder()
                        .code(ResponseCode.SUCCESS.getCode())
                        .info(ResponseCode.SUCCESS.getInfo())
                        .data(responseDTO)
                        .build();
                log.info("营销交易组队结算完成：{} outTradeNo:{} response:{}",responseDTO.getUserId(),responseDTO.getOutTradeNo(),JSON.toJSONString(response));
                return response;

            } catch (AppException e) {
            log.info("营销交易组队结算异常：{},LockMarketPayOrderRequestDTO:{}",requestDTO.getUserId(),JSON.toJSONString(requestDTO));
            return Response.<SettlementMarketPayOrderResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
            }catch (Exception e) {
            log.info("营销交易组队结算异常：{},LockMarketPayOrderRequestDTO:{}",requestDTO.getUserId(),JSON.toJSONString(requestDTO));
            return Response.<SettlementMarketPayOrderResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }


    @RequestMapping(value = "lock_market_pay_order",method = RequestMethod.POST)
    @Override
    public Response<LockMarketPayOrderResponseDTO> lockMarketPayOrder(@RequestBody LockMarketPayOrderRequestDTO requestDTO) {
        try {
            String userId = requestDTO.getUserId();
            String goodsId = requestDTO.getGoodsId();
            Long activityId = requestDTO.getActivityId();
            String source = requestDTO.getSource();
            String channel = requestDTO.getChannel();
            String outTradeNo = requestDTO.getOutTradeNo();
            String teamId = requestDTO.getTeamId();
            String notifyUrl = requestDTO.getNotifyUrl();

            log.info("营销交易锁单:{} LockMarketPayOrderRequestDTO:{}", userId, JSON.toJSONString(requestDTO));

            if (StringUtils.isBlank(userId) || StringUtils.isBlank(goodsId) ||
                    activityId == null || StringUtils.isBlank(source) ||
                    StringUtils.isBlank(channel)||StringUtils.isBlank(notifyUrl)) {

                return Response.<LockMarketPayOrderResponseDTO>builder()
                        .code(ResponseCode.ILLEGAL_PARAMETER.getCode())
                        .info(ResponseCode.ILLEGAL_PARAMETER.getInfo())
                        .build();
            }

            //使用userId和outTradeNo在group buy order list表中查询预购订单是否存在
            MarketPayOrderEntity marketPayOrderEntity = tradeOrderService.queryNoPayMarketPayOrderByOutTradeNo(userId, outTradeNo);

            //如果存在那么锁定预购订单
            if (null != marketPayOrderEntity) {
                LockMarketPayOrderResponseDTO lockMarketPayOrderResponseDTO = new LockMarketPayOrderResponseDTO();
                lockMarketPayOrderResponseDTO.setOrderId(marketPayOrderEntity.getOrderId());
                lockMarketPayOrderResponseDTO.setDeductionPrice(marketPayOrderEntity.getDeductionPrice());
                lockMarketPayOrderResponseDTO.setStatus(marketPayOrderEntity.getStatus().getCode());
                lockMarketPayOrderResponseDTO.setOriginalPrice(marketPayOrderEntity.getOriginalPrice());
                lockMarketPayOrderResponseDTO.setPayPrice(marketPayOrderEntity.getPayPrice());
                log.info("交易锁单记录已经存在:{} 详细信息如下:{}", userId, JSON.toJSONString(marketPayOrderEntity));

                return Response.<LockMarketPayOrderResponseDTO>builder()
                        .code(ResponseCode.SUCCESS.getCode())
                        .info(ResponseCode.SUCCESS.getInfo())
                        .data(lockMarketPayOrderResponseDTO)
                        .build();
            }
            if (null != teamId) {//拼团单号存在
                //查询一下拼单目标是否达成
                GroupBuyProgressVO groupBuyProgressVO = tradeOrderService.queryGroupBuyProgress(teamId);
                //这个对象不为空并且目标数量等于锁单数量
                if (null != groupBuyProgressVO && Objects.equals(groupBuyProgressVO.getTargetCount(), groupBuyProgressVO.getLockCount())) {
                    log.info("交易锁单拦截，拼团已经完成:{} :{}", userId, teamId);
                    return Response.<LockMarketPayOrderResponseDTO>builder()
                            .code(ResponseCode.E0006.getCode())
                            .info(ResponseCode.E0006.getInfo())
                            .build();
                }
            }
            //开始营销优惠试算
            TrialBalanceEntity trialBalanceEntity = indexGroupBuyMarketService.indexMarketTrial(MarketProductEntity.builder()
                    .userId(userId)
                    .source(source)
                    .channel(channel)
                    .goodsId(goodsId)
                    .activityId(activityId)
                    .build());
            log.info("优惠试算结束");
            //人群限定
            if(!trialBalanceEntity.getIsVisible()||!trialBalanceEntity.getIsEnable()){
                return  Response.<LockMarketPayOrderResponseDTO>builder()
                        .code(ResponseCode.E0007.getCode())
                        .info(ResponseCode.E0007.getInfo())
                        .build();
            }

            //获取计算完后的优惠信息
            GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = trialBalanceEntity.getGroupBuyActivityDiscountVO();

            //第一个就添加，已存在就+1
            marketPayOrderEntity = tradeOrderService.lockMarketPayOrder(
                    UserEntity.builder().userId(userId).build(),
                    PayActivityEntity.builder()
                            .teamId(teamId)
                            .activityId(activityId)
                            .activityName(groupBuyActivityDiscountVO.getActivityName())
                            .targetCount(groupBuyActivityDiscountVO.getTarget())
                            .validTime(groupBuyActivityDiscountVO.getValidTime())
                            .startTime(groupBuyActivityDiscountVO.getStartTime())
                            .endTime(groupBuyActivityDiscountVO.getEndTime())
                            .build(),
                    PayDiscountEntity.builder()
                            .source(source)
                            .channel(channel)
                            .goodsId(goodsId)
                            .goodsName(trialBalanceEntity.getGoodsName())
                            .originalPrice(trialBalanceEntity.getOriginalPrice())
                            .deductionPrice(trialBalanceEntity.getDeductionPrice())
                            .payPrice(trialBalanceEntity.getPayPrice())
                            .outTradeNo(outTradeNo)
                            .notifyUrl(notifyUrl)
                            .build());
            log.info("交易锁单记录(新):{} marketPayOrderEntity:{}", userId, JSON.toJSONString(marketPayOrderEntity));

            return Response.<LockMarketPayOrderResponseDTO>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .data(LockMarketPayOrderResponseDTO.builder()
                            .orderId(marketPayOrderEntity.getOrderId())
                            .deductionPrice(marketPayOrderEntity.getDeductionPrice())
                            .status(marketPayOrderEntity.getStatus().getCode())
                            .originalPrice(marketPayOrderEntity.getOriginalPrice())
                            .payPrice(marketPayOrderEntity.getPayPrice())
                            .build())
                    .build();

        } catch (AppException e) {
            log.error("营销交易锁单业务异常:{} LockMarketPayOrderRequestDTO:{}", requestDTO.getUserId(), JSON.toJSONString(requestDTO), e);

            return Response.<LockMarketPayOrderResponseDTO>builder()
                    .code(e.getCode())
                    .info(e.getInfo())
                    .build();
        } catch (Exception e) {
            return Response.<LockMarketPayOrderResponseDTO>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }


}
