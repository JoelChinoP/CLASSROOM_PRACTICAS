import com.google.api.services.classroom.model.DriveFile;
import com.google.api.services.classroom.model.Material;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;


import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

public class DriveFileCopy {

    static File get (String fileId, String locationId)
            throws IOException, GeneralSecurityException {
        File archivo = null;
        Drive service = ApiCredentials.getDriveService();

        try {
            archivo = new File().setParents(Collections.singletonList(locationId));
            archivo = service.files().copy(fileId, archivo).execute();

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e);
        }
        return archivo;
    }

    static HashMap<String, String> getHashMap (List<Material> materialList) {
        HashMap<String, String> hashMap = new HashMap<>();

        for (Material material: materialList) {
            DriveFile fileDrive = material
                    .getDriveFile()
                    .getDriveFile();
            hashMap.put(fileDrive.getTitle().substring(0,2), fileDrive.getId());
        }

        return hashMap;
    }
}
