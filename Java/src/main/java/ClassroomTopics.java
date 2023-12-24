import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.classroom.model.ListTopicResponse;
import com.google.api.services.classroom.model.Topic;
import com.google.api.services.classroom.Classroom;

public class ClassroomTopics {

    static Topic getTopic(String courseId, String topicId)
        throws GeneralSecurityException, IOException {
        
        Classroom service = ApiCredentials.getClassroomService();

        Topic topic = null;
        try {
            // Get the topic.
            topic = service.courses().topics().get(courseId, topicId).execute();

        } catch (GoogleJsonResponseException e) {
            // TODO (developer) - handle error appropriately
            GoogleJsonError error = e.getDetails();
            if (error.getCode() == 404) {
                System.out.printf("The courseId or topicId does not exist: %s, %s.\n", courseId, topicId);
            }
            throw e;
            
        } catch (Exception e) {
            throw e;
        }
        return topic;
        
    }

    static List<Topic> listTopics(String courseId)
            throws GeneralSecurityException, IOException {
        return listTopics(courseId, 20);
    }

    static List<Topic> listTopics(String courseId, int pageSize)
        throws GeneralSecurityException, IOException {

        Classroom service = ApiCredentials.getClassroomService();
        List<Topic> topics = new ArrayList<>();

        try {
            ListTopicResponse response =
                service
                    .courses()
                    .topics()
                    .list(courseId)
                    .setPageSize(pageSize)
                    .execute();

            /* Ensure that the response is not null before retrieving data from it to avoid errors. */
            topics.addAll(response.getTopic());

            if (topics.isEmpty())
                System.out.println("No topics found.");

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

        return topics;
    }

}