/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.axis2.description;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

public class WSDL11ToAxisServiceBuilderTest extends TestCase {
    /**
     * Tests processing of an operation that declares multiple faults referring to the same message.
     * In this case, {@link WSDL11ToAxisServiceBuilder} must correctly populate the
     * {@link AxisMessage} object for both faults. In particular,
     * {@link AxisMessage#getElementQName()} must return consistent information. This is a
     * regression test for AXIS2-4533.
     * 
     * @throws Exception
     */
    public void testMultipleFaultsWithSameMessage() throws Exception {
        InputStream in = new FileInputStream("test-resources/wsdl/faults.wsdl");
        try {
            AxisService service = new WSDL11ToAxisServiceBuilder(in).populateService();
            AxisOperation operation = service.getOperation(new QName("urn:test", "test"));
            assertNotNull(operation);
            List<AxisMessage> faultMessages = operation.getFaultMessages();
            assertEquals(2, faultMessages.size());
            AxisMessage error1 = faultMessages.get(0);
            AxisMessage error2 = faultMessages.get(1);
            assertEquals("errorMessage", error1.getName());
            assertEquals("errorMessage", error2.getName());
            assertEquals(new QName("urn:test", "error"), error1.getElementQName());
            assertEquals(new QName("urn:test", "error"), error2.getElementQName());
        } finally {
            in.close();
        }
    }
    
    public void testHttpVerbInAxisBinding() throws Exception {
        InputStream in = new FileInputStream("test-resources/wsdl/simpleHttpBinding.wsdl");
        final String targetNamespace = "http://www.example.org";
        final QName serviceName = new QName(targetNamespace, "FooService");
        final String portName = "FooHttpGetPort";
        final QName operationName = new QName(targetNamespace, "getFoo");
        final String expectedHttpVerb = "GET";
        
        AxisService service = new WSDL11ToAxisServiceBuilder(in, serviceName, portName).populateService();
        AxisBinding binding = service.getEndpoint(portName).getBinding();
        String actualHttpVerb = (String)binding.getProperty(
            WSDL2Constants.ATTR_WHTTP_METHOD);
        
        // Sanity checks
        assertNotNull("The HTTP verb declared in the '" + binding.getName() +
            "' should not be null", actualHttpVerb);
        assertEquals("The HTTP verb declared in the '" + binding.getName() +
            "' was not the expected one", expectedHttpVerb, 
            actualHttpVerb.toUpperCase());
        
        // Building HTTP location table information
        AxisBindingOperation bindingOperation = 
            (AxisBindingOperation)binding.getChild(operationName);
        String httpLocation = (String)bindingOperation.getProperty(
            WSDL2Constants.ATTR_WHTTP_LOCATION);
        String httpLocationTableKey = getConstantFromHTTPLocation(
            httpLocation, expectedHttpVerb);
        
        Map httpLocationTable = (Map)binding.getProperty(
            WSDL2Constants.HTTP_LOCATION_TABLE);
        
        // Was the HTTP location table properly built?
        AxisOperation operation = bindingOperation.getAxisOperation();
        assertSame("The expected AxisBindingOperation is not present in the " +
            "HTTPLocationTable under the '" + httpLocationTableKey +"' key", 
            operation, httpLocationTable.get(httpLocationTableKey));
    }

    private String getConstantFromHTTPLocation(String httpLocation, String httpMethod) {
      if (httpLocation.charAt(0) != '?') {
          httpLocation = "/" + httpLocation;
      }
      int index = httpLocation.indexOf("{");
      if (index > -1) {
          httpLocation = httpLocation.substring(0, index);
      }
      return httpMethod + httpLocation;
    }

}
