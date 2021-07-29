package com.github.shoothzj.config.client.impl.mysql.mapper;

import com.github.shoothzj.config.client.impl.mysql.domain.ConfigNotifyPo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author shoothzj
 */
public interface ConfigNotifyMapper {

    @Insert("Insert into config_notify (config_name, config_item_id, notify_time) values (#{configName}, #{configItemId}, #{notifyTime})")
    Integer saveConfig(ConfigNotifyPo configNotifyPo);

    /**
     * @return the largest dao
     */
    @Select("SELECT id from config_notify ORDER BY id DESC LIMIT 0,1;")
    Long findTopByOrderByIdDesc();

    @Select("SELECT * from config_notify WHERE id > #{maxIdScanned} ORDER BY id ASC LIMIT 0,500;")
    @Results(value = {@Result(property = "configName", column = "config_name"),
            @Result(property = "configItemId", column = "config_item_id"),
            @Result(property = "notifyTime", column = "notify_time")})
    List<ConfigNotifyPo> findNext500(@Param("maxIdScanned") long maxIdScanned);

    @Delete("DELETE FROM config_notify WHERE notify_time < #{agingTime}")
    void deleteBefore(@Param("agingTime") LocalDateTime agingTime);

}
