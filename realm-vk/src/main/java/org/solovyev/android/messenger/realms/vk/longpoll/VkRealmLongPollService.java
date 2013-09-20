package org.solovyev.android.messenger.realms.vk.longpoll;

import org.solovyev.android.http.HttpRuntimeIoException;
import org.solovyev.android.http.HttpTransactions;
import org.solovyev.android.messenger.longpoll.LongPollResult;
import org.solovyev.android.messenger.longpoll.RealmLongPollService;
import org.solovyev.android.messenger.realms.RealmException;
import org.solovyev.android.messenger.realms.vk.VkAccount;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * User: serso
 * Date: 6/24/12
 * Time: 12:19 AM
 */
public class VkRealmLongPollService implements RealmLongPollService {

	@Nonnull
	private final VkAccount realm;

	public VkRealmLongPollService(@Nonnull VkAccount realm) {
		this.realm = realm;
	}

	@Override
	public Object startLongPolling() throws RealmException {
		try {
			return HttpTransactions.execute(new VkGetLongPollServerHttpTransaction(realm));
		} catch (HttpRuntimeIoException e) {
			throw new RealmException(realm.getId(), e);
		} catch (IOException e) {
			throw new RealmException(realm.getId(), e);
		}
	}

	@Override
	public LongPollResult waitForResult(@Nullable Object longPollingData) throws RealmException {
		try {
			if (longPollingData instanceof LongPollServerData) {
				return HttpTransactions.execute(new VkGetLongPollingDataHttpTransaction((LongPollServerData) longPollingData));
			} else {
				return null;
			}
		} catch (HttpRuntimeIoException e) {
			throw new RealmException(realm.getId(), e);
		} catch (IOException e) {
			throw new RealmException(realm.getId(), e);
		}
	}
}
