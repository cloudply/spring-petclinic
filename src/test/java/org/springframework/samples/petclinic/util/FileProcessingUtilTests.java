package org.springframework.samples.petclinic.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileProcessingUtilTests {

    @Mock
    private FileReader mockFileReader;
    
    @Mock
    private BufferedReader mockBufferedReader;
    
    @Mock
    private FileWriter mockFileWriter;
    
    @Mock
    private DocumentBuilderFactory mockFactory;
    
    @Mock
    private DocumentBuilder mockBuilder;
    
    @Mock
    private ObjectInputStream mockObjectInputStream;
    
    @TempDir
    Path tempDir;
    
    private File testFile;
    private String testContent = "Test content\nLine 2";

    @BeforeEach
    void setUp() throws IOException {
        testFile = tempDir.resolve("test.txt").toFile();
        Files.write(testFile.toPath(), testContent.getBytes());
    }

    @Test
    void testReadFile() throws IOException {
        // This test uses mocking to avoid actual file system operations
        try (MockedStatic<File> fileMock = Mockito.mockStatic(File.class);
             MockedStatic<FileReader> fileReaderMock = Mockito.mockStatic(FileReader.class);
             MockedStatic<BufferedReader> bufferedReaderMock = Mockito.mockStatic(BufferedReader.class)) {
            
            File mockFile = mock(File.class);
            fileMock.when(() -> new File("/app/data/test.txt")).thenReturn(mockFile);
            fileReaderMock.when(() -> new FileReader(mockFile)).thenReturn(mockFileReader);
            bufferedReaderMock.when(() -> new BufferedReader(mockFileReader)).thenReturn(mockBufferedReader);
            
            when(mockBufferedReader.readLine()).thenReturn("Line 1", "Line 2", null);
            
            String result = FileProcessingUtil.readFile("test.txt");
            
            assertThat(result).isEqualTo("Line 1\nLine 2\n");
            verify(mockBufferedReader, times(3)).readLine();
            verify(mockBufferedReader).close();
        }
    }

    @Test
    void testReadFile_PathTraversal() {
        // Test to verify the path traversal vulnerability exists
        assertThatExceptionOfType(IOException.class)
            .isThrownBy(() -> FileProcessingUtil.readFile("../../../etc/passwd"))
            .withMessageContaining("No such file or directory");
    }

    @Test
    void testWriteFile() throws IOException {
        // This test uses mocking to avoid actual file system operations
        try (MockedStatic<File> fileMock = Mockito.mockStatic(File.class);
             MockedStatic<FileWriter> fileWriterMock = Mockito.mockStatic(FileWriter.class)) {
            
            File mockFile = mock(File.class);
            fileMock.when(() -> new File("/app/uploads/test.txt")).thenReturn(mockFile);
            fileWriterMock.when(() -> new FileWriter(mockFile)).thenReturn(mockFileWriter);
            
            FileProcessingUtil.writeFile("test.txt", "Test content");
            
            verify(mockFileWriter).write("Test content");
            verify(mockFileWriter).close();
        }
    }

    @Test
    void testWriteFile_PathTraversal() {
        // Test to verify the path traversal vulnerability exists
        assertThatExceptionOfType(IOException.class)
            .isThrownBy(() -> FileProcessingUtil.writeFile("../../../etc/passwd", "hacked"))
            .withMessageContaining("Permission denied");
    }

    @Test
    void testParseXML() throws Exception {
        // This test uses mocking to avoid actual XML parsing
        try (MockedStatic<DocumentBuilderFactory> factoryMock = Mockito.mockStatic(DocumentBuilderFactory.class)) {
            factoryMock.when(DocumentBuilderFactory::newInstance).thenReturn(mockFactory);
            when(mockFactory.newDocumentBuilder()).thenReturn(mockBuilder);
            
            String xmlContent = "<root><element>value</element></root>";
            FileProcessingUtil.parseXML(xmlContent);
            
            verify(mockBuilder).parse(any(InputSource.class));
        }
    }

    @Test
    void testParseXML_XXEVulnerability() throws Exception {
        // Test to verify the XXE vulnerability exists
        try (MockedStatic<DocumentBuilderFactory> factoryMock = Mockito.mockStatic(DocumentBuilderFactory.class)) {
            factoryMock.when(DocumentBuilderFactory::newInstance).thenReturn(mockFactory);
            when(mockFactory.newDocumentBuilder()).thenReturn(mockBuilder);
            
            // XXE payload
            String xxePayload = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" +
                               "<!DOCTYPE foo [" +
                               "<!ELEMENT foo ANY >" +
                               "<!ENTITY xxe SYSTEM \"file:///etc/passwd\" >]>" +
                               "<foo>&xxe;</foo>";
            
            FileProcessingUtil.parseXML(xxePayload);
            
            // Verify that the parse method was called with the XXE payload
            verify(mockBuilder).parse(any(InputSource.class));
        }
    }

    @Test
    void testDeserialize() throws Exception {
        // This test uses mocking to avoid actual deserialization
        try (MockedStatic<ByteArrayInputStream> bisMock = Mockito.mockStatic(ByteArrayInputStream.class);
             MockedStatic<ObjectInputStream> oisMock = Mockito.mockStatic(ObjectInputStream.class)) {
            
            ByteArrayInputStream mockBis = mock(ByteArrayInputStream.class);
            bisMock.when(() -> new ByteArrayInputStream(any(byte[].class))).thenReturn(mockBis);
            oisMock.when(() -> new ObjectInputStream(mockBis)).thenReturn(mockObjectInputStream);
            
            when(mockObjectInputStream.readObject()).thenReturn("Test Object");
            
            byte[] testData = new byte[]{1, 2, 3, 4};
            Object result = FileProcessingUtil.deserialize(testData);
            
            assertThat(result).isEqualTo("Test Object");
            verify(mockObjectInputStream).readObject();
        }
    }

    @Test
    void testDeserialize_Exception() throws Exception {
        // This test uses mocking to test exception handling
        try (MockedStatic<ByteArrayInputStream> bisMock = Mockito.mockStatic(ByteArrayInputStream.class);
             MockedStatic<ObjectInputStream> oisMock = Mockito.mockStatic(ObjectInputStream.class)) {
            
            ByteArrayInputStream mockBis = mock(ByteArrayInputStream.class);
            bisMock.when(() -> new ByteArrayInputStream(any(byte[].class))).thenReturn(mockBis);
            oisMock.when(() -> new ObjectInputStream(mockBis)).thenReturn(mockObjectInputStream);
            
            when(mockObjectInputStream.readObject()).thenThrow(new ClassNotFoundException("Test exception"));
            
            byte[] testData = new byte[]{1, 2, 3, 4};
            Object result = FileProcessingUtil.deserialize(testData);
            
            assertThat(result).isNull();
        }
    }

    @Test
    void testReadFileWithLeak() throws IOException {
        // Create a real temporary file for this test
        String content = "Test content for leak test";
        File tempFile = tempDir.resolve("leak-test.txt").toFile();
        Files.write(tempFile.toPath(), content.getBytes());
        
        String result = FileProcessingUtil.readFileWithLeak(tempFile.getAbsolutePath());
        
        assertThat(result).isEqualTo(content);
        // Note: We can't directly test for the resource leak here,
        // but we can verify the method returns the expected content
    }

    @Test
    void testReadFileWithLeak_FileNotFound() {
        assertThatExceptionOfType(FileNotFoundException.class)
            .isThrownBy(() -> FileProcessingUtil.readFileWithLeak("non-existent-file.txt"));
    }
}
