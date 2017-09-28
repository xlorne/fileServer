package com.lorne.file.server.controller;

import com.lorne.core.framework.exception.ServiceException;
import com.lorne.file.server.service.UploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * create by lorne on 2017/9/26
 */
@RestController
@RequestMapping("/image")
@Api(value = "图片文件服务接口")
public class ImageFileController {



    @Autowired
    private UploadService uploadService;


    @ApiOperation(value="上传图片", notes="上传图片")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public String uploadImage(

            @ApiParam(value = "文件流,name=file")
            @RequestParam("file") MultipartFile file,

            @ApiParam(value = "模块名称")
            @RequestParam("groupName") String groupName,

            @ApiParam(value = "裁剪尺寸（数组类型）如:20x20,30x30,100x100")
            @RequestParam("cutSize") String cutSize) throws ServiceException {
        return uploadService.uploadImage(groupName,cutSize,file);
    }



    @ApiOperation(value="裁剪图片", notes="裁剪图片")
    @RequestMapping(value = "/cut", method = RequestMethod.POST)
    public boolean cutImage(

            @ApiParam(value = "主文件名称")
            @RequestParam("fileName") String fileName,

            @ApiParam(value = "裁剪尺寸（数组类型）如:20x20,30x30,100x100")
            @RequestParam("cutSize") String cutSize) throws ServiceException {

        return uploadService.cutImage(fileName,cutSize);
    }





}
