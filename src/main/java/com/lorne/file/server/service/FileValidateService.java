package com.lorne.file.server.service;

import com.lorne.core.framework.exception.ServiceException;
import org.springframework.web.multipart.MultipartFile;

/**
 * create by lorne on 2017/9/28
 */
public interface FileValidateService {



    void validateFile(MultipartFile file) throws ServiceException;

}
