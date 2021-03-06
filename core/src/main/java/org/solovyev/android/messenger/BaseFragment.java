package org.solovyev.android.messenger;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.github.rtyley.android.sherlock.roboguice.fragment.RoboSherlockFragment;
import com.google.inject.Inject;
import org.solovyev.android.tasks.TaskListeners;
import org.solovyev.android.view.ViewFromLayoutBuilder;
import roboguice.event.EventManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static org.solovyev.android.messenger.App.getTaskService;
import static org.solovyev.android.messenger.App.getTheme;

public abstract class BaseFragment extends RoboSherlockFragment {

	@Inject
	@Nonnull
	private MultiPaneManager multiPaneManager;

	@Inject
	@Nonnull
	private EventManager eventManager;

	private final int layoutResId;

	@Nonnull
	private Context themeContext;

	@Nonnull
	private final TaskListeners taskListeners = new TaskListeners(getTaskService());

	@Nonnull
	private final FragmentUi fragmentUi = new FragmentUi(this);

	public BaseFragment(int layoutResId) {
		this.layoutResId = layoutResId;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		themeContext = new ContextThemeWrapper(activity, App.getTheme().getContentThemeResId(isDialog()));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		fragmentUi.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View root = ViewFromLayoutBuilder.newInstance(layoutResId).build(themeContext);

		getMultiPaneManager().onCreatePane(this.getActivity(), container, root);

		root.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));

		return root;
	}

	@Override
	public void onViewCreated(View root, Bundle savedInstanceState) {
		super.onViewCreated(root, savedInstanceState);
		fragmentUi.onViewCreated();

		getMultiPaneManager().showTitle(getSherlockActivity(), this, getFragmentTitle());
	}

	@Override
	public void onPause() {
		taskListeners.removeAllTaskListeners();
		super.onPause();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		if (!wasViewCreated()) {
			fragmentUi.copyLastSavedInstanceState(outState);
		}

		fragmentUi.clearLastSavedInstanceState();
	}

	protected MessengerTheme.Icons getIcons() {
		return getTheme().getIcons(isDialog());
	}

	@Nullable
	protected abstract CharSequence getFragmentTitle();

	@Nonnull
	public MultiPaneManager getMultiPaneManager() {
		return multiPaneManager;
	}

	@Override
	public BaseFragmentActivity getSherlockActivity() {
		return (BaseFragmentActivity) super.getSherlockActivity();
	}

	protected boolean isDialog() {
		return getSherlockActivity().isDialog();
	}

	@Nonnull
	public Context getThemeContext() {
		return themeContext;
	}

	@Nonnull
	public TaskListeners getTaskListeners() {
		return taskListeners;
	}

	public boolean wasViewCreated() {
		return fragmentUi.wasViewCreated();
	}

	@Nonnull
	protected EventManager getEventManager() {
		return eventManager;
	}
}
