import requests

API_URL = "https://detect.roboflow.com/guidobilletes/1"
API_KEY = "V0JV6bkDyq6us26QOomX"

def classify_image(image_path):
    with open(image_path, "rb") as image_file:
        image_data = image_file.read()

    params = {
        "api_key": API_KEY,
    }

    response = requests.post(API_URL, params=params, files={"file": image_data})

    if response.status_code == 200:
        result = response.json()
        if result["predictions"] and result["predictions"][0]["confidence"] > .8:
            return result["predictions"][0]["class"]
        else:
            return None
    else:
        return None