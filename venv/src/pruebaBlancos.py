import os
import preprocesamiento.blancos2 as bla

def work_files_in_directory(directory):
    files = os.listdir(directory)
    # Filtrar solo archivos
    files = [f for f in files if os.path.isfile(os.path.join(directory, f))]

    for i, filename in enumerate(files):
        full_path = os.path.join(directory, filename)
        print(f"Trabajando: {full_path}")
        bla.white_balance(full_path,filename)

directory = './venv/src/data/RecursosBll1000'
try:
    work_files_in_directory(directory)
except Exception as e:
    print(f"Ocurrió un error: {e}")