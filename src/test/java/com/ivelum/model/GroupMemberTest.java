package com.ivelum.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.BadRequestException;
import com.ivelum.exception.DeserializationException;
import org.junit.Test;


public class GroupMemberTest extends CubModelBaseTest {
  @Test
  public void testDeserialization() throws DeserializationException {
    String gmJsonStr = getFixture("groupmember");
    GroupMember gm = (GroupMember) Cub.factory.fromString(gmJsonStr);

    assertNull(gm.getApiKey());
    assertNotNull(gm.created);
    assertFalse(gm.isAdmin);
    assertFalse(gm.member.isExpanded());
    assertEquals("mbr_123", gm.member.getId());

    assertFalse(gm.group.isExpanded());
    assertEquals("grp_123", gm.group.getId());
    assertNull(gm.deleted);
  }

  @Test
  public void testDeserializationDeletedWebhook() throws DeserializationException {
    String gmJsonStr = getFixture("groupmember_deleted");
    GroupMember gm = (GroupMember) Cub.factory.fromString(gmJsonStr);

    assertTrue(gm.deleted);
  }

  @Test
  public void testParamsAreRequired() {
    try {
      GroupMember.list(null);
      fail("GroupMember.list BadRequestException was not thrown");
    } catch (BadRequestException e) {
      assertNull(e.getApiError().params);
      assertTrue(e.getApiError().description.contains("required"));
    } catch (Exception e) {
      e.printStackTrace();
      fail("Unexpected exception");
    }
  }
}