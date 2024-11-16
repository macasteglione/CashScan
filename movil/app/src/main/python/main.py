import requests


def classify_image(image_path, env_variables):
    api_url = env_variables.get("API_URL")
    api_key = env_variables.get("API_KEY")

    with open(image_path, "rb") as image_file:
        image_data = image_file.read()

    params = {
        "api_key": api_key,
    }

    response = requests.post(api_url, params=params, files={"file": image_data})

    if response.status_code == 200:
        result = response.json()
        if result["predictions"] and result["predictions"][0]["confidence"] > .85:
            return result["predictions"][0]["class"]
        else:
            return None
    else:
        return None
