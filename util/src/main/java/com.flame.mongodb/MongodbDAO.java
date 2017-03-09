
package com.flame.mongodb;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ComparatorUtils;
import org.apache.commons.collections.comparators.ComparableComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

/**
 * Mongodb通用数据操作.
 * @author Jon <br>
 * @version 1.0.0 2017-3-8<br>
 * @since JDK 1.8.0
 */
@Repository(value="MongodbDAO")
public final class MongodbDAO {

    /**
     * Mongodb数据操作工具类..
     */

    MongoOperations operations;


    public <T> T getPO(Class<T> cls, Serializable id) {
        return this.operations.findById(id, cls);
    }



    public <T> List<T> getPOList(Class<T> cls) {
        return this.operations.findAll(cls);
    }


    public <T> T getPO(Class<T> cls, Criteria... queryParam) {
        if(queryParam==null|| queryParam.length==0)
            return null;
        final Query query = new Query();
        for (Criteria criteria : queryParam) {
            if (criteria != null) {
                query.addCriteria(criteria);
            }
        }
        return this.operations.findOne(query, cls);
    }



    public <T> List<T> getPOListByIds(Class<T> cls, String[] ids) {
        if(ids==null || ids.length==0)
            return new ArrayList<T>();
        final Collection<String> idList = new ArrayList<String>();
        for (String id : ids) {
            idList.add(id);
        }
        return this.operations.find(Query.query(
                Criteria.where(ClassAnnotationUtils.getPrimaryKeyField(cls).getName()).in(idList)), cls);
    }



    public <T> List<T> getPOList(Class<T> cls, Criteria... queryParam) {
        return this.getPOList(cls, null, null, queryParam);
    }



    public <T> List<T> getPOList(Class<T> cls, Paginate paginate, List<Order> orders, Criteria... queryParam) {
        /**
         * 如果没有任何条件，返回空，如果不作处理，mongdb会查出所有的
         */
        if(queryParam==null|| queryParam.length==0)
            return new  ArrayList<T>();
        final Query query = new Query();

        if (paginate != null) {
            query.skip(paginate.getPageIndex() * paginate.getPageSize());
            query.limit(paginate.getPageSize());
        }

        if (orders != null && orders.size() > 0) {
            query.with(new Sort(orders));
        }
        for (Criteria criteria : queryParam) {
            if (criteria != null) {
                query.addCriteria(criteria);
            }
        }
        return this.operations.find(query, cls);
    }



    public <T> Page<T> getPOPageList(Class<T> cls, Paginate paginate,
                                     List<Order> orders, Criteria... queryParam) {

//		if(queryParam==null || queryParam.length==0)
//			return null;
        final Query query = new Query();

        if (paginate != null) {
            query.skip(paginate.getPageIndex() * paginate.getPageSize());
            query.limit(paginate.getPageSize());
        }

        if (orders != null && orders.size() > 0) {
            query.with(new Sort(orders));
        }
        if(queryParam != null) {//������
            for (Criteria criteria : queryParam) {
                if (criteria != null) {
                    query.addCriteria(criteria);
                }
            }
        }
        long totalCount = this.getRowCount(cls, queryParam);
        Page<T> pageResult = new PageImpl<T>(this.operations.find(query, cls),null,totalCount);
        return pageResult;
    }



    public <T> long getRowCount(Class<T> cls, Criteria... queryParam) {
        final Query query = createQuery(queryParam);
        return this.operations.count(query, cls);
    }



    public <T> void saveOrUpdatePOList(List<T> boList) {
        for (T po : boList) {
            this.saveOrUpdatePO(po);
        }
    }



    public <T> void saveOrUpdatePO(T po) {
        this.operations.save(po);
    }



    public <T> void saveOrUpdateDocument(Class<T> cls, String propertyName, Object propertyValue,
                                         Criteria... queryParam) {
        final Map<String, Object> updatePropertys = new HashMap<String, Object>();
        updatePropertys.put(propertyName, propertyValue);
        this.saveOrUpdateDocument(cls, updatePropertys, queryParam);
    }



    public <T> void saveOrUpdateDocument(Class<T> cls, Map<String, Object> updatePropertys, Criteria... queryParam) {
        final Query query = createQuery(queryParam);

        Update update = new Update();
        for (Entry<String, Object> updateProperty : updatePropertys.entrySet()) {
            update.set(updateProperty.getKey(), updateProperty.getValue());
        }

        this.operations.updateMulti(query, update, cls);
    }


    public <T> void UpdateUnsetDocument(Class<T> cls, List<String> unsetPropertys, Criteria... queryParam) {
        final Query query = createQuery(queryParam);

        Update update = new Update();
        for (String updateProperty : unsetPropertys) {
            update.unset(updateProperty);
        }
        this.operations.updateMulti(query, update, cls);
    }



    public <T> void deleteDocument(Class<T> cls, String propertyName, Criteria... queryParam) {
        final String[] propertyNames = new String[] { propertyName };
        this.deleteDocument(cls, propertyNames, queryParam);
    }



    public <T> void deleteDocument(Class<T> cls, String[] propertyNames, Criteria... queryParam) {
        final Query query = createQuery(queryParam);

        Update update = new Update();
        for (String propertyName : propertyNames) {
            update.set(propertyName, null);
        }

        this.operations.updateMulti(query, update, cls);

    }



    public <T> void saveArray(Class<T> cls, String propertyName, Object value, Criteria... queryParam) {
        final Query query = createQuery(queryParam);
        Update update = new Update();
        update.push(propertyName, value);
        this.operations.upsert(query, update, cls);

    }



    public <T> void deleteArray(Class<T> cls, String propertyName, Object value, Criteria... queryParam) {

        final Query query = createQuery(queryParam);
        Update update = new Update();
        update.pull(propertyName, value);
        this.operations.updateMulti(query, update, cls);
    }



    public <T> void deleteArray(Class<T> cls, String propertyName, Object[] values, Criteria... queryParam) {
        final Query query = createQuery(queryParam);
        Update update = new Update();
        update.pullAll(propertyName, values);
        this.operations.upsert(query, update, cls);
    }



    public <T> void saveOrUpdateObjectArray(Class<T> cls, String propertyName, Object value, Criteria... queryParam) {
        final Query query = createQuery(queryParam);
        Update update = new Update();
        update.set(propertyName, value);
        this.operations.upsert(query, update, cls);

    }



    public <T> void deleteObjectArray(Class<T> cls, String propertyName, Criteria... queryParam) {
        final Query query = createQuery(queryParam);
        Update update = new Update();
        update.unset(propertyName);
        this.operations.upsert(query, update, cls);

    }



    public <T> void updateInc(Class<T> cls, String propertyName,int inc, Criteria... queryParam) {
        final Query query = createQuery(queryParam);
        Update update = new Update();
        update.inc(propertyName, inc);
        this.operations.updateMulti(query, update, cls);
    }

    /**
     * ���ݶ�̬��ѯ��������Query. <br>
     * @param queryParam ��̬��ѯ����
     * @return Query.
     */
    private Query createQuery(Criteria... queryParam) {
        final Query query = new Query();
        if(queryParam != null) {
            for (Criteria criteria : queryParam) {
                if (criteria != null) {
                    // qiushaohua@2014-03-06 �����ж�
                    query.addCriteria(criteria);
                }
            }
        }
        return query;
    }



    public <T> void deletePO(T po) {
        this.operations.remove(po);
    }



    public <T> void deletePO(Class<T> cls, Serializable id) {
        this.operations.remove(new Query(Criteria.where(ClassAnnotationUtils.getPrimaryKeyField(cls).getName()).is(id)), cls);
    }



    public <T> void deletePOList(Class<T> cls, List<T> poList) {
        for (T po : poList) {
            this.deletePO(po);
        }
    }


    public <T> void deletePOList(Class<T> cls, Criteria... queryParam) {
        final Query query = createQuery(queryParam);
        this.operations.remove(query, cls);
    }


    public <T> List<T> getPoDistinct(String collectionName,String key,final Criteria... queryParam) {
        /**
         * ���û���κ����������ؿգ������������mongdb�������е�
         */
        if(queryParam==null|| queryParam.length==0)
            return new  ArrayList<T>();
        Query query = createQuery(queryParam);
        return  this.operations.getCollection(collectionName).distinct(key, query.getQueryObject());
    }


    public <T> List<T> getPODistinctList(String collectionName,String key, Paginate paginate, List<Order> orders, final Criteria... queryParam){
        /**
         * ���û���κ����������ؿգ������������mongdb�������е�
         */
        if(queryParam==null|| queryParam.length==0)
            return new  ArrayList<T>();
        final Query query = new Query();

        if (paginate != null) {
            query.skip(paginate.getPageIndex() * paginate.getPageSize());
            query.limit(paginate.getPageSize());
        }

        if (orders != null && orders.size() > 0) {
            query.with(new Sort(orders));
        }
        for (Criteria criteria : queryParam) {
            if (criteria != null) {
                query.addCriteria(criteria);
            }
        }

        return  this.operations.getCollection(collectionName).distinct(key, query.getQueryObject());
    }



    public <T> List<T> unionList(final List<Order> orderList, final int count, final List<T>... list) {
        final List<T> sortList = new ArrayList<T>();
        for (List<T> listTemp : list) {
            sortList.addAll(listTemp);
        }

        // �ںϲ��ļ��ϵ���������1��ʱ������
        if (sortList.size() > 1) {
            // ����Ҫ����Ķ�������ԣ���ָ����ʹ�õ�������������ָ��������Ĭ������
            final ArrayList<Object> sortFields = new ArrayList<Object>();
            if(CollectionUtils.isNotEmpty(orderList)){
                for (Order order : orderList) {
                    // ����һ���������
                    Comparator comparator = ComparableComparator.getInstance();
                    comparator = ComparatorUtils.nullLowComparator(comparator);
                    if (!order.isAscending()) {
                        comparator = ComparatorUtils.reversedComparator(comparator);
                    }
                    sortFields.add(new BeanComparator(order.getProperty(), comparator));
                }

                // ����һ��������
                final ComparatorChain multiSort = new ComparatorChain(sortFields);
                // ��ʼ����������
                Collections.sort(sortList, multiSort);
            }

            if (sortList.size() > count && count>0) {
                return sortList.subList(0, count);
            } else {
                return sortList;
            }
        } else {
            return sortList;
        }
    }


    public <T> List<BasicDBObject> groupBy(Class<T> cls,String propertyName, Criteria... queryParam) {
        Criteria criteria =null;
        if(queryParam != null){
            criteria = new Criteria();
            criteria.andOperator(queryParam);
        }
        GroupBy groupBy = GroupBy.key(propertyName).initialDocument("{count:0}").reduceFunction("function(doc, prev){prev.count+=1}");
        GroupByResults resluts = operations.group(criteria,operations.getCollectionName(cls), groupBy,cls);
        if(resluts != null){
            List<BasicDBObject> list = (List<BasicDBObject>) resluts.getRawResults().get("retval");
            return list;
        }
        return null;
    }


    public List<Order> orderDescBy(String... properNames) {
        if(properNames !=null && properNames.length > 0){
            int len = properNames.length;
            List<Order> orders = new ArrayList<Order>();
            for(int i=0;i<len;i++ ){
                Order order = new Order(Direction.DESC,properNames[i]);
                orders.add(order);
            }
            return orders;
        }
        return null;
    }


    public <T> Long sum(Class<T> cls, String propertyName,Criteria... queryParam) {
        Long total = 0L;
        Criteria criteria =null;
        if(queryParam != null){
            criteria = new Criteria();
            criteria.andOperator(queryParam);
        }
        Query query = Query.query(criteria);
        String reduce = "function (doc,aggr) { aggr.total += doc."+propertyName+"}";
        DBObject results = operations.getCollection(
                operations.getCollectionName(cls)).group(new BasicDBObject(propertyName, 1),
                query.getQueryObject(), new BasicDBObject("total", total), reduce);
        return Long.parseLong((String) results.get("total"));
    }


    public <T> List<BasicDBObject> sumArray(Class<T> cls, String propertyName,String showName,
                                            Criteria... queryParam) {
        Long total = 0L;
        Criteria criteria =null;
        if(queryParam != null){
            criteria = new Criteria();
            criteria.andOperator(queryParam);
        }
        Query query = Query.query(criteria);
        String reduce = "function (doc,aggr) {"
                + "for (var i in doc."+propertyName+"){"
                + "aggr.total +=1}}";
        DBObject results = operations.getCollection(
                operations.getCollectionName(cls)).group(new BasicDBObject(showName, 1),
                query.getQueryObject(), new BasicDBObject("total", total), reduce);
        Map<String,BasicDBObject> map =  results.toMap();
        List<BasicDBObject> list = new ArrayList<BasicDBObject>();
        for (String key : map.keySet()) {
            list.add(map.get(key));
        }
        return list;
    }


    public List<Order> orderAscBy(String... properNames) {
        if(properNames !=null && properNames.length > 0){
            int len = properNames.length;
            List<Order> orders = new ArrayList<Order>();
            for(int i=0;i<len;i++ ){
                Order order = new Order(Direction.ASC,properNames[i]);
                orders.add(order);
            }
            return orders;
        }
        return null;
    }


    public <T> Long sum(Class<T> cls, String propertyName, String groupName,
                        Criteria... queryParam) {
        Long total = 0L;
        Criteria criteria =null;
        if(queryParam != null){
            criteria = new Criteria();
            criteria.andOperator(queryParam);
        }
        Query query = Query.query(criteria);
        String reduce = "function (doc,aggr) { aggr.total += doc."+propertyName+"}";
        List<DBObject> results = (List<DBObject>) operations.getCollection(
                operations.getCollectionName(cls)).group(new BasicDBObject(groupName, 1),
                query.getQueryObject(), new BasicDBObject("total", total), reduce);

        if(CollectionUtils.isNotEmpty(results)){
            DBObject result = results.get(0);
            return new Double(result.get("total")+"").longValue();
        }
        return 0L;
    }

    public <T> Double sumDouble(Class<T> cls, String propertyName,
                                String groupName, Criteria... queryParam) {
        Criteria criteria =null;
        Double total = 0.00;
        if(queryParam != null){
            criteria = new Criteria();
            criteria.andOperator(queryParam);
        }
        Query query = Query.query(criteria);
        String reduce = "function (doc,aggr) { aggr.total += doc."+propertyName+"}";
        List<DBObject> results = (List<DBObject>) operations.getCollection(
                operations.getCollectionName(cls)).group(new BasicDBObject(groupName, 1),
                query.getQueryObject(), new BasicDBObject("total", total), reduce);

        if(CollectionUtils.isNotEmpty(results)){
            DBObject result = results.get(0);
            return new Double(result.get("total")+"");
        }
        return total;
    }


}
