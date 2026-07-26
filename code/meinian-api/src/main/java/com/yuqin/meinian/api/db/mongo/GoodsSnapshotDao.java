package com.yuqin.meinian.api.db.mongo;

import com.yuqin.meinian.api.db.entity.CheckupItem;
import com.yuqin.meinian.api.db.entity.GoodsSnapshotEntity;
import jakarta.annotation.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    /**
     * 根据商品快照id获取商品的快照信息
     *
     * @param snapshotId 商品快照id(_id)
     * @return 商品快照的信息
     */
    public GoodsSnapshotEntity findById(String snapshotId) {
        return mongoTemplate.findById(snapshotId, GoodsSnapshotEntity.class);
    }

    /**
     * 查询适合指定性别的体检项目列表
     *
     * @param id  体检项目快照记录的ID
     * @param sex 体检人的性别（"男" 或 "女"）
     * @return 适合该性别的体检项目列表
     */
    public List<CheckupItem> searchCheckup(String id, String sex) {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("_id").is(id)),
                Aggregation.unwind("$examItems"),  // 或 $checkup
                Aggregation.match(Criteria.where("examItems.sex").in("无", sex)),
                // ★ 新增一行：把展开后的子文档提到根，直接返回项目本身
                Aggregation.replaceRoot("$examItems")
        );

        AggregationResults<CheckupItem> results = mongoTemplate.aggregate(
                aggregation, "goods_snapshot", CheckupItem.class
        );

        return results.getMappedResults(); // 直接就是 List<CheckupItem>
    }
}
