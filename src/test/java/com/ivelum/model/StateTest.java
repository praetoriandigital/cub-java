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
  public void testListAndGet() throws CubException {
    Params params = new Params();
    params.setCount(1);
    params.setValue("order_by", "name");
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