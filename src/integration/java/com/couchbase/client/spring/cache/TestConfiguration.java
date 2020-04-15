/*
 * Copyright (C) 2015 Couchbase Inc., the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.couchbase.client.spring.cache;

import java.util.ArrayList;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.mock.Bucket.BucketType;
import com.couchbase.mock.BucketConfiguration;
import com.couchbase.mock.CouchbaseMock;

/**
 * Spring Configuration for basic integration tests.
 *
 * @author Simon Baslé
 */
@Configuration
public class TestConfiguration {

    @Bean
    public CouchbaseMock couchbaseMock() throws Exception {
        BucketConfiguration bucketConfiguration = new BucketConfiguration();
        bucketConfiguration.numNodes = 1;
        bucketConfiguration.numReplicas = 1;
        bucketConfiguration.numVBuckets = 1024;
        bucketConfiguration.name = "default";
        bucketConfiguration.type = BucketType.COUCHBASE;
        bucketConfiguration.password = "";
        ArrayList<BucketConfiguration> configList = new ArrayList<BucketConfiguration>();
        configList.add(bucketConfiguration);
        CouchbaseMock couchbaseMock = new CouchbaseMock(0, configList);
        couchbaseMock.start();
        couchbaseMock.waitForStartup();
        return couchbaseMock;
    }
    
    @Bean(name = "cluster")
    public Cluster couchbaseCluster() throws Exception {
        CouchbaseMock couchbaseMock = couchbaseMock();
        int httpPort = couchbaseMock.getHttpPort();
        int carrierPort = couchbaseMock.getCarrierPort("default");

        System.out.println("carrierPort="+carrierPort);
        
        Cluster cluster = CouchbaseCluster.create(DefaultCouchbaseEnvironment.builder()
                .bootstrapCarrierDirectPort(carrierPort)
                .bootstrapHttpDirectPort(httpPort)
                .socketConnectTimeout(10000)
                .connectTimeout(15000)
                .build(), "couchbase://127.0.0.1");
        return cluster;
    }
    
    @Bean(name = "bucket", destroyMethod = "close")
    public Bucket couchbaseBucket() throws Exception {
        return couchbaseCluster().openBucket("default");
    }
}
