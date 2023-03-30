package guru.qa.tests;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.pdftest.PDF.containsText;
import static org.hamcrest.MatcherAssert.assertThat;

public class ReadingFilesInsideOfZipTest {

    private ClassLoader classLoader = ReadingFilesInsideOfZipTest.class.getClassLoader();
    String zipFileName = "resources.zip";
    String xlsxFileName = "1..Nac..politika._9_11.dekabrya_.xlsx";
    String pdfFileName = "PDF_example1.pdf";
    String csvFileName = "sheet.csv";

    @Test
    @DisplayName("Check if the given file names are located inside of the .zip archive")
    void zipTest() throws Exception {

        Set<String> expectedFileNames = new HashSet<>(Arrays.asList(xlsxFileName, pdfFileName, csvFileName));
        Set<String> actualFileNames = new HashSet<>();

        try (InputStream is = classLoader.getResourceAsStream(zipFileName);
             ZipInputStream zipInputStream = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                String entryName = entry.getName();
                if (expectedFileNames.contains(entryName)) {
                    actualFileNames.add(entryName);
                }
            }
        }
        Assertions.assertEquals(expectedFileNames, actualFileNames);
    }

    @Test
    @DisplayName("Reading .csv file inside of the .zip archive")
    void csvCheckTest() throws Exception {
        try (InputStream is = classLoader.getResourceAsStream(zipFileName);
             ZipInputStream zs = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zs.getNextEntry()) != null) {
                if (entry.getName().equals(csvFileName)) {
                    InputStreamReader isr = new InputStreamReader(zs);
                    CSVReader csvReader = new CSVReader(isr);
                    List<String[]> content = csvReader.readAll();
                    Assertions.assertArrayEquals(new String[]{"R.U0.ab2c3713-c248-415a-8242-01e72830eebd", "213,891685", "136", "136"}, content.get(4));
                    break;
                }
            }
        }
    }

    @Test
    @DisplayName("Reading .pdf file inside of the .zip archive")
    void pdfCheckTest() throws Exception {
        try (InputStream is = classLoader.getResourceAsStream(zipFileName);
             ZipInputStream zs = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zs.getNextEntry()) != null) {
                if (entry.getName().equals(pdfFileName)) {
                    InputStreamReader isr = new InputStreamReader(zs);
                    PDF pdf = new PDF(zs);
                    assertThat(pdf, containsText("This is the tenth anniversary of the World Happiness"));
                    break;
                }
            }
        }
    }

    @Test
    @DisplayName("Reading .xlsx file inside of the .zip archive")
    void xlsxCheckTest() throws Exception {
        try (InputStream is = classLoader.getResourceAsStream(zipFileName);
             ZipInputStream zs = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zs.getNextEntry()) != null) {
                if (entry.getName().equals(xlsxFileName)) {
                    InputStreamReader isr = new InputStreamReader(zs);
                    XLS xls = new XLS(zs);
                      Assertions.assertEquals(
                            "Uno, dos, tres, cuatro, cinco, cinco, seis", xls.excel.getSheetAt(0)
                                    .getRow(96)
                                    .getCell(2)
                                    .getStringCellValue()
                    );
                    break;
                }
            }
        }
    }
}
