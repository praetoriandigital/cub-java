package com.ivelum.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.DeserializationException;

import org.junit.Test;


public class MemberPositionTest extends CubModelBaseTest {
  @Test
  public void testDeserialization() throws DeserializationException {
    String mpJsonStr = getFixture("memberposition");

    MemberPosition mp = (MemberPosition) Cub.factory.fromString(mpJsonStr);

    assertNotNull(mp.created);
    assertEquals("position", mp.position);
    assertEquals("unit", mp.unit);
    assertEquals((Integer) 1, mp.dayFrom);
    assertEquals((Integer) 1, mp.dayTo);
    assertEquals((Integer) 1, mp.monthFrom);
    assertEquals((Integer) 1, mp.monthTo);
    assertEquals((Integer) 1, mp.yearTo);
    assertEquals((Integer) 1, mp.yearFrom);

    assertFalse(mp.member.isExpanded());
    assertEquals("mbr_123", mp.member.getId());
  }
}
