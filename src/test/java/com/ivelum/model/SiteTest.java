package com.ivelum.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.CubException;
import com.ivelum.exception.DeserializationException;

import org.junit.Test;


public class SiteTest extends CubModelBaseTest {
  @Test
  public void testGet() throws CubException {
    String id = "ste_K3NPzFHzcut9hlW7";
    Site site = Site.get(id);
    assertEquals(id, site.id);
    assertNotNull(site.domain);
  }

  @Test
  public void testDeserialization() throws DeserializationException {
    String siteJsonStr = getFixture("site");
    Site site = (Site) Cub.factory.fromString(siteJsonStr);

    assertEquals("domain", site.domain);
    assertEquals(1, site.tags.size());
    assertTrue(site.tags.contains("EMS"));
  }
}