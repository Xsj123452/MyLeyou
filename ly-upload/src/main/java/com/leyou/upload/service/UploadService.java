package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.upload.config.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    @Autowired
    private UploadProperties props;
    public String uploadImage(MultipartFile file){
        try {
            //判断文件是否符合要求
            BufferedImage image = ImageIO.read(file.getInputStream());
            if(image == null){
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            String type = file.getContentType();
//            System.out.println(props.getAllowTypes());
            if(!props.getAllowTypes().contains(type)){
                throw new LyException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //获取文件名，并上传到fastdfs
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);
            //返回路径
            System.out.println(props.getBaseUrl()+storePath.getFullPath());
                return props.getBaseUrl()+storePath.getFullPath();

        } catch (IOException e) {
            log.error("上传失败",e);
            throw new LyException(ExceptionEnum.UPLOAD_FILE_FAIL);
        }

    }
}
