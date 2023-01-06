package com.xl.domain.service;

import com.xl.domain.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author xxll
 */
public interface UploadService {
    ResponseResult uploadImg(MultipartFile img);
}