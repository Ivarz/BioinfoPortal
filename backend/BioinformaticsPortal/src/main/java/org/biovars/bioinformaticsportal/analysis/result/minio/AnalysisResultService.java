package org.biovars.bioinformaticsportal.analysis.result.minio;

import io.minio.ListObjectsArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.minio.MinioClient;
import io.minio.GetObjectArgs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalysisResultService {

    private static final Logger logger = LoggerFactory.getLogger(AnalysisResultService.class);

    private final MinioClient minioClient;


    @Value("${minio.bucket}")
    private String bucket;

    AnalysisResultService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public List<AnalysisResultObjectDTO> listObjects(String userId,
                                                     String analysisId) throws ServerException,
            InsufficientDataException,
            ErrorResponseException,
            IOException,
            NoSuchAlgorithmException,
            InvalidKeyException,
            InvalidResponseException,
            XmlParserException,
            InternalException {
        var result = new ArrayList();
        var objs = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(bucket)
                        .prefix(userId + "/" + analysisId)
                        .recursive(true)
                        .build()
        );
        for (var obj : objs) {
            var object = obj.get();
            System.out.println(object.objectName());
            Path p = Paths.get(object.objectName());
            String fileName = p.getFileName().toString();

            result.add(new AnalysisResultObjectDTO(analysisId, userId, object.objectName(), fileName));
        }
        return result;
    }

    public void downloadFile(String userId, String analysisId, String fileName, HttpServletResponse response) throws Exception {
        String objectName = userId + "/" + analysisId + "/" + fileName;
        try (InputStream stream = minioClient.getObject(
                GetObjectArgs.builder().bucket(bucket).object(objectName).build())) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=" + objectName);
            stream.transferTo(response.getOutputStream());
        } catch (Exception e) {
            throw new Exception("Error while downloading file from MinIO", e);
        }
    }

    public void deleteAnalysis(String userId, String analysisId, HttpServletResponse response) {
        String dirName = userId + "/" + analysisId;
        try {
            var objs = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucket)
                            .prefix(userId + "/" + analysisId)
                            .recursive(true)
                            .build()
            );
            for (var obj : objs) {
                var o = obj.get();
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucket)
                                .object(o.objectName())
                                .build());
            }
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(dirName)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
