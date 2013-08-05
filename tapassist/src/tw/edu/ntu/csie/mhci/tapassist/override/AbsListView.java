/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tw.edu.ntu.csie.mhci.tapassist.override;

import java.lang.reflect.Field;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Debug;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;

public abstract class AbsListView extends android.widget.AbsListView {

	public AbsListView(Context context) {
		super(context);
	}

	@SuppressWarnings("unchecked")
	private <T> T getSupperclassField(String name, Class<T> cls) {
		try {
			Field field = AbsListView.class.getSuperclass().getDeclaredField(name);
			return  (T) field.get(this);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private void setValueToSupperclassField(String name, Object value) {
		try {
			Field field = AbsListView.class.getSuperclass().getDeclaredField(name);
			field.set(this, value);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private void initAbsListView() {
		// Setting focusable in touch mode will set the focusable property to
		// true
		setClickable(true);
		setFocusableInTouchMode(true);
		setWillNotDraw(false);
		setAlwaysDrawnWithCacheEnabled(false);
		setScrollingCacheEnabled(true);

		/**
		 * reflection 
		 * @author ggm
		 */
		Context mContext = getSupperclassField("mContext", Context.class);
		final ViewConfiguration configuration = ViewConfiguration.get(mContext);

		setValueToSupperclassField("mTouchSlop", configuration.getScaledTouchSlop());
		setValueToSupperclassField("mMinimumVelocity",configuration.getScaledMinimumFlingVelocity());
		setValueToSupperclassField("mMaximumVelocity",configuration.getScaledMaximumFlingVelocity());
		setValueToSupperclassField("mOverscrollDistance",configuration.getScaledOverscrollDistance());
		setValueToSupperclassField("mOverflingDistance",configuration.getScaledOverflingDistance());
		setValueToSupperclassField("mDensityScale",getContext().getResources().getDisplayMetrics().density);
		
	}
	
	/*
	//FIXME(ggm)
	void keyPressed() {
		if (!isEnabled() || !isClickable()) {
			return;
		}

		Drawable selector = mSelector;
		Rect selectorRect = mSelectorRect;
		if (selector != null && (isFocused() || touchModeDrawsInPressedState())
				&& !selectorRect.isEmpty()) {

			final View v = getChildAt(mSelectedPosition - mFirstPosition);

			if (v != null) {
				if (v.hasFocusable())
					return;
				v.setPressed(true);
			}
			setPressed(true);

			final boolean longClickable = isLongClickable();
			Drawable d = selector.getCurrent();
			if (d != null && d instanceof TransitionDrawable) {
				if (longClickable) {
					((TransitionDrawable) d).startTransition(ViewConfiguration
							.getLongPressTimeout());
				} else {
					((TransitionDrawable) d).resetTransition();
				}
			}
			if (longClickable && !mDataChanged) {
				if (mPendingCheckForKeyLongPress == null) {
					mPendingCheckForKeyLongPress = new CheckForKeyLongPress();
				}
				mPendingCheckForKeyLongPress.rememberWindowAttachCount();
				postDelayed(mPendingCheckForKeyLongPress,
						ViewConfiguration.getLongPressTimeout());
			}
		}
	}

	//FIXME(ggm)
	final class CheckForTap implements Runnable {
		public void run() {
			if (mTouchMode == TOUCH_MODE_DOWN) {
				mTouchMode = TOUCH_MODE_TAP;
				final View child = getChildAt(mMotionPosition - mFirstPosition);
				if (child != null && !child.hasFocusable()) {
					mLayoutMode = LAYOUT_NORMAL;

					if (!mDataChanged) {
						child.setPressed(true);
						setPressed(true);
						layoutChildren();
						positionSelector(mMotionPosition, child);
						refreshDrawableState();

						final int longPressTimeout = ViewConfiguration
								.getLongPressTimeout();
						final boolean longClickable = isLongClickable();

						if (mSelector != null) {
							Drawable d = mSelector.getCurrent();
							if (d != null && d instanceof TransitionDrawable) {
								if (longClickable) {
									((TransitionDrawable) d)
											.startTransition(longPressTimeout);
								} else {
									((TransitionDrawable) d).resetTransition();
								}
							}
						}

						if (longClickable) {
							if (mPendingCheckForLongPress == null) {
								mPendingCheckForLongPress = new CheckForLongPress();
							}
							mPendingCheckForLongPress
									.rememberWindowAttachCount();
							postDelayed(mPendingCheckForLongPress,
									longPressTimeout);
						} else {
							mTouchMode = TOUCH_MODE_DONE_WAITING;
						}
					} else {
						mTouchMode = TOUCH_MODE_DONE_WAITING;
					}
				}
			}
		}
	}

	//FIXME(ggm)
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (!isEnabled()) {
			// A disabled view that is clickable still consumes the touch
			// events, it just doesn't respond to them.
			return isClickable() || isLongClickable();
		}

		if (mPositionScroller != null) {
			mPositionScroller.stop();
		}

		if (!mIsAttached) {
			// Something isn't right.
			// Since we rely on being attached to get data set change
			// notifications,
			// don't risk doing anything where we might try to resync and find
			// things
			// in a bogus state.
			return false;
		}

		if (mFastScroller != null) {
			boolean intercepted = mFastScroller.onTouchEvent(ev);
			if (intercepted) {
				return true;
			}
		}

		final int action = ev.getAction();

		View v;

		initVelocityTrackerIfNotExists();
		mVelocityTracker.addMovement(ev);

		switch (action & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN: {
			switch (mTouchMode) {
			case TOUCH_MODE_OVERFLING: {
				mFlingRunnable.endFling();
				if (mPositionScroller != null) {
					mPositionScroller.stop();
				}
				mTouchMode = TOUCH_MODE_OVERSCROLL;
				mMotionX = (int) ev.getX();
				mMotionY = mLastY = (int) ev.getY();
				mMotionCorrection = 0;
				mActivePointerId = ev.getPointerId(0);
				mDirection = 0;
				break;
			}

			default: {
				mActivePointerId = ev.getPointerId(0);
				final int x = (int) ev.getX();
				final int y = (int) ev.getY();
				int motionPosition = pointToPosition(x, y);
				if (!mDataChanged) {
					if ((mTouchMode != TOUCH_MODE_FLING)
							&& (motionPosition >= 0)
							&& (getAdapter().isEnabled(motionPosition))) {
						// User clicked on an actual view (and was not stopping
						// a fling).
						// It might be a click or a scroll. Assume it is a click
						// until
						// proven otherwise
						mTouchMode = TOUCH_MODE_DOWN;
						// FIXME Debounce
						if (mPendingCheckForTap == null) {
							mPendingCheckForTap = new CheckForTap();
						}
						postDelayed(mPendingCheckForTap,
								ViewConfiguration.getTapTimeout());
					} else {
						if (mTouchMode == TOUCH_MODE_FLING) {
							// Stopped a fling. It is a scroll.
							createScrollingCache();
							mTouchMode = TOUCH_MODE_SCROLL;
							mMotionCorrection = 0;
							motionPosition = findMotionRow(y);
							mFlingRunnable.flywheelTouch();
						}
					}
				}

				if (motionPosition >= 0) {
					// Remember where the motion event started
					v = getChildAt(motionPosition - mFirstPosition);
					mMotionViewOriginalTop = v.getTop();
				}
				mMotionX = x;
				mMotionY = y;
				mMotionPosition = motionPosition;
				mLastY = Integer.MIN_VALUE;
				break;
			}
			}

			if (performButtonActionOnTouchDown(ev)) {
				if (mTouchMode == TOUCH_MODE_DOWN) {
					removeCallbacks(mPendingCheckForTap);
				}
			}
			break;
		}

		case MotionEvent.ACTION_MOVE: {
			int pointerIndex = ev.findPointerIndex(mActivePointerId);
			if (pointerIndex == -1) {
				pointerIndex = 0;
				mActivePointerId = ev.getPointerId(pointerIndex);
			}
			final int y = (int) ev.getY(pointerIndex);

			if (mDataChanged) {
				// Re-sync everything if data has been changed
				// since the scroll operation can query the adapter.
				layoutChildren();
			}

			switch (mTouchMode) {
			case TOUCH_MODE_DOWN:
			case TOUCH_MODE_TAP:
			case TOUCH_MODE_DONE_WAITING:
				// Check if we have moved far enough that it looks more like a
				// scroll than a tap
				startScrollIfNeeded(y);
				break;
			case TOUCH_MODE_SCROLL:
			case TOUCH_MODE_OVERSCROLL:
				scrollIfNeeded(y);
				break;
			}
			break;
		}

		case MotionEvent.ACTION_UP: {
			switch (mTouchMode) {
			case TOUCH_MODE_DOWN:
			case TOUCH_MODE_TAP:
			case TOUCH_MODE_DONE_WAITING:
				final int motionPosition = mMotionPosition;
				final View child = getChildAt(motionPosition - mFirstPosition);

				final float x = ev.getX();
				final boolean inList = x > mListPadding.left
						&& x < getWidth() - mListPadding.right;

				if (child != null && !child.hasFocusable() && inList) {
					if (mTouchMode != TOUCH_MODE_DOWN) {
						child.setPressed(false);
					}

					if (mPerformClick == null) {
						mPerformClick = new PerformClick();
					}

					final AbsListView.PerformClick performClick = mPerformClick;
					performClick.mClickMotionPosition = motionPosition;
					performClick.rememberWindowAttachCount();

					mResurrectToPosition = motionPosition;

					if (mTouchMode == TOUCH_MODE_DOWN
							|| mTouchMode == TOUCH_MODE_TAP) {
						final Handler handler = getHandler();
						if (handler != null) {
							handler.removeCallbacks(mTouchMode == TOUCH_MODE_DOWN ? mPendingCheckForTap
									: mPendingCheckForLongPress);
						}
						mLayoutMode = LAYOUT_NORMAL;
						if (!mDataChanged && mAdapter.isEnabled(motionPosition)) {
							mTouchMode = TOUCH_MODE_TAP;
							setSelectedPositionInt(mMotionPosition);
							layoutChildren();
							child.setPressed(true);
							positionSelector(mMotionPosition, child);
							setPressed(true);
							if (mSelector != null) {
								Drawable d = mSelector.getCurrent();
								if (d != null
										&& d instanceof TransitionDrawable) {
									((TransitionDrawable) d).resetTransition();
								}
							}
							if (mTouchModeReset != null) {
								removeCallbacks(mTouchModeReset);
							}
							mTouchModeReset = new Runnable() {
								@Override
								public void run() {
									mTouchMode = TOUCH_MODE_REST;
									child.setPressed(false);
									setPressed(false);
									if (!mDataChanged) {
										performClick.run();
									}
								}
							};
							postDelayed(mTouchModeReset,
									ViewConfiguration.getPressedStateDuration());
						} else {
							mTouchMode = TOUCH_MODE_REST;
							updateSelectorState();
						}
						return true;
					} else if (!mDataChanged
							&& mAdapter.isEnabled(motionPosition)) {
						performClick.run();
					}
				}
				mTouchMode = TOUCH_MODE_REST;
				updateSelectorState();
				break;
			case TOUCH_MODE_SCROLL:
				final int childCount = getChildCount();
				if (childCount > 0) {
					final int firstChildTop = getChildAt(0).getTop();
					final int lastChildBottom = getChildAt(childCount - 1)
							.getBottom();
					final int contentTop = mListPadding.top;
					final int contentBottom = getHeight() - mListPadding.bottom;
					if (mFirstPosition == 0 && firstChildTop >= contentTop
							&& mFirstPosition + childCount < mItemCount
							&& lastChildBottom <= getHeight() - contentBottom) {
						mTouchMode = TOUCH_MODE_REST;
						reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
					} else {
						final VelocityTracker velocityTracker = mVelocityTracker;
						velocityTracker.computeCurrentVelocity(1000,
								mMaximumVelocity);

						final int initialVelocity = (int) (velocityTracker
								.getYVelocity(mActivePointerId) * mVelocityScale);
						// Fling if we have enough velocity and we aren't at a
						// boundary.
						// Since we can potentially overfling more than we can
						// overscroll, don't
						// allow the weird behavior where you can scroll to a
						// boundary then
						// fling further.
						if (Math.abs(initialVelocity) > mMinimumVelocity
								&& !((mFirstPosition == 0 && firstChildTop == contentTop
										- mOverscrollDistance) || (mFirstPosition
										+ childCount == mItemCount && lastChildBottom == contentBottom
										+ mOverscrollDistance))) {
							if (mFlingRunnable == null) {
								mFlingRunnable = new FlingRunnable();
							}
							reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);

							mFlingRunnable.start(-initialVelocity);
						} else {
							mTouchMode = TOUCH_MODE_REST;
							reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
							if (mFlingRunnable != null) {
								mFlingRunnable.endFling();
							}
							if (mPositionScroller != null) {
								mPositionScroller.stop();
							}
						}
					}
				} else {
					mTouchMode = TOUCH_MODE_REST;
					reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
				}
				break;

			case TOUCH_MODE_OVERSCROLL:
				if (mFlingRunnable == null) {
					mFlingRunnable = new FlingRunnable();
				}
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				final int initialVelocity = (int) velocityTracker
						.getYVelocity(mActivePointerId);

				reportScrollStateChange(OnScrollListener.SCROLL_STATE_FLING);
				if (Math.abs(initialVelocity) > mMinimumVelocity) {
					mFlingRunnable.startOverfling(-initialVelocity);
				} else {
					mFlingRunnable.startSpringback();
				}

				break;
			}

			setPressed(false);

			if (mEdgeGlowTop != null) {
				mEdgeGlowTop.onRelease();
				mEdgeGlowBottom.onRelease();
			}

			// Need to redraw since we probably aren't drawing the selector
			// anymore
			invalidate();

			final Handler handler = getHandler();
			if (handler != null) {
				handler.removeCallbacks(mPendingCheckForLongPress);
			}

			recycleVelocityTracker();

			mActivePointerId = INVALID_POINTER;

			if (PROFILE_SCROLLING) {
				if (mScrollProfilingStarted) {
					Debug.stopMethodTracing();
					mScrollProfilingStarted = false;
				}
			}

			if (mScrollStrictSpan != null) {
				mScrollStrictSpan.finish();
				mScrollStrictSpan = null;
			}
			break;
		}

		case MotionEvent.ACTION_CANCEL: {
			switch (mTouchMode) {
			case TOUCH_MODE_OVERSCROLL:
				if (mFlingRunnable == null) {
					mFlingRunnable = new FlingRunnable();
				}
				mFlingRunnable.startSpringback();
				break;

			case TOUCH_MODE_OVERFLING:
				// Do nothing - let it play out.
				break;

			default:
				mTouchMode = TOUCH_MODE_REST;
				setPressed(false);
				View motionView = this.getChildAt(mMotionPosition
						- mFirstPosition);
				if (motionView != null) {
					motionView.setPressed(false);
				}
				clearScrollingCache();

				final Handler handler = getHandler();
				if (handler != null) {
					handler.removeCallbacks(mPendingCheckForLongPress);
				}

				recycleVelocityTracker();
			}

			if (mEdgeGlowTop != null) {
				mEdgeGlowTop.onRelease();
				mEdgeGlowBottom.onRelease();
			}
			mActivePointerId = INVALID_POINTER;
			break;
		}

		case MotionEvent.ACTION_POINTER_UP: {
			onSecondaryPointerUp(ev);
			final int x = mMotionX;
			final int y = mMotionY;
			final int motionPosition = pointToPosition(x, y);
			if (motionPosition >= 0) {
				// Remember where the motion event started
				v = getChildAt(motionPosition - mFirstPosition);
				mMotionViewOriginalTop = v.getTop();
				mMotionPosition = motionPosition;
			}
			mLastY = y;
			break;
		}

		case MotionEvent.ACTION_POINTER_DOWN: {
			// New pointers take over dragging duties
			final int index = ev.getActionIndex();
			final int id = ev.getPointerId(index);
			final int x = (int) ev.getX(index);
			final int y = (int) ev.getY(index);
			mMotionCorrection = 0;
			mActivePointerId = id;
			mMotionX = x;
			mMotionY = y;
			final int motionPosition = pointToPosition(x, y);
			if (motionPosition >= 0) {
				// Remember where the motion event started
				v = getChildAt(motionPosition - mFirstPosition);
				mMotionViewOriginalTop = v.getTop();
				mMotionPosition = motionPosition;
			}
			mLastY = y;
			break;
		}
		}

		return true;
	}
*/
}
