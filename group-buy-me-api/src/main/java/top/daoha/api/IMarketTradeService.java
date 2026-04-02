package top.daoha.api;

import top.daoha.api.dto.LockMarketPayOrderRequestDTO;
import top.daoha.api.dto.LockMarketPayOrderResponseDTO;
import top.daoha.api.response.Response;

public interface IMarketTradeService {

    Response<LockMarketPayOrderResponseDTO> lockMarketPayOrder(LockMarketPayOrderRequestDTO requestDTO);
}
