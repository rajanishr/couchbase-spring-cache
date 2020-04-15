package com.couchbase.client.spring.cache.wiring.xml;

import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import com.couchbase.mock.CouchbaseMock;

public class TestCouchEnv {

    public static CouchbaseEnvironment testEnv(CouchbaseMock couchbaseMock) {
        int httpPort = couchbaseMock.getHttpPort();
        int carrierPort = couchbaseMock.getCarrierPort("default");
        return DefaultCouchbaseEnvironment
                .builder()
                .bootstrapCarrierDirectPort(carrierPort)
                .bootstrapHttpDirectPort(httpPort)
                .socketConnectTimeout(10000)
                .connectTimeout(15000)
                .build();
    }

}
