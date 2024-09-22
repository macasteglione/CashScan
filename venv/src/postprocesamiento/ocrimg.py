import cv2
import pytesseract as pt
def scan_chars(image_path):
  
    print("el path es: "+image_path)
    image = cv2.imread(image_path)

    if image is None:
        print(f"Error al cargar la imagen: {image_path}")
        exit(1)

    pt.tesseract_cmd = r'C:\Program Files\Tesseract-OCR\tesseract.exe'

    # Convertir a escala de grises
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    # Opcional: aplicar un umbral o desenfoque para mejorar la detección
    # gray = cv2.GaussianBlur(gray, (5, 5), 0)
    # _, gray = cv2.threshold(gray, 127, 255, cv2.THRESH_BINARY)

    # Usar Tesseract para hacer OCR en la imagen
    text = pt.image_to_string(gray)

    # Mostrar el texto reconocido
    print("Texto reconocido:")
    print(text)

    # Mostrar la imagen
    cv2.imshow('Imagen', image)
    cv2.waitKey(0)
    cv2.destroyAllWindows()

    