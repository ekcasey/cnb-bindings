/*
 * Copyright 2020 the original author or authors.
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

package org.springframework.cloud.bindings.boot;

import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.core.env.Environment;

import java.util.Map;

import static org.springframework.cloud.bindings.boot.Guards.isTypeEnabled;

/**
 * An implementation of {@link BindingsPropertiesProcessor} that detects {@link Binding}s of type: {@value TYPE}.
 */
public final class CassandraBindingsPropertiesProcessor {

    /**
     * The {@link Binding} type that this processor is interested in: {@value}.
     **/
    public static final String TYPE = "cassandra";

    public final static class Boot2 extends SpringBootVersionResolver implements BindingsPropertiesProcessor {

        private static final int BOOT_VERSION = 2;

        Boot2(int forcedVersion) {
            super(forcedVersion);
        }

        public Boot2() {
        }

        @Override
        public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
            if (!isTypeEnabled(environment, TYPE)) {
                return;
            }
            if (!isBootMajorVersionEnabled(BOOT_VERSION)) {
                return;
            }

            bindings.filterBindings(TYPE).forEach(binding -> {
                MapMapper map = new MapMapper(binding.getSecret(), properties);
                map.from("cluster-name").to("spring.data.cassandra.cluster-name");
                map.from("compression").to("spring.data.cassandra.compression");
                map.from("contact-points").to("spring.data.cassandra.contact-points");
                map.from("keyspace-name").to("spring.data.cassandra.keyspace-name");
                map.from("password").to("spring.data.cassandra.password");
                map.from("port").to("spring.data.cassandra.port");
                map.from("ssl").to("spring.data.cassandra.ssl");
                map.from("username").to("spring.data.cassandra.username");

                map.from("request.throttler.drain-interval").to("spring.data.cassandra.request.throttler.drain-interval");
                map.from("request.throttler.max-concurrent-requests").to("spring.data.cassandra.request.throttler.max-concurrent-requests");
                map.from("request.throttler.max-queue-size").to("spring.data.cassandra.request.throttler.max-queue-size");
                map.from("request.throttler.max-requests-per-second").to("spring.data.cassandra.request.throttler.max-requests-per-second");
            });
        }
    }

    /**
     * This is a special case for Boot 3.
     */
    public final static class Boot3 extends SpringBootVersionResolver implements BindingsPropertiesProcessor {

        private static final int BOOT_VERSION = 3;

        Boot3(int forcedVersion) {
            super(forcedVersion);
        }

        public Boot3() {
        }

        @Override
        public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
            if (!isTypeEnabled(environment, TYPE)) {
                return;
            }
            if (!isBootMajorVersionEnabled(BOOT_VERSION)) {
                return;
            }

            bindings.filterBindings(TYPE).forEach(binding -> {
                MapMapper map = new MapMapper(binding.getSecret(), properties);
                map.from("cluster-name").to("spring.cassandra.cluster-name");
                map.from("compression").to("spring.cassandra.compression");
                map.from("contact-points").to("spring.cassandra.contact-points");
                map.from("keyspace-name").to("spring.cassandra.keyspace-name");
                map.from("password").to("spring.cassandra.password");
                map.from("port").to("spring.cassandra.port");
                map.from("ssl").to("spring.cassandra.ssl");
                map.from("username").to("spring.cassandra.username");

                map.from("request.throttler.drain-interval").to("spring.cassandra.request.throttler.drain-interval");
                map.from("request.throttler.max-concurrent-requests").to("spring.cassandra.request.throttler.max-concurrent-requests");
                map.from("request.throttler.max-queue-size").to("spring.cassandra.request.throttler.max-queue-size");
                map.from("request.throttler.max-requests-per-second").to("spring.cassandra.request.throttler.max-requests-per-second");
            });
        }
    }

}
