package hsel.softsmart.warenkorbanalyse.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class FileUtil {

    public static File parse(MultipartFile multipartFile) throws IOException {
        File file = File.createTempFile("warenkorbanalyse", ".tmp");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(multipartFile.getBytes());
        fileOutputStream.close();
        return file;
    }
}
