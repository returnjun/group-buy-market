package top.daoha.api;

import top.daoha.api.dto.GoodsMarketRequestDTO;
import top.daoha.api.dto.GoodsMarketResponseDTO;
import top.daoha.api.response.Response;

public interface IMarketIndexService {

    Response<GoodsMarketResponseDTO> queryGroupBuyMarketConfig(GoodsMarketRequestDTO requestDTO);
}
