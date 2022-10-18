package com.rkyang.gulimallthirdparty;

import com.aliyun.oss.OSSClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;
import java.io.InputStream;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Autowired
    private OSSClient ossClient;

    @Test
    void testUploadOSSByAlibaba() {
        try {
            InputStream inputStream = new FileInputStream("C:\\Users\\RKyan\\Pictures\\main.jpg");
            ossClient.putObject("rkyang-gulimall", "test3.jpg", inputStream);
            System.out.println("使用alibaba cloud oss上传成功……");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void contextLoads() {
    }

}
