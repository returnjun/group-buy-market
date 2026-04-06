package top.daoha.infrastructure.dcc;

import org.springframework.stereotype.Service;
import top.daoha.types.annotations.DCCValue;
import top.daoha.types.common.Constants;

import java.util.Arrays;
import java.util.List;

@Service
public class DCCService {

    @DCCValue("downgradeSwitch:0")
    private String downgradeSwitch;

    @DCCValue("cutRange:100")
    private String cutRange;

    @DCCValue("scBlackList:s02c02")
    private String scBlackList;

    public boolean isDowngradeSwitch() {
        return "1".equals(downgradeSwitch);
    }

    public boolean isCutRange(String userId) {
        //计算哈希码的绝对值
        int hashCode = Math.abs(userId.hashCode());

        //获取最后两位
        int lastTwoDigits = hashCode % 100;

        //判断是否在指定范围内
        if (lastTwoDigits <= Integer.parseInt(cutRange)) {
            return true;
        }
        return false;
    }

    public boolean isScBlackList(String source, String channel) {
        List<String> list = Arrays.asList(scBlackList.split(Constants.SPLIT));
        return list.contains(source + channel);
    }
}
