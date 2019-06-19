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
import com.ivelum.exception.CubException;
import com.ivelum.exception.DeserializationException;
import com.ivelum.net.Params;
import java.util.List;
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
  
  @Test
  public void testSearchGroupMember() throws CubException {
    String apiKey = "apiKey";
    setGetMock("/groupmembers/?member=mbr_123&group=grp_123", "groupmembers_search", 200, apiKey);
  
    Params params = new Params(apiKey);
    params.setValue("member", "mbr_123");
    params.setValue("group", "grp_123");
  
    List<CubObject> result = GroupMember.list(params);
  
    assert result.size() == 1;
  }
  
  @Test
  public void testCreateGroupMemberWhenAlreadyExists() throws CubException {
    String fixtureName = "validation_error_groupmember_already_exists";
    String apiKey = "apiKey";
    mockPostToListEndpoint(GroupMember.class, 400, fixtureName, apiKey);
    GroupMember gm = new GroupMember();
  
    gm.member = new ExpandableField<>("mbr_123");
    gm.group = new ExpandableField<>("grp_123");
    gm.isAdmin = false;
    try {
      gm.save(new Params(apiKey));
      fail("BadRequestException expected");
    } catch (BadRequestException e) {
      assertEquals(e.getApiError().description, "This group member already exists");
    }
  }
  
  @Test
  public void testGroupMemberCreation() throws CubException {
    
    GroupMember gm = new GroupMember();
    gm.member = new ExpandableField<>("mbr_123");
    gm.group = new ExpandableField<>("grp_123");
    gm.isAdmin = false;
  
    String apiKey = "apiKey";
    mockPostToListEndpoint(GroupMember.class, 200, "groupmember", apiKey);
    gm.save(new Params(apiKey));
    
    GroupMember gmFromFixture = (GroupMember) Cub.factory.fromString(getFixture("groupmember"));
    assertEquals(gm.id, gmFromFixture.id);
  }
}