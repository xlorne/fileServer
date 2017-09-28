package com.lorne.file.server.service.impl;

import com.github.tobato.fastdfs.domain.FileInfo;
import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.proto.storage.DownloadCallback;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.github.tobato.fastdfs.service.TrackerClient;
import com.lorne.core.framework.exception.ParamException;
import com.lorne.core.framework.exception.ServiceException;
import com.lorne.core.framework.utils.KidUtils;
import com.lorne.file.server.model.ImageWH;
import com.lorne.file.server.service.FileValidateService;
import com.lorne.file.server.service.UploadService;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * create by lorne on 2017/9/26
 */
@Service
public class UploadServiceImpl implements UploadService {


    @Autowired
    protected AppendFileStorageClient storageClient;

    @Autowired
    private FileValidateService fileValidateService;

    @Autowired
    private FastFileStorageClient fastFileStorageClient;


    @Override
    public String uploadFile(String groupName, MultipartFile file) throws ServiceException {

        if (file.getSize() <= 0) {
            throw new ParamException("file is null.");
        }

        fileValidateService.validateFile(file);

        try {
            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            StorePath path = storageClient.uploadFile(groupName, file.getInputStream(), file.getSize(), ext);
            if(path==null) {
                throw new ServiceException("upload error.");
            }
            return path.getFullPath();
        } catch (Exception e) {
            throw new ServiceException(e);
        }
    }


    @Override
    public boolean removeFile(String fileName) throws ServiceException {

        if(StringUtils.isEmpty(fileName)){
            throw new ParamException("fileName is null");
        }
        StorePath storePath = StorePath.praseFromUrl(fileName);

        storageClient.deleteFile(storePath.getGroup(),storePath.getPath());

        return true;
    }


    @Override
    public String uploadSlaveFile(String fileName, String prefixName, MultipartFile file) throws ServiceException {

        if (file.getSize() <= 0) {
            throw new ParamException("file is null.");
        }

        if(StringUtils.isEmpty(fileName)){
            throw new ParamException("fileName is null");
        }

        if(StringUtils.isEmpty(prefixName)){
            throw new ParamException("prefixName is null");
        }

        fileValidateService.validateFile(file);

        StorePath storePath = StorePath.praseFromUrl(fileName);

        try {

            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            StorePath slaveFile = storageClient.uploadSlaveFile(storePath.getGroup(),storePath.getPath(),
                    file.getInputStream(), file.getSize(),prefixName, ext);

            if(slaveFile==null) {
                throw new ServiceException("upload error.");
            }
            return slaveFile.getFullPath();

        }catch (Exception e){
            throw new ServiceException(e);
        }
    }


    @Override
    public String uploadImage(String groupName, String cutSize, MultipartFile file) throws ServiceException {

        if (file.getSize() <= 0) {
            throw new ParamException("file is null.");
        }

        if(StringUtils.isEmpty(groupName)){
            throw new ParamException("groupName is null");
        }

        List<ImageWH> whs =loadCutSize(cutSize);

        fileValidateService.validateFile(file);

        try {

            String ext = FilenameUtils.getExtension(file.getOriginalFilename());
            StorePath path = storageClient.uploadFile(groupName, file.getInputStream(), file.getSize(), ext);
            if(path==null) {
                throw new ServiceException("upload error.");
            }


            File sourceFile = new File(KidUtils.getUUID());
            FileUtils.copyInputStreamToFile(file.getInputStream(),sourceFile);

            uploadCutImages(whs,sourceFile,path,ext);

            sourceFile.delete();

            return path.getFullPath();
        } catch (Exception e) {
            throw new ServiceException(e);
        }

    }


    private List<ImageWH> loadCutSize(String cutSize) throws ParamException{
        List<ImageWH> whs =null;
        if(StringUtils.isNotEmpty(cutSize)) {
            try {
                List<String> sizes = Arrays.asList(cutSize.split(","));
                whs = new ArrayList<>();
                for (String size : sizes) {
                    String vals[] = size.split("x");
                    int w = Integer.parseInt(vals[0]);
                    int h = Integer.parseInt(vals[1]);

                    whs.add(new ImageWH(w, h));
                }

            }catch (Exception e){
                throw new ParamException("cutSize is error");
            }
        }

        return whs;
    }


    private void uploadCutImages(List<ImageWH> whs,File sourceFile,StorePath path,String ext) throws ServiceException {
        //上传裁剪的图片
        try {
            for (ImageWH wh : whs) {

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                Thumbnails.of(sourceFile).size(wh.getW(), wh.getH()).toOutputStream(out);
                InputStream newInput = new ByteArrayInputStream(out.toByteArray());

                int newSize = out.size();

                String prefixName = String.format("%dx%d", wh.getW(), wh.getH());

                StorePath slaveFile = storageClient.uploadSlaveFile(path.getGroup(), path.getPath(),
                        newInput, newSize, prefixName, ext);
                if (slaveFile == null) {
                    throw new ServiceException("cutImage upload error.");
                }
            }
        }catch (Exception e){
            throw new ServiceException(e);
        }
    }

    @Override
    public boolean cutImage(String fileName, String cutSize) throws ServiceException {

        if(StringUtils.isEmpty(fileName)){
            throw new ParamException("fileName is null");
        }

        List<ImageWH> whs =loadCutSize(cutSize);


        StorePath path = StorePath.praseFromUrl(fileName);

        String ext = FilenameUtils.getExtension(fileName);

        InputStream sourceStream =storageClient.downloadFile(path.getGroup(), path.getPath(), new DownloadCallback<InputStream>() {

            @Override
            public InputStream recv(InputStream inputStream) throws IOException {
                return inputStream;
            }
        });

        if(sourceStream==null){
            throw new ServiceException("download error");
        }

        File file = new File(KidUtils.getUUID());
        try {
            FileUtils.copyInputStreamToFile(sourceStream,file);
            uploadCutImages(whs,file,path,ext);

        } catch (IOException e) {
            throw new ServiceException(e);
        }finally {
            file.delete();
        }

        return true;
    }


    @Override
    public boolean removeFiles(String fileName) throws ServiceException {

        if(StringUtils.isEmpty(fileName)){
            throw new ParamException("fileName is null");
        }
        StorePath storePath = StorePath.praseFromUrl(fileName);

        FileInfo fileInfo =  storageClient.queryFileInfo(storePath.getGroup(),storePath.getPath());



        storageClient.deleteFile(storePath.getGroup(),storePath.getPath());

        return true;
    }
}
