package com.ivelum.model;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.CubException;
import com.ivelum.net.Params;
import org.junit.Test;


public class ProductTest extends CubModelBaseTest {
  @Test
  public void testReadProduct() throws CubException {
    Product fitureProduct = (Product) Cub.factory.fromString(getFixture("product"));
    assertEquals(fitureProduct.id, "prd_001");
    
    String apiKey = "apiKey";
    setGetMock(String.format("/products/%s/", fitureProduct.id), "product", 200, apiKey);
    Product product = Product.get(fitureProduct.id, new Params(apiKey));
    
    assertEquals(fitureProduct.id, product.id);
    assertEquals("Product name", product.name);
    assertEquals(1, product.skus.size());
    assertEquals("sku_001", product.skus.get(0).getId());
    assertFalse(product.skus.get(0).isExpanded());
    assertTrue(product.isActive);
    assertEquals("good", product.type);
  }
  
  @Test
  public void testReadProductWithExpands() throws CubException {
    Product fixtureProduct = (Product) Cub.factory.fromString(getFixture("product_skus_expanded"));
    
    String apiKey = "apiKey";
    setGetMock(
        String.format("/products/%s/?expand=skus", fixtureProduct.id),
        "product_skus_expanded",
        200,
        apiKey);
    Params params = new Params(apiKey);
    params.setExpands("skus");
    Product product = Product.get(fixtureProduct.id, params);
    
    assertEquals(1, product.skus.size());
    assertTrue(product.skus.get(0).isExpanded());
    assertEquals((Integer)100, product.skus.get(0).getExpanded().price);
  }
}