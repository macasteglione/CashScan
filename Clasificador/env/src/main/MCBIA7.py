import json
import tensorflow as tf
from tensorflow.keras import layers, models
from tensorflow.keras.preprocessing.image import ImageDataGenerator
from tensorflow.keras.callbacks import EarlyStopping
import os

# Ruta de imagenes
ruta_imagenes = r'D:\IA-32\env\Data'

# Parámetros de entrenamiento
tamaño_imagen = (224, 224)
batch_size = 32
epochs = 20  # Vueltas totales

# Aumentación de datos (No tocar)
datagen = ImageDataGenerator(
    rescale=1./255,             # Normalización de las imágenes
    validation_split=0.2,       # Dividir datos en entrenamiento y validación
    rotation_range=20,          # Rotaciones aleatorias
    width_shift_range=0.2,      # Desplazamientos horizontales aleatorios
    height_shift_range=0.2,     # Desplazamientos verticales aleatorios
    shear_range=0.2,            # Cizallamiento aleatorio
    zoom_range=0.2,             # Zoom aleatorio
    horizontal_flip=True        # Invertir horizontalmente
)

# Dataset de entrenamiento
train_generator = datagen.flow_from_directory(
    ruta_imagenes,
    target_size=tamaño_imagen,
    batch_size=batch_size,
    class_mode='categorical',
    subset='training'
)

# Dataset de validación
validation_generator = datagen.flow_from_directory(
    ruta_imagenes,
    target_size=tamaño_imagen,
    batch_size=batch_size,
    class_mode='categorical',
    subset='validation'
)

# Mapeo de clases
class_indices = train_generator.class_indices
with open('mapeo_clases.json', 'w') as f:
    json.dump(class_indices, f)

print(f'Mapeo de clases guardado: {class_indices}')

# Definición de modelo de red neuronal con Dropout y regularización L2
model = models.Sequential([
    layers.Conv2D(32, (3, 3), activation='relu', input_shape=(224, 224, 3)),
    layers.MaxPooling2D((2, 2)),
    
    layers.Conv2D(64, (3, 3), activation='relu'),
    layers.MaxPooling2D((2, 2)),

    layers.Conv2D(128, (3, 3), activation='relu'),
    layers.MaxPooling2D((2, 2)),

    layers.Conv2D(128, (3, 3), activation='relu'),
    layers.MaxPooling2D((2, 2)),

    layers.Flatten(),

    layers.Dense(512, activation='relu', kernel_regularizer=tf.keras.regularizers.l2(0.001)),  # Regularización L2
    layers.Dropout(0.5),  # Dropout para evitar el sobreajuste

    layers.Dense(len(train_generator.class_indices), activation='softmax')  # Número de clases de salida
])

# Compilacion del modelo
model.compile(optimizer='adam',
              loss='categorical_crossentropy',
              metrics=['accuracy'])

#Callback de EarlyStopping
early_stopping = EarlyStopping(monitor='val_loss', patience=3, restore_best_weights=True)

train_dataset = tf.data.Dataset.from_generator(
    lambda: train_generator,
    output_signature=(
        tf.TensorSpec(shape=(None, 224, 224, 3), dtype=tf.float32),
        tf.TensorSpec(shape=(None, len(train_generator.class_indices)), dtype=tf.float32)
    )
).repeat()

validation_dataset = tf.data.Dataset.from_generator(
    lambda: validation_generator,
    output_signature=(
        tf.TensorSpec(shape=(None, 224, 224, 3), dtype=tf.float32),
        tf.TensorSpec(shape=(None, len(validation_generator.class_indices)), dtype=tf.float32)
    )
).repeat()

# Entrenamiento
history = model.fit(
    train_dataset,
    steps_per_epoch=train_generator.samples // batch_size,
    validation_data=validation_dataset,
    validation_steps=validation_generator.samples // batch_size,
    epochs=epochs,
    callbacks=[early_stopping]  # Callback EarlyStopping
)

# Guardar el modelo entrenado
model.save('modelo_billetes.keras')

# Transformar el modelo
converter = tf.lite.TFLiteConverter.from_keras_model(model)
tflite_model = converter.convert()

# Guardar el modelo
with open('modelo_billetes.tflite', 'wb') as f:
    f.write(tflite_model)

print("Modelo entrenado y convertido a TensorFlow Lite")
