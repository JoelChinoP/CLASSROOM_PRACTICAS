import os.path

from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
from google_auth_oauthlib.flow import InstalledAppFlow
from googleapiclient.discovery import build
from googleapiclient.errors import HttpError

#Permisos que se colocan en el archivo token.json
SCOPES = ["https://www.googleapis.com/auth/classroom.courses", 
          "https://www.googleapis.com/auth/classroom.topics",
          "https://www.googleapis.com/auth/classroom.courseworkmaterials",
          "https://www.googleapis.com/auth/classroom.coursework.me",
          "https://www.googleapis.com/auth/calendar",
          "https://www.googleapis.com/auth/drive"]

def get_credentials():
  creds = None

  #Si el archivo token.json existe se actualizan los SCOPES
  if os.path.exists("credenciales/token.json"):
    creds = Credentials.from_authorized_user_file("credenciales/token.json", SCOPES)

  #Si las credenciales no son validas o no existen
  if not creds or not creds.valid:
    
    #Se actualizan las credenciales si expiraron
    if creds and creds.expired and creds.refresh_token:
      creds.refresh(Request())
      print("***Actualizacion de credenciales exitosa")

    #Se solicita al usuario que inicie sesion
    else:
      flow = InstalledAppFlow.from_client_secrets_file(
          "credenciales/credentials.json", SCOPES
      )
      creds = flow.run_local_server(port=0)
      print("***Autenticacion de credenciales exitosa!")

    #Se guardan las credenciales en el archivo token.json
    with open("credenciales/token.json", "w") as token:
      token.write(creds.to_json())

  return creds


def main():
  #Se llama al metodo que retorna las credenciales
  creds = get_credentials()

  try:
    #Se construye el servicio de la API con las credenciales
    service = build("classroom", "v1", credentials=creds)
    print("***Service funciona correctamente!")

  except HttpError as error:
    print(f"An error occurred: {error}")
    

if __name__ == "__main__":
  main()