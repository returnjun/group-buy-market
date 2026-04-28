package top.daoha.trigger.http;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.springframework.web.bind.annotation.*;
import top.daoha.api.IDCCService;
import top.daoha.api.response.Response;
import top.daoha.types.enums.ResponseCode;

import javax.annotation.Resource;

@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/v1/gbm/dcc/")
public class DCCController implements IDCCService {
//http://localhost:8091/api/v1/gbm/dcc/update_config?key=downgradeSwitch&value=1
    /**
     * 可以动态配置的参数有downgradeSwitch选择活动是否打开或者关闭
     * cutRange是一个0-1的数选择百分之多少的用户可以看见这个活动
     * scBlackList可以选择黑名单用户
     */

    @Resource
    private RTopic dccTopic;

    @RequestMapping(value = "update_config",method = RequestMethod.GET)
    @Override
    public Response<Boolean> updateConfig(@RequestParam String key, @RequestParam String value) {
        try{
            dccTopic.publish(key + "," + value);

            return Response.<Boolean>builder()
                    .code(ResponseCode.SUCCESS.getCode())
                    .info(ResponseCode.SUCCESS.getInfo())
                    .build();
        }catch (Exception e){
            return Response.<Boolean>builder()
                    .code(ResponseCode.UN_ERROR.getCode())
                    .info(ResponseCode.UN_ERROR.getInfo())
                    .build();
        }
    }
}
