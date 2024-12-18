import tensorflow as tf
import numpy as np

print("TensorFlow version:", tf.__version__)
print("NumPy version:", np.__version__)

a = tf.constant(2)
b = tf.constant(3)
c = a + b

print("Resultado de la suma en TensorFlow: ", c)