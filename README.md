# mall学习教程的踩坑记录
该仓库的内容主要来自[mall-learning](https://github.com/macrozheng/mall-learning)项目，主要记录学习该仓库期间涉及的疑难点。

## 高版本SpringBoot配置Swagger2的问题
这一部分主要想解决的问题是更新SpringBoot版本至2.6.4+之后会导致Swagger2报空指针异常导致启动失败。
当前本仓库是[mall-tiny-swagger2](https://github.com/macrozheng/mall-learning/tree/master/mall-tiny-swagger2)的tiny版，只保留了部分内容来生成Swagger文档的示例。

首先需要增加一个Bean [Swagger2Config.java](./src/main/java/com/macro/mall/tiny/config/Swagger2Config.java)，因为我对Spring还不是很熟悉，从功能上看应该是在Spring Bean注册之后
修改Bean的一些属性，使得Spring框架可以发现和注册Swagger相关的Bean。代码如下:
```Java
@Bean
public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
    return new BeanPostProcessor() {
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if (bean instanceof WebMvcRequestHandlerProvider || bean instanceof WebFluxRequestHandlerProvider) {
                customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
            }
            return bean;
        }
        
        private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
            List<T> copy = mappings.stream().filter(mapping -> mapping.getPatternParser() == null).collect(Collectors.toList());
            mappings.clear();
            mappings.addAll(copy);
        }
        
        private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
            try {
                Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
                field.setAccessible(true);
                return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                throw new IllegalStateException(e);
            }
        }
    };
}
```
其次是修改[application.yml](./src/main/resources/application.yml)，其中server端口是8088，作者增加了mvc.pathmatch.matching-strategy.ANT_PATH_MATCHER来修正文档无法正常显示，虽然不清楚这个选项的原理和作用。
如果在本机ip启动项目，在查看Swagger时需要到http://localhost:8088/swagger-ui/而不是http://localhost:8088/swagger-ui.html，似乎新版本的Swagger位置不同了？

项目原作者的博客：[升级 SpringBoot 2.6.x 版本后，Swagger 没法用了。。。](https://blog.csdn.net/zhenghongcs/article/details/123652544)