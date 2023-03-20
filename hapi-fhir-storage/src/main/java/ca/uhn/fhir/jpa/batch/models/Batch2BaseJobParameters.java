/*-
 * #%L
 * HAPI FHIR Storage api
 * %%
 * Copyright (C) 2014 - 2023 Smile CDR, Inc.
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
package ca.uhn.fhir.jpa.batch.models;

import javax.annotation.Nonnull;

/**
 * Base parameters for StartJob as well as other requests
 */
public class Batch2BaseJobParameters {

	/**
	 * The id of the jobdefinition that is to be executed
	 */
	private final String myJobDefinitionId;

	/**
	 * If true, will search for existing jobs
	 * first and return any that have already completed or are inprogress/queued first
	 */
	private boolean myUseExistingJobsFirst;

	public Batch2BaseJobParameters(@Nonnull String theJobDefinitionId) {
		myJobDefinitionId = theJobDefinitionId;
	}

	public String getJobDefinitionId() {
		return myJobDefinitionId;
	}

	public boolean isUseExistingJobsFirst() {
		return myUseExistingJobsFirst;
	}

	public void setUseExistingJobsFirst(boolean theUseExistingJobsFirst) {
		myUseExistingJobsFirst = theUseExistingJobsFirst;
	}
}
