package top.daoha.trigger.http;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import top.daoha.api.dto.NotifyRequestDTO;

@Slf4j
@RestController()
@CrossOrigin("*")
@RequestMapping("/api/test/")
public class TestApiClientController {

    @RequestMapping(value = "group_buy_notify",method = RequestMethod.POST)
    public String groupBuyNotify(@RequestBody NotifyRequestDTO notifyRequestDTO){
        log.info("模拟测试第三方服务接收拼团回调 {}", JSON.toJSONString(notifyRequestDTO));
        return "success";
    }

}
