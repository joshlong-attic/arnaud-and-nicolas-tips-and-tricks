package bootiful.graalvm;

import org.springframework.aot.generate.GenerationContext;
import org.springframework.aot.generate.MethodReference;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotContribution;
import org.springframework.beans.factory.aot.BeanFactoryInitializationAotProcessor;
import org.springframework.beans.factory.aot.BeanFactoryInitializationCode;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.desktop.AppReopenedEvent;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

// https://tanzu.vmware.com/content/white-papers/spring-boot-3
// https://github.com/spring-tips/spring-boot-3-aot
// https://springone.io/history-of-spring

@ImportRuntimeHints(GraalvmApplication.MyHintsRegistrar.class)
@SpringBootApplication
@RegisterReflectionForBinding (ShoppingCart.class)
public class GraalvmApplication {

    static class MyHintsRegistrar implements RuntimeHintsRegistrar {

        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            hints.serialization().registerType(ShoppingCart.class);
//             hints.reflection().registerType(ShoppingCart.class ,
//                     MemberCategory.values()) ;
        }
    }

    @Bean
    static MyBeanFactoryPostProcessor myBeanFactoryPostProcessor() {
        return new MyBeanFactoryPostProcessor();
    }

    static class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

            for (var beanName : beanFactory.getBeanDefinitionNames()) {
                var bd = beanFactory.getBeanDefinition(beanName);
                System.out.println("found beanDefinition " + beanName);

            }
        }
    }

    @Bean
    static MyBeanFactoryInitilizationAotProcessor myBeanFactoryInitilizationAotProcessor() {
        return new MyBeanFactoryInitilizationAotProcessor();
    }

    static class MyBeanFactoryInitilizationAotProcessor
            implements BeanFactoryInitializationAotProcessor {

        @Override
        public BeanFactoryInitializationAotContribution processAheadOfTime(ConfigurableListableBeanFactory beanFactory) {
            var serializableTypes = new HashSet<String>();

            for (var beanName : beanFactory.getBeanDefinitionNames()) {
                var bd = beanFactory.getBeanDefinition(beanName);
                var clzzName = bd.getBeanClassName();
                if (StringUtils.hasText(clzzName)) {
                    try {
                        var clzz = Class.forName(clzzName);
                        if (Serializable.class.isAssignableFrom(clzz)) {
                            serializableTypes.add(clzz.getName());
                            System.out.println("adding serialization type " + clzz.getName());
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            return (generationContext, beanFactoryInitializationCode) -> {
                var hints = generationContext.getRuntimeHints();
                for (var serializableType : serializableTypes) {
                    hints.serialization().registerType(TypeReference.of(serializableType));
                }

                var gm = beanFactoryInitializationCode. getMethods().add("myMethod" , b -> b.addCode(
                        """
                            System.out.println("hello, world");     
                            """
                )) ;
                //beanFactoryInitializationCode.addInitializer(gm);

            };
        }
    }


    @Configuration
    static class MyConfig {

        @Bean
        Bar bar() {
            return new Bar();
        }

    }

    @Component
    static class Foo {


    }

    static class Bar {
    }

    public static void main(String[] args) {
        SpringApplication.run(GraalvmApplication.class, args);
    }

    static String note(int index) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return index == 0 ? Thread.currentThread().toString() : "";
    }


    @Bean
    ApplicationRunner aot(Bar bar, Foo foo) {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {

            }
        };
    }

    @Bean
    ApplicationRunner loomDemo() {
        return args -> {

            // merci José Paumard
            var exe = Executors.newVirtualThreadPerTaskExecutor();
            var observed = new ConcurrentSkipListSet<String>();

            var threads = IntStream
                    .range(0, 1000)
                    .mapToObj(index -> Thread
                            .ofVirtual()
                            .unstarted(() -> {
                                observed.add(note(index));
                                observed.add(note(index));
                                observed.add(note(index));
                                observed.add(note(index));
                            }))
                    .toList();

            for (var t : threads) t.start();

            for (var t : threads) t.join();

            System.out.println(observed);

        };
    }


}


@Controller
@ResponseBody
class SimpleHttpController {

    @GetMapping("/hello")
    Map<String, String> hello() {
        return Map.of("greeting", "bonjour");
    }
}


@Component
class ShoppingCart implements Serializable {

    ShoppingCart() {
        System.out.println("creating shopping cart");
    }
}