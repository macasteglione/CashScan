import tensorflow as tf
import numpy as np
import os
from tensorflow.keras.preprocessing import image

# Cargar el modelo TFLite
def cargar_modelo_tflite(ruta_modelo_tflite):
    interpreter = tf.lite.Interpreter(model_path=ruta_modelo_tflite)
    interpreter.allocate_tensors()
    input_details = interpreter.get_input_details()
    output_details = interpreter.get_output_details()
    return interpreter, input_details, output_details

# Preprocesar la imagen
def cargar_imagen(ruta_imagen, tamaño_imagen=(224, 224)):
    img = image.load_img(ruta_imagen, target_size=tamaño_imagen)  # Cargar y redimensionar
    img_array = image.img_to_array(img)
    img_array = np.expand_dims(img_array, axis=0)  # Añadir dimensión para batch
    img_array /= 255.0  # Normalizar
    return img_array

# Hacer una predicción usando el modelo TFLite
def predecir_con_tflite(interpreter, input_details, output_details, img_array):
    interpreter.set_tensor(input_details[0]['index'], img_array)
    interpreter.invoke()
    output = interpreter.get_tensor(output_details[0]['index'])
    return np.argmax(output), output

# Probar todas las imágenes en una carpeta
def predecir_todas_imagenes(carpeta_imagenes, interpreter, input_details, output_details):
    conteo_clases = {}  # Diccionario para contar las predicciones por clase

    for nombre_archivo in os.listdir(carpeta_imagenes):
        if nombre_archivo.endswith('.jpg') or nombre_archivo.endswith('.png'):
            ruta_imagen = os.path.join(carpeta_imagenes, nombre_archivo)
            print(f'Procesando: {ruta_imagen}')
            
            # Cargar y preprocesar la imagen
            img_array = cargar_imagen(ruta_imagen)

            # Hacer la predicción
            clase_predicha, probabilidades = predecir_con_tflite(interpreter, input_details, output_details, img_array)

            # Aumentar el conteo de la clase predicha
            if clase_predicha in conteo_clases:
                conteo_clases[clase_predicha] += 1
            else:
                conteo_clases[clase_predicha] = 1

            print(f'Imagen: {nombre_archivo}')
            print(f'Clase predicha: {clase_predicha}')
            print(f'Probabilidades: {probabilidades}\n')

    # Mostrar el conteo final de las predicciones
    print("Conteo de predicciones por clase:")
    for clase, conteo in conteo_clases.items():
        print(f'Clase {clase}: {conteo} imágenes')

# Ruta al modelo y carpeta de imágenes
ruta_modelo_tflite = 'modelo_billetes.tflite'  # Cambia la ruta si es necesario
carpeta_imagenes_prueba = r'd:\IA-32\Imagenes billetes\200'  # Cambia a la ruta de la carpeta con imágenes

# Cargar el modelo
interpreter, input_details, output_details = cargar_modelo_tflite(ruta_modelo_tflite)

# Probar todas las imágenes de la carpeta
predecir_todas_imagenes(carpeta_imagenes_prueba, interpreter, input_details, output_details)
