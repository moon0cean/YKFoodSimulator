```
******************************************************************************************************
__   ___   __  ______              _    _____ _                 _       _             
\ \ / / | / /  |  ___|            | |  /  ___(_)               | |     | |            
 \ V /| |/ /   | |_ ___   ___   __| |  \ `--. _ _ __ ___  _   _| | __ _| |_ ___  _ __ 
  \ / |    \   |  _/ _ \ / _ \ / _` |   `--. \ | '_ ` _ \| | | | |/ _` | __/ _ \| '__|
  | | | |\  \  | || (_) | (_) | (_| |  /\__/ / | | | | | | |_| | | (_| | || (_) | |   
  \_/ \_| \_/  \_| \___/ \___/ \__,_|  \____/|_|_| |_| |_|\__,_|_|\__,_|\__\___/|_|      
                                                                                                      
******************************************************************************************************
```

## Introduction

YK Food Simulator simulates CRUD api calls to Food Manager application through a configurable @Scheduler

The system has been implemented using the following stack of technologies:

* Spring Boot (Java)

## Pre-requisites
* JDK 17
* Docker. Refer to documentation to install
* Maven 
* Food Manager up and running

## Installation

* Build project
  - `mvn clean install`  
  - `docker build --tag=food-simulator:latest .` (required if used in docker-compose)

## Food Simulator

Once started, the application will start sending random api calls to Food Manager application, thus, it is required to have Food Manager up and running.
You can configure the scheduler via application properties (which can be overridden by environment variables) to control the simulator behavior:

* scheduler.minRequests: minimum requests to be triggered
* scheduler.maxRequests: maximum requests to be triggered
* scheduler.ttl: (not yet implemented)
* scheduler.interval: interval between each request batch in milliseconds
* scheduler.retry=3 ((not yet implemented))




