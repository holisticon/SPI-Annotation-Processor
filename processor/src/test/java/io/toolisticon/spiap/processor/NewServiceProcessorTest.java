package io.toolisticon.spiap.processor;


import io.toolisticon.annotationprocessortoolkit.tools.MessagerUtils;
import io.toolisticon.compiletesting.CompileTestBuilder;
import io.toolisticon.compiletesting.GeneratedFileObjectMatcher;
import io.toolisticon.compiletesting.JavaFileObjectUtils;
import org.junit.Before;
import org.junit.Test;

import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;


/**
 * Tests of {@link SpiProcessor}.
 */

public class NewServiceProcessorTest {


    @Before
    public void init() {

        MessagerUtils.setPrintMessageCodes(true);

        compileTestBuilder = CompileTestBuilder
                .compilationTest()
                .addProcessors(ServiceProcessor.class);

    }


    CompileTestBuilder.CompilationTestBuilder compileTestBuilder;


    @Test
    public void test_valid_usage() {

        compileTestBuilder
                .addProcessors()
                .addSources(JavaFileObjectUtils.readFromResource("serviceprocessor/TestcaseValidUsage.java"))
                .compilationShouldSucceed()
                .expectedFileObjectExists(StandardLocation.SOURCE_OUTPUT, "META-INF.services", "io.toolisticon.spiap.processor.serviceprocessortest.TestSpi", JavaFileObjectUtils.readFromString("testcase", "io.toolisticon.spiap.processor.tests.TestcaseValidUsage\n"))
                .expectedFileObjectExists(StandardLocation.SOURCE_OUTPUT, "META-INF.services", "io.toolisticon.spiap.processor.serviceprocessortest.TestSpi", new GeneratedFileObjectMatcher<FileObject>() {
                    @Override
                    public boolean check(FileObject fileObject) throws IOException {
                        return fileObject.getCharContent(false).toString().contains("processor");
                    }
                })
                .testCompilation();
    }


    @Test
    public void test_annotation_must_be_placed_on_class() {

        compileTestBuilder
                .addSources(JavaFileObjectUtils.readFromResource("serviceprocessor/TestcaseUsageOnInterface.java"))
                .compilationShouldFail()
                .expectedErrorMessages(ServiceProcessorMessages.ERROR_SPI_ANNOTATION_MUST_BE_PLACED_ON_CLASS.getCode())
                .testCompilation();
    }


    @Test
    public void test_annotation_value_attribute_must_only_contain_interfaces() {

        compileTestBuilder
                .addSources(JavaFileObjectUtils.readFromResource("serviceprocessor/TestcaseValueAttributeMustOnlyContainInterfaces.java"))
                .compilationShouldFail()
                .expectedErrorMessages(ServiceProcessorMessages.ERROR_VALUE_ATTRIBUTE_MUST_ONLY_CONTAIN_INTERFACES.getCode())
                .testCompilation();
    }


    @Test
    public void test_annotated_type_must_implement_configured_interfaces() {

        compileTestBuilder
                .addSources(JavaFileObjectUtils.readFromResource("serviceprocessor/TestcaseMustImplementAnnotatedInterface.java"))
                .compilationShouldFail()
                .expectedErrorMessages(ServiceProcessorMessages.ERROR_ANNOTATED_CLASS_MUST_IMPLEMENT_CONFIGURED_INTERFACES.getCode())
                .testCompilation();
    }


    @Test
    public void test_processing_should_succees_with_plain_interfaces() {

        compileTestBuilder
                .addSources(JavaFileObjectUtils.readFromResource("serviceprocessor/TestcaseValidUseWithPlainInterface.java"))
                .compilationShouldSucceed()
                .testCompilation();
    }


    @Test
    public void test_multiple_services_implemented() {

        compileTestBuilder
                .addSources(JavaFileObjectUtils.readFromResource("serviceprocessor/TestcaseMultipleServices.java"))
                .compilationShouldSucceed()
                .testCompilation();
    }


    @Test
    public void test_OutOfService_annotated_services_shouldnt_be_processed() {

        compileTestBuilder
                .addSources(JavaFileObjectUtils.readFromResource("serviceprocessor/TestcaseDontProcessOutOfServiceService.java"))
                .expectedNoteMessages(ServiceProcessorMessages.INFO_SKIP_ELEMENT_ANNOTATED_AS_OUT_OF_SERVICE.getCode())
                .compilationShouldSucceed()
                .testCompilation();
    }


}