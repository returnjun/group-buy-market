package top.daoha.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;
import top.daoha.domain.trade.model.entity.NotifyTaskEntity;
import top.daoha.infrastructure.dao.po.NotifyTask;

import java.util.List;

@Mapper
public interface INotifyTaskDao {

    void insert(NotifyTask notifyTask);

    List<NotifyTask> queryUnExecutedNotifyTaskList() ;

    NotifyTask queryUnExecutedNotifyTaskByTeamId(String teamId);

    int updateNotifyTaskStatusSuccess(NotifyTask notifyTask) ;

    int updateNotifyTaskStatusRetry(NotifyTask notifyTask) ;

    int updateNotifyTaskStatusError(NotifyTask notifyTask) ;
}
