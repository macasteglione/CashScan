import requests
import re


def getClass(image_path, env_variables):
    api_url = env_variables.get("API_URL")
    api_key = env_variables.get("API_KEY")

    with open(image_path, "rb") as image_file:
        image_data = image_file.read()

    params = {
        "api_key": api_key,
    }

    response = requests.post(
        api_url,
        params=params,
        files={"file": image_data}
    )

    if response.status_code == 200:
        result = response.json()

        if "predictions" in result and result["predictions"]:
            max_confidence = 0
            best_prediction = None

            for prediction in result["predictions"]:
                if prediction["confidence"] > max_confidence:
                    max_confidence = prediction["confidence"]
                    best_prediction = prediction

            if max_confidence > 0.85:
                return best_prediction["class"]
            else:
                print(f"Confianza insuficiente para clasificar la imagen: {result}")
                return None
        else:
            print(f"No se encontraron predicciones en la respuesta: {result}")
            return None
    else:
        print(f"Error en la solicitud: {response.reason}")
        return None


def usd(image_path, env_variables_usd={"API_URL":"https://detect.roboflow.com/coinsandbanknotes/3", "API_KEY":"x57vjbnPECjrCl0bcM6p"}):
    result = getClass(image_path, env_variables_usd)
    if result is None:
        return None
    return re.match(r"(\d+)", result).group(1)


def brl(image_path, env_variables_brl={"API_URL":"https://detect.roboflow.com/cedulas-9fprk/17", "API_KEY":"x57vjbnPECjrCl0bcM6p"}):
    result = getClass(image_path, env_variables_brl)
    if result is None:
        return None
    return re.match(r"(\d+)", result).group(1)


def ars(image_path, env_variables_ars={"API_URL":"https://detect.roboflow.com/guidobilletes/1", "API_KEY":"x57vjbnPECjrCl0bcM6p"}):
    return getClass(image_path, env_variables_ars)