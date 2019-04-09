package com.ivelum.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.ivelum.Cub;
import com.ivelum.CubModelBaseTest;
import com.ivelum.exception.DeserializationException;

import java.util.List;

import org.junit.Test;


public class MemberTest extends CubModelBaseTest {
  @Test
  public void testDeserialization() throws DeserializationException {
    String memberResponse = getFixture("member");

    Member member = (Member) Cub.factory.fromString(memberResponse);

    assertFalse(member.isActive);
    assertFalse(member.isAdmin);
    assertFalse(member.isProfileEditable);
    assertEquals("notes", member.notes);
    assertEquals("personalId", member.personalId);
    assertFalse(member.user.isExpanded());
    assertEquals("usr_123", member.user.getId());
    assertNull(member.deleted);
    assertFalse(member.organization.isExpanded());
    assertEquals("org_123", member.organization.getId());

    List<ExpandableField<MemberPosition>> positions = member.positions;

    assertEquals(1, positions.size());
    assertFalse(positions.get(0).isExpanded());
    assertEquals("mpo_123", positions.get(0).getId());

    List<ExpandableField<GroupMember>> groupMembership = member.groupMembership;

    assertEquals(1, groupMembership.size());
    assertFalse(groupMembership.get(0).isExpanded());
    assertEquals("grm_123", groupMembership.get(0).getId());
  }

  @Test
  public void testDeserializationDeletedWebhook() throws DeserializationException {
    String memberResponse = getFixture("member_deleted");

    Member member = (Member) Cub.factory.fromString(memberResponse);
    assertTrue(member.deleted);
  }
}
