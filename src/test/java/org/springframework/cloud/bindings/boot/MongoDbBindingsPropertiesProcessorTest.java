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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.bindings.Binding;
import org.springframework.cloud.bindings.Bindings;
import org.springframework.cloud.bindings.FluentMap;
import org.springframework.mock.env.MockEnvironment;

import java.nio.file.Paths;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.cloud.bindings.boot.MongoDbBindingsPropertiesProcessor.TYPE;

@DisplayName("MongoDB BindingsPropertiesProcessor")
final class MongoDbBindingsPropertiesProcessorTest {

    private final Bindings bindings = new Bindings(
            new Binding("test-name", Paths.get("test-path"),
                    new FluentMap()
                            .withEntry(Binding.TYPE, TYPE)
                            .withEntry("authentication-database", "test-authentication-database")
                            .withEntry("database", "test-database")
                            .withEntry("grid-fs-database", "test-grid-fs-database")
                            .withEntry("host", "test-host")
                            .withEntry("password", "test-password")
                            .withEntry("port", "test-port")
                            .withEntry("uri", "test-uri")
                            .withEntry("username", "test-username")
            )
    );

    private final MockEnvironment environment = new MockEnvironment();

    private final HashMap<String, Object> properties = new HashMap<>();

    @Test
    @DisplayName("contributes properties")
    void test() {
        new MongoDbBindingsPropertiesProcessor().process(environment, bindings, properties);
        assertThat(properties)
                .containsEntry("spring.data.mongodb.authentication-database", "test-authentication-database")
                .containsEntry("spring.data.mongodb.database", "test-database")
                .containsEntry("spring.data.mongodb.gridfs.database", "test-grid-fs-database")
                .containsEntry("spring.data.mongodb.host", "test-host")
                .containsEntry("spring.data.mongodb.password", "test-password")
                .containsEntry("spring.data.mongodb.port", "test-port")
                .containsEntry("spring.data.mongodb.uri", "test-uri")
                .containsEntry("spring.data.mongodb.username", "test-username");
    }

    @Test
    @DisplayName("can be disabled")
    void disabled() {
        environment.setProperty("org.springframework.cloud.bindings.boot.mongodb.enable", "false");

        new MongoDbBindingsPropertiesProcessor().process(environment, bindings, properties);

        assertThat(properties).isEmpty();
    }

}
