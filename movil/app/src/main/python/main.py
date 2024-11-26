import requests


def classify_image(image_path, env_variables):
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
                print("Confianza insuficiente para clasificar la imagen.")
                return None
        else:
            print("No se encontraron predicciones en la respuesta.")
            return None
    else:
        print(f"Error en la solicitud: {response.status_code}")
        return None
