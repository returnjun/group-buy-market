package top.daoha.api;

import top.daoha.api.response.Response;

public interface IDCCService {

    Response<Boolean> updateConfig(String key, String value);
}
