package com.asset.management;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 企业核心资产调度管理系统 - 主启动类
 * 
 * @author 宋思泽
 * @version 1.0.0
 */
@SpringBootApplication
@MapperScan("com.asset.management.mapper")
public class AssetManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(AssetManagementApplication.class, args);
        System.out.println("\n=================================================");
        System.out.println("企业核心资产调度管理系统启动成功！");
        System.out.println("访问地址：http://localhost:8080/api");
        System.out.println("=================================================\n");
    }
}

