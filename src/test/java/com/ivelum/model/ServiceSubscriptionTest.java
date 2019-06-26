package com.ivelum.model;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.CubException;
import com.ivelum.net.Params;
import java.util.List;
import org.junit.Test;



public class ServiceSubscriptionTest extends CubModelBaseTest {
  @Test
  public void testReadInstance() throws CubException {
    ServiceSubscription fixtureSS = (ServiceSubscription) Cub.factory.fromString(
        getFixture("serviceubscription__customer_org_expanded"));
    assertEquals(fixtureSS.id, "ssu_001");
    
    String apiKey = "apiKey";
    setGetMock(
        String.format("/servicesubscriptions/%s/?expand=customer__organization", fixtureSS.id),
        "serviceubscription__customer_org_expanded",
        200,
        apiKey);
    Params params = new Params(apiKey);
    params.setExpands("customer__organization");
    ServiceSubscription ss = ServiceSubscription.get(fixtureSS.id, params);
    
    assertEquals(fixtureSS.id, ss.id);
    assertEquals("coupon", ss.coupon);
    assertTrue(ss.verificationPending);
    assertFalse(ss.cancelAtPeriodEnd);
    assertEquals("cus_001", ss.customer.getId());
    assertTrue(ss.customer.isExpanded());
    assertTrue(ss.customer.getExpanded().organization.isExpanded());
    assertEquals("org_001", ss.customer.getExpanded().organization.getId());
  }
  
  @Test
  public void testSearchByOrganization() throws CubException {
    String apiKey = "apiKey";
    setGetMock(
        "/servicesubscriptions/?customer__organization=org_001",
        "servicesubscription_search",
        200,
        apiKey);
    
    List<CubObject> searchResult = ServiceSubscription.listByOrg("org_001", new Params(apiKey));
  
    assertEquals(1, searchResult.size());
    ServiceSubscription ss = (ServiceSubscription) searchResult.get(0);
    
    assertEquals("coupon", ss.coupon);
    assertFalse(ss.verificationPending);
    assertFalse(ss.cancelAtPeriodEnd);
    assertEquals("cus_001", ss.customer.getId());
    assertFalse(ss.customer.isExpanded());
  }
}