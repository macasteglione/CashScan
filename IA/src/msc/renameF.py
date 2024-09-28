import os

def rename_files_in_directory(directory):
    files = os.listdir(directory)
    
    files = [f for f in files if os.path.isfile(os.path.join(directory, f))]

    for i, filename in enumerate(files):
        new_name = f"imgb-1000-{i + 1}.jpg"
        
        src = os.path.join(directory, filename)
        dst = os.path.join(directory, new_name)
        
        os.rename(src, dst)
        print(f"Renombrado: {filename} -> {new_name}")

directory = './venv/src/data/RecursosBll1000/' 

rename_files_in_directory(directory)
