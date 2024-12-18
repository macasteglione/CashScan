import tensorflow as tf
import numpy as np
import os
from tensorflow.keras.preprocessing import image

def cargar_modelo_tflite(ruta_modelo_tflite):
    interpreter = tf.lite.Interpreter(model_path=ruta_modelo_tflite)
    interpreter.allocate_tensors()
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()
    return interpreter, input_details, output_details

def cargar_imagen(ruta_imagen, tamaño_imagen=(224, 224)):
    img = image.load_img(ruta_imagen, target_size=tamaño_imagen)
    img_array = image.img_to_array(img)
    img_array = np.expand_dims(img_array, axis=0)
    img_array /= 255.0
    return img_array

def predecir_con_tflite(interpreter, input_details, output_details, img_array):
    interpreter.set_tensor(input_details[0]['index'], img_array)
    interpreter.invoke()
    output = interpreter.get_tensor(output_details[0]['index'])
    return np.argmax(output), output

def predecir_todas_imagenes(carpeta_imagenes, interpreter, input_details, output_details):
    for nombre_archivo in os.listdir(carpeta_imagenes):
        if nombre_archivo.endswith('.jpg') or nombre_archivo.endswith('.png'):
            ruta_imagen = os.path.join(carpeta_imagenes, nombre_archivo)
            print(f'Procesando: {ruta_imagen}')
            
            img_array = cargar_imagen(ruta_imagen)

            clase_predicha, probabilidades = predecir_con_tflite(interpreter, input_details, output_details, img_array)

            print(f'Imagen: {nombre_archivo}')
            print(f'Clase predicha: {clase_predicha}')
            print(f'Probabilidades: {probabilidades}\n')

# Ruta al modelo y carpeta de imágenes
ruta_modelo_tflite = 'modelo_billetes.tflite' 
carpeta_imagenes_prueba = r'D:\IA-32\env\Data\2000'

# Carga de modelo
interpreter, input_details, output_details = cargar_modelo_tflite(ruta_modelo_tflite)

predecir_todas_imagenes(carpeta_imagenes_prueba, interpreter, input_details, output_details)
