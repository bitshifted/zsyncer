/*
 * Copyright (C) 2008 The Guava Authors
 * Copyright (c) 2020, Bitshift (bitshifted.co), Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.salesforce.zsync.internal.util;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

/**
 * This class is based on Google Guava {@code Stopwatch} class. It contains just enough similar methods
 * to make it compatible with original zsync4j code, but avoid dependency to Google Guava.
 *
 * @author Vladimir Djurovic
 */
public class Stopwatch {

	private final Ticker ticker;
	private boolean isRunning;
	private long elapsedNanos;
	private long startTick;

	Stopwatch() {
		this.ticker = Ticker.systemTicker();
	}

	Stopwatch(Ticker ticker) {
		if(ticker == null) {
			throw new NullPointerException("Ticker can not be null.");
		}
		this.ticker = ticker;
	}

	/**
	 * Creates (but does not start) a new stopwatch, using the specified time source.
	 *
	 */
	public static Stopwatch createUnstarted(Ticker ticker) {
		return new Stopwatch(ticker);
	}

	/**
	 * Creates (but does not start) a new stopwatch using {@link System#nanoTime} as its time source.
	 *
	 */
	public static Stopwatch createUnstarted() {
		return new Stopwatch();
	}

	/**
	 * Starts the stopwatch.
	 *
	 * @return this {@code Stopwatch} instance
	 * @throws IllegalStateException if the stopwatch is already running.
	 */
	public Stopwatch start() {
		if(isRunning) {
			throw new IllegalArgumentException("This stopwatch is already started.");
		}
		isRunning = true;
		startTick = ticker.read();
		return this;
	}

	/**
	 * Sets the elapsed time for this stopwatch to zero, and places it in a stopped state.
	 *
	 * @return this {@code Stopwatch} instance
	 */
	public Stopwatch reset() {
		elapsedNanos = 0;
		isRunning = false;
		return this;
	}

	/**
	 * Stops the stopwatch. Future reads will return the fixed duration that had elapsed up to this
	 * point.
	 *
	 * @return this {@code Stopwatch} instance
	 * @throws IllegalStateException if the stopwatch is already stopped.
	 */
	public Stopwatch stop() {
		if(!isRunning) {
			throw new IllegalArgumentException("This stopwatch is already stopped.");
		}
		long tick = ticker.read();
		isRunning = false;
		elapsedNanos += tick - startTick;
		return this;
	}

	/**
	 * Returns the current elapsed time shown on this stopwatch, expressed in the desired time unit,
	 * with any fraction rounded down.
	 *
	 * <p><b>Note:</b> the overhead of measurement can be more than a microsecond, so it is generally
	 * not useful to specify {@link TimeUnit#NANOSECONDS} precision here.
	 *
	 * <p>It is generally not a good idea to use an ambiguous, unitless {@code long} to represent
	 * elapsed time. Therefore, we recommend using {@link #elapsed()} instead, which returns a
	 * strongly-typed {@link Duration} instance.
	 *
	 * @since 14.0 (since 10.0 as {@code elapsedTime()})
	 */
	public long elapsed(TimeUnit desiredUnit) {
		return desiredUnit.convert(elapsedNanos(), NANOSECONDS);
	}



	private long elapsedNanos() {
		return isRunning ? ticker.read() - startTick + elapsedNanos : elapsedNanos;
	}
}
