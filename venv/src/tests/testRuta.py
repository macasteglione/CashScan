import cv2
import os

# Ruta al directorio de imágenes
ruta_directorio = "./venv/src/data"

# Crear la ruta completa a una imagen
ruta_imagen = os.path.join(ruta_directorio, "imgb-1000-3.jpg")

# Verificar si la imagen existe
if os.path.exists(ruta_imagen):
    # Leer la imagen
    img = cv2.imread(ruta_imagen)
    # ... (resto de tu código)
else:
    print("La imagen no existe")
