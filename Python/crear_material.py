from quickstart import get_credentials
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError

from datetime import datetime, timedelta
from listar_ids import cambiar_link

creds = get_credentials()

def create_material(name, course_id, topic_id, drive_id, date_time):

    #Se añade la diferencia de hora
    dif_hora = timedelta(hours=5)
    date_time = date_time + dif_hora

    try:
        service = build("classroom", "v1", credentials=creds)

        # Formato de fecha requerido por la API
        fecha_str = date_time.strftime('%Y-%m-%dT%H:%M:%S.%fZ')

        # Crear un material de curso
        material = {
            'title': name,
            'materials': [
                {
                    'link': {
                        'url': f'https://drive.google.com/file/d/{drive_id}/view'
                    }
                }
            ],
            'topicId': topic_id,
            'scheduledTime': fecha_str,
            'state': 'DRAFT' #no publicado
        }

        # Crear el material de curso
        service.courses().courseWorkMaterials().create(courseId=course_id, body=material).execute()
        print(f"**Material: {name} programado!")

    except HttpError as error:
        print(f"An error occurred: {error}")
        return error
    

def delete_straft(course_id):
    service = build("classroom", "v1", credentials=creds)

    # Obtiene la lista de cursos
    courses = service.courses().list().execute()

    # Itera sobre los cursos y obtiene los materiales no publicados
    for course in courses.get('courses', []):
        course_id = course['id']
        #materials = service.courses().courseWorkMaterials().list(courseId=course_id).execute()
        materials = service.courses().courseWorkMaterials().list(
            courseId=course_id,
            courseWorkMaterialStates='DRAFT'
        ).execute()

        # Itera sobre los materiales y filtra los no publicados
        for mat in materials.get('courseWorkMaterial', []):
            titulo = mat['title']

            try:
                # Elimina el material de trabajo del curso
                service.courses().courseWorkMaterials().delete(courseId=course_id, id=mat['id']).execute()
                print (f"**{titulo} eliminado!")
            except HttpError as error:
                print(f"An error occurred: {error}")
                print(mat['id'])
    
if __name__ == "__main__":
    id_216b = "529153591750"
    titulo = "S9 - FILOSOFIA BIOMEDICASnn "
    cursos_id = "625475682875"
    programar = datetime(2023, 11, 23, 21, 0)
    drive = cambiar_link("10DvsRfOqqz8XVY7686bD0pgNvt1_zpqW")
    create_material (titulo, id_216b, cursos_id, drive, programar)