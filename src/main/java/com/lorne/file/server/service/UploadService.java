package com.lorne.file.server.service;

import com.lorne.core.framework.exception.ServiceException;
import org.springframework.web.multipart.MultipartFile;

/**
 * create by lorne on 2017/9/26
 */
public interface UploadService {

    String uploadFile(String groupName,MultipartFile file) throws ServiceException;

    boolean removeFile(String fileName) throws ServiceException;

    String uploadSlaveFile(String fileName, String prefixName, MultipartFile file) throws ServiceException;

    String uploadImage(String groupName, String cutSize, MultipartFile file) throws ServiceException;

    boolean cutImage(String fileName, String cutSize)throws ServiceException;

    boolean removeFiles(String fileName) throws ServiceException;
}
