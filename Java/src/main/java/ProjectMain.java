import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.google.api.client.http.FileContent;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.*;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.model.*;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchGetValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.api.services.calendar.Calendar;

public class ProjectMain {
    
    public static void main(String[] args) throws IOException, GeneralSecurityException {
        System.out.println("HOLA MUNDO \n");

        String courseId = "650163376669";
        String modeloId = "650437997576";
        String folderId = "1pT7pq1RUbid8ufv9hfaEdJ4EbfjESpoK";

        Map<String, String> materials = new HashMap<>();
        materials.put("PRÁCTICAS SEMANA 2", getDateTime(2023, 12, 30, 20, 30));
        materials.put("SOLUCIONARIOS SEMANA 1", getDateTime(2023, 12, 31, 7, 0));

        HashMap<String, String[]> courses = new HashMap<>();
            courses.put("1.", new String[]{"BIOLOGÍA", "650434957400"});
            courses.put("2.", new String[]{"CIVICA", "650435898520"});
            courses.put("3.", new String[]{"FILOSOFÍA", "650434930364"});
            courses.put("4.", new String[]{"FÍSICA", "650435473681"});
            courses.put("5.", new String[]{"GEOGRAFÍA", "650436204075"});
            courses.put("6.", new String[]{"HISTORIA", "650435915735"});
            courses.put("7.", new String[]{"INGLES", "650436625744"});
            courses.put("8.", new String[]{"LENGUAJE", "650435968539"});
            courses.put("9.", new String[]{"LITERATURA", "650435900394"});
            courses.put("10", new String[]{"MATEMATICA", "650434909686"});
            courses.put("11", new String[]{"PSICOLOGÍA", "650435917133"});
            courses.put("12", new String[]{"QUÍMICA", "650435856963"});
            courses.put("13" , new String[]{"RAZONAMIENTO MATEMÁTICO", "650435850887"});
            courses.put("14" , new String[]{"RAZONAMIENTO VERBAL", "646644423634"});


        subirMateriales(
                courseId,
                modeloId,
                folderId,
                materials,
                courses
        );

        listarIds();

    }

    public static void subirMateriales (String courseId, String modeloId, String folderId,
                                        Map<String, String> materials, HashMap<String, String[]> courses)
            throws IOException, GeneralSecurityException{

        for (String name: materials.keySet()) {
            CourseWorkMaterial workMaterials = ClassroomWorkMaterials.search(name, modeloId);

            if (workMaterials != null) {
                HashMap<String, String> hashMap = DriveFileCopy.getHashMap(workMaterials.getMaterials());

                for (String key : courses.keySet()) {

                    String title = name.charAt(0)
                            + name.substring(name.length()-2, name.length()).trim()
                            + " - "
                            + courses.get(key)[0]
                            + " INGENIERIAS";

                    File driveFile = DriveFileCopy.get(hashMap.get(key), folderId);

                    String link = "https://drive.google.com/file/d/"
                            + driveFile.getId()
                            + "/view";

                    //String date = materials(name);

                    ClassroomWorkMaterials.prepare(
                            courseId,
                            courses.get(key)[1],
                            title,
                            link,
                            materials.get(name)
                    );
                }
            } else {
                System.out.println("NADA");
            }
        }
    }

    public static String getDateTime(int anio, int mes, int dia, int hora, int minuto) {
        LocalDateTime dateTime = LocalDateTime.of(anio, mes, dia, hora, minuto);
        LocalDateTime dateTimeConDiferencia = dateTime.plusHours(5);

        // Convertir a formato Zulú (UTC)
        String formattedDateTime = dateTimeConDiferencia
                .atOffset(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));

        return formattedDateTime;
    }

    public static void listarIds () throws GeneralSecurityException, IOException {
        Classroom service = ApiCredentials.getClassroomService();

        ListCoursesResponse response = service.courses().list()
                .setPageSize(10)
                .execute();

        List<Course> courses = response.getCourses();
        if (courses == null || courses.size() == 0) {
            System.out.println("No courses found.");
        } else {
            System.out.println("Courses:");
            for (Course course : courses) {
                System.out.printf("%s\n", course.getName()+": "+course.getId());

                List<Topic> topics = ClassroomTopics.listTopics(course.getId());
                for (Topic topic : topics) {
                    System.out.println("\t"+topic.getName()+": "+topic.getTopicId());
                }
            }
        }
    }
}
