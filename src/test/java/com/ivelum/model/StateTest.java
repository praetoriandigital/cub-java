package com.ivelum.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.CubException;
import com.ivelum.net.Params;
import java.util.List;

import org.junit.Test;


public class StateTest extends CubModelBaseTest {

  @Test
  public void testListByCountry() throws CubException {
    Params params = new Params();
    params.setCount(100);
    params.setValue("country", "cry_3zxJkF8kKdowkmRp"); // USA
    List<CubObject> states = State.list(params);
    assertEquals(60, states.size());
  }

  @Test
  public void testListAndGet() throws CubException {
    Params params = new Params();
    params.setCount(1);
    params.setValue("order_by", "name");
    // Using the utf8mb4_0900_ai_ci collation, states with
    // an accent mark at the beginning appear first.
    // Use a country where all states names consist of ascii letters.
    params.setValue("country__name", "United States");
    List<CubObject> states = State.list(params);

    Character first = ((State) states.get(0)).name.charAt(0);

    params.setValue("order_by", "-name");
    states = State.list(params);
    State lastState = (State) states.get(0);

    Country country = Country.get(lastState.country.getId());
    Character last = lastState.name.charAt(0);
    assertTrue(first < last);

    params.setCount(10).setExpands("country");
    params.setValue("country__name", country.name);

    states = State.list(params);

    // all countries in response related to the same country
    // can be unstable, better to use count as max of states in one country
    // but trying keep it simple and fast.
    for (CubObject obj : states) {
      State state = (State) obj;
      assertEquals(country, state.country.getExpanded());
    }
  }
}