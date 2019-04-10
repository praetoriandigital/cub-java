package com.ivelum.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.CubException;
import com.ivelum.exception.DeserializationException;
import com.ivelum.net.Params;
import java.util.List;
import org.junit.Test;


public class CountryTest extends CubModelBaseTest {

  @Test
  public void testListAndGet() throws CubException {
    Params params = new Params();
    params.setValue("order_by", "name");
    params.setCount(1);
    params.setOffset(0);
    List<CubObject> countries = Country.list(params);
    assertEquals(1, countries.size());

    Country country = (Country) countries.get(0);
    assertNull(country.getApiKey());

    assertTrue(country.name.startsWith("A"));

    params.setValue("order_by", "-name");
    countries = Country.list(params);
    assertEquals(1, countries.size());
    country = (Country) countries.get(0);
    assertNull(country.getApiKey());
    assertFalse(country.name.startsWith("A"));

    Country countryCopy = Country.get(country.id);

    assertEquals(countryCopy, country);

    countryCopy.name = "changed";
    countryCopy.code2 = "changed";
    countryCopy.reload();

    assertEquals(country.name, countryCopy.name);
    assertEquals(country.code2, countryCopy.code2);
    assertEquals(country.getApiKey(), countryCopy.getApiKey());
  }

  @Test
  public void testDeserialization() throws DeserializationException {
    String countryJsonStr = getFixture("country");
    Country country = (Country) Cub.factory.fromString(countryJsonStr);

    assertEquals("cry_123", country.id);
    assertEquals((Integer) 1, country.code);
    assertEquals("code2", country.code2);
    assertEquals("code3", country.code3);
    assertEquals("name", country.name);
  }
}