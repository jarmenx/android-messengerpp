package org.solovyev.android.messenger.chats;

import com.google.inject.Inject;
import junit.framework.Assert;
import org.joda.time.DateTime;
import org.solovyev.android.messenger.AbstractMessengerTestCase;
import org.solovyev.android.messenger.entities.Entity;
import org.solovyev.android.messenger.messages.*;
import org.solovyev.android.messenger.realms.TestAccount;
import org.solovyev.android.messenger.realms.TestRealm;
import org.solovyev.android.messenger.users.UserDao;
import org.solovyev.android.messenger.users.Users;
import org.solovyev.common.text.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.solovyev.android.messenger.chats.Chats.newAccountChat;

public class SqliteChatDaoTest extends AbstractMessengerTestCase {

	@Inject
	private UserDao userDao;

	@Inject
	private ChatDao chatDao;

	@Inject
	private MessageDao messageDao;

	@Inject
	private TestRealm testRealmDef;

	@Inject
	private TestAccount testRealm;

	public void setUp() throws Exception {
		super.setUp();
		messageDao.deleteAll();
		chatDao.deleteAll();
	}

	public void testChatOperations() throws Exception {

		final ArrayList<AccountChat> chats = new ArrayList<AccountChat>();

		final Entity realmUser = testRealm.newUserEntity("01");
		final String userId = realmUser.getEntityId();

		userDao.create(Users.newEmptyUser(realmUser));

		chatDao.mergeChats(userId, chats);

		Assert.assertTrue(chatDao.readChatsByUserId(userId).isEmpty());

		final Entity realmChat1 = testRealm.newChatEntity("01");
		chats.add(newAccountChat(realmChat1, false));
		final Entity realmChat2 = testRealm.newChatEntity("02");
		chats.add(newAccountChat(realmChat2, false));
		final Entity realmChat3 = testRealm.newChatEntity("03");
		chats.add(newAccountChat(realmChat3, false));
		final Entity realmChat4 = testRealm.newChatEntity("04");
		chats.add(newAccountChat(realmChat4, false));
		chatDao.mergeChats(userId, chats);

		Chat chat = chatDao.read(realmChat4.getEntityId());
		Assert.assertNotNull(chat);
		Assert.assertEquals(realmChat4.getEntityId(), chat.getEntity().getEntityId());

		List<MutableMessage> messages = new ArrayList<MutableMessage>();
		messages.add(newMessage("01", false));
		messages.add(newMessage("02", false));
		messages.add(newMessage("03", true));
		messages.add(newMessage("04", true));
		messageDao.mergeMessages(realmChat4.getEntityId(), messages, false);

		messages = new ArrayList<MutableMessage>();
		messages.add(newMessage("07", true));
		messages.add(newMessage("08", false));
		messages.add(newMessage("09", true));
		messages.add(newMessage("06", true));
		messageDao.mergeMessages(realmChat1.getEntityId(), messages, false);

		messages = new ArrayList<MutableMessage>();
		messages.add(newMessage("10", true));
		messages.add(newMessage("11", true));
		messageDao.mergeMessages(realmChat2.getEntityId(), messages, false);

		final Map<Entity, Integer> actualUnreadChats = chatDao.getUnreadChats();
		assertFalse(actualUnreadChats.isEmpty());
		assertTrue(actualUnreadChats.containsKey(realmChat1));
		assertTrue(actualUnreadChats.containsKey(realmChat4));
		assertFalse(actualUnreadChats.containsKey(realmChat2));
		assertFalse(actualUnreadChats.containsKey(realmChat3));
		assertEquals(Integer.valueOf(2), actualUnreadChats.get(realmChat4));
		assertEquals(Integer.valueOf(1), actualUnreadChats.get(realmChat1));
	}

	private MutableMessage newMessage(String realmMessageId, boolean read) {
		final MutableMessage message = Messages.newMessage(testRealm.newMessageEntity(realmMessageId));
		message.setAuthor(testRealm.newUserEntity("user_01"));
		message.setRecipient(testRealm.newUserEntity("user_03"));
		message.setSendDate(DateTime.now());
		message.setBody(Strings.generateRandomString(10));
		message.setRead(read);
		return message;
	}

	@Override
	public void tearDown() throws Exception {
		messageDao.deleteAll();
		chatDao.deleteAll();
		super.tearDown();
	}
}
