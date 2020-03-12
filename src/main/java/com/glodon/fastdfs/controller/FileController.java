package com.glodon.fastdfs.controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.glodon.fastdfs.utils.FdfsUtil;

@RestController
@RequestMapping(value = "/file")
public class FileController {
	
	@Autowired
	private FdfsUtil fdfsUtil;
	
	
	
	
	/**
	 * 单文件上传
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/upload")
	public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) throws Exception {
		if(file.isEmpty()) {
			return ResponseEntity.badRequest().body("文件不能为空！");
		}
		String fileUrl = fdfsUtil.uploadFile(file);
		return ResponseEntity.ok(fileUrl);
	}
	
	/**
	 * 多文件上传
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/uploads")
	public ResponseEntity<?> upload(@RequestParam("file") MultipartFile[] files) throws Exception {
		if( ObjectUtils.isEmpty(files)) {
			return ResponseEntity.badRequest().body("文件不能为空！");
		}
		List<String> fileUrls = new ArrayList<String>();
		for(MultipartFile file : files) {
			fileUrls.add(fdfsUtil.uploadFile(file));
		}
		
		return ResponseEntity.ok(fileUrls);
	}
	
	/**
	 * 删除文件
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	@DeleteMapping("/delete")
	public ResponseEntity<String> delete(@RequestParam("fileName") String fileName) throws Exception {
		fdfsUtil.deleteFile(fileName);
		return ResponseEntity.ok("删除成功");
	}
	
	/**
	 * 下载文件 ，建议直接nginx 请求 文件地址 ，不必采用本方法
	 * @param fileName
	 * @param response
	 * @throws Exception
	 */
	@GetMapping("/download")
	public void download(@RequestParam("fileName") String fileName, HttpServletResponse response) throws Exception {
		InputStream is = fdfsUtil.downloadFile(fileName);
		byte[] buffer = new byte[1024];
		
		OutputStream os = response.getOutputStream();
        int i = is.read(buffer);
        while (i != -1) {
            os.write(buffer, 0, i);
            i = is.read(buffer);
        }
	}
	
	/**
	 * 获取文件 服务器地址
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/url")
	public ResponseEntity<String> getWebUrl() throws Exception {
		String url = fdfsUtil.getWebServerUrl();
		return ResponseEntity.ok(url);
	}
}
