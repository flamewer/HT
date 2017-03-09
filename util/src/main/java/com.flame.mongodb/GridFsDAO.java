
package com.flame.mongodb;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Repository;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSFile;

/**
 * Mongodb文件操作. <br>
 * @since JDK 1.8.0
 */
@Repository(value="fsDao")
public class GridFsDAO{

	private GridFsOperations gridFsTemplate;




	public String storeFile(InputStream content, String filename) {
		final GridFSFile file = this.gridFsTemplate.store(content, filename);
		return file.getId().toString();
	}



	public String storeFile(InputStream content, String filename,String contentType) {
		final GridFSFile file = this.gridFsTemplate.store(content, filename,contentType);
		return file.getId().toString();
	}


	public void deleteFile(String fileId) {
		Query query = buildFileIdQuery(fileId);
		if(query != null){
			List<GridFSDBFile> results = this.gridFsTemplate.find(query);
			if(CollectionUtils.isNotEmpty(results) && results.size() < 10) {
				this.gridFsTemplate.delete(query);
			}

		}

	}

	/**
	 * 构造fileId 的查询条件
	 * @param fileId
	 * @return
	 */
	private Query buildFileIdQuery(final String fileId) {
		if(StringUtils.isEmpty(fileId)){
			return null;
		}
		final Criteria criteria = Criteria.where("_id").is(new ObjectId(fileId));
		final Query query = new Query(criteria);
		return query;
	}


	public InputStream getInputStream(String fileId) {
		Query query = buildFileIdQuery(fileId);
		if(query != null) {
			final GridFSDBFile file = gridFsTemplate.findOne(query);
			if (file != null) {
				return file.getInputStream();
			}
		}

		return null;
	}


	public GridFSDBFile getFile(String fileId) {
		if(null == fileId){
			return null;
		}
		Query query = buildFileIdQuery(fileId);
		final GridFSDBFile file = gridFsTemplate.findOne(query);
		return file;
	}


	public String storeFile(InputStream content, String filename,
			String contentType, Map<String,Object> object) {
		GridFSFile file = null;
		if(object!=null && object.size()>0){
			DBObject obj = new BasicDBObject();
			obj.putAll(object);
			file = this.gridFsTemplate.store(content, filename, contentType, obj);
		}else{
			file = this.gridFsTemplate.store(content, filename, contentType);
		}
		return file.getId().toString();
	}


	public GridFSFile storeGridFile(InputStream content, String filename,
			String contentType) {

		return this.gridFsTemplate.store(content, filename,contentType);
	}


}
