package org.uofm.ot.executionStack.controller;

import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.uofm.ot.executionStack.ObjectTellerExecutionStackApplication;
import org.uofm.ot.executionStack.exception.OTExecutionStackException;
import org.uofm.ot.executionStack.transferObjects.KnowledgeObjectBuilder;
import org.uofm.ot.executionStack.transferObjects.KnowledgeObjectDTO;
import org.uofm.ot.executionStack.transferObjects.Result;

/**
 * Created by nggittle on 3/22/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ObjectTellerExecutionStackApplication.class})
public class ExecutionControllerTest {

  public static final String INPUT_SPEC_ONE_INPUT
      = "<rdf:RDF xmlns:ot='http://uofm.org/objectteller/' xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>"
      + "<rdf:Description rdf:about='http://uofm.org/objectteller/inputMessage'>"
      + "<ot:noofparams>1</ot:noofparams>"
      + "<ot:params>"
      + "<rdf:Seq>"
      + "<rdf:li>rxcui</rdf:li>"
      + "</rdf:Seq>"
      + "</ot:params>"
      + "</rdf:Description>"
      + "<rdf:Description rdf:about='http://uofm.org/objectteller/rxcui/'>"
      + "<ot:datatype>MAP</ot:datatype>"
      + "</rdf:Description>"
      + "</rdf:RDF>";

  public static final String INPUT_SPEC_TWO_INPUTS
      = "<rdf:RDF xmlns:ot='http://uofm.org/objectteller/' xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>"
      + "<rdf:Description rdf:about='http://uofm.org/objectteller/inputMessage'>"
      + "<ot:noofparams>2</ot:noofparams>"
      + "<ot:params>"
      + "<rdf:Seq>"
      + "<rdf:li>rxcui</rdf:li>"
      + "<rdf:li>rxcui2</rdf:li>"
      + "</rdf:Seq>"
      + "</ot:params>"
      + "</rdf:Description>"
      + "<rdf:Description rdf:about='http://uofm.org/objectteller/rxcui/'>"
      + "<ot:datatype>MAP</ot:datatype>"
      + "</rdf:Description>"
      + "</rdf:RDF>";

  private static final String OUTPUT_SPEC_RET_STR
      = "<rdf:RDF xmlns:ot='http://uofm.org/objectteller/' xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>"
      + "<rdf:Description rdf:about='http://uofm.org/objectteller/outputMessage'>"
      + "<ot:returntype>STRING</ot:returntype> </rdf:Description> </rdf:RDF>";

  private static final String OUTPUT_SPEC_RET_INT
      = "<rdf:RDF xmlns:ot='http://uofm.org/objectteller/' xmlns:rdf='http://www.w3.org/1999/02/22-rdf-syntax-ns#'>"
      + "<rdf:Description rdf:about='http://uofm.org/objectteller/outputMessage'>"
      + "<ot:returntype>INT</ot:returntype> </rdf:Description> </rdf:RDF>";

  @Autowired
  private ExecutionController exCon;

  @Rule
  public ExpectedException expectedEx = ExpectedException.none();

  @Test
  public void testCalculateWithNullInputsAndNullKO() throws Exception {
    expectedEx.expect(NullPointerException.class);
    exCon.validateAndExecute(null, null);
    assertNotNull(null);
  }

  @Test
  public void testCalculateWithEmptyKOandNullInputs() throws Exception {

    KnowledgeObjectDTO ko = new KnowledgeObjectDTO();
    expectedEx.expect(OTExecutionStackException.class);
    expectedEx.expectMessage("No inputs given.");
    assertNotNull(exCon.validateAndExecute(null, ko));
  }

  @Test
  public void testCalculateWithWrongInput() throws Exception {
    KnowledgeObjectDTO ko = new KnowledgeObjectBuilder()
        .inputMessage(INPUT_SPEC_ONE_INPUT)
        .outputMessage(OUTPUT_SPEC_RET_STR)
        .build();
    Map<String, Object> inputs = new HashMap<>();
    inputs.put("test", "test");
    expectedEx.expect(OTExecutionStackException.class);
    expectedEx.expectMessage("Error in converting RDF ioSpec for ko:  Input parameter rxcui is missing.");
    assertNotNull(exCon.validateAndExecute(inputs, ko));
  }

  @Test
  public void testCalculateWithCorrectInputsButNoPayload() {
    KnowledgeObjectDTO ko = new KnowledgeObjectBuilder()
        .inputMessage(INPUT_SPEC_ONE_INPUT)
        .outputMessage(OUTPUT_SPEC_RET_STR)
        .build();
    Map<String, Object> inputs = new HashMap<>();
    inputs.put("rxcui", "test");
    Result expectedResult = new Result(null);
    expectedResult.setSource(null);

    expectedEx.expect(OTExecutionStackException.class);
    expectedEx.expectMessage("Knowledge object payload content is NULL or empty");
    Result generatedResult = exCon.validateAndExecute(inputs, ko);

  }

  @Test
  public void testCalculateWithPythonSyntaxErrorToThrowEx() {
    KnowledgeObjectDTO ko = new KnowledgeObjectBuilder()
        .inputMessage(INPUT_SPEC_ONE_INPUT)
        .outputMessage(OUTPUT_SPEC_RET_STR)
        .payloadContent("def execute(a)\n    return str(a)") // Syntax error
        .payloadEngineType("PYTHON")
        .payloadFunctionName("execute")
        .build();

    Map<String, Object> inputs = new HashMap<>();
    inputs.put("rxcui", "1723222 2101 10767");
    Result expectedResult = new Result(null);
    expectedResult.setSource(null);

    expectedEx.expect(OTExecutionStackException.class);
    expectedEx.expectMessage(startsWith("Error while executing payload code"));
    Result generatedResult = exCon.validateAndExecute(inputs, ko);
  }

  @Test
  public void testCalculateWithTooManyInputsToThrowEx() {
    KnowledgeObjectDTO ko = new KnowledgeObjectBuilder()
        .inputMessage(INPUT_SPEC_ONE_INPUT)
        .outputMessage(OUTPUT_SPEC_RET_STR)
        .payloadContent("def execute(a):\n    return str(a)")
        .payloadEngineType("PYTHON")
        .payloadFunctionName("execute")
        .build();

    Map<String, Object> inputs = new HashMap<>();
    inputs.put("rxcui", "1723222 2101 10767");
    inputs.put("rxcui2", "1723222 2101 10767");
    Result expectedResult = new Result(null);
    expectedResult.setSource(null);

    expectedEx.expect(OTExecutionStackException.class);
    expectedEx.expectMessage("Error in converting RDF ioSpec for ko: Number of input parameters should be 1.");
    Result generatedResult = exCon.validateAndExecute(inputs, ko);
  }

  @Test
  public void testCalculateWithTooFewInputsToThrowEx() {
    KnowledgeObjectDTO ko = new KnowledgeObjectBuilder()
        .inputMessage(INPUT_SPEC_TWO_INPUTS)
        .outputMessage(OUTPUT_SPEC_RET_STR)
        .payloadContent("def execute(a):\n    return str(a)")
        .payloadEngineType("PYTHON")
        .payloadFunctionName("execute")
        .build();

    Map<String, Object> inputs = new HashMap<>();
    inputs.put("rxcui", "1723222 2101 10767");
    Result expectedResult = new Result(null);
    expectedResult.setSource(null);

    expectedEx.expect(OTExecutionStackException.class);
    expectedEx.expectMessage("Error in converting RDF ioSpec for ko: Number of input parameters should be 2. Input parameter rxcui2 is missing.");
    Result generatedResult = exCon.validateAndExecute(inputs, ko);
  }

  @Test
  public void testCalculateOneInputSuccess() {
    KnowledgeObjectDTO ko = new KnowledgeObjectBuilder()
        .inputMessage(INPUT_SPEC_ONE_INPUT)
        .outputMessage(OUTPUT_SPEC_RET_STR)
        .payloadContent("def execute(a):\n    return str(a)")
        .payloadEngineType("PYTHON")
        .payloadFunctionName("execute")
        .build();
    Map<String, Object> inputs = new HashMap<>();
    inputs.put("rxcui", "1723222");
    Result expectedResult = new Result(null);
    expectedResult.setResult("{u'rxcui': u'1723222'}");

    Result generatedResult = exCon.validateAndExecute(inputs, ko);
    assertEquals(expectedResult, generatedResult);
  }

  @Test
  public void testCalculateTwoInputsSuccess() {
    KnowledgeObjectDTO ko = new KnowledgeObjectBuilder()
        .inputMessage(INPUT_SPEC_TWO_INPUTS)
        .outputMessage(OUTPUT_SPEC_RET_STR)
        .payloadContent("def execute(a):\n    return str(a)")
        .payloadEngineType("PYTHON")
        .payloadFunctionName("execute")
        .build();
    Map<String, Object> inputs = new HashMap<>();
    inputs.put("rxcui", "1723222");
    inputs.put("rxcui2", "1723222");
    Result expectedResult = new Result(null);
    expectedResult.setResult("{u'rxcui': u'1723222', u'rxcui2': u'1723222'}");

    Result generatedResult = exCon.validateAndExecute(inputs, ko);
    assertEquals(expectedResult, generatedResult);
  }

  @Test
  public void testIntReturnedWhenExpectStringToThrowEx () {
    KnowledgeObjectDTO ko = new KnowledgeObjectBuilder()
        .inputMessage(INPUT_SPEC_ONE_INPUT)
        .outputMessage(OUTPUT_SPEC_RET_STR)
        .payloadContent("def execute(a):\n    return 1")
        .payloadEngineType("PYTHON")
        .payloadFunctionName("execute")
        .build();
    Map<String, Object> inputs = new HashMap<>();
    inputs.put("rxcui", "1723222");
    Result expectedResult = new Result(null);
    expectedResult.setResult("{u'rxcui': u'1723222'}");

    expectedEx.expect(OTExecutionStackException.class);
    expectedEx.expectMessage(startsWith("Type mismatch while converting python result to java. Check input spec and code payload java.lang.ClassCastException:"));

    Result generatedResult = exCon.validateAndExecute(inputs, ko);
  }

  @Test
  public void testStringReturnedWhenExpectIntToThrowEx() {
    KnowledgeObjectDTO ko = new KnowledgeObjectBuilder()
        .inputMessage(INPUT_SPEC_ONE_INPUT)
        .outputMessage(OUTPUT_SPEC_RET_INT)
        .payloadContent("def execute(a):\n    return str(a)")
        .payloadEngineType("PYTHON")
        .payloadFunctionName("execute")
        .build();
    Map<String, Object> inputs = new HashMap<>();
    inputs.put("rxcui", "1723222");
    Result expectedResult = new Result(null);
    expectedResult.setResult("{u'rxcui': u'1723222'}");

    expectedEx.expect(OTExecutionStackException.class);
    expectedEx.expectMessage(startsWith("Type mismatch while converting python result to java. Check input spec and code payload java.lang.ClassCastException:"));

    Result generatedResult = exCon.validateAndExecute(inputs, ko);
  }

  @Test
  public void testCalculateIntSuccess() {
    KnowledgeObjectDTO ko = new KnowledgeObjectBuilder()
        .inputMessage(INPUT_SPEC_ONE_INPUT)
        .outputMessage(OUTPUT_SPEC_RET_INT)
        .payloadContent("def execute(a):\n    return 42")
        .payloadEngineType("PYTHON")
        .payloadFunctionName("execute")
        .build();
    Map<String, Object> inputs = new HashMap<>();
    inputs.put("rxcui", "1723222");
    Result expectedResult = new Result(null);
    expectedResult.setResult("42");

    Result generatedResult = exCon.validateAndExecute(inputs, ko);
    assertEquals(expectedResult, generatedResult);
  }
}