/*-
 * #%L
 * HAPI FHIR JPA Model
 * %%
 * Copyright (C) 2014 - 2024 Smile CDR, Inc.
 * %%
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
 * #L%
 */
package ca.uhn.fhir.jpa.model.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Empty subclass to avoid bean wiring conflicts in projects that want to define a different implementation
public class SubscriptionSettings extends BaseSubscriptionSettings {
	private static final Logger ourLog = LoggerFactory.getLogger(SubscriptionSettings.class);

	static {
		// FIXME KHS
		ourLog.info("Initializing SubscriptionSettings");
	}

	public SubscriptionSettings() {
		super();
		// FIXME KHS remove
		ourLog.info("Initializing SubscriptionSettings");
	}
}
