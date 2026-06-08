package com.yuqin.meinian.api.common;

import com.yuqin.meinian.api.exception.HisException;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
public class MinIO {
    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.bucket}")
    private String bucket;

    private MinioClient client;

    @PostConstruct
    public void init() {
        // 初始化MinIO客户端连接
        // 使用建造者模式配置服务端点和管理员凭证
        this.client = new MinioClient.Builder()
                .endpoint(endpoint)          // 设置MinIO服务器地址
                .credentials(accessKey, secretKey)  // 设置访问密钥
                .build();                    // 构建MinIO客户端实例
    }

    /**
     * 上传图片到MinIO对象存储
     *
     * @param path 文件在MinIO中的存储路径（包含文件名）
     * @param file Spring MultipartFile文件对象
     * @throws HisException 当文件上传失败时抛出业务异常
     */
    public void uploadImage(String path, MultipartFile file) {
        try {
            // 使用MinIO客户端上传文件到指定存储桶
            this.client.putObject(PutObjectArgs.builder()
                    .bucket(bucket)                    // 设置存储桶名称
                    .object(path)                      // 设置文件在MinIO中的存储路径
                    .stream(file.getInputStream(),     // 文件输入流
                            -1,                        // 文件大小：-1表示自动检测
                            5 * 1024 * 1024)           // 分片大小：5MB，用于大文件分片上传（这个工具类不只是给上传图片用，还有大文件）
                    .contentType("image/jpeg")         // 设置文件内容类型为JPEG图片
                    .build());                         // 构建上传参数并执行上传

            // 记录上传成功日志
            log.debug("向{}保存了Img文件", path);

        } catch (Exception e) {
            // 记录错误日志并抛出业务异常
            log.error("保存文件失败", e);
            throw new HisException("保存文件失败");
        }
    }

    public void uploadExcel(String path, MultipartFile file) {
        try {
            // Excel文件的MIME类型
            String mime = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

            // 使用MinIO客户端上传文件到对象存储
            // bucket: 存储桶名称
            // path: 文件在存储桶中的路径
            // file.getInputStream(): 获取上传文件的输入流
            // -1: 表示流大小未知，由SDK自动计算
            // 20 * 1024 * 1024: Excel文件大小限制为20MB
            // contentType: 设置文件内容类型为Excel格式
            this.client.putObject(PutObjectArgs.builder()
                    .bucket(bucket)                    // 指定存储桶
                    .object(path)                      // 指定文件路径
                    .stream(file.getInputStream(), -1, 20 * 1024 * 1024)  // 文件流，自动计算大小，限制20MB
                    .contentType(mime)                 // 设置内容类型为Excel
                    .build());                         // 构建上传参数并执行上传

            // 记录调试日志，文件上传成功
            log.debug("向{}保存了Excel文件", path);

        } catch (Exception e) {
            // 记录错误日志并抛出业务异常
            log.error("保存文件失败", e);
            throw new HisException("保存文件失败");
        }
    }

    /**
     * 下载文件
     * 从MinIO对象存储中获取指定路径的文件输入流
     *
     * @param path 文件在MinIO中的存储路径（对象键）
     * @return InputStream 文件输入流，可直接用于文件下载或读取
     * @throws HisException 当文件下载失败时抛出业务异常
     * @example <pre>
     * // 使用示例：
     * InputStream inputStream = downloadFile("user/avatar/12345.jpg");
     * // 将inputStream写入HttpServletResponse输出流实现文件下载
     * </pre>
     */
    public InputStream downloadFile(String path) {
        try {
            // 从MinIO获取文件输入流
            return client.getObject(GetObjectArgs.builder()
                    .bucket(bucket)      // 设置存储桶名称
                    .object(path)        // 设置文件路径（对象键）
                    .build());
        } catch (Exception e) {
            log.error("文件下载失败，文件路径：{}", path, e);
            throw new HisException("文件下载失败");
        }
    }

    public boolean doesObjectExist(String path) {
        try {
            // 尝试获取对象的元数据
            client.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(path)
                            .build()
            );
            return true;
        } catch (ErrorResponseException e) {
            // 这是 MinIO 抛出的明确“不存在”错误
            if (e.errorResponse().code().equals("NoSuchKey")) {
                return false;
            }
            // 其它与“不存在”无关的错误，按需处理或记录日志
            throw new RuntimeException("检查文件存在性时发生错误", e);
        } catch (Exception e) {
            // 处理其他意外错误，如网络问题
            throw new RuntimeException("检查文件存在性时发生未知错误", e);
        }
    }

    public void removeFile(List<String> paths) {
        try {
            List<DeleteObject> objects = paths.stream()
                    .map(DeleteObject::new)
                    .collect(Collectors.toList());

            Iterable<Result<DeleteError>> results = client.removeObjects(
                    RemoveObjectsArgs.builder()
                            .bucket(bucket)
                            .objects(objects)
                            .build());

            // 收集所有错误
            List<String> errors = new ArrayList<>();
            for (Result<DeleteError> result : results) {
                try {
                    DeleteError error = result.get();
                    errors.add(error.objectName() + ": " + error.message());
                } catch (Exception e) {
                    errors.add("解析删除结果异常: " + e.getMessage());
                }
            }

            if (!errors.isEmpty()) {
                log.error("MinIO批量删除部分失败: {}", errors);
                throw new HisException("MinIO删除文件失败: " + String.join("; ", errors));
            }
        } catch (HisException e) {
            throw e;
        } catch (Exception e) {
            log.error("MinIO删除文件异常", e);
            throw new HisException("MinIO删除文件失败");
        }
    }

}

