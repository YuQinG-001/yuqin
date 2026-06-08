package com.yuqin.meinian.api.db.mongo;

import com.yuqin.meinian.api.db.entity.GoodsSnapshotEntity;
import jakarta.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

@Repository
public class GoodsSnapshotDao {
    @Resource
    private MongoTemplate mongoTemplate;

    // 根据md5Hash判断是否存在商品快照，存在就返回快照 id，不存在则返回null
    public String hasGoodsSnapshot(String md5Hash) {
        // 构建查询条件：根据md5Hash字段精确匹配
        Criteria criteria = Criteria.where("md5Hash").is(md5Hash);
        Query query = new Query(criteria);

        // 设置分页参数：只查询第一条匹配的记录，提高查询效率
        query.skip(0);  // 从第0条开始
        query.limit(1); // 只取1条记录（拿到1个就不再继续扫描了，效率高。）

        // 执行查询，返回单个实体对象
        GoodsSnapshotEntity entity = mongoTemplate.findOne(query, GoodsSnapshotEntity.class);

        // 如果找到记录则返回其主键_id，否则返回null
        return entity != null ? entity.get_id() : null;
    }

    // 保存或更新快照信息。
    public String insert(GoodsSnapshotEntity entity) {
        // 使用save方法：当entity的_id为null时执行插入，否则执行更新
        // 返回保存后的实体，并获取其主键_id
        return mongoTemplate.save(entity).get_id();
    }
}
