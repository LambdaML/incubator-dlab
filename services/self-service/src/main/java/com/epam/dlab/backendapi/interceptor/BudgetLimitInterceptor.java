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

package com.epam.dlab.backendapi.interceptor;

import com.epam.dlab.auth.UserInfo;
import com.epam.dlab.backendapi.dao.BillingDAO;
import com.epam.dlab.exceptions.ResourceQuoteReachedException;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
public class BudgetLimitInterceptor implements MethodInterceptor {
	@Inject
	private BillingDAO billingDAO;

	@Override
	public Object invoke(MethodInvocation mi) throws Throwable {
		if (userQuoteReached(mi) || billingDAO.isBillingQuoteReached()) {
			final Method method = mi.getMethod();
			log.warn("Execution of method {} failed because of reaching resource limit quote", method.getName());
			throw new ResourceQuoteReachedException("Operation can not be finished. Resource quote is reached");
		} else {
			return mi.proceed();
		}
	}

	private Boolean userQuoteReached(MethodInvocation mi) {
		return Arrays.stream(mi.getArguments())
				.filter(arg -> arg.getClass().equals(UserInfo.class))
				.findAny()
				.map(u -> ((UserInfo) u).getName())
				.map(billingDAO::isUserQuoteReached)
				.orElse(Boolean.FALSE);
	}
}
