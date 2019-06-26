package com.ivelum.model;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNull;

import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.CubException;
import com.ivelum.net.Params;
import org.junit.Test;


public class CustomerTest extends CubModelBaseTest {
  @Test
  public void testReadInstance() throws CubException {
    Customer fixtureCustomer = (Customer) Cub.factory.fromString(
        getFixture("customer_organization"));
    assertEquals(fixtureCustomer.id, "cus_001");
    
    String apiKey = "apiKey";
    setGetMock(
        String.format("/customers/%s/", fixtureCustomer.id), "customer_organization", 200, apiKey);
    Customer customer = Customer.get(fixtureCustomer.id, new Params(apiKey));
    
    assertEquals(fixtureCustomer.id, customer.id);
    assertNull(customer.user);
    assertFalse(customer.organization.isExpanded());
    assertEquals("org_001", customer.organization.getId());
  }
}