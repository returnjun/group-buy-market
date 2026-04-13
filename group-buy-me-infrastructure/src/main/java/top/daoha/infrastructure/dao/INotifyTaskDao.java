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

    int updateNotifyTaskStatusSuccess(String teamId) ;

    int updateNotifyTaskStatusRetry(String teamId) ;

    int updateNotifyTaskStatusError(String teamId) ;
}
