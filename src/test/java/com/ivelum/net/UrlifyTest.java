package com.ivelum.net;

import static org.junit.Assert.assertTrue;

import com.ivelum.exception.InvalidRequestException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;


public class UrlifyTest {
  @Test
  public void testFromParams() throws InvalidRequestException, ParseException {
    DateFormat format = new SimpleDateFormat("yyyy-mm-dd");
    format.setTimeZone(TimeZone.getTimeZone("UTC"));
    Params params = new Params();
    Date date = format.parse("2018-01-30");
    params.setValue("date", date);
    String result = Urlify.fromParams(params);
    assertTrue(result.contains("date=2018-01-30"));
  }
}