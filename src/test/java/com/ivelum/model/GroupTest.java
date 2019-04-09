package com.ivelum.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.DeserializationException;
import org.junit.Test;


public class GroupTest extends CubModelBaseTest {
  @Test
  public void testDeserialization() throws DeserializationException {
    String groupJsonStr = getFixture("group");
    Group group = (Group) Cub.factory.fromString(groupJsonStr);

    assertNotNull(group.created);
    assertEquals("description", group.description);
    assertEquals("name", group.name);
    assertEquals("type", group.type);

    Group.ActiveMembers activeMembers = group.activeMembers;

    assertEquals((Integer) 1, activeMembers.invited);
    assertEquals((Integer) 1, activeMembers.joined);

    assertFalse(group.organization.isExpanded());
    assertEquals("org_123", group.organization.getId());
    assertNull(group.deleted);
  }

  @Test
  public void testDeserializationDeletedWebhook() throws DeserializationException {
    String groupJsonStr = getFixture("group__deleted");
    Group group = (Group) Cub.factory.fromString(groupJsonStr);
    assertTrue(group.deleted);
  }
}