from quickstart import get_credentials
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError

creds = get_credentials()

def main():
    try:
       service = build("classroom", "v1", credentials=creds)

       results = service.courses().list(pageSize=10).execute()
       courses = results.get("courses", [])

       if not courses:
          print("cursos no encontrados")
          return

       for course in courses:
          print(f"\nNombre: {course['name']} \t ID: {course['id']}")
          print("-------------------------------------------")

          topics = []

          # Obtener información sobre los temas del curso
          topics_results = service.courses().topics().list(courseId=course['id']).execute()
          #topics = topics_results.get('topics', [])
          topics.extend(topics_results.get('topic', []))

          if not topics:
              print("No se encontraron temas para este curso.")
          else:
              print("Temas (topics):")
              count = 1
              for topic in topics:
                  # Obtener información sobre cada tema
                  topic_info = service.courses().topics().get(courseId=course['id'], id=topic['topicId']).execute()
                  print(f"  ID: {topic_info['topicId']} -> {topic_info['name']}")
              print("-------------------------------------------")
    
    except HttpError as error:
       print(f"An error occurred: {error}")


def get_material(course_id, name):
    # Crea el servicio Classroom
    service = build('classroom', 'v1', credentials=creds)

    # Llamar al método list de courseWorkMaterials con el filtro por materialId
    response = service.courses().courseWorkMaterials().list(courseId=course_id).execute()

    # Obtener la lista de materiales de trabajo que cumplen con el filtro
    materials_list = response.get('courseWorkMaterial', [])

    # Buscar directamente el material con el nombre deseado
    material = next((m for m in materials_list if m.get('title') == name), None)

    if material:
        return material

    return None


def get_dict(course_id, material_name):
    diccionario = {}
    coursework = get_material(course_id, material_name)
    enlace = coursework.get('materials', [])

    for material in enlace:
       if material.get('driveFile'):
        file = material['driveFile']
        file_id = file['driveFile']['id']
        file_name = file['driveFile']['title']

        #id_new = cambiar_link(file_id)

        # Construye el enlace directo a Google Drive
        diccionario[file_name[:2]] = cambiar_link(file_id)

    return diccionario


def cambiar_link(file_id):
    # Crea el servicio de drive
    service = build('drive', 'v3', credentials=creds)

    try:
        #id de la carpeta donde se copian los documentos
        folder_id = "1sPzPNE-a435bve2mUrqI02p6g2rmM6-Y"

        #se copia y se obtiene el archivo
        archivo = service.files().copy(fileId=file_id, body={'parents': [folder_id]}).execute()
        return archivo['id']
    
    except Exception as e:
        print(f"Error al copiar el archivo: {e}")
        return None


if __name__ == "__main__":
  main()