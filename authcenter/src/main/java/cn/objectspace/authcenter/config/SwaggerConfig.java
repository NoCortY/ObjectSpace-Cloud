package cn.objectspace.authcenter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
* @Description: Swagger配置类
* @Author: NoCortY
* @Date: 2019/12/20
*/
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket userApi(){
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("cn.objectspace.authcenter.web.controller"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder().title("ObjectSpace个人网站通用接口开放平台")
                .description("此接口开放平台属于ObjectSpace个人网站所有,并提供部分通用接口供开发者使用")
                .license("个人博客:Object's Blog").licenseUrl("http://blog.objectspace.cn")
                .version("1.0").build();
    }
}
