import cv2
import numpy as np
import os

def gray_world_white_balance(image):
    image_float = image.astype(np.float32)

    mean_b = np.mean(image_float[:, :, 0]) 
    mean_g = np.mean(image_float[:, :, 1])  
    mean_r = np.mean(image_float[:, :, 2])  
    
    mean_global = (mean_r + mean_g + mean_b) / 3
    
    scale_b = mean_global / mean_b
    scale_g = mean_global / mean_g
    scale_r = mean_global / mean_r
    
    image_float[:, :, 0] *= scale_b  
    image_float[:, :, 1] *= scale_g  
    image_float[:, :, 2] *= scale_r  
    
    image_float = np.clip(image_float, 0, 255)
    
    balanced_image = image_float.astype(np.uint8)
    
    return balanced_image

def resize_image(image, max_width):
    height, width = image.shape[:2]
    
    scale = max_width / width
    
    new_width = int(width * scale)
    new_height = int(height * scale)
    
    resized_image = cv2.resize(image, (new_width, new_height))
    
    return resized_image


def white_balance(full_path, filename):
    image = cv2.imread(full_path)

    if image is None:
        print(f"Error al cargar la imagen: {full_path}")
        exit(1)

    balanced_image = gray_world_white_balance(image)

    max_width = 800
    resized_image = resize_image(balanced_image, max_width)

    output_dir = './venv/src/data/ResultadosBll1000/'
    os.makedirs(output_dir, exist_ok=True)
    filename_without_ext = os.path.splitext(filename)[0]
    # Guardar la imagen y verificar si fue exitosa
    output_path = os.path.join(output_dir, f'{filename_without_ext}-EBA.jpg')
    success = cv2.imwrite(output_path, resized_image)

    if success:
        print(f"Imagen guardada exitosamente en: {output_path}")
    else:
        print("Error al guardar la imagen.")