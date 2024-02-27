package com.vincle.foodManager;

import com.vincle.foodManager.api.DefaultApi;
import com.vincle.foodManager.model.Container;
import com.vincle.foodManager.model.Customer;
import com.vincle.foodManager.model.FoodType;
import com.vincle.foodManager.model.Status;
import com.vincle.foodManager.model.Uom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(DefaultSchedulerConfig.class)
public class FoodManagerSimulator implements ApplicationRunner {

    @Autowired
    DefaultSchedulerConfig defaultSchedulerConfig;

    private DefaultApi api;

    private static final Logger logger = LoggerFactory.getLogger(FoodManagerSimulator.class);

    public static void main(String[] args) {
        SpringApplication.run(FoodManagerSimulator.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("Application started with command-line arguments: {}", Arrays.toString(args.getSourceArgs()));
    }

    @EventListener
    private void init(ContextRefreshedEvent event) {
        logger.info("simulator init");
        if (defaultSchedulerConfig == null) {
            logger.error("no default config found");
        } else {
            logger.info("Default scheduler config found: " + defaultSchedulerConfig.toString());
        }
    }

    @Bean
    public DefaultApi getApi() {
        if (api == null)
            return new DefaultApi();
        return api;
    }


    @Bean
    public List<FoodType> getFoodTypeList() throws ApiException {
        return getApi().getFoodTypes();
    }

    @Bean
    public List<Customer> getCustomerList() throws ApiException {
        return getApi().getCustomers();
    }

    @Bean
    public List<Status> getStatusList() throws ApiException {
        return getApi().getStatuses();
    }

    @Bean
    public List<Uom> getUomList() throws ApiException {
        return getApi().getUoms();
    }

    @Bean
    public List<Container> getContainerList() throws ApiException {
        return getApi().getContainers();
    }

}
