package xyf.frpc.remoting;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public abstract class HeartBeatTask implements Runnable {
	
	public static final long DEFAULT_HEART_BEAT_INTERVAL = 5000;
	
	public static final TimeUnit HEART_TIME_UNIT = TimeUnit.MILLISECONDS;
	
	public static final long DEFAULT_LOST_THRESHOLD = 60000;
	
	//in second
	private long lostThreshold = DEFAULT_LOST_THRESHOLD;
	

	public long getLostThreshold() {
		return lostThreshold;
	}

	public void setLostThreshold(long lostThreshold) {
		this.lostThreshold = lostThreshold;
	}

	
	public void run() {
		
	}
	
	
	public static class HeartBeatThreadFactory implements ThreadFactory {
		private final static String THREAD_NAME_PREFIX = "frpc-heart-beat-thread-";
		private final static AtomicLong ID = new AtomicLong();
		public Thread newThread(Runnable r) {
			Thread t = new Thread(r);
			if(Thread.currentThread().isDaemon()) {
				t.setDaemon(true);
			}
			if(r instanceof HeartBeatTask) {
				t.setName(THREAD_NAME_PREFIX + ID.getAndIncrement());
			}
			return t;
		}
	}

}
