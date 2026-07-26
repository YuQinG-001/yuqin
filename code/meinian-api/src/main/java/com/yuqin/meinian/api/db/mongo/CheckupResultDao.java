package com.yuqin.meinian.api.db.mongo;

import com.yuqin.meinian.api.db.entity.CheckupItem;
import com.yuqin.meinian.api.db.entity.CheckupResultEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CheckupResultDao {
    private final MongoTemplate mongoTemplate;
    public String selectIdByAppointmentNo(String appointmentNo) {
        Criteria criteria = Criteria.where("uuid").is(appointmentNo);
        Query query = new Query(criteria);
        CheckupResultEntity entity = mongoTemplate.findOne(query, CheckupResultEntity.class);
        return entity.get_id();
    }
    /**
     * 插入体检结果记录
     *
     * @param uuid    体检记录唯一标识符，用于关联体检单或体检人
     * @param checkup 体检项目列表，包含具体的检查项目和数据
     * @return boolean 插入是否成功，true-成功，false-失败
     */
    public boolean insert(String uuid, List<CheckupItem> checkup) {
        // 创建体检结果实体对象
        CheckupResultEntity entity = new CheckupResultEntity();

        // 设置实体属性
        entity.setUuid(uuid);        // 设置唯一标识
        entity.setCheckup(checkup);  // 设置体检项目数据

        // 初始化空的地点列表
        entity.setPlace(new ArrayList<>());

        // 初始化空的结果列表
        entity.setResult(new ArrayList<>());

        // 执行数据库插入操作
        // mongoTemplate.insert()方法会将实体插入到对应的MongoDB集合中
        // 插入成功后，MongoDB会自动生成_id字段并回填到实体对象中
        entity = mongoTemplate.insert(entity);

        // 判断插入是否成功：检查_id字段是否不为null
        // 如果_id不为null，说明MongoDB成功生成了主键，插入操作成功
        return entity.get_id() != null;
    }
}