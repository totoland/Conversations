/*
 * Copyright (c) 2017, Daniel Gultsch All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package eu.siacs.conversations.utils;

import android.graphics.Typeface;
import android.text.ParcelableSpan;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.TypefaceSpan;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.siacs.conversations.ui.widget.ZeroWidthSpan;

public class StylingHelper {

	private static List<SpanPattern> SPAN_PATTERNS = Arrays.asList(new BoldSpanPattern(), new ItalicSpanPattern(), new StrikeThroughSpanPattern());

	private static void applySpanForPattern(SpannableStringBuilder builder, final int start, final int end) {
		for(SpanPattern spanPattern : SPAN_PATTERNS) {
			Matcher matcher = spanPattern.getPattern().matcher(builder).region(start, end);
			while (matcher.find()) {
				applySpan(builder,matcher,spanPattern.createSpan());
			}
		}
	}

	private static void applySpan(SpannableStringBuilder builder, Matcher matcher, ParcelableSpan span) {
		builder.setSpan(span, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		builder.setSpan(new ZeroWidthSpan(), matcher.start(), matcher.start() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		builder.setSpan(new ZeroWidthSpan(), matcher.end() - 1, matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
	}

	public static void format(SpannableStringBuilder body) {
		final MonospaceSpanPattern monospaceSpanPattern = new MonospaceSpanPattern();
		Matcher matcher = monospaceSpanPattern.getPattern().matcher(body);
		int previous = 0;
		while(matcher.find()) {
			if (previous < matcher.start()) {
				applySpanForPattern(body,previous,matcher.start());
			}
			applySpan(body,matcher,monospaceSpanPattern.createSpan());
			previous = matcher.end();
		}
		if (previous < body.length()) {
			applySpanForPattern(body,previous,body.length());
		}
	}

	public static abstract class SpanPattern {

		private final Pattern pattern;

		public SpanPattern(String c) {
			this.pattern = Pattern.compile("(?:" + Pattern.quote(c + c) + "(.+?)" + Pattern.quote(c + c) + ")|(?:" + Pattern.quote(c) + "(.+?)" + Pattern.quote(c) + ")");
		}

		public SpanPattern(char c) {
			this(String.valueOf(c));
		}

		public Pattern getPattern() {
			return this.pattern;
		}

		abstract ParcelableSpan createSpan();
	}

	public static class MonospaceSpanPattern extends SpanPattern {

		public MonospaceSpanPattern() {
			super('`');
		}

		@Override
		ParcelableSpan createSpan() {
			return new TypefaceSpan("monospace");
		}
	}

	public static class BoldSpanPattern extends SpanPattern {


		public BoldSpanPattern() {
			super('*');
		}

		@Override
		ParcelableSpan createSpan() {
			return new StyleSpan(Typeface.BOLD);
		}
	}

	public static class ItalicSpanPattern extends SpanPattern {

		public ItalicSpanPattern() {
			super('_');
		}

		@Override
		ParcelableSpan createSpan() {
			return new StyleSpan(Typeface.ITALIC);
		}
	}

	public static class StrikeThroughSpanPattern extends SpanPattern {


		public StrikeThroughSpanPattern() {
			super('~');
		}

		@Override
		ParcelableSpan createSpan() {
			return new StrikethroughSpan();
		}
	}

}
