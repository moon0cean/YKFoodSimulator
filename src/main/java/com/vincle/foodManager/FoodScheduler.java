package com.vincle.foodManager;

import com.vincle.foodManager.api.DefaultApi;
import com.vincle.foodManager.model.Container;
import com.vincle.foodManager.model.Customer;
import com.vincle.foodManager.model.Food;
import com.vincle.foodManager.model.FoodType;
import com.vincle.foodManager.model.Status;
import com.vincle.foodManager.model.Uom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class FoodScheduler {
    @Autowired
    DefaultApi api;
    @Autowired
    List<FoodType> foodTypeList;
    @Autowired
    List<Container> containerList;
    @Autowired
    List<Status> statusList;
    @Autowired
    List<Uom> uomList;
    @Autowired
    List<Customer> customerList;
    @Autowired
    DefaultSchedulerConfig defaultSchedulerConfig;

    private static final Logger logger = LoggerFactory.getLogger(FoodScheduler.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private List<String> createdFoodIds = new ArrayList<>();

    // TODO: Figure out how to use a different ThreadPoolExecutor to spawn multiple threads per execution
    @Scheduled(fixedRateString = "${scheduler.interval}")
    public void simulateFoodCUD() {

        int numberOfRequests = ThreadLocalRandom.current().nextInt(defaultSchedulerConfig.getMinRequests(), defaultSchedulerConfig.getMaxRequests());
        String threadName = Thread.currentThread().getName();

        logger.info("Thread[{}] started batch of requests at {}, number of requests: {}", threadName, dateFormat.format(new Date()), numberOfRequests);
        for (int idx = 0; idx < numberOfRequests; idx++) {
            logger.info("Thread[{}] processing request[{}] ", threadName, idx);
            int randomFoodTypeIdx = ThreadLocalRandom.current().nextInt(foodTypeList.size()) % foodTypeList.size();
            int randomStatusIdx = ThreadLocalRandom.current().nextInt(statusList.size()) % statusList.size();
            int randomContainerIdx = ThreadLocalRandom.current().nextInt(containerList.size()) % containerList.size();
            int randomUomIdx = ThreadLocalRandom.current().nextInt(uomList.size()) % uomList.size();
            int randomCustomerIdx = ThreadLocalRandom.current().nextInt(customerList.size()) % customerList.size();

            FoodType foodType = foodTypeList.get(randomFoodTypeIdx);
            Food food = new Food();
            food.setName(foodType.getDescription() + "_" + UUID.randomUUID());
            food.setFoodTypeId(foodType);
            food.setContainerTypeId(containerList.get(randomContainerIdx));
            food.setCapacity(uomList.get(randomUomIdx));

            Status status = statusList.get(randomStatusIdx);
            if (createdFoodIds.isEmpty()) {
                status = new Status();
                status.statusId("CREATED");
            }
            food.setStatusId(status);

            final int i = idx;
            try {
                switch (status.getStatusId()) {
                    case "CREATED":
                        food.setCreatedBy(customerList.get(randomCustomerIdx));
                        api.createFoodAsync(food, new ApiCallback<>() {
                            @Override
                            public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                                logger.error("Thread[{}] failed creation request[{}] ", threadName, i);
                            }

                            @Override
                            public void onSuccess(String foodId, int statusCode, Map<String, List<String>> responseHeaders) {
                                createdFoodIds.add(foodId);
                                logger.info("Thread[{}] success creation request[{}] with foodId {}", threadName, i, foodId);
                            }

                            @Override
                            public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {
                                // FIXME: why is this invoked?
//                                logger.error("Thread[{}] uploadProgress unsupported [{}]: ", threadId, i);
                            }

                            @Override
                            public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {
                                // FIXME: why is this invoked?
//                                logger.error("Thread[{}] downloadProgress unsupported [{}]: ", threadId, i);
                            }
                        });
                        break;
                    case "UPDATED":
                        food.setLastUpdatedBy(customerList.get(randomCustomerIdx));
                        int randomFoodIdx = ThreadLocalRandom.current().nextInt(createdFoodIds.size()) % createdFoodIds.size();
                        api.updateFoodAsync(createdFoodIds.get(randomFoodIdx), food, new ApiCallback<>() {
                            @Override
                            public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                                logger.error("Thread[{}] failed update request[{}] ", threadName, i);
                            }

                            @Override
                            public void onSuccess(String foodId, int statusCode, Map<String, List<String>> responseHeaders) {
                                logger.info("Thread[{}] success update request[{}] with foodId {}", threadName, i, foodId);
                            }

                            @Override
                            public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {
                                // FIXME: why is this invoked?
//                                logger.error("Thread[{}] uploadProgress unsupported [{}]: ", threadId, i);
                            }

                            @Override
                            public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {
                                // FIXME: why is this invoked?
//                                logger.error("Thread[{}] downloadProgress unsupported [{}]: ", threadId, i);
                            }
                        });
                        break;
                    case "DELETED":
                        food.setLastUpdatedBy(customerList.get(randomCustomerIdx));
                        randomFoodIdx = ThreadLocalRandom.current().nextInt(createdFoodIds.size()) % createdFoodIds.size();
                        api.deleteFoodAsync(createdFoodIds.get(randomFoodIdx), new ApiCallback<>() {
                            @Override
                            public void onFailure(ApiException e, int statusCode, Map<String, List<String>> responseHeaders) {
                                logger.error("Thread[{}] failed delete request[{}] ", threadName, i);
                            }

                            @Override
                            public void onSuccess(String foodId, int statusCode, Map<String, List<String>> responseHeaders) {
                                // FIXME: fix concurrency issue
                                createdFoodIds.remove(randomFoodIdx);
                                logger.info("Thread[{}] success delete request[{}] with foodId {}", threadName, i, foodId);
                            }

                            @Override
                            public void onUploadProgress(long bytesWritten, long contentLength, boolean done) {
                                // FIXME: why is this invoked?
//                                logger.error("Thread[{}] uploadProgress unsupported [{}]: ", threadId, i);
                            }

                            @Override
                            public void onDownloadProgress(long bytesRead, long contentLength, boolean done) {
//                                logger.error("Thread[{}] downloadProgress unsupported [{}]: ", threadId, i);
                            }
                        });
                        break;
                    default:
                        logger.error("Unsupported status: " + status.getStatusId());
                        break;

                }
            } catch (ApiException e) {
                logger.error(e.getMessage());
            }
        }
        logger.info("Thread[{}] finished batch of requests at {}", threadName, dateFormat.format(new Date()));
    }

}
