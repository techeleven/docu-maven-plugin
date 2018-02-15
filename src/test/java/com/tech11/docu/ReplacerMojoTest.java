package com.tech11.docu;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

public class ReplacerMojoTest {

	static final String PATTERN = "#tech11-doc-lookup #(\\w*).*";

	@Test
	public void testRegEx() {
		String testContent = "asdfsadf #tech11-doc-lookup #CODE123 comment\r\nasdfsaf";

		Pattern p = Pattern.compile(PATTERN);
		
		Matcher m = p.matcher(testContent);
		
		System.out.println(testContent);
		System.out.println("############");
		
		//testContent = testContent.replaceAll(PATTERN, "xxx");
		
		while (m.find()) {
			System.out.println(m.group(1));
			testContent = m.replaceAll("<b>"+m.group(1)+"</b>");
			//testContent = m.quoteReplacement("test1");
			
		}
		
		System.out.println("----");
		System.out.println(testContent);
	}

}
