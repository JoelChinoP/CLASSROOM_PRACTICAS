import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.model.*;
import com.google.api.services.drive.model.File;

public class ProjectMain {
    
    public static void main(String[] args) throws IOException, GeneralSecurityException {
        System.out.println("HOLA MUNDO \n");

        String courseId = "id"; //id del salon ejemplo 216b
        String modeloId = "id"; //id del modelo por ejm modelo ingenierias
        String folderId = "id"; //id del folder donde se copian los pdf

        Map<String, String> materials = new HashMap<>();
        materials.put("PRÁCTICAS SEMANA 1", getDateTime(2023, 12, 25, 7, 0)); //nombre y fecha(dia, mes, año, hora minuto)

        HashMap<String, String[]> courses = new HashMap<>();
            courses.put("13" , new String[]{"RAZONAMIENTO MATEMÁTICO", "id"}); //nombre y id del curso
            courses.put("14" , new String[]{"RAZONAMIENTO VERBAL", "id"});
/*
        subirMateriales(
                courseId,
                modeloId,
                folerId,
                materials,
                courses
        );*/


    }

    public static void subirMateriales (String courseId, String modeloId, String folderId,
                                        Map<String, String> materials, HashMap<String, String[]> courses)
            throws IOException, GeneralSecurityException{

        for (String name: materials.keySet()) {
            CourseWorkMaterial workMaterials = ClassroomWorkMaterials.search(name, modeloId);

            if (workMaterials != null) {
                HashMap<String, String> hashMap = DriveFileCopy.getHashMap(workMaterials.getMaterials());

                for (String key : courses.keySet()) {

                    String title = "P"
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
