package com.ivelum.model;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;

import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.CubException;
import com.ivelum.net.Params;
import org.junit.Test;


public class PlanTest extends CubModelBaseTest {
  @Test
  public void testReadProduct() throws CubException {
    Plan fixturePlan = (Plan) Cub.factory.fromString(getFixture("plan"));
    assertEquals(fixturePlan.id, "pln_001");
    
    String apiKey = "apiKey";
    setGetMock(String.format("/plans/%s/", fixturePlan.id), "plan", 200, apiKey);
    Plan plan = Plan.get(fixturePlan.id, new Params(apiKey));
    
    assertEquals(fixturePlan.id, plan.id);
    assertEquals("description", plan.description);
    assertEquals((Integer) 1, plan.intervalCount);
    assertEquals((Integer) 100, plan.amount);
    assertEquals("month", plan.interval);
    assertFalse(plan.product.isExpanded());
    assertFalse(plan.requireVerification);
  }
}