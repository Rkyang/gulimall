# gulimall
guli mall

## 2023.1.6
### search服务启动报Caused by: java.lang.ClassNotFoundException: org.springframework.boot.context.properties.ConfigurationBeanFactoryMetadata
1. pom.xml中:
   1. 版本管理里增加`<spring-cloud.version>2021.0.3</spring-cloud.version>`
   2. 增加
   ```xml
   <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
   ```

