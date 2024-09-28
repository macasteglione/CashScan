import os
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

        # Nuevas dimensiones
        new_width = int(width * scale)
        new_height = int(height * scale)

        # Redimensionar la imagen
        resized_image = cv2.resize(image, (new_width, new_height))
        return resized_image
    else:
        # Imagen muy pequeñas como para redimensionar
        return image

def cut_image(image_path, filename):
    image = cv2.imread(image_path)
    if image is None:
        print(f"Error al cargar la imagen: {image_path}")
        exit(1)

    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    blurred = cv2.GaussianBlur(gray, (5, 5), 0)
    edges = cv2.Canny(blurred, 100, 200)
    #50-150

    contours, _ = cv2.findContours(edges, cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)
    cv2.drawContours(image, contours, -1, (0, 255, 0), 2)

    max_size = 800
    resized_image = resize_image(image, max_size)

    # Verifica si la carpeta de destino existe, si no, créala
    output_dir = './venv/src/data/RecortesBll1000/'
    os.makedirs(output_dir, exist_ok=True)

    filename_without_ext = os.path.splitext(filename)[0]
    output_path = os.path.join(output_dir, f'{filename_without_ext}-REC.jpg')
    success = cv2.imwrite(output_path, resized_image)

    if success:
        print(f"Imagen guardada exitosamente en: {output_path}")
    else:
        print("Error al guardar la imagen.")

    mask = np.zeros_like(gray)

    # Rellenar la máscara con los contornos
    cv2.drawContours(mask, contours, -1, (255), thickness=cv2.FILLED)

    # Crear una imagen donde todo lo fuera de los contornos será negro (usando la máscara)
    result = cv2.bitwise_and(image, image, mask=mask)

    # Redimensionar la nueva imagen recortada
    resized_result = resize_image(result, max_size)

    output_path_rec2 = os.path.join(output_dir, f'{filename_without_ext}-REC2.jpg')
    success_rec2 = cv2.imwrite(output_path_rec2, resized_result)

    if success_rec2:
        print(f"Imagen recortada y guardada exitosamente en: {output_path_rec2}")
    else:
        print("Error al guardar la imagen recortada.")