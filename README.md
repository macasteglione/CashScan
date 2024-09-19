# Reconocimiento de Billetes

Este proyecto está diseñado para ofrecer un sistema que facilita el reconocimiento de billetes mediante técnicas de procesamiento de imágenes, utilizando Python.

## Instalación

#### 1. Crear y activar un entorno virtual

```sh
python -m venv env
source env/bin/activate
```

#### 2. Instalar las dependencias

Si ya tienes un archivo requirements.txt, usa el siguiente comando para instalar las dependencias necesarias:

```sh
pip install -r requirements.txt
```

Si necesitas generar el archivo requirements.txt desde tu entorno actual:

```sh
pip freeze > requirements.txt
```

#### 3. Crear las carpetas de salida

Crear la carpeta donde se guardarán los resultados de los scripts:

```sh
mkdir src/pesos
mkdir src/pesos/100
# O el numero del peso: 100_contours, 100_data, 100_canny, 100_resized
```

#### 4. Ejecución del proyecto

Para ejecutar el programa principal:

```sh
python src/preprocess/resize.py
# O, dependiendo de tu sistema:
python3 src/preprocess/resize.py
```

#### 5. Dependencia qt6

Para utilizar la biblioteca matplotlib se necesitan instalar las siguientes dependencias:

```sh
sudo apt install qt6-base-dev
sudo apt install libxcb-cursor0
```

## Otros Comandos

#### Desactivar el entorno virtual

Para salir del entorno virtual:

```sh
deactivate
```

#### Listar paquetes instalados

Puedes listar los paquetes instalados en el entorno actual usando:

```sh
pip list
```
