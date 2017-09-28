package com.lorne.file.server.controller;

import com.lorne.core.framework.exception.ServiceException;
import com.lorne.file.server.service.UploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * create by lorne on 2017/9/26
 */
@RestController
@RequestMapping("/file")
@Api(value = "文件服务接口")
public class FileController {



    @Autowired
    private UploadService uploadService;


    @ApiOperation(value="上传文件", notes="上传文件")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadFile(

            @ApiParam(value = "文件流,name=file")
            @RequestParam("file") MultipartFile file,

            @ApiParam(value = "模块名称")
            @RequestParam("groupName") String groupName) throws ServiceException {
        return uploadService.uploadFile(groupName,file);
    }



    @ApiOperation(value="上传从文件", notes="上传从文件")
    @RequestMapping(value = "/uploadSlave", method = RequestMethod.POST)
    public String uploadSlaveFile(

            @ApiParam(value = "从文件流,name=file")
            @RequestParam("file") MultipartFile file,

            @ApiParam(value = "主文件名称")
            @RequestParam("fileName") String fileName,

            @ApiParam(value = "前缀名称")
            @RequestParam("prefixName") String prefixName ) throws ServiceException {

        return uploadService.uploadSlaveFile(fileName,prefixName,file);
    }



    @ApiOperation(value="删除文件", notes="删除文件")
    @RequestMapping(value = "/removeFile", method = RequestMethod.POST)
    public boolean removeFile(
            @ApiParam(value = "模块名称")
            @RequestParam("fileName") String fileName) throws ServiceException {
        return uploadService.removeFile(fileName);
    }


    @ApiOperation(value="删除文件以及从文件", notes="删除文件以及从文件")
    @RequestMapping(value = "/removeFiles", method = RequestMethod.POST)
    public boolean removeFiles(
            @ApiParam(value = "模块名称")
            @RequestParam("fileName") String fileName) throws ServiceException {
        return uploadService.removeFiles(fileName);
    }


}
