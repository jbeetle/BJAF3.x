package beetle.generate.file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FileOperate {
    public FileOperate() {
    }

    public void createFile(String content, String dir, String fileName) {
        createFolder(dir);
        try {
            File file = new File(dir + fileName);
            if (file.exists()) {
                file.delete();
            }
            FileWriter fw = new FileWriter(dir + fileName);
            fw.write(content);
            fw.close();
            System.out.println("createFile " + dir + fileName + " success");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    public void createFolder(String folder) {
        boolean success = true;
        File fileDirectory = new File(folder);
        if (!fileDirectory.exists()) {
            success = fileDirectory.mkdirs();
        }
        fileDirectory = null;
        if (!success) {
            System.out.println("createFolder Fail");
        } else {
            return;
        }
    }


}
