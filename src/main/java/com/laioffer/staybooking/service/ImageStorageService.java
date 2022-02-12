package com.laioffer.staybooking.service;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.laioffer.staybooking.exception.GCSUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Service
public class ImageStorageService {
    //application.propertites 的gcs的bucket的值，这样是动态加入
    @Value("${gcs.bucket}")
    private String bucketName;

    public String save(MultipartFile file) throws GCSUploadException {
        Credentials credentials = null;
        try {
            //把gcs那个credentials从resorces拿过来
            //getResourceAsStream（）决定了这个文件必须在resources里面
            credentials = GoogleCredentials.fromStream(getClass().getClassLoader().getResourceAsStream("staybooking-credentials.json"));

        } catch (IOException e) {
            throw new GCSUploadException("Failed to load GCP credentials(staybooking-credentials.json)");
        }

        Storage storage = StorageOptions.newBuilder()
                .setCredentials(credentials)
                .build()
                .getService();

        //java自带随机的名字 universal unique id
        String filename = UUID.randomUUID().toString();

        BlobId blobId = BlobId.of(bucketName, filename); //folder name + filename
        //Blob = 很大的文件
        BlobInfo blobInfo = null; //meta data

        try {
            //GCS的操作
            blobInfo = storage.createFrom( //一个meta data， 一个filename
                    BlobInfo
                            .newBuilder(blobId)
                            .setContentType("image/jpeg")
                            .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
                            //Acl是access control list，文件访问权限
                            //前端是另一个程序，无法读取后端返回的这个上传后的url，此条指令就是给前端一个文件读取权限
                            //（前端程序crediential， role）但我们这儿前端没做，所以就向所有人打开了
                            .build(),
                    file.getInputStream());//file.getInputStream()读取file内容，传入要上传的BlobInfo里面。文件的实际data

        } catch (IOException e) {

            throw new GCSUploadException("Failed to upload images to GCS");
        }

        return blobInfo.getMediaLink(); //return 上传后图片的url
    }
}

