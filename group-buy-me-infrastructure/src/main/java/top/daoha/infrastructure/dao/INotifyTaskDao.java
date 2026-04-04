package top.daoha.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;
import top.daoha.infrastructure.dao.po.NotifyTask;

@Mapper
public interface INotifyTaskDao {

    void insert(NotifyTask notifyTask);

}
