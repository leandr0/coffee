/**
 * 
 */
package com.lrgoncalves.coffee.model.hateos;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lrgoncalves
 *
 */
public class ResponseUtils {

	public static String compileResponse(final String businessObject, final String HATEOS) {

		Pattern p = Pattern.compile("\\}$");
		Matcher m = p.matcher(businessObject);

		String businessObjectJson = m.replaceAll(",");

		p = Pattern.compile("^\\{");
		m = p.matcher(HATEOS);

		String hateos = m.replaceAll("");

		return businessObjectJson+hateos;
	}

}
