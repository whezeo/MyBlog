package com.xl.domain.utils;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.*;
import com.qcloud.cos.model.DeleteObjectsRequest.KeyVersion;
import com.qcloud.cos.region.Region;
import com.qcloud.cos.transfer.Download;
import com.qcloud.cos.transfer.TransferManager;
import com.qcloud.cos.transfer.TransferManagerConfiguration;
import com.qcloud.cos.transfer.Upload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 腾讯云对象存储工具类
 * @author liveb
 */

@Component
public class CosUtils {

    //腾讯云的SecretId
    private  static String secretId = "AKIDpkSwKXX7UfeT4TgiZDamPp35uZMDuOfh";
    //腾讯云的SecretKey
    private static String secretKey = "vd7xb4MhpiOr9E9vYyBIHE0b3JMCrKZa";

    //腾讯云的bucket (存储桶)

    private static String bucket = "xxll-1316295555";

    //腾讯云的region(bucket所在地区)

    private static String region = "ap-chengdu";

    //腾讯云的访问基础链接:

    private static String baseUrl = "https://xxll-1316295555.cos.ap-chengdu.myqcloud.com/";
//    @Value("${COS.secretId}")
//    public void setSecretId(String secretId) {
//        CosUtils.secretId = secretId;
//    }
//    @Value("${COS.secretKey}")
//    public static void setSecretKey(String secretKey) {
//        CosUtils.secretKey = secretKey;
//    }
//    @Value("${COS.bucket}")
//    public static void setBucket(String bucket) {
//        CosUtils.bucket = bucket;
//    }
//    @Value("${COS.region}")
//    public static void setRegion(String region) {
//        CosUtils.region = region;
//    }
//    @Value("${COS.url}")
//    public static void setBaseUrl(String baseUrl) {
//        CosUtils.baseUrl = baseUrl;
//    }
    /**
     * 获取cos客户端
     *
     * @return COSClient
     */
    private static COSClient getCOSClient() {

        // 1 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(secretId, secretKey);

        // 2 设置bucket的区域, COS地域的简称请参照 https://www.qcloud.com/document/product/436/6224
        ClientConfig clientconfig = new ClientConfig(new Region(region));

        // 3 生成cos客户端并且返回
        return new COSClient(cred, clientconfig);

    }

    static String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 文件以流形式下载
     *
     * @param fileURL  文件链接
     * @param response
     */
    public static void downloadFile(String fileURL, HttpServletResponse response) {

        // 生成cos客户端
        COSClient cosclient = getCOSClient();

        if (!cosclient.doesObjectExist(bucket, getKeyByFileURL(fileURL))) {
            throw new RuntimeException("cos文件不存在");
        }

        // 获取下载输入流
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucket, getKeyByFileURL(fileURL));
        // 限流使用的单位是 bit/s, 这里设置下载带宽限制为10MB/s
//      getObjectRequest.setTrafficLimit(80*1024*1024);
        COSObject cosObject = cosclient.getObject(getObjectRequest);
        COSObjectInputStream cosObjectInput = cosObject.getObjectContent();

        //文件下载设置
        response.reset();
        response.setCharacterEncoding("utf-8");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", cosObject.getObjectMetadata().getContentDisposition().replace("inline", "attachment"));

        try {

            // 读取文件内容。
            OutputStream out = response.getOutputStream();
            BufferedInputStream br = new BufferedInputStream(cosObjectInput);
            byte[] buf = new byte[2048];
            int len = 0;
            while ((len = br.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            out.close();
            br.close();

            // 关闭输入流
            cosObjectInput.close();

        } catch (IOException e) {
            throw new RuntimeException("cos文件下载失败");
        } finally {
            cosclient.shutdown();
        }
    }

    // 如果要获取超过maxkey数量的object或者获取所有的object, 则需要循环调用listobject, 用上一次返回的next marker作为下一次调用的marker,
    // 直到返回的truncated为false

    /**
     * 获取指定文件夹下的文件列表
     *
     * @param fileDir
     * @return
     */
    public static List<String> getListByDirPath(String fileDir) {

        //处理文件夹路径前无/后加/
        fileDir = getCorrectFolderPath(fileDir);

        // 生成cos客户端
        COSClient cosclient = getCOSClient();

        ListObjectsRequest listObjectsRequest = new ListObjectsRequest();
        // 设置bucket名称
        listObjectsRequest.setBucketName(bucket);
        // prefix表示列出的object的key以prefix开始
        listObjectsRequest.setPrefix(fileDir);
        // deliter表示分隔符, 设置为/表示列出当前目录下的object, 设置为空表示列出所有的object
        listObjectsRequest.setDelimiter("/");
        // 设置最大遍历出多少个对象, 一次listobject最大支持1000
        listObjectsRequest.setMaxKeys(1000);
        ObjectListing objectListing = null;

        List<String> resultUrls = new ArrayList<String>();
        do {

            try {
                objectListing = cosclient.listObjects(listObjectsRequest);
            } catch (CosServiceException e) {
                e.printStackTrace();
                return resultUrls;
            } catch (CosClientException e) {
                e.printStackTrace();
                return resultUrls;
            }
            // common prefix表示表示被delimiter截断的路径, 如delimter设置为/, common prefix则表示所有子目录的路径
//            List<String> commonPrefixs = objectListing.getCommonPrefixes();

            // object summary表示所有列出的object列表
            List<COSObjectSummary> cosObjectSummaries = objectListing.getObjectSummaries();
            for (COSObjectSummary cosObjectSummary : cosObjectSummaries) {
                // 文件的路径key
                String key = cosObjectSummary.getKey();
                // 文件的etag
                String etag = cosObjectSummary.getETag();
                // 文件的长度
                long fileSize = cosObjectSummary.getSize();
                // 文件的存储类型
                String storageClasses = cosObjectSummary.getStorageClass();
                cosObjectSummary.getLastModified();

                System.out.println("key: " + key + "   etag: " + etag + "   fileSize: " + fileSize + "   storageClasses: " + storageClasses);
                resultUrls.add(baseUrl + key);
            }

            String nextMarker = objectListing.getNextMarker();
            listObjectsRequest.setMarker(nextMarker);
        } while (objectListing.isTruncated());

        cosclient.shutdown();

        return resultUrls;
    }

    /**
     * 删除单个文件(不带版本号, 即bucket未开启多版本)
     *
     * @param fileUrl
     */
    public static void DelSingleFile(String fileUrl) {

        //生成cos客户端
        COSClient cosclient = getCOSClient();

        try {
            if (cosclient.doesObjectExist(bucket, getKeyByFileURL(fileUrl))) {
                cosclient.deleteObject(bucket, getKeyByFileURL(fileUrl));
            }
        } catch (CosServiceException e) { // 如果是其他错误, 比如参数错误， 身份验证不过等会抛出CosServiceException
            e.printStackTrace();
        } catch (CosClientException e) { // 如果是客户端错误，比如连接不上COS
            e.printStackTrace();
        } finally {
            // 关闭客户端
            cosclient.shutdown();
        }

    }

    /**
     * 批量删除文件(不带版本号, 即bucket未开启多版本)
     *
     * @param fileUrlList
     */
    public static void batchDelFile(List<String> fileUrlList) {
        // 生成cos客户端
        COSClient cosclient = getCOSClient();

        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(bucket);
        // 设置要删除的key列表, 最多一次删除1000个
        ArrayList<KeyVersion> keyList = new ArrayList<>();
        // 传入要删除的文件名
        for (String fileUrl : fileUrlList) {
            keyList.add(new KeyVersion(getKeyByFileURL(fileUrl)));
        }
        deleteObjectsRequest.setKeys(keyList);

        // 批量删除文件
        try {
            cosclient.deleteObjects(deleteObjectsRequest);
        } catch (CosClientException e) { // 如果是客户端错误，比如连接不上COS
            e.printStackTrace();
        } finally {
            // 关闭客户端
            cosclient.shutdown();
        }
    }

    /**
     * 文件上传
     *
     * @param file 文件
     * @param fileDir 桶中文件的路径
     * @return
     */
    public static String uploadFile(MultipartFile file, String fileDir) {

        // 生成cos客户端
        COSClient cosclient = getCOSClient();
        fileDir =   getCorrectFolderPath(fileDir);

        String fileName = String.format("%s.%s", UUID.randomUUID().toString().replace("-", ""), getExtension(Objects.requireNonNull(file.getOriginalFilename())));

        try (InputStream inputStream = file.getInputStream()) {

            // 从输入流上传(需提前告知输入流的长度, 否则可能导致 oom)
            ObjectMetadata objectMetadata = new ObjectMetadata();
            // 设置输入流长度
            objectMetadata.setContentLength(inputStream.available());
            // 设置 Content type, 默认是 application/octet-stream
            objectMetadata.setContentType(getContentType(getExtension(file.getOriginalFilename())));
            // 设置不缓存
            objectMetadata.setCacheControl("no-cache");
            objectMetadata.setHeader("Pragma", "no-cache");
            // inline在线预览,中文乱码已处理,下载文件的时候可以用原来上传的名字
            objectMetadata.setContentDisposition("inline;filename=" + URLEncoder.encode(file.getOriginalFilename(), "utf-8"));
            cosclient.putObject(bucket, fileDir + fileName, inputStream, objectMetadata);

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 关闭客户端
        cosclient.shutdown();

        return baseUrl + fileDir + fileName;
    }

    /**
     * 文件批量上传
     *
     * @param files
     * @param fileDir
     * @return
     */
    public static String batchUploadFile(MultipartFile[] files, String fileDir) {

        //处理文件夹路径前无/后加/
        fileDir = getCorrectFolderPath(fileDir);

        // 生成cos客户端
        COSClient cosclient = getCOSClient();

        String resultUrls = "";
        try {

            for (MultipartFile file : files) {
                String fileName = String.format("%s.%s", UUID.randomUUID().toString().replace("-", ""), getExtension(file.getOriginalFilename()));

                InputStream inputStream = file.getInputStream();
                // 从输入流上传(需提前告知输入流的长度, 否则可能导致 oom)
                ObjectMetadata objectMetadata = new ObjectMetadata();
                // 设置输入流长度为500
                objectMetadata.setContentLength(inputStream.available());
                // 设置 Content type, 默认是 application/octet-stream
                objectMetadata.setContentType(getContentType(getExtension(file.getOriginalFilename())));
                // 设置不缓存
                objectMetadata.setCacheControl("no-cache");
                objectMetadata.setHeader("Pragma", "no-cache");
                // inline在线预览,中文乱码已处理,下载文件的时候可以用原来上传的名字
                objectMetadata.setContentDisposition("inline;filename=" + URLEncoder.encode(file.getOriginalFilename(), "utf-8"));
                cosclient.putObject(bucket, fileDir + fileName, inputStream, objectMetadata);

                resultUrls = resultUrls + "," + baseUrl + fileDir + fileName;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // 关闭客户端
        cosclient.shutdown();

        return resultUrls.substring(1);
    }

    public static TransferManager cosTransferManager() {

        // 线程池大小，建议在客户端与 COS 网络充足（例如使用腾讯云的 CVM，同地域上传 COS）的情况下，设置成16或32即可，可较充分的利用网络资源
        // 对于使用公网传输且网络带宽质量不高的情况，建议减小该值，避免因网速过慢，造成请求超时。
        ExecutorService threadPool = Executors.newFixedThreadPool(32);
        // 传入一个 threadpool, 若不传入线程池，默认 TransferManager 中会生成一个单线程的线程池。
        TransferManager transferManager = new TransferManager(getCOSClient(), threadPool);
        // 设置高级接口的分块上传阈值和分块大小为10MB
        TransferManagerConfiguration transferManagerConfiguration = new TransferManagerConfiguration();
        transferManagerConfiguration.setMultipartUploadThreshold(10 * 1024 * 1024);
        transferManagerConfiguration.setMinimumUploadPartSize(10 * 1024 * 1024);
        transferManager.setConfiguration(transferManagerConfiguration);
        return transferManager;
    }

    public static Upload upload(String filePath, MultipartFile file) {
        TransferManager manager = cosTransferManager();
        Upload result = null;
        try (InputStream inputStream = file.getInputStream()) {
            result = manager.upload(bucket, filePath, inputStream, new ObjectMetadata());
            result.waitForUploadResult();
        } catch (CosClientException | InterruptedException | IOException e) {
            e.printStackTrace();
        } finally {
            manager.shutdownNow();
        }
        return result;
    }

    public static Download download(String filePath, File destFile) {
        TransferManager manager = cosTransferManager();
        Download download = null;
        try {
            download = manager.download(bucket, filePath, destFile);
            download.waitForCompletion();
        } catch (CosClientException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            manager.shutdownNow();
        }
        return download;
    }

    /**
     * 获取对象键
     * 官方文档：腾讯云对象存储 COS 中的对象需具有合法的对象键，对象键（ObjectKey）是对象在存储桶中的唯一标识。
     * 例如：在对象的访问地址examplebucket-1250000000.cos.ap-guangzhou.myqcloud.com/folder/picture.jpg 中，对象键为folder/picture.jpg。
     *
     * @param fileUrl
     * @return
     */
    private static String getKeyByFileURL(String fileUrl) {
        //获取完整路径中的文件名，截去"http://oss-cn-shenzhen.aliyuncs.com/"剩下的就是文件名
        if (fileUrl.indexOf("http") > -1) {
            return fileUrl.substring(fileUrl.indexOf("/", 9) + 1);
        }
        return fileUrl;
    }

    /**
     * 处理正确文件夹路径(前无/后加/)
     * 例如：/upload/image -> upload/image/
     *
     * @param fileDir
     * @return
     */
    private static String getCorrectFolderPath(String fileDir) {
        //处理文件夹路径前无/后加/
        if (StringUtils.isEmpty(fileDir)) {
            fileDir = "";
        } else {
            fileDir = (fileDir.indexOf("/") == 0) ? fileDir.substring(1) : fileDir;
            fileDir = (fileDir.lastIndexOf("/") == fileDir.length() - 1) ? fileDir : fileDir + "/";
        }
        return fileDir;
    }

    /**
     * 判断COS服务文件上传时文件的contentType
     *
     * @param FilenameExtension 文件后缀
     * @return ContentType
     */
    private static String getContentType(String FilenameExtension) {

        //image/jpg 可以在线预览
        if (FilenameExtension.equalsIgnoreCase("gif")
                || FilenameExtension.equalsIgnoreCase("jpeg")
                || FilenameExtension.equalsIgnoreCase("jpg")
                || FilenameExtension.equalsIgnoreCase("png")) {
            return "image/jpg";
        }
        if (FilenameExtension.equalsIgnoreCase("bmp")) {
            return "image/bmp";
        }
        if (FilenameExtension.equalsIgnoreCase("html")) {
            return "text/html";
        }
        if (FilenameExtension.equalsIgnoreCase("txt")) {
            return "text/plain";
        }
        if (FilenameExtension.equalsIgnoreCase("vsd")) {
            return "application/vnd.visio";
        }
        if (FilenameExtension.equalsIgnoreCase("pptx") ||
                FilenameExtension.equalsIgnoreCase("ppt")) {
            return "application/vnd.ms-powerpoint";
        }
        if (FilenameExtension.equalsIgnoreCase("docx") ||
                FilenameExtension.equalsIgnoreCase("doc")) {
            return "application/msword";
        }
        if (FilenameExtension.equalsIgnoreCase("pdf")) {
            return "application/pdf";
        }
        if (FilenameExtension.equalsIgnoreCase("ppt")) {
            return "application/x-ppt";
        }
        if (FilenameExtension.equalsIgnoreCase("xml")) {
            return "text/xml";
        }
        if (FilenameExtension.equalsIgnoreCase("mp3")) {
            return "audio/mp3";
        }
        if (FilenameExtension.equalsIgnoreCase("mp4")) {
            return "video/mp4";
        }
        if (FilenameExtension.equalsIgnoreCase("avi")) {
            return "video/avi";
        }
        if (FilenameExtension.equalsIgnoreCase("wmv")) {
            return "video/x-ms-wmv";
        }
        return "image/jpg";
    }


}


