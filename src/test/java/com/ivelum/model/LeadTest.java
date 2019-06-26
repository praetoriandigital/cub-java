package com.ivelum.model;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.CubException;
import com.ivelum.net.Params;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import org.junit.Test;


public class LeadTest extends CubModelBaseTest {
  @Test
  public void testReadInstance() throws CubException {
    Lead fixtureLead = (Lead) Cub.factory.fromString(getFixture("lead"));
    assertEquals(fixtureLead.id, "led_001");
    
    String apiKey = "apiKey";
    setGetMock(String.format("/leads/%s/", fixtureLead.id), "lead", 200, apiKey);
    Lead lead = Lead.get(fixtureLead.id, new Params(apiKey));
    
    assertEquals(fixtureLead.id, lead.id);
    assertEquals("email@email.com", lead.email);
    assertTrue(lead.production);
    assertEquals("127.0.0.1", lead.remoteIp);
    assertEquals("ste_001", lead.site.getId());
    assertNull(lead.source);
    assertEquals("http://localhost/form_page/", lead.url);
    assertEquals("John", lead.data.get("first_name").getAsString());
  }
  
  @Test
  public void testListAll() throws CubException {
    String apiKey = "apiKey";
    Date fromDate = null;
    try {
      DateFormat f = new SimpleDateFormat("yyyy-MM-dd");
      f.setTimeZone(TimeZone.getTimeZone("UTC"));
      fromDate = f.parse("2012-01-01");
    } catch (ParseException e) {
      fail("Data init failed");
    }
    
    
    setGetMock(
        "/leads/?created__gt=1325376000&count=2",
        "leads",
        200,
        apiKey);
    setGetMock(
        "/leads/?created__gt=1325376000&offset=2&count=2",
        "empty_list_response",
        200,
        apiKey);
    
    int offset = 0;
    int count = 2;
    int totalLeads = 0;
    int iterations = 0;
  
    Params params = new Params(apiKey);
    params.setCount(count);
  
    List<CubObject> result;
    do {
      result = Lead.list(fromDate, params);
      params.setOffset(offset+=count);
      totalLeads += result.size(); // for asserts
      iterations += 1; // for asserts
    } while (result.size() > 0);
  
    assertEquals(2, totalLeads);
    assertEquals(2, iterations);

  }
}