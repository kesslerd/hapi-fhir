package ca.uhn.fhir.jpa.search.helper;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.RuntimeResourceDefinition;
import ca.uhn.fhir.context.RuntimeSearchParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchParamHelper {

	@Autowired
	private FhirContext myFhirContext;


	public Collection<RuntimeSearchParam> getPatientSearchParamsForResourceType(String theResourceType) {
		RuntimeResourceDefinition runtimeResourceDefinition = myFhirContext.getResourceDefinition(theResourceType);
		Map<String, RuntimeSearchParam> searchParams = new HashMap<>();

		RuntimeSearchParam  patientSearchParam = runtimeResourceDefinition.getSearchParam("patient");
		if (patientSearchParam != null) {
			searchParams.put(patientSearchParam.getName(), patientSearchParam);

		}
		RuntimeSearchParam  subjectSearchParam = runtimeResourceDefinition.getSearchParam("subject");
		if (subjectSearchParam != null) {
			searchParams.put(subjectSearchParam.getName(), subjectSearchParam);
		}

		List<RuntimeSearchParam> compartmentSearchParams = getPatientCompartmentRuntimeSearchParams(runtimeResourceDefinition);
		compartmentSearchParams.forEach(param -> searchParams.put(param.getName(), param));

		return searchParams.values();
	}

	/**
	 * Search the resource definition for a compartment named 'patient' and return its related Search Parameter.
	 */
	public List<RuntimeSearchParam> getPatientCompartmentRuntimeSearchParams(RuntimeResourceDefinition runtimeResourceDefinition) {
		List<RuntimeSearchParam> patientSearchParam = new ArrayList<>();
		List<RuntimeSearchParam> searchParams = runtimeResourceDefinition.getSearchParamsForCompartmentName("Patient");
		return searchParams;
//		if (searchParams == null || searchParams.size() == 0) {
//			String errorMessage = String.format("Resource type [%s] is not eligible for this type of export, as it contains no Patient compartment, and no `patient` or `subject` search parameter", myResourceType);
//			throw new IllegalArgumentException(errorMessage);
//		} else if (searchParams.size() == 1) {
//			patientSearchParam = searchParams.get(0);
//		} else {
//			String errorMessage = String.format("Resource type [%s] is not eligible for Group Bulk export, as we are unable to disambiguate which patient search parameter we should be searching by.", myResourceType);
//			throw new IllegalArgumentException(errorMessage);
//		}
//		return patientSearchParam;
	}
}
