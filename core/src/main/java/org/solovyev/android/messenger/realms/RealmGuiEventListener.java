package org.solovyev.android.messenger.realms;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import org.solovyev.android.fragments.AbstractFragmentReuseCondition;
import org.solovyev.android.messenger.MessengerFragmentActivity;
import org.solovyev.common.JPredicate;
import roboguice.event.EventListener;

import javax.annotation.Nonnull;

/**
 * User: serso
 * Date: 3/5/13
 * Time: 1:50 PM
 */
public class RealmGuiEventListener implements EventListener<RealmGuiEvent> {

	@Nonnull
	private static final String TAG = RealmGuiEventListener.class.getSimpleName();

	@Nonnull
	private final MessengerFragmentActivity activity;

	public RealmGuiEventListener(@Nonnull MessengerFragmentActivity activity) {
		this.activity = activity;
	}

	@Override
	public void onEvent(@Nonnull RealmGuiEvent event) {
		final Account account = event.getRealm();

		switch (event.getType()) {
			case realm_view_requested:
				handleRealmViewRequestedEvent(account);
				break;
			case realm_view_cancelled:
				handleRealmViewCancelledEvent(account);
				break;
			case realm_edit_requested:
				handleRealmEditRequestedEvent(account);
				break;
			case realm_edit_finished:
				handleRealmEditFinishedEvent(event);
				break;
		}
	}

	private void handleRealmViewCancelledEvent(@Nonnull Account account) {
		activity.getSupportFragmentManager().popBackStack();
	}

	private void handleRealmEditRequestedEvent(@Nonnull Account account) {
		final Bundle fragmentArgs = new Bundle();
		fragmentArgs.putString(BaseRealmConfigurationFragment.EXTRA_REALM_ID, account.getId());
		if (activity.isDualPane()) {
			activity.getMultiPaneFragmentManager().setSecondFragment(account.getRealmDef().getConfigurationFragmentClass(), fragmentArgs, null, BaseRealmConfigurationFragment.FRAGMENT_TAG, true);
		} else {
			activity.getMultiPaneFragmentManager().setMainFragment(account.getRealmDef().getConfigurationFragmentClass(), fragmentArgs, null, BaseRealmConfigurationFragment.FRAGMENT_TAG, true);
		}
	}

	private void handleRealmViewRequestedEvent(@Nonnull Account account) {
		if (activity.isDualPane()) {
			showRealmFragment(account, false);
			if (activity.isTriplePane()) {
				activity.getMultiPaneFragmentManager().emptifyThirdFragment();
			}
		} else {
			showRealmFragment(account, true);
		}
	}

	private void showRealmFragment(@Nonnull Account account, boolean firstPane) {
		final Bundle fragmentArgs = new Bundle();
		fragmentArgs.putString(MessengerRealmFragment.EXTRA_REALM_ID, account.getId());
		if (firstPane) {
			activity.getMultiPaneFragmentManager().setMainFragment(MessengerRealmFragment.class, fragmentArgs, RealmFragmentReuseCondition.forRealm(account), MessengerRealmFragment.FRAGMENT_TAG, true);
		} else {
			activity.getMultiPaneFragmentManager().setSecondFragment(MessengerRealmFragment.class, fragmentArgs, RealmFragmentReuseCondition.forRealm(account), MessengerRealmFragment.FRAGMENT_TAG, false);
		}
	}

	private void handleRealmEditFinishedEvent(@Nonnull RealmGuiEvent event) {
		final RealmGuiEventType.FinishedState state = (RealmGuiEventType.FinishedState) event.getData();
		assert state != null;
		switch (state) {
			case back:
				activity.getMultiPaneFragmentManager().goBack();
				break;
			case removed:
				activity.getMultiPaneFragmentManager().goBackTillStart();
				if (activity.isDualPane()) {
					activity.getMultiPaneFragmentManager().emptifySecondFragment();
				}
				break;
			case status_changed:
				// do nothing as we can change state only from realm info fragment and that is OK
				break;
			case saved:
				activity.getMultiPaneFragmentManager().goBackTillStart();
				break;
		}
	}

    /*
	**********************************************************************
    *
    *                           STATIC
    *
    **********************************************************************
    */

	/**
	 * Fragment will be reused if it's instance of {@link MessengerRealmFragment} and
	 * contains same realm as one passed in constructor
	 */
	private static class RealmFragmentReuseCondition extends AbstractFragmentReuseCondition<MessengerRealmFragment> {

		@Nonnull
		private final Account account;

		private RealmFragmentReuseCondition(@Nonnull Account account) {
			super(MessengerRealmFragment.class);
			this.account = account;
		}

		@Nonnull
		public static JPredicate<Fragment> forRealm(@Nonnull Account account) {
			return new RealmFragmentReuseCondition(account);
		}

		@Override
		protected boolean canReuseFragment(@Nonnull MessengerRealmFragment fragment) {
			return account.equals(fragment.getAccount());
		}
	}
}
