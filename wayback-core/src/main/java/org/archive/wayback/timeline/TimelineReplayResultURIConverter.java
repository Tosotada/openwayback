/* TimelineReplayResultURIConverter
 *
 * $Id$
 *
 * Created on 4:27:40 PM Apr 24, 2006.
 *
 * Copyright (C) 2006 Internet Archive.
 *
 * This file is part of wayback.
 *
 * wayback is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * any later version.
 *
 * wayback is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with wayback; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.archive.wayback.timeline;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.archive.wayback.ResultURIConverter;
import org.archive.wayback.WaybackConstants;

/**
 * Exotic implementation of ResultURIConverter that is aware of 4 contexts
 * for Replay URIs: Frameset, Timeline, Meta and Inline. In order to allow the 
 * rest of the application to remain unaware of these various contexts, this
 * class allows has extra methods which set the correct mode. The default mode 
 * is Frameset, meaning the base implementations use this context.
 *
 * @author brad
 * @version $Date$, $Revision$
 */
public class TimelineReplayResultURIConverter extends ResultURIConverter {
	private final static String META_MODE_YES = "yes";
	
	private final static String REPLAY_URI_PREFIX_PROPERTY = "replayuriprefix";
	private final static String META_URI_PREFIX_PROPERTY = "metauriprefix";
	private final static String FRAMESET_URI_PREFIX_PROPERTY = "frameseturiprefix";
	private final static String TIMELINE_URI_PREFIX_PROPERTY = "timelineuriprefix";
	
	private final static String REPLAY_URI_DEFAULT_SUFFIX = "replay";
	private final static String META_URI_DEFAULT_SUFFIX = "meta";
	private final static String FRAMESET_URI_DEFAULT_SUFFIX = "frameset";
	private final static String TIMELINE_URI_DEFAULT_SUFFIX = "timeline";

	protected String prefixPropertyName = FRAMESET_URI_PREFIX_PROPERTY;
	protected String defaultSuffix = FRAMESET_URI_DEFAULT_SUFFIX;
	
	protected String getPrefix() {
		return getConfigOrContextRelative(prefixPropertyName,defaultSuffix);
	}
	
	/* (non-Javadoc)
	 * @see org.archive.wayback.ResultURIConverter#makeReplayURI(java.lang.String, java.lang.String)
	 */
	public String makeReplayURI(String datespec, String url) {
		String resolution = wbRequest.get(WaybackConstants.REQUEST_RESOLUTION);
		String metaMode = wbRequest.get(WaybackConstants.REQUEST_META_MODE);

		StringBuilder buf = new StringBuilder(100);
		buf.append(getPrefix());
		buf.append("?q=exactdate:").append(datespec);
		if(resolution != null && resolution.length() > 0) {
			buf.append("%20").append(WaybackConstants.REQUEST_RESOLUTION);
			buf.append(":").append(resolution);
		}
		if(metaMode != null && (metaMode.length() > 0) && 
				metaMode.equals(META_MODE_YES)) {
			
			buf.append("%20").append(WaybackConstants.REQUEST_META_MODE);
			buf.append(":").append(META_MODE_YES);
		}
		buf.append("%20type:urlclosestquery%20url:");
		try {
			buf.append(URLEncoder.encode(url,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// should not be able to happen -- with hard-coded UTF-8, anyways..
			e.printStackTrace();
		}
		return buf.toString();
	}	

	/**
	 * configure the uriConverter to return frameset urls
	 */
	public void setFramesetMode() {
		prefixPropertyName = FRAMESET_URI_PREFIX_PROPERTY;
		defaultSuffix = FRAMESET_URI_DEFAULT_SUFFIX;
	}
	/**
	 * configure the uriConverter to return timline urls
	 */
	public void setTimelineMode() {
		prefixPropertyName = TIMELINE_URI_PREFIX_PROPERTY;
		defaultSuffix = TIMELINE_URI_DEFAULT_SUFFIX;
	}
	/**
	 * configure the uriConverter to return inline urls
	 */
	public void setInlineMode() {
		prefixPropertyName = REPLAY_URI_PREFIX_PROPERTY;
		defaultSuffix = REPLAY_URI_DEFAULT_SUFFIX;
	}
	/**
	 * configure the uriConverter to return meta urls
	 */
	public void setMetaMode() {
		prefixPropertyName = META_URI_PREFIX_PROPERTY;
		defaultSuffix = META_URI_DEFAULT_SUFFIX;
	}
}