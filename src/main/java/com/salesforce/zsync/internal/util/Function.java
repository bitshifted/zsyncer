/*
 * Copyright (C) 2007 The Guava Authors
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
 */package com.salesforce.zsync.internal.util;


/**
 * This class is based on Google Guava {@code Function} interface. It contains just enough similar methods
 * to make it compatible with original zsync4j code, but avoid dependency to Google Guava.
 *
 * @author Vladimir Djurovic
 */
public interface Function <F, T> extends java.util.function.Function<F, T> {

	T apply(F input);
}
