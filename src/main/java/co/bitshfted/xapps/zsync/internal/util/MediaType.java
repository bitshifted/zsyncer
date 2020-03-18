/**
 * Copyright (c) 2015, Salesforce.com, Inc. All rights reserved.
 * Copyright (c) 2020, Bitshift (bitshifted.co), Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Salesforce.com nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY
 * WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package co.bitshfted.xapps.zsync.internal.util;

import java.util.*;

/**
 * This class is based on Google Guava {@code MediaType} class. It contains just enough similar methods
 * to make it compatible with original zsync4j code, but avoid dependency to Google Guava.
 *
 * @author Vladimir Djurovic
 */
public class MediaType {

	private final String type;
	private final String subtype;
	private final Map<String, List<String>> parameters;

	private int hashCode;

	private MediaType(String type, String subtype, Map<String, List<String>> parameters) {
		this.type = type;
		this.subtype = subtype;
		this.parameters = parameters;
	}


	/** Returns the top-level media type. For example, {@code "text"} in {@code "text/plain"}. */
	public String type() {
		return type;
	}

	/** Returns the media subtype. For example, {@code "plain"} in {@code "text/plain"}. */
	public String subtype() {
		return subtype;
	}

	/** Returns a multimap containing the parameters of this media type. */
	public Map<String, List<String>> parameters() {
		return parameters;
	}

	/**
	 * Creates a new media type with the given type and subtype.
	 *
	 * @throws IllegalArgumentException if type or subtype is invalid or if a wildcard is used for the
	 *     type, but not the subtype.
	 */
	public static MediaType create(String type, String subtype) {
		return new MediaType(type, subtype, Map.of());
	}

	public static MediaType create(String type, String subtype, Map<String, List<String>> params) {
		return new MediaType(type, subtype, params);
	}

	/**
	 * Parses a media type from its string representation.
	 *
	 * @throws IllegalArgumentException if the input is not parsable
	 */
	public static MediaType parse(String input) {
		Objects.requireNonNull(input);
		String[] mainparts = input.split(";"); // split type and parameters parts
		String[] types = mainparts[0].split("/");
		String type = types[0].trim();
		String subtype = types[1].trim();

		Map<String, List<String>> params = new HashMap<>();
		if (mainparts.length == 2) {
			String[] paramParts = mainparts[1].trim().split("=");
			if(paramParts.length == 2) {
				List<String> values = new ArrayList<>();
				values.add(paramParts[1].trim());
				params.put(paramParts[0].trim(), values);
			}
		}
		return new MediaType(type, subtype, params);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof MediaType) {
			MediaType that = (MediaType) obj;
			return this.type.equals(that.type)
					&& this.subtype.equals(that.subtype)
					// compare parameters regardless of order
					&& this.parameters().equals(that.parameters());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		// racy single-check idiom
		int h = hashCode;
		if (h == 0) {
			h = Objects.hash(type, subtype, parameters);
			hashCode = h;
		}
		return h;
	}


}
