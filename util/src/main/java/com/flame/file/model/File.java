package com.flame.file.model;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

/**
 * 文件.<br>
 * 
 * @author Jon <br>
 * @see
 * @since JDK 1.8.0
 */
public class File implements Serializable {
    
	/**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5799397001410635045L;
    
    private String fileId; // 附件ID
	private String fileName;// 附件名称
	private InputStream fileContent;// 附件内容
	private String userId; // 上传人Id
	private Date uploadTime; // 上传时间
	private String fileSize;

	/**
	 * 获取fileId.
	 * 
	 * @return the fileId
	 */
	public String getFileId() {
		return fileId;
	}

	/**
	 * 设置fileId.
	 * 
	 * @param fileId
	 *            the fileId to set
	 */
	public void setFileId(String fileId) {
		this.fileId = fileId;
	}

	/**
	 * 获取fileName.
	 * 
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * 设置fileName.
	 * 
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * 获取fileSize.
	 * 
	 * @return the fileSize
	 */
	public String getFileSize() {
		return fileSize;
	}

	/**
	 * 设置fileSize.
	 * 
	 * @param fileSize
	 *            the fileSize to set
	 */
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	/**
	 * 获取fileContent.
	 * 
	 * @return the fileContent
	 */
	public InputStream getFileContent() {
		return fileContent;
	}

	/**
	 * 设置fileContent.
	 * 
	 * @param fileContent
	 *            the fileContent to set
	 */
	public void setFileContent(InputStream fileContent) {
		this.fileContent = fileContent;
	}

	/**
	 * 获取userId.
	 * 
	 * @return the userId
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * 设置userId.
	 * 
	 * @param userId
	 *            the userId to set
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * 获取uploadTime.
	 * 
	 * @return the uploadTime
	 */
	public Date getUploadTime() {
		return uploadTime;
	}

	/**
	 * 设置uploadTime.
	 * 
	 * @param uploadTime
	 *            the uploadTime to set
	 */
	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

}
