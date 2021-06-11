package hsel.softsmart.warenkorbanalyse.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Eine Unterstützungsklasse, die Methoden zur Bearbeitung von Dateien zur Verfügung stellt.
 */
public abstract class FileUtil {

    /**
     * Konvertiert eine {@link MultipartFile} in eine {@link File}.
     *
     * @param multipartFile Datei, die konvertiert werden soll
     * @return konvertierte Datei
     * @throws IOException Wenn in den IO ein Fehler auftritt
     */
    public static File parse(MultipartFile multipartFile) throws IOException {
        File file = File.createTempFile("warenkorbanalyse", ".tmp");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(multipartFile.getBytes());
        fileOutputStream.close();
        return file;
    }
}
