package top.daoha.api;

import top.daoha.api.dto.*;
import top.daoha.api.response.Response;

public interface IMarketTradeService {

    Response<LockMarketPayOrderResponseDTO> lockMarketPayOrder(LockMarketPayOrderRequestDTO requestDTO);

    Response<SettlementMarketPayOrderResponseDTO> settlementMarketPayOrder(SettlementMarketPayOrderRequestDTO requestDTO);

    Response<RefundMarketPayOrderResponseDTO> refundMarketPayOrder(RefundMarketPayOrderRequestDTO requestDTO);
}
