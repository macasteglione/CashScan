import cv2
import numpy as np


def resize_image(image, max_size):
    # Obtener las dimensiones originales de la imagen
    height, width = image.shape[:2]

    if width > max_size or height > max_size:
        if width > height:
            scale = max_size / width
        else:
            scale = max_size / height

        # Calcular las nuevas dimensiones
        new_width = int(width * scale)
        new_height = int(height * scale)

        # Redimensionar la imagen
        resized_image = cv2.resize(image, (new_width, new_height))
        return resized_image
    else:
        # Si la imagen ya es más pequeña que el tamaño máximo, devolver la imagen original
        return image


iname = "imgb-1000-8"

image = cv2.imread('src/data/ResultadosBll1000/'+iname+'.jpg')
#image = cv2.imread('src/data/RecursosBll1000/'+iname+'.jpg')

# Convertir a escala de grises
gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

# Aplicar desenfoque para reducir ruido
blurred = cv2.GaussianBlur(gray, (5, 5), 0)

# Usar la detección de bordes Canny
edges = cv2.Canny(blurred, 50, 150)

# Encontrar contornos
contours, _ = cv2.findContours(edges, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)

# Dibujar los contornos en la imagen original
cv2.drawContours(image, contours, -1, (0, 255, 0), 2)

max_size = 800
resized_image = resize_image(image, max_size)


# Mostrar el resultado
cv2.imwrite('src/data/ResultadosBll1000/'+ iname + '-REC.jpg', resized_image)
"""
cv2.imshow('Resized Image', resized_image)
cv2.waitKey(0)
cv2.destroyAllWindows()
"""