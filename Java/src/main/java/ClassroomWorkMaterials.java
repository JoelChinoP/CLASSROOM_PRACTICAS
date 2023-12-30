import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.classroom.Classroom;
import com.google.api.services.classroom.model.*;
;

public class ClassroomWorkMaterials {

    static CourseWorkMaterial prepare (String courseId, String topicId, String title, String link, String date)
        throws GeneralSecurityException, IOException {

        return create(courseId, topicId, title, link, "DRAFT", date);
    }
    
    static CourseWorkMaterial post (String courseId, String topicId, String title, String link)
        throws GeneralSecurityException, IOException {

        return create(courseId, topicId, title, link, "PUBLISHED", "");
    }

    static CourseWorkMaterial create (String courseId, String topicId, String title, String link, String state, String date)
            throws GeneralSecurityException, IOException {

        Classroom service = ApiCredentials.getClassroomService();

        // [START classroom_create_coursework_code_snippet]
        CourseWorkMaterial courseWork = null;

        try {
            Link articleLink = new Link().setUrl(link);
            List<Material> materials = Collections.singletonList(new Material().setLink(articleLink));

            CourseWorkMaterial content =
                    new CourseWorkMaterial()
                            .setTitle(title)
                            .setTopicId(topicId)
                            .setMaterials(materials)
                            .setState(state)
                            .setScheduledTime(date);

            courseWork = service.courses().courseWorkMaterials().create(courseId, content).execute();

        } catch (GoogleJsonResponseException e) {

            GoogleJsonError error = e.getDetails();

            if (error.getCode() == 404) {
                System.out.printf("The courseId does not exist: %s.\n", courseId);
            } else {
                throw e;
            }
            throw e;

        } catch (Exception e) {
            throw e;
        }

        return courseWork;


    }

    public static List<CourseWorkMaterial> getList(String courseId)
            throws GeneralSecurityException, IOException {
            return getList(courseId,5);
    }

    public static List<CourseWorkMaterial> getList(String courseId, int pageSize)
        throws GeneralSecurityException, IOException {

        Classroom service = ApiCredentials.getClassroomService();
        List<CourseWorkMaterial> workMaterials = new ArrayList<>();

        try {
            ListCourseWorkMaterialResponse response =
                    service
                            .courses()
                            .courseWorkMaterials()
                            .list(courseId)
                            .setPageSize(pageSize)
                            .execute();

            /* Ensure that the response is not null before retrieving data from it to avoid errors. */
            workMaterials.addAll(response.getCourseWorkMaterial());

            if (workMaterials.isEmpty())
                System.out.println("No workMaterials found.");

        } catch (GoogleJsonResponseException e) {
            // TODO (developer) - handle error appropriately
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 404) {
                System.out.printf("The courseId does not exist: %s.\n", courseId);
            } else {
                throw e;
            }
        } catch (Exception e) {
            throw e;
        }

        return workMaterials;
    }

    public static CourseWorkMaterial search (String materialName, String courseId)
            throws GeneralSecurityException, IOException{

        List<CourseWorkMaterial> workMaterials = ClassroomWorkMaterials.getList(courseId);

        for (CourseWorkMaterial workMaterial : workMaterials) {
            if (workMaterial.getTitle().equals(materialName))
                return workMaterial;
        }
        return null;
    }
}
