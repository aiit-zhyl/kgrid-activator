package org.uofm.ot.activator.services;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.*;
import static org.mockito.Matchers.contains;

import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.uofm.ot.activator.ObjectTellerExecutionStackApplication;
import org.uofm.ot.activator.TestUtils;
import org.uofm.ot.activator.domain.KnowledgeObject;
import org.uofm.ot.activator.exception.OTExecutionStackException;
import org.uofm.ot.activator.domain.KnowledgeObjectBuilder;
import org.uofm.ot.activator.domain.Result;

/**
 * Created by nggittle on 3/22/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ObjectTellerExecutionStackApplication.class})
public class ActivationServiceTest {

  @Autowired
  private ActivationService activationService;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void testCalculateWithNullInputsAndNullKO() throws Exception {
    expectedEx.expect(NullPointerException.class);
    activationService.validateAndExecute(null, null);
    assertNotNull(null);
  }

  @Test
  public void testCalculateWithEmptyKOandNullInputs() throws Exception {

    KnowledgeObject ko = new KnowledgeObject();
    expectedEx.expect(OTExecutionStackException.class);
    expectedEx.expectMessage("No inputs given.");
    assertNotNull(activationService.validateAndExecute(null, ko));
  }

  @Test
  public void testCalculateWithWrongInput() throws Exception {
    KnowledgeObject ko = new KnowledgeObjectBuilder()
        .inputMessage(TestUtils.INPUT_SPEC_ONE_INPUT)
        .outputMessage(TestUtils.OUTPUT_SPEC_RET_STR)
        .build();
    Map<String, Object> inputs = new HashMap<>();
    inputs.put("test", "test");
    expectedEx.expect(OTExecutionStackException.class);
    expectedEx.expectMessage("Error in converting RDF ioSpec for ko:  Input parameter rxcui is missing.");
    assertNotNull(activationService.validateAndExecute(inputs, ko));
  }

  @Test
  public void testCalculateWithCorrectInputsButNoPayload() {
    KnowledgeObject ko = new KnowledgeObjectBuilder()
        .inputMessage(TestUtils.INPUT_SPEC_ONE_INPUT)
        .outputMessage(TestUtils.OUTPUT_SPEC_RET_STR)
        .build();
    Map<String, Object> inputs = new HashMap<>();
    inputs.put("rxcui", "test");
    Result expectedResult = new Result();
    expectedResult.setSource(null);

    expectedEx.expect(OTExecutionStackException.class);
    expectedEx.expectMessage("Knowledge object payload content is NULL or empty");
    Result generatedResult = activationService.validateAndExecute(inputs, ko);

  }

  @Test
  public void testCalculateWithSyntaxErrorToThrowEx() {
    KnowledgeObject ko = new KnowledgeObjectBuilder()
        .inputMessage(TestUtils.INPUT_SPEC_ONE_INPUT)
        .outputMessage(TestUtils.OUTPUT_SPEC_RET_STR)
        .payloadContent("function execute(a) return a}") // Syntax error
        .payloadEngineType("JAVASCRIPT")
        .payloadFunctionName("execute")
        .build();

    Map<String, Object> inputs = new HashMap<>();
    inputs.put("rxcui", "1723222 2101 10767");
    Result expectedResult = new Result();
    expectedResult.setSource(null);

    expectedEx.expect(OTExecutionStackException.class);
    expectedEx.expectMessage(contains("Error occurred while executing javascript code SyntaxError:"));
    Result generatedResult = activationService.validateAndExecute(inputs, ko);
  }

  @Test
  public void testCalculateWithTooManyInputsToThrowEx() {
    KnowledgeObject ko = new KnowledgeObjectBuilder()
        .inputMessage(TestUtils.INPUT_SPEC_ONE_INPUT)
        .outputMessage(TestUtils.OUTPUT_SPEC_RET_STR)
        .payloadContent(TestUtils.CODE)
        .payloadEngineType("JAVASCRIPT")
        .payloadFunctionName("execute")
        .build();

    Map<String, Object> inputs = new HashMap<>();
    inputs.put("rxcui", "1723222 2101 10767");
    inputs.put("rxcui2", "1723222 2101 10767");
    Result expectedResult = new Result();
    expectedResult.setSource(null);

    expectedEx.expect(OTExecutionStackException.class);
    expectedEx.expectMessage("Error in converting RDF ioSpec for ko: Number of input parameters should be 1.");
    Result generatedResult = activationService.validateAndExecute(inputs, ko);
  }

  @Test
  public void testCalculateWithTooFewInputsToThrowEx() {
    KnowledgeObject ko = new KnowledgeObjectBuilder()
        .inputMessage(TestUtils.INPUT_SPEC_TWO_INPUTS)
        .outputMessage(TestUtils.OUTPUT_SPEC_RET_STR)
        .payloadContent(TestUtils.CODE)
        .payloadEngineType("JAVASCRIPT")
        .payloadFunctionName("execute")
        .build();

    Map<String, Object> inputs = new HashMap<>();
    inputs.put("rxcui", "1723222 2101 10767");
    Result expectedResult = new Result();
    expectedResult.setSource(null);

    expectedEx.expect(OTExecutionStackException.class);
    expectedEx.expectMessage("Error in converting RDF ioSpec for ko: Number of input parameters should be 2. Input parameter rxcui2 is missing.");
    Result generatedResult = activationService.validateAndExecute(inputs, ko);
  }

  @Test
  public void testCalculateOneInputSuccess() {

    KnowledgeObject ko = new KnowledgeObjectBuilder()
        .inputMessage(TestUtils.INPUT_SPEC_ONE_INPUT)
        .outputMessage(TestUtils.OUTPUT_SPEC_RET_STR)
        .payloadContent(TestUtils.CODE)
        .payloadEngineType("JAVASCRIPT")
        .payloadFunctionName("execute")
        .build();
    Map<String, Object> inputs = new HashMap<>();
    inputs.put("rxcui", "1723222");
    Result expectedResult = new Result();
    expectedResult.setResult("{rxcui=1723222}");

    Result generatedResult = activationService.validateAndExecute(inputs, ko);
    assertEquals(expectedResult, generatedResult);
  }

  @Test
  public void testCalculateTwoInputsSuccess() {
    KnowledgeObject ko = new KnowledgeObjectBuilder()
        .inputMessage(TestUtils.INPUT_SPEC_TWO_INPUTS)
        .outputMessage(TestUtils.OUTPUT_SPEC_RET_STR)
        .payloadContent(TestUtils.CODE)
        .payloadEngineType("JAVASCRIPT")
        .payloadFunctionName("execute")
        .build();
    Map<String, Object> inputs = new HashMap<>();
    inputs.put("rxcui", "1723222");
    inputs.put("rxcui2", "1723222");
    Result expectedResult = new Result();
    expectedResult.setResult("{rxcui2=1723222, rxcui=1723222}");

    Result generatedResult = activationService.validateAndExecute(inputs, ko);
    assertEquals(expectedResult, generatedResult);
  }

  @Test
  public void testStringReturnedWhenExpectIntToThrowEx() {
    KnowledgeObject ko = new KnowledgeObjectBuilder()
        .inputMessage(TestUtils.INPUT_SPEC_ONE_INPUT)
        .outputMessage(TestUtils.OUTPUT_SPEC_RET_INT)
        .payloadContent(TestUtils.CODE)
        .payloadEngineType("JAVASCRIPT")
        .payloadFunctionName("execute")
        .build();
    Map<String, Object> inputs = new HashMap<>();
    inputs.put("rxcui", "1723222");
    Result expectedResult = new Result();
    expectedResult.setResult("{u'rxcui': u'1723222'}");

    expectedEx.expect(OTExecutionStackException.class);
    expectedEx.expectMessage(contains("Type mismatch while converting javascript result to java"));

    Result generatedResult = activationService.validateAndExecute(inputs, ko);
  }

  @Test
  public void testCalculateIntSuccess() {
    KnowledgeObject ko = new KnowledgeObjectBuilder()
        .inputMessage(TestUtils.INPUT_SPEC_ONE_INPUT)
        .outputMessage(TestUtils.OUTPUT_SPEC_RET_INT)
        .payloadContent("function execute(a){ return 42}")
        .payloadEngineType("JAVASCRIPT")
        .payloadFunctionName("execute")
        .build();
    Map<String, Object> inputs = new HashMap<>();
    inputs.put("rxcui", "1723222");
    Result expectedResult = new Result();
    expectedResult.setResult(new Integer(42));

    Result generatedResult = activationService.validateAndExecute(inputs, ko);
    assertEquals(expectedResult, generatedResult);
  }
}