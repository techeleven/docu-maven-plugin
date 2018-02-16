package com.tech11.docu;

import static org.mockito.Mockito.when;

import java.io.IOException;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class AbstractReplacerMojoTest {

	static AbstractReplacerMojo cut;

	@BeforeAll
	static void init() {
		cut = new AbstractReplacerMojo() {
			@Override
			void doExecute() throws IOException {
				// no implementation required for the following test cases
			}
		};
	}

	@Test
	void testPatternCase1OneSpace() {
		String testContent = "bla blub " + AbstractReplacerMojo.LOOKUP_IDENT + " test bla bla";
		verify(testContent, new String[] { "test" });
	}

	@Test
	void testPatternCase2ManySpace() {
		String testContent = "bla blub " + AbstractReplacerMojo.LOOKUP_IDENT + "    test bla bla";
		verify(testContent, new String[] { "test" });
	}

	@Test
	void testPatternCaseNoSpace() {
		String testContent = "bla blub" + AbstractReplacerMojo.LOOKUP_IDENT + "test bla bla";
		verify(testContent, new String[] { "test" });
	}

	@Test
	void testPatternCaseTwoKeys() {
		String testContent = "bla blub " + AbstractReplacerMojo.LOOKUP_IDENT + "1234 bla bla\n\nnew line "
				+ AbstractReplacerMojo.LOOKUP_IDENT + "2345 bla bla";
		verify(testContent, new String[] { "1234", "2345" });
	}

	@Test
	void testPatternNoMatch() {
		String testContent = "bla blub #lookup 1234";
		verify(testContent, new String[] {});
	}

	void verify(String testContent, String... keys) {
		Document mockedDoc = Mockito.mock(Document.class);
		when(mockedDoc.getElementById(Matchers.anyString()))
				.thenReturn(new Element("div").html("dummy"));

		cut.docuRepoDoc = mockedDoc;
		Replacement r = cut.replaceContent(testContent);
		Assertions.assertEquals(keys.length > 0 ? true : false, r.modified);
		for (String key : keys)
			Assertions.assertTrue(r.getFoundKeys().contains(key));
		Assertions.assertTrue(!r.content.contains(AbstractReplacerMojo.LOOKUP_IDENT));
		Mockito.verify(mockedDoc, Mockito.times(keys.length)).getElementById(Matchers.anyString());
	}

}
