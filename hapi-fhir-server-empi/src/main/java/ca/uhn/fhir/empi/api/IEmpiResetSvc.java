package ca.uhn.fhir.empi.api;

/*-
 * #%L
 * HAPI FHIR - Enterprise Master Patient Index
 * %%
 * Copyright (C) 2014 - 2020 University Health Network
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

public interface IEmpiResetSvc {

	/**
	 * Given a resource type, delete the underlying EMPI links, and their related person objects.
	 *
	 * @param theResourceType The type of resources
	 *
	 * @return the count of deleted EMPI links
	 */
	long expungeAllEmpiLinksOfTargetType(String theResourceType);

	/**
	 * Delete all EMPI links, and their related Person objects.
	 *
	 *
	 * @return the count of deleted EMPI links
	 */
	long expungeAllEmpiLinks();
}
