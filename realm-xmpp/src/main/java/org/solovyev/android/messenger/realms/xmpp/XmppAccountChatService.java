package org.solovyev.android.messenger.realms.xmpp;

import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smackx.OfflineMessageManager;
import org.solovyev.android.messenger.chats.ApiChat;
import org.solovyev.android.messenger.chats.Chat;
import org.solovyev.android.messenger.chats.ChatMessage;
import org.solovyev.android.messenger.chats.AccountChatService;
import org.solovyev.android.messenger.entities.Entity;
import org.solovyev.android.messenger.realms.Account;
import org.solovyev.android.messenger.realms.RealmConnectionException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * User: serso
 * Date: 3/5/13
 * Time: 9:17 PM
 */
class XmppAccountChatService extends AbstractXmppRealmService implements AccountChatService {

	public XmppAccountChatService(@Nonnull XmppAccount realm, @Nonnull XmppConnectionAware connectionAware) {
		super(realm, connectionAware);
	}

	@Nonnull
	@Override
	public List<ChatMessage> getChatMessages(@Nonnull String realmUserId) throws RealmConnectionException {
		return doOnConnection(new XmppConnectedCallable<List<ChatMessage>>() {
			@Override
			public List<ChatMessage> call(@Nonnull Connection connection) throws RealmConnectionException, XMPPException {
				final OfflineMessageManager offlineManager = new OfflineMessageManager(connection);
				try {
					if (offlineManager.supportsFlexibleRetrieval()) {
						return XmppAccount.toMessages(getRealm(), offlineManager.getMessages());
					}
				} catch (XMPPException e) {
					// ok, not supported by server
				}

				return Collections.emptyList();
			}
		});
	}

	@Nonnull
	@Override
	public List<ChatMessage> getNewerChatMessagesForChat(@Nonnull String realmChatId, @Nonnull String realmUserId) {
		return Collections.emptyList();
	}

	@Nonnull
	@Override
	public List<ChatMessage> getOlderChatMessagesForChat(@Nonnull String realmChatId, @Nonnull String realmUserId, @Nonnull Integer offset) {
		return Collections.emptyList();
	}

	@Nonnull
	@Override
	public List<ApiChat> getUserChats(@Nonnull String realmUserId) {
		return Collections.emptyList();
	}

	@Nullable
	@Override
	public String sendChatMessage(@Nonnull Chat chat, @Nonnull ChatMessage message) throws RealmConnectionException {
		return doOnConnection(new MessengerSender(chat, message, getRealm()));
	}

	@Nonnull
	@Override
	public Chat newPrivateChat(@Nonnull final Entity realmChat, @Nonnull String realmUserId1, @Nonnull final String realmUserId2) throws RealmConnectionException {
		return doOnConnection(new XmppConnectedCallable<Chat>() {
			@Override
			public Chat call(@Nonnull Connection connection) throws RealmConnectionException, XMPPException {
				org.jivesoftware.smack.Chat smackChat = connection.getChatManager().createChat(realmUserId2, realmChat.getRealmEntityId(), new XmppMessageListener(getRealm(), realmChat));
				return XmppAccount.toApiChat(smackChat, Collections.<Message>emptyList(), getRealm()).getChat();
			}
		});
	}

	private static final class MessengerSender implements XmppConnectedCallable<String> {

		@Nonnull
		private final Chat chat;

		@Nonnull
		private final ChatMessage message;

		@Nonnull
		private final Account account;

		private MessengerSender(@Nonnull Chat chat, @Nonnull ChatMessage message, @Nonnull Account account) {
			this.chat = chat;
			this.message = message;
			this.account = account;
		}

		@Override
		public String call(@Nonnull Connection connection) throws RealmConnectionException, XMPPException {
			final ChatManager chatManager = connection.getChatManager();

			final Entity realmChat = chat.getEntity();
			org.jivesoftware.smack.Chat smackChat = chatManager.getThreadChat(realmChat.getRealmEntityId());
			if (smackChat == null) {
				// smack forget about chat ids after restart => need to create chat here
				smackChat = chatManager.createChat(chat.getSecondUser().getRealmEntityId(), realmChat.getRealmEntityId(), new XmppMessageListener(account, realmChat));
			} else {
				// todo serso: remove if unnecessary
				smackChat.addMessageListener(new XmppMessageListener(account, realmChat));
			}

			smackChat.sendMessage(message.getBody());

			return null;
		}
	}
}
