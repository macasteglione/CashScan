import tensorflow as tf
import numpy as np
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

# Ruta al modelo y a la imagen de prueba
ruta_modelo_tflite = 'modelo_billetes.tflite'  # Cambia la ruta si es necesario
ruta_imagen_prueba = r'D:\IA-32\env\Data\2000\2000d-50823ki5-ingestion-565fc7d87f-fmpk5_jpg.rf.c261a2ff14bda6c5177aba8e99bdd8cf.jpg'  # Cambia a la ruta de la imagen

# Cargar el modelo
interpreter, input_details, output_details = cargar_modelo_tflite(ruta_modelo_tflite)

# Cargar y preprocesar la imagen
img_array = cargar_imagen(ruta_imagen_prueba)

# Hacer la predicción
clase_predicha, probabilidades = predecir_con_tflite(interpreter, input_details, output_details, img_array)

print(f'Clase predicha: {clase_predicha}')
print(f'Probabilidades: {probabilidades}')