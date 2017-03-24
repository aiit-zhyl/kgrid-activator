package org.uofm.ot.executionStack.transferObjects;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.uofm.ot.executionStack.exception.OTExecutionStackException;

/**
 * Created by nggittle on 3/23/2017.
 */
public class CodeMetadataTest {

  private org.uofm.ot.executionStack.transferObjects.ioSpec ioSpec;
  ArrayList<ParamDescription> paramList;

  @Before
  public void setUp() throws Exception {
    paramList = new ArrayList<>();
    ioSpec = new ioSpec();
  }

  @Test(expected = OTExecutionStackException.class)
  public void givenAMissingParamThrowsError() throws Exception {
    paramList.add(new ParamDescription("rxcui2", DataType.STRING, 0, 2));
    ioSpec.setParams(paramList);
    ioSpec.setNoOfParams(paramList.size());

    Map<String, Object> inputs = new HashMap<>();
    inputs.put("rxcui", "1723222 2101 10767");
    assertEquals(" Input parameter rxcui2 is missing.", ioSpec.verifyInput(inputs));
  }

  @Test(expected = OTExecutionStackException.class)
  public void givenTwoMissingParamsThrowsErrorContainingBoth() throws Exception {
    paramList.add(new ParamDescription("rxcui2", DataType.STRING, 0, 2));
    paramList.add(new ParamDescription("name", DataType.STRING, 0, 2));
    ioSpec.setParams(paramList);
    ioSpec.setNoOfParams(paramList.size());

    Map<String, Object> inputs = new HashMap<>();
    inputs.put("rxcui", "1723222 2101 10767");
    inputs.put("rxcu", "1723222 2101 10767");
    assertEquals(" Input parameter rxcui2 is missing. Input parameter name is missing.", ioSpec.verifyInput(inputs));

  }

}