package cn.id0755.sdk.android.utils.net;

import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;

/**
 * 屏幕唤醒工具类
 */
public class WakeLockUtil {
	private static final String TAG = "MicroMsg.WakeLockUtil";

	private PowerManager.WakeLock wakeLock = null;
	private Handler mHandler = null;
	private Runnable mReleaseRunnable = new Runnable() {
		@Override
		public void run() {
			unLock();
		}
	};

	public WakeLockUtil(final Context context){
		final PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
		wakeLock.setReferenceCounted(false);
		mHandler = new Handler(context.getMainLooper());
	}

	@Override
	protected void finalize() throws Throwable {
		unLock();
	}

	public void lock(final long timeInMills) {
		lock();
		mHandler.postDelayed(mReleaseRunnable, timeInMills);
	}

	public void lock() {
		mHandler.removeCallbacks(mReleaseRunnable);
		wakeLock.acquire();
	}

	public void unLock() {
		mHandler.removeCallbacks(mReleaseRunnable);
		if (wakeLock.isHeld()) {
			wakeLock.release();
		}
	}

	public boolean isLocking() {
		return wakeLock.isHeld();
	}
	
}
