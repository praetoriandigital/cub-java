package com.ivelum.model;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.CubException;
import com.ivelum.net.Params;
import org.junit.Test;



public class SkuTest extends CubModelBaseTest {
  @Test
  public void testReadItem() throws CubException {
    Sku fixtureSku = (Sku) Cub.factory.fromString(getFixture("sku"));
    assertEquals(fixtureSku.id, "sku_001");
    String apiKey = "apiKey";
    setGetMock(String.format("/skus/%s/", fixtureSku.id), "sku", 200, apiKey);
  
    Sku sku = Sku.get(fixtureSku.id, new Params(apiKey));
  
    assertEquals(sku.id, fixtureSku.id);
    assertTrue(sku.isActive);
    assertEquals(sku.price, (Integer)100);
    assertEquals(sku.product.getId(), "prd_001");
    assertFalse(sku.product.isExpanded());
  }
}