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

/**
 * This class is based on Google Guava {@code Ticker} class. It contains just enough similar methods
 * to make it compatible with original zsync4j code, but avoid dependency to Google Guava.
 *
 * @author Vladimir Djurovic
 */
public abstract class Ticker {

	/**
	 * Returns the number of nanoseconds elapsed since this ticker's fixed point of reference.
	 */
	public abstract long read();

	/**
	 * A ticker that reads the current time using {@link System#nanoTime}.
	 */
	public static Ticker systemTicker() {
		return SYSTEM_TICKER;
	}

	private static final Ticker SYSTEM_TICKER =
			new Ticker() {
				@Override
				public long read() {
					return System.nanoTime();
				}
			};
}
