package com.lorne.file.server.service.impl;

import com.lorne.core.framework.exception.ParamException;
import com.lorne.core.framework.exception.ServiceException;
import com.lorne.file.server.service.FileValidateService;

import org.apache.commons.io.FilenameUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

/**
 * create by lorne on 2017/9/28
 */
@Service
public class FileValidateServiceImpl implements FileValidateService {

    @Value("${file.validate.type}")
    private String fileTypes;

    private List<String> types;
    private boolean hasLoad = false;

    private void init(){
        if(!hasLoad) {
            types = Arrays.asList(fileTypes.split(","));
            hasLoad = true;
        }
    }

    @Override
    public void validateFile(MultipartFile file) throws ServiceException {

        init();

        String ext = FilenameUtils.getExtension(file.getOriginalFilename());

        if(!types.contains(ext)){
            throw new ParamException("file type error.");
        }
    }
}
