import cv2
import numpy as np

def correct_illumination(image):
    # Convert image to grayscale
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    # Apply Gaussian blur
    blur = cv2.GaussianBlur(gray, (21, 21), 0)
    # Subtract blurred image from original to correct illumination
    corrected = cv2.divide(gray, blur, scale=255)
    return corrected

def apply_clahe(image):
    # Convert to LAB color space
    lab = cv2.cvtColor(image, cv2.COLOR_BGR2LAB)
    l, a, b = cv2.split(lab)
    # Apply CLAHE to L-channel
    clahe = cv2.createCLAHE(clipLimit=3.0, tileGridSize=(8,8))
    l_clahe = clahe.apply(l)
    # Merge channels and convert back to BGR
    lab_clahe = cv2.merge((l_clahe, a, b))
    image_clahe = cv2.cvtColor(lab_clahe, cv2.COLOR_LAB2BGR)
    return image_clahe

def balance_white(image):
    # Convert to float for processing
    image_float = image.astype(np.float32)
    
    # Calculate mean per channel (R, G, B)
    mean_r = np.mean(image_float[:, :, 2])
    mean_g = np.mean(image_float[:, :, 1])
    mean_b = np.mean(image_float[:, :, 0])
    
    # Global mean
    mean_global = (mean_r + mean_g + mean_b) / 3
    
    # Scaling factors for each channel
    scale_r = mean_global / mean_r
    scale_g = mean_global / mean_g
    scale_b = mean_global / mean_b
    
    # Scale the channels
    image_float[:, :, 2] *= scale_r
    image_float[:, :, 1] *= scale_g
    image_float[:, :, 0] *= scale_b
    
    # Clip values to range [0, 255] and convert back to uint8
    image_float = np.clip(image_float, 0, 255)
    balanced_image = image_float.astype(np.uint8)
    
    return balanced_image

def denoise_image(image):
    # Apply bilateral filter for noise reduction
    denoised_image = cv2.bilateralFilter(image, 9, 75, 75)
    return denoised_image

def preprocess_image(image):
    # 1. Correct Illumination
    illumination_corrected = correct_illumination(image)
    
    # 2. Apply White Balance
    white_balanced = balance_white(illumination_corrected)
    
    # 3. Apply CLAHE for contrast enhancement
    contrast_enhanced = apply_clahe(white_balanced)
    
    # 4. Denoise the image
    final_image = denoise_image(contrast_enhanced)
    
    return final_image

# Load your images
image_paths = ['src/data/RecursosBll1000/imgb-1000-211.jpg', 'src/data/RecursosBll1000/imgb-1000-2.jpg', 'src/data/RecursosBll1000/imgb-1000-10.jpg']
images = [cv2.imread(path) for path in image_paths]

# Preprocess the images
preprocessed_images = [preprocess_image(img) for img in images]

# Save the processed images for analysis
for path in image_paths:
    img = cv2.imread(path)
    if img is None:
        print(f"Error: Unable to load image at {path}")
    else:
        preprocessed_image = preprocess_image(img)
        output_path = f"src/data/ResultadosBll1000/processed_{path.split('/')[-1]}"
        cv2.imwrite(output_path, preprocessed_image)
        print(f"Processed image saved at: {output_path}")