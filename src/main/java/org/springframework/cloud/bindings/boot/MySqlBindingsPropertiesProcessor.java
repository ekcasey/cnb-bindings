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
 *
 * @see <a href="https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html">JDBC URL Format</a>
 */
public final class MySqlBindingsPropertiesProcessor implements BindingsPropertiesProcessor {

    /**
     * The {@link Binding} type that this processor is interested in: {@value}.
     **/
    public static final String TYPE = "mysql";

    /**
     * MySQL connection protocol constant.
     */
    private static final String MYSQL_PROTOCOL = "mysql";
    
    /**
     *  MariaDB connection protocol constant. 
     */
    private static final String MARIADB_PROTOCOL = "mariadb";
    
    @Override
    public void process(Environment environment, Bindings bindings, Map<String, Object> properties) {
        if (!isTypeEnabled(environment, TYPE)) {
            return;
        }

        bindings.filterBindings(TYPE).forEach(binding -> {
            MapMapper map = new MapMapper(binding.getSecret(), properties);

            //jdbc properties
            map.from("username").to("spring.datasource.username");
            map.from("password").to("spring.datasource.password");
            map.from("host", "port", "database").to("spring.datasource.url",
                    (host, port, database) -> String.format("jdbc:%s://%s:%s/%s", evalProtocol(), host, port, database));

            // jdbcURL takes precedence
            map.from("jdbc-url").to("spring.datasource.url");

            try {
                Class.forName("org.mariadb.jdbc.Driver", false, getClass().getClassLoader());
                properties.put("spring.datasource.driver-class-name", "org.mariadb.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                try {
                    Class.forName("com.mysql.cj.jdbc.Driver", false, getClass().getClassLoader());
                    properties.put("spring.datasource.driver-class-name", "com.mysql.cj.jdbc.Driver");
                } catch (ClassNotFoundException ignored) {
                }
            }

            //r2dbc properties
            map.from("password").to("spring.r2dbc.password");
            map.from("host", "port", "database").to("spring.r2dbc.url",
                    (host, port, database) -> String.format("r2dbc:%s://%s:%s/%s", evalProtocol(), host, port, database));
            map.from("username").to("spring.r2dbc.username");

            // r2dbcURL takes precedence
            map.from("r2dbc-url").to("spring.r2dbc.url");
        });
    }
    
    private String evalProtocol()
    {
    	// Default to "mysql"
    	String connectionProtocol = MYSQL_PROTOCOL;
    	
    	/* Starting with Spring Boot 2.7.0, the previous MySQL r2dbc driver is no longer supported and 
    	 * documentation suggests using the MariaDB R2DBC driver as an alternative.  Some versions
    	 * of the MariaDB R2DBC driver do not support "mysql" as part of the connection
    	 * protocol; "mariadb" should be used instead when the MariaDB R2DBC driver class is on
    	 * the classpath.
    	 */
    	
    	try {
    		Class.forName("org.mariadb.r2dbc.MariadbConnection");
    		connectionProtocol = MARIADB_PROTOCOL;
    	}
    	catch (ClassNotFoundException ignored) {
        }
    	
    	return connectionProtocol; 
    }

}
