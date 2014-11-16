/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.integrationtests;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.fortyoneconcepts.valjogen.integrationtests.util.WarningOnJUnitErrorRule;

import difflib.*;
import static com.fortyoneconcepts.valjogen.integrationtests.util.TestSupport.*;

/**
 * Compares current output from our annotation processor with pre-verified output that acts as a golden master. If the output changes then a bug
 * is assumed. Naturally, this means that this test will fail initially and when output is altered on purpose. In these cases the developer should promote
 * the generated output to a new golden master (copy relevant files). A note about this will automatically be output in case of an error.
 *
 * The test assumes that our build processor has executed our annotation processor as part of the build process of the tests. In case no out is detected
 * then check the build configuration file (POM).
 *
 * @author mmc
 */
@RunWith(Parameterized.class)
public class CompareGeneratedSourcesWithExpectedTest
{
	private static Path expectedSourcePath;
	private static Path actualOutputPath;

	@Parameters(name="{index} - {0} vs {1}")
    public static Collection<Object[]> data() throws Throwable
    {
    	expectedSourcePath = getRootPathExpectedOutput();
    	actualOutputPath = getRootPathGeneratedOutput();

    	return getData(expectedSourcePath, actualOutputPath);
	}

	private final Path expectedJavaFilePath;
	private final Path actualJavaFilePath;

	@Rule
    public WarningOnJUnitErrorRule rule = new WarningOnJUnitErrorRule();

	public CompareGeneratedSourcesWithExpectedTest(String expectedDescription, String actualDescription, Path expectedJavaFilePath, Path actualJavaFilePath)
	{
		this.expectedJavaFilePath=expectedJavaFilePath;
		this.actualJavaFilePath=actualJavaFilePath;
	}

	@Test() // Nb. Must be executed from test class - can not be run individually.
	public void compareSourcesTest() throws Exception
	{
		rule.setErrorMsgHeader(() -> "NOTE: Set of file names in '"+expectedJavaFilePath.toString()+"' must match set of file names of generated output at '"+actualJavaFilePath.toString()+"'.");

		// Check if filenames are identical:
		String expectedFileName = getFileName(expectedJavaFilePath);
		String actualFileName = getFileName(actualJavaFilePath);
		Assert.assertEquals("Expected and actual names of files does not match", expectedFileName, actualFileName);

		rule.setErrorMsgHeader(() -> {
	     return "NOTE: compareSourcesTest("+expectedFileName+") has detected a change which will trigger a failure even if output has changed on purpose." +
	            " If new version of generated output is indeed correct please update expected file(s) stored at '"+expectedSourcePath.toString()+"' with result from generated file(s) at "+actualOutputPath+" to make this test pass."+System.lineSeparator() +
                " To investigate in a *nx shell type 'diff "+expectedJavaFilePath.toString()+" "+actualJavaFilePath+"'.";
		});

		// Read expected and actual sources but remove dates:
		List<String> expectedContent = Files.readAllLines(expectedJavaFilePath).stream().map(s -> removeDate(s)).collect(Collectors.toList());
		List<String> actualContent = Files.readAllLines(actualJavaFilePath).stream().map(s -> removeDate(s)).collect(Collectors.toList());

		// Compute diff. Get the Patch object. Patch is the container for computed deltas.
        Patch patch = DiffUtils.diff(expectedContent, actualContent);

        List<Delta> deltas = patch.getDeltas();

        Assert.assertTrue("Expected file "+expectedFileName+ " and actual file "+actualFileName+" has different content. Changes are: "+System.lineSeparator()+deltas.stream().map(Object::toString).collect(Collectors.joining(System.lineSeparator())), deltas.isEmpty());
	}

	private String removeDate(String txt)
	{
		String result=txt.replaceAll("date\\s?=\\s?\"[^\"]+\"", "date=\"\"");
		return result;
	}
}
