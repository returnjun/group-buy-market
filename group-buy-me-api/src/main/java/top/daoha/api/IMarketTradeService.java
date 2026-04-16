package top.daoha.api;

import top.daoha.api.dto.LockMarketPayOrderRequestDTO;
import top.daoha.api.dto.LockMarketPayOrderResponseDTO;
import top.daoha.api.dto.SettlementMarketPayOrderRequestDTO;
import top.daoha.api.dto.SettlementMarketPayOrderResponseDTO;
import top.daoha.api.response.Response;

public interface IMarketTradeService {

    Response<LockMarketPayOrderResponseDTO> lockMarketPayOrder(LockMarketPayOrderRequestDTO requestDTO);

    Response<SettlementMarketPayOrderResponseDTO> settlementMarketPayOrder(SettlementMarketPayOrderRequestDTO requestDTO);

}
