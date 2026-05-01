package me.sunlianhui.coinflow.common.utils;

public class SnowflakeIdGenerator {

	// Epoch timestamp (2020-01-01)
	private final long twepoch = 1577808000000L;

	private final long workerIdBits = 5L;
	private final long datacenterIdBits = 5L;
	private final long maxWorkerId = ~(-1L << workerIdBits);       // 31
	private final long maxDatacenterId = ~(-1L << datacenterIdBits); // 31
	private final long sequenceBits = 12L;

	private final long workerIdShift = sequenceBits;
	private final long datacenterIdShift = sequenceBits + workerIdBits;
	private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

	private final long sequenceMask = ~(-1L << sequenceBits);

	private long workerId;
	private long datacenterId;
	private long sequence = 0L;
	private long lastTimestamp = -1L;

	public SnowflakeIdGenerator(long workerId, long datacenterId) {
		if (workerId > maxWorkerId || workerId < 0) {
			throw new IllegalArgumentException("workerId invalid");
		}
		if (datacenterId > maxDatacenterId || datacenterId < 0) {
			throw new IllegalArgumentException("datacenterId invalid");
		}
		this.workerId = workerId;
		this.datacenterId = datacenterId;
	}

	public synchronized long nextId() {
		long timestamp = currentTime();

		if (timestamp < lastTimestamp) {
			// System clock moved backwards, wait until it catches up
			long offset = lastTimestamp - timestamp;
			try {
				Thread.sleep(offset); // Wait for clock to catch up
				timestamp = currentTime();
				if (timestamp < lastTimestamp) {
					// If still not caught up after waiting, throw an error
					throw new RuntimeException("Clock moved backwards. Still invalid.");
				}
			} catch (InterruptedException e) {
				throw new RuntimeException("Thread interrupted while waiting for clock to catch up.", e);
			}
		}


		if (lastTimestamp == timestamp) {
			sequence = (sequence + 1) & sequenceMask;
			if (sequence == 0) {
				timestamp = waitUntilNextMillis(lastTimestamp);
			}
		} else {
			sequence = 0L;
		}

		lastTimestamp = timestamp;

		return ((timestamp - twepoch) << timestampLeftShift)
				| (datacenterId << datacenterIdShift)
				| (workerId << workerIdShift)
				| sequence;
	}

	private long waitUntilNextMillis(long lastTimestamp) {
		long timestamp = currentTime();
		while (timestamp <= lastTimestamp) {
			timestamp = currentTime();
		}
		return timestamp;
	}

	private long currentTime() {
		return System.currentTimeMillis();
	}
}
