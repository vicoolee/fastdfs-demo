package com.glodon.fastdfs.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadByteArray;
import com.github.tobato.fastdfs.exception.FdfsUnsupportStorePathException;
import com.github.tobato.fastdfs.service.FastFileStorageClient;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class FdfsUtil {
	@Autowired
	private FastFileStorageClient storageClient;

	@Autowired
	private FdfsWebServer fdfsWebServer;

	/**
	 * MultipartFile类型的文件上传
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public String uploadFile(MultipartFile file) throws IOException {
		StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(),
				FilenameUtils.getExtension(file.getOriginalFilename()), createMetaData());
		return getResAccessUrl(storePath);
	}

	/**
	 * 普通的文件上传
	 *
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public String uploadFile(File file) throws IOException {
		FileInputStream inputStream = new FileInputStream(file);
		StorePath path = storageClient.uploadFile(inputStream, file.length(),
				FilenameUtils.getExtension(file.getName()), createMetaData());
		return getResAccessUrl(path);
	}

	/**
	 * 带输入流形式的文件上传
	 *
	 * @param is
	 * @param size
	 * @param fileName
	 * @return
	 */
	public String uploadFileStream(InputStream is, long size, String fileName) {
		StorePath path = storageClient.uploadFile(is, size, fileName, null);
		return getResAccessUrl(path);
	}

	/**
	 * 将一段文本文件写到fastdfs的服务器上
	 *
	 * @param content
	 * @param fileExtension
	 * @return
	 */
	public String uploadFile(String content, String fileExtension) {
		byte[] buff = content.getBytes(StandardCharsets.UTF_8);
		ByteArrayInputStream stream = new ByteArrayInputStream(buff);
		StorePath path = storageClient.uploadFile(stream, buff.length, fileExtension, createMetaData());
		return getResAccessUrl(path);
	}

	/**
	 * 返回文件上传成功后的地址名
	 * 
	 * @param storePath
	 * @return
	 */
	private String getResAccessUrl(StorePath storePath) {
		String fileUrl = storePath.getFullPath();
		return fileUrl;
	}
	
	public String getWebServerUrl() {
		return fdfsWebServer.getWebServerUrl() ;
	}
	/**
	 * 删除文件
	 * 
	 * @param fileUrl
	 */
	public void deleteFile(String fileUrl) {
		if (StringUtils.isEmpty(fileUrl)) {
			return;
		}
		try {
			StorePath storePath = StorePath.parseFromUrl(fileUrl);
			storageClient.deleteFile(storePath.getGroup(), storePath.getPath());
		} catch (FdfsUnsupportStorePathException e) {
			log.warn(e.getMessage());
		}
	}
	/**
	 * 删除文件
	 * @param fileUrl
	 * @return
	 */
	public InputStream downloadFile(String fileUrl) {
		StorePath storePath = StorePath.parseFromUrl(fileUrl);
		DownloadByteArray callback = new DownloadByteArray();
        byte[] content = storageClient.downloadFile(storePath.getGroup(), storePath.getPath(), callback);
        InputStream inputStream = new ByteArrayInputStream(content);
        return inputStream;
	}
	
	
	public String uploadImage(InputStream is, long size, String fileExtName, Set<MetaData> metaData) {
		StorePath path = storageClient.uploadImageAndCrtThumbImage(is, size, fileExtName, metaData);
		return getResAccessUrl(path);
	}
	
	private Set<MetaData> createMetaData() {
        Set<MetaData> metaDataSet = new HashSet<MetaData>();
        metaDataSet.add(new MetaData("Author", "liwk"));
        metaDataSet.add(new MetaData("CreateDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));
        return metaDataSet;
    }
}
