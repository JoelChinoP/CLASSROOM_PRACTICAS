from listar_ids import get_dict
from crear_material import create_material
from crear_material import delete_straft
from datetime import datetime
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError

#material_name = "PRÁCTICAS 10"    #Incluir tildes
#datetime(2023, 11, 24, 21, 0)     #año, mes, dia, hora, minuto

material = {
  "SOLUCIONARIO 11"    : datetime(2023, 12, 9, 20, 0),
  "PRÁCTICAS 12"  : datetime(2023, 12, 8, 21, 0)
}

courses = {
  '1.': ['BIOLOGIA', '625475942786']   , '2.': ['CIVICA', '625475959565']  ,
  '3.': ['FILOSOFIA', '625475682875']  , '4.': ['FISICA', '529168615101']  ,
  '5.': ['GEOGRAFIA', '625471875367']  , '6.': ['HISTORIA', '625475911574']  ,
  '7.': ['INGLES', '625476071400']     , '8.': ['LENGUAJE', '625476370223']  ,
  '9.': ['LITERATURA', '529168497812'] , '10': ['MATEMATICA', '529168392034']  ,
  '11': ['PSICOLOGIA', '529168495742'] , '12': ['QUIMICA', '529168241654']  ,
  '13': ['RAZONAMIENTO MATEMATICO', '625475413106'] , '14': ['RAZONAMIENTO VERBAL', '529166619933']
}

id_216b = ""
id_modelo = ""

def main():

  #en caso de haber creado mal los materiales, usar la siguiente linea
  #delete_straft(id_216b)

  """"""
  for m_name in material:
    date_time = material[m_name]

    #retorna un diccionario con la misma clave y los enlace de drive
    dict = get_dict(id_modelo, m_name)
    
    for key in courses:
      num = m_name[-2:].strip()

      #:1 Primer caracter y -1 ultimo caracter
      title = f"{m_name[:1]}{num} - {courses[key][0]} BIOMEDICAS"

      #se crea material
      try:
        create_material (title, id_216b, courses[key][1], dict[key], date_time)
      except :
        print(f"{courses[key][0]} no ENCONTRADO")

if __name__ == "__main__":
  main()

#NOTA:
  #Si ocurrio algun error, borrar o comentar las claves que ya fueron publicadas