import os
import postprocesamiento.ocrimg as ocr
def work_files_in_directory(directory):
    files = os.listdir(directory)
    
    files = [f for f in files if os.path.isfile(os.path.join(directory, f))]

    for i, filename in enumerate(files):
        full_path = os.path.join(directory, filename)
        print(f"Trabajando: {full_path}")
        ocr.scan_chars(full_path)

directory = './venv/src/data/RecursosOCR'
try:
    work_files_in_directory(directory)
except Exception as e:
    print(f"Ocurrió un error: {e}")