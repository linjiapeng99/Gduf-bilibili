package com.gduf.bilibili.api;

import com.gduf.bilibili.domain.JsonResponse;
import com.gduf.bilibili.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class FileApi {
    @Autowired
    private FileService fileService;

    /**
     * 文件分片上传
     * @param slice
     * @param fileMd5
     * @param sliceNo
     * @param totalSliceNo
     * @return
     * @throws Exception
     */
    @PutMapping("/file-slices")
    public JsonResponse<String> uploadFileBySlices(MultipartFile slice, String fileMd5, Integer sliceNo, Integer totalSliceNo) throws Exception {
        String path = fileService.uploadFileBySlices(slice, fileMd5, sliceNo, totalSliceNo);
        return JsonResponse.success(path);
    }

    /**
     * 获取文件md5唯一标识的
     * @param file
     * @return
     * @throws Exception
     */
    @PostMapping("/md5files")
    public JsonResponse<String> getFileMd5(MultipartFile file) throws Exception {
        String fileMd5 = fileService.getFileMd5(file);
        return JsonResponse.success(fileMd5);
    }
}
