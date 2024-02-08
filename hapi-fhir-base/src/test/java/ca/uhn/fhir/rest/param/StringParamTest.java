package ca.uhn.fhir.rest.param;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IQueryParameterType;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

import static ca.uhn.fhir.rest.api.Constants.PARAMQUALIFIER_STRING_TEXT;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class StringParamTest {

	private static final Logger ourLog = (Logger) LoggerFactory.getLogger(StringParam.class);
	private ListAppender<ILoggingEvent> myListAppender = new ListAppender<>();

	@Mock
	private FhirContext myContext;

	@BeforeEach
	public void beforeEach(){
		myListAppender = new ListAppender<>();
		myListAppender.start();
		ourLog.addAppender(myListAppender);
	}

	@AfterEach
	public void afterEach(){
		myListAppender.stop();
	}

	@Test
	public void testEquals() {
		StringParam input = new StringParam("foo", true);

		assertThat(input.equals(input)).isTrue();
		assertThat(input.equals(null)).isFalse();
		assertThat(input.equals("")).isFalse();
		assertThat(input.equals(new StringParam("foo", false))).isFalse();
	}

	@Nested
	public class TestTextQualifier {

		@Test
		void setTextResetsOtherQualifiers() {
			// exact
			StringParam sp = new StringParam("the-value", true);
			sp.setText(false);
			assertThat(sp.isExact()).isTrue();
			sp.setText(true);
			assertThat(sp.isExact()).isFalse();

			// contains
			sp = new StringParam("the-value");
			sp.setContains(true);

			sp.setText(false);
			assertThat(sp.isContains()).isTrue();
			sp.setText(true);
			assertThat(sp.isContains()).isFalse();

			// exact
			sp = new StringParam("the-value");
			sp.setExact(true);

			sp.setText(false);
			assertThat(sp.isExact()).isTrue();
			sp.setText(true);
			assertThat(sp.isExact()).isFalse();

			// missing
			sp = new StringParam("the-value");
			sp.setMissing(true);

			sp.setText(false);
			assertThat(sp.getMissing()).isTrue();
			sp.setText(true);
			assertThat(sp.getMissing()).isNull();
		}

		@Test
		void doSetValueAsQueryToken_withCustomSearchParameterAndTextQualifier_enablesTextSearch() {
			StringParam sp = new StringParam("the-value");
			sp.doSetValueAsQueryToken(myContext, "value-string", PARAMQUALIFIER_STRING_TEXT, "yellow");
			assertTextQualifierSearchParameterIsValid(sp, "yellow");
		}

		@Test
		void doGetQueryParameterQualifier_withCustomSearchParameterAndTextQualifier_returnsTextQualifier() {
			StringParam sp = new StringParam("the-value");
			sp.doSetValueAsQueryToken(myContext, "value-string", PARAMQUALIFIER_STRING_TEXT, "yellow");

			assertThat(((IQueryParameterType) sp).getQueryParameterQualifier()).isEqualTo(PARAMQUALIFIER_STRING_TEXT);
		}
	}


	@Test
	public void doSetValueAsQueryToken_withCustomSearchParameterAndNicknameQualifier_enablesNicknameExpansion(){
		String customSearchParamName = "someCustomSearchParameter";
		StringParam stringParam = new StringParam();
		stringParam.doSetValueAsQueryToken(myContext, customSearchParamName, ":nickname", "John");
		assertNicknameQualifierSearchParameterIsValid(stringParam, "John");
		assertNicknameWarningLogged(true);
	}

	@ParameterizedTest
	@ValueSource(strings = {"name", "given"})
	public void doSetValueAsQueryToken_withPredefinedSearchParametersAndNicknameQualifier_enablesNicknameExpansion(String theSearchParameterName){
		StringParam stringParam = new StringParam();
		stringParam.doSetValueAsQueryToken(myContext, theSearchParameterName, ":nickname", "John");
		assertNicknameQualifierSearchParameterIsValid(stringParam, "John");
		assertNicknameWarningLogged(false);
	}

	private void assertNicknameQualifierSearchParameterIsValid(StringParam theStringParam, String theExpectedValue){
		assertThat(theStringParam.isNicknameExpand()).isTrue();
		assertThat(theStringParam.isExact()).isFalse();
		assertThat(theStringParam.isContains()).isFalse();
		assertThat(theStringParam.isText()).isFalse();
		assertThat(theStringParam.getValue()).isEqualTo(theExpectedValue);
	}

	private void assertTextQualifierSearchParameterIsValid(StringParam theStringParam, String theExpectedValue){
		assertThat(theStringParam.isNicknameExpand()).isFalse();
		assertThat(theStringParam.isExact()).isFalse();
		assertThat(theStringParam.isContains()).isFalse();
		assertThat(theStringParam.isText()).isTrue();
		assertThat(theStringParam.getValue()).isEqualTo(theExpectedValue);
	}

	private void assertNicknameWarningLogged(boolean theWasLogged){
		String expectedMessage = ":nickname qualifier was assigned to a search parameter other than one of the intended parameters \"name\" and \"given\"";
		Level expectedLevel = Level.DEBUG;
		List<ILoggingEvent> warningLogs = myListAppender
			.list
			.stream()
			.filter(event -> expectedMessage.equals(event.getFormattedMessage()))
			.filter(event -> expectedLevel.equals(event.getLevel()))
			.collect(Collectors.toList());

		if (theWasLogged) {
			assertThat(warningLogs.size()).isEqualTo(1);
		} else {
			assertThat(warningLogs.isEmpty()).isTrue();
		}
	}
	
}
