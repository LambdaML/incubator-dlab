/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.epam.dlab.auth.core;

import org.apache.directory.ldap.client.api.LdapConnection;
import org.apache.directory.ldap.client.api.LdapNetworkConnection;

import java.io.IOException;

public class SimpleConnection extends DlabLdapConnection {

	private final LdapNetworkConnection connection;

	public SimpleConnection(LdapNetworkConnection connection) {
		this.connection = connection;
	}

	@Override
	public LdapConnection getConnection() {
		return this.connection;
	}

	@Override
	public void close() throws IOException {
		if (connection != null) {
			connection.close();
		}

	}
}
